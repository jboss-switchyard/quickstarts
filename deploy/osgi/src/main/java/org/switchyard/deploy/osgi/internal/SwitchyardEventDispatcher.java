/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
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
import org.switchyard.deploy.osgi.SwitchyardEvent;
import org.switchyard.deploy.osgi.SwitchyardListener;

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
 */
public class SwitchyardEventDispatcher implements SwitchyardListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SwitchyardEventDispatcher.class);

    private final Set<SwitchyardListener> listeners = new CopyOnWriteArraySet<SwitchyardListener>();
    private final Map<Bundle, SwitchyardEvent> states = new ConcurrentHashMap<Bundle, SwitchyardEvent>();
    private final ExecutorService executor;
    private final ExecutorService sharedExecutor;
    private final EventAdminListener eventAdminListener;
    private final ServiceTracker<SwitchyardListener, SwitchyardListener> containerListenerTracker;

    SwitchyardEventDispatcher(final BundleContext bundleContext, ExecutorService sharedExecutor) {

        assert bundleContext != null;
        assert sharedExecutor != null;

        executor = Executors.newSingleThreadExecutor(new SwitchyardThreadFactory("Switchyard Event Dispatcher"));

        this.sharedExecutor = sharedExecutor;

        EventAdminListener listener = null;
        try {
            getClass().getClassLoader().loadClass("org.osgi.service.event.EventAdmin");
            listener = new EventAdminListener(bundleContext);
        } catch (Throwable t) {
            // Ignore, if the EventAdmin package is not available, just don't use it
            LOGGER.debug("EventAdmin package is not available, just don't use it");
        }
        this.eventAdminListener = listener;

        this.containerListenerTracker = new ServiceTracker<SwitchyardListener, SwitchyardListener>(bundleContext, SwitchyardListener.class.getName(), new ServiceTrackerCustomizer<SwitchyardListener, SwitchyardListener>() {
            public SwitchyardListener addingService(ServiceReference<SwitchyardListener> reference) {
                SwitchyardListener listener = bundleContext.getService(reference);
                synchronized (listeners) {
                    sendInitialEvents(listener);
                    listeners.add(listener);
                }
                return listener;
            }

            public void modifiedService(ServiceReference<SwitchyardListener> reference, SwitchyardListener service) {
            }

            public void removedService(ServiceReference<SwitchyardListener> reference, SwitchyardListener service) {
                listeners.remove(service);
                bundleContext.ungetService(reference);
            }
        });
        this.containerListenerTracker.open();
    }

    private void sendInitialEvents(SwitchyardListener listener) {
        for (Map.Entry<Bundle, SwitchyardEvent> entry : states.entrySet()) {
            try {
                callListener(listener, new SwitchyardEvent(entry.getValue(), true));
            } catch (RejectedExecutionException ree) {
                LOGGER.warn("Executor shut down", ree);
                break;
            }
        }
    }

    public void switchyardEvent(final SwitchyardEvent event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Sending switchyard container event {} for bundle {}", toString(event), event.getBundle().getSymbolicName());
        }

        synchronized (listeners) {
            callListeners(event);
            states.put(event.getBundle(), event);
        }

        if (eventAdminListener != null) {
            try {
                sharedExecutor.submit(new Runnable() {
                    public void run() {
                        eventAdminListener.switchyardEvent(event);
                    }
                });
            } catch (RejectedExecutionException ree) {
                LOGGER.warn("Executor shut down", ree);
            }
        }
    }

    @SuppressWarnings({"ThrowableResultOfMethodCallIgnored"})
    private static String toString(SwitchyardEvent event) {
        return "SwitchyardEvent[type=" + getEventType(event.getType())
                + (event.getDependencies() != null ? ", dependencies=" + Arrays.asList(event.getDependencies()) : "")
                + (event.getCause() != null ? ", exception=" + event.getCause().getMessage() : "")
                + "]";
    }

    private static String getEventType(int type) {
        switch (type) {
            case SwitchyardEvent.CREATING:
                return "CREATING";
            case SwitchyardEvent.CREATED:
                return "CREATED";
            case SwitchyardEvent.DESTROYING:
                return "DESTROYING";
            case SwitchyardEvent.DESTROYED:
                return "DESTROYED";
            case SwitchyardEvent.FAILURE:
                return "FAILURE";
            case SwitchyardEvent.GRACE_PERIOD:
                return "GRACE_PERIOD";
            default:
                return "UNKNOWN";
        }
    }

    private void callListeners(SwitchyardEvent event) {
        for (final SwitchyardListener listener : listeners) {
            try {
                callListener(listener, event);
            } catch (RejectedExecutionException ree) {
                LOGGER.warn("Executor shut down", ree);
                break;
            }
        }
    }

    private void callListener(final SwitchyardListener listener, final SwitchyardEvent event) throws RejectedExecutionException {
        try {
            executor.invokeAny(Collections.<Callable<Void>>singleton(new Callable<Void>() {
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
            listeners.remove(listener);
        } catch (ExecutionException ee) {
            LOGGER.warn("Listener caused an exception, will be ignored", ee);
            listeners.remove(listener);
        }
    }

    void destroy() {
        executor.shutdown();
        // wait for the queued tasks to execute
        try {
            executor.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // ignore
        }
        containerListenerTracker.close();
        // clean up the EventAdmin tracker if we're using that
        if (eventAdminListener != null) {
            eventAdminListener.destroy();
        }
    }

    public void removeSwitchyardBundle(Bundle bundle) {
        states.remove(bundle);
    }

    private static class EventAdminListener implements SwitchyardListener {

        private final ServiceTracker tracker;

        EventAdminListener(BundleContext context) {
            tracker = new ServiceTracker(context, EventAdmin.class.getName(), null);
            tracker.open();
        }

        @SuppressWarnings({"ThrowableResultOfMethodCallIgnored"})
        public void switchyardEvent(SwitchyardEvent event) {
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
                case SwitchyardEvent.CREATING:
                    topic = EventConstants.TOPIC_CREATING;
                    break;
                case SwitchyardEvent.CREATED:
                    topic = EventConstants.TOPIC_CREATED;
                    break;
                case SwitchyardEvent.DESTROYING:
                    topic = EventConstants.TOPIC_DESTROYING;
                    break;
                case SwitchyardEvent.DESTROYED:
                    topic = EventConstants.TOPIC_DESTROYED;
                    break;
                case SwitchyardEvent.FAILURE:
                    topic = EventConstants.TOPIC_FAILURE;
                    break;
                case SwitchyardEvent.GRACE_PERIOD:
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
