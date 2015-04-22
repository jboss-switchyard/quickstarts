/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.switchyard.deploy.osgi.internal;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.switchyard.deploy.osgi.EventConstants;
import org.switchyard.deploy.osgi.SwitchYardEvent;
import org.switchyard.deploy.osgi.SwitchYardListener;

import java.util.Arrays;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * SwitchYardEventDispatcher.
 */
public class SwitchYardEventDispatcher implements SwitchYardListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SwitchYardEventDispatcher.class);

    private final Set<SwitchYardListener> _listeners = new CopyOnWriteArraySet<SwitchYardListener>();
    private final Map<Bundle, SwitchYardEvent> _states = new ConcurrentHashMap<Bundle, SwitchYardEvent>();
    private final ExecutorService _executor;
    private final ExecutorService _sharedExecutor;
    private final EventAdminListener _eventAdminListener;
    private final ServiceTracker<SwitchYardListener, SwitchYardListener> _containerListenerTracker;

    SwitchYardEventDispatcher(final BundleContext bundleContext, ExecutorService sharedExecutor) {

        assert bundleContext != null;
        assert sharedExecutor != null;

        _executor = Executors.newSingleThreadExecutor(new SwitchYardThreadFactory("Switchyard Event Dispatcher"));

        _sharedExecutor = sharedExecutor;

        EventAdminListener listener = null;
        try {
            getClass().getClassLoader().loadClass("org.osgi.service.event.EventAdmin");
            listener = new EventAdminListener(bundleContext);
        } catch (Throwable t) {
            // Ignore, if the EventAdmin package is not available, just don't use it
            LOGGER.debug("EventAdmin package is not available, just don't use it");
        }
        _eventAdminListener = listener;

        _containerListenerTracker = new ServiceTracker<SwitchYardListener, SwitchYardListener>(bundleContext, SwitchYardListener.class.getName(), new ServiceTrackerCustomizer<SwitchYardListener, SwitchYardListener>() {
            public SwitchYardListener addingService(ServiceReference<SwitchYardListener> reference) {
                SwitchYardListener listener = bundleContext.getService(reference);
                synchronized (_listeners) {
                    sendInitialEvents(listener);
                    _listeners.add(listener);
                }
                return listener;
            }

            public void modifiedService(ServiceReference<SwitchYardListener> reference, SwitchYardListener service) {
            }

            public void removedService(ServiceReference<SwitchYardListener> reference, SwitchYardListener service) {
                _listeners.remove(service);
                bundleContext.ungetService(reference);
            }
        });
        _containerListenerTracker.open();
    }

    private void sendInitialEvents(SwitchYardListener listener) {
        for (Map.Entry<Bundle, SwitchYardEvent> entry : _states.entrySet()) {
            try {
                callListener(listener, new SwitchYardEvent(entry.getValue(), true));
            } catch (RejectedExecutionException ree) {
                LOGGER.warn("Executor shut down", ree);
                break;
            }
        }
    }

    public void switchyardEvent(final SwitchYardEvent event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Sending switchyard container event {} for bundle {}", toString(event), event.getBundle().getSymbolicName());
        }

        synchronized (_listeners) {
            callListeners(event);
            _states.put(event.getBundle(), event);
        }

        if (_eventAdminListener != null) {
            try {
                _sharedExecutor.submit(new Runnable() {
                    public void run() {
                        _eventAdminListener.switchyardEvent(event);
                    }
                });
            } catch (RejectedExecutionException ree) {
                LOGGER.warn("Executor shut down", ree);
            }
        }
    }

    @SuppressWarnings({"ThrowableResultOfMethodCallIgnored"})
    private static String toString(SwitchYardEvent event) {
        return "SwitchyardEvent[type=" + getEventType(event.getType())
                + (event.getDependencies() != null ? ", dependencies=" + Arrays.asList(event.getDependencies()) : "")
                + (event.getCause() != null ? ", exception=" + event.getCause().getMessage() : "")
                + "]";
    }

    private static String getEventType(int type) {
        switch (type) {
            case SwitchYardEvent.CREATING:
                return "CREATING";
            case SwitchYardEvent.CREATED:
                return "CREATED";
            case SwitchYardEvent.DESTROYING:
                return "DESTROYING";
            case SwitchYardEvent.DESTROYED:
                return "DESTROYED";
            case SwitchYardEvent.FAILURE:
                return "FAILURE";
            case SwitchYardEvent.GRACE_PERIOD:
                return "GRACE_PERIOD";
            default:
                return "UNKNOWN";
        }
    }

    private void callListeners(SwitchYardEvent event) {
        for (final SwitchYardListener listener : _listeners) {
            try {
                callListener(listener, event);
            } catch (RejectedExecutionException ree) {
                LOGGER.warn("Executor shut down", ree);
                break;
            }
        }
    }

    private void callListener(final SwitchYardListener listener, final SwitchYardEvent event) throws RejectedExecutionException {
        try {
            _executor.invokeAny(Collections.<Callable<Void>>singleton(new Callable<Void>() {
                public Void call() throws Exception {
                    listener.switchyardEvent(event);
                    return null;
                }
            }), 60L, TimeUnit.SECONDS);
        } catch (InterruptedException ie) {
            LOGGER.warn("Thread interrupted", ie);
            Thread.currentThread().interrupt();
        } catch (TimeoutException te) {
            LOGGER.warn("Listener timed out, will be ignored", te);
            _listeners.remove(listener);
        } catch (ExecutionException ee) {
            LOGGER.warn("Listener caused an exception, will be ignored", ee);
            _listeners.remove(listener);
        }
    }

    void destroy() {
        _executor.shutdown();
        // wait for the queued tasks to execute
        try {
            _executor.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // ignore
        }
        _containerListenerTracker.close();
        // clean up the EventAdmin tracker if we're using that
        if (_eventAdminListener != null) {
            _eventAdminListener.destroy();
        }
    }

    public void removeSwitchyardBundle(Bundle bundle) {
        _states.remove(bundle);
    }

    private static class EventAdminListener implements SwitchYardListener {

        private final ServiceTracker tracker;

        EventAdminListener(BundleContext context) {
            tracker = new ServiceTracker(context, EventAdmin.class.getName(), null);
            tracker.open();
        }

        @SuppressWarnings({"ThrowableResultOfMethodCallIgnored"})
        public void switchyardEvent(SwitchYardEvent event) {
            EventAdmin eventAdmin = (EventAdmin) tracker.getService();
            if (eventAdmin == null) {
                return;
            }

            Dictionary<String, Object> props = new Hashtable<String, Object>();
            props.put(EventConstants.TYPE, event.getType());
            props.put(EventConstants.EVENT, event);
            props.put(EventConstants.TIMESTAMP, event.getTimestamp());
            props.put(EventConstants.BUNDLE, event.getBundle());
            props.put(EventConstants.BUNDLE_SYMBOLICNAME, event.getBundle().getSymbolicName());
            props.put(EventConstants.BUNDLE_ID, event.getBundle().getBundleId());
            props.put(EventConstants.BUNDLE_VERSION, event.getBundle().getVersion());
            props.put(EventConstants.EXTENDER_BUNDLE, event.getExtenderBundle());
            props.put(EventConstants.EXTENDER_BUNDLE_ID, event.getExtenderBundle().getBundleId());
            props.put(EventConstants.EXTENDER_BUNDLE_SYMBOLICNAME, event.getExtenderBundle().getSymbolicName());
            props.put(EventConstants.EXTENDER_BUNDLE_VERSION, event.getExtenderBundle().getVersion());

            if (event.getCause() != null) {
                props.put(EventConstants.CAUSE, event.getCause());
            }
            if (event.getDependencies() != null) {
                props.put(EventConstants.DEPENDENCIES, event.getDependencies());
            }
            String topic;
            switch (event.getType()) {
                case SwitchYardEvent.CREATING:
                    topic = EventConstants.TOPIC_CREATING;
                    break;
                case SwitchYardEvent.CREATED:
                    topic = EventConstants.TOPIC_CREATED;
                    break;
                case SwitchYardEvent.DESTROYING:
                    topic = EventConstants.TOPIC_DESTROYING;
                    break;
                case SwitchYardEvent.DESTROYED:
                    topic = EventConstants.TOPIC_DESTROYED;
                    break;
                case SwitchYardEvent.FAILURE:
                    topic = EventConstants.TOPIC_FAILURE;
                    break;
                case SwitchYardEvent.GRACE_PERIOD:
                    topic = EventConstants.TOPIC_GRACE_PERIOD;
                    break;
                default:
                    throw new IllegalStateException("Unknown switchyard event type: " + event.getType());
            }
            eventAdmin.postEvent(new Event(topic, props));
        }

        /**
         * Perform cleanup at Switchyard extender shutdown.
         */
        public void destroy() {
            tracker.close();
        }

    }

}
