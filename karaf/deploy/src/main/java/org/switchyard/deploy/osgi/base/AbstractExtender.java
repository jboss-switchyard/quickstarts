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
package org.switchyard.deploy.osgi.base;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.Constants;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * Base class to write bundle extenders.
 * This extender tracks started bundles (or starting if they have a lazy activation
 * policy) and will create an {@link Extension} for each of them to manage it.
 *
 * The extender will handle all concurrency and synchronization issues, see
 * {@link Extension} for more information about the additional constraints.
 *
 * The extender guarantee that all extensions will be stopped synchronously with
 * the STOPPING event of a given bundle and that all extensions will be stopped
 * before the extender bundle is stopped.
 *
 */
public abstract class AbstractExtender implements BundleActivator, BundleTrackerCustomizer<Bundle>, SynchronousBundleListener {

    private final ConcurrentMap<Bundle, Extension> _extensions = new ConcurrentHashMap<Bundle, Extension>();
    private final ConcurrentMap<Bundle, FutureTask> _destroying = new ConcurrentHashMap<Bundle, FutureTask>();
    private volatile boolean _stopping;

    private boolean _synchronous;
    private boolean _preemptiveShutdown;
    private BundleContext _context;
    private ExecutorService _executors;
    private BundleTracker _tracker;

    /**
     * Check if the extender is synchronous or not.
     * If the flag is set, the extender will start the extension synchronously
     * with the bundle being tracked or started.  Else, the starting of the
     * extension will be delegated to a thread pool.
     *
     * @return if the extender is synchronous
     */
    public boolean isSynchronous() {
        return _synchronous;
    }

    /**
     * Check if the extender performs a preemptive shutdown
     * of all extensions when the framework is being stopped.
     * The default behavior is to wait for the framework to stop
     * the bundles and stop the extension at that time.
     *
     * @return if the extender use a preemptive shutdown
     */
    public boolean isPreemptiveShutdown() {
        return _preemptiveShutdown;
    }

    /**
     * Retrieve the bundle context for extensions.
     * @return bundle context
     */
    public BundleContext getBundleContext() {
        return _context;
    }

    /**
     * Retrieve ExecutorService.
     * @return ExecutorService
     */
    public ExecutorService getExecutors() {
        return _executors;
    }

    /**
     * Indicates whether extensions should be started in the calling thread.
     * @param synchronous true for sync, false otherwise
     */
    public void setSynchronous(boolean synchronous) {
        _synchronous = synchronous;
    }

    /**
     * Specify whether extensions are subject to preemptive shutdown.
     * @param preemptiveShutdown true for preemptiveShutdown
     */
    public void setPreemptiveShutdown(boolean preemptiveShutdown) {
        _preemptiveShutdown = preemptiveShutdown;
    }

    @Override
    public void start(BundleContext context) throws Exception {
        _context = context;
        _context.addBundleListener(this);
        _tracker = new BundleTracker<Bundle>(_context, Bundle.ACTIVE | Bundle.STARTING, this);
        if (!_synchronous) {
            _executors = createExecutor();
        }
        doStart();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        _stopping = true;
        while (!_extensions.isEmpty()) {
            Collection<Bundle> toDestroy = chooseBundlesToDestroy(_extensions.keySet());
            if (toDestroy == null || toDestroy.isEmpty()) {
                toDestroy = new ArrayList<Bundle>(_extensions.keySet());
            }
            for (Bundle bundle : toDestroy) {
                destroyExtension(bundle);
            }
        }
        doStop();
        if (_executors != null) {
            _executors.shutdown();
            try {
                _executors.awaitTermination(60, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                // Ignore
                e.getMessage();
            }
            _executors = null;
        }
    }

    public void bundleChanged(BundleEvent event) {
        Bundle bundle = event.getBundle();
        if (bundle.getState() != Bundle.ACTIVE && bundle.getState() != Bundle.STARTING) {
            // The bundle is not in STARTING or ACTIVE state anymore
            // so destroy the context.  Ignore our own bundle since it
            // needs to kick the orderly shutdown.
            if (bundle != _context.getBundle()) {
                destroyExtension(bundle);
            }
        }
    }

    @Override
    public Bundle addingBundle(Bundle bundle, BundleEvent event) {
        modifiedBundle(bundle, event, bundle);
        return bundle;
    }

    @Override
    public void modifiedBundle(Bundle bundle, BundleEvent event, Bundle object) {
        // If the bundle being stopped is the system bundle,
        // do an orderly shutdown of all blueprint contexts now
        // so that service usage can actually be useful
        if (bundle.getBundleId() == 0 && bundle.getState() == Bundle.STOPPING) {
            if (_preemptiveShutdown) {
                try {
                    stop(_context);
                } catch (Exception e) {
                    error("Error while performing preemptive shutdown", e);
                }
                return;
            }
        }
        if (bundle.getState() != Bundle.ACTIVE && bundle.getState() != Bundle.STARTING) {
            // The bundle is not in STARTING or ACTIVE state anymore
            // so destroy the context.  Ignore our own bundle since it
            // needs to kick the orderly shutdown and not unregister the namespaces.
            if (bundle != _context.getBundle()) {
                destroyExtension(bundle);
            }
            return;
        }
        // Do not track bundles given we are stopping
        if (_stopping) {
            return;
        }
        // For starting bundles, ensure, it's a lazy activation,
        // else we'll wait for the bundle to become ACTIVE
        if (bundle.getState() == Bundle.STARTING) {
            String activationPolicyHeader = (String) bundle.getHeaders().get(Constants.BUNDLE_ACTIVATIONPOLICY);
            if (activationPolicyHeader == null || !activationPolicyHeader.startsWith(Constants.ACTIVATION_LAZY)) {
                // Do not track this bundle yet
                return;
            }
        }
        createExtension(bundle);
    }

    @Override
    public void removedBundle(Bundle bundle, BundleEvent event, Bundle object) {
        // Nothing to do
        destroyExtension(bundle);
    }
    

    protected void doStart() throws Exception {
        startTracking();
    }

    protected void doStop() throws Exception {
        stopTracking();
    }

    protected void startTracking() {
        _tracker.open();
    }

    protected void stopTracking() {
        _tracker.close();
    }

    /**
     * Create the executor used to start extensions asynchronously.
     *
     * @return an
     */
    protected ExecutorService createExecutor() {
        return Executors.newScheduledThreadPool(3);
    }

    /**
     *
     * @param bundles
     * @return
     */
    protected Collection<Bundle> chooseBundlesToDestroy(Set<Bundle> bundles) {
        return null;
    }

    /**
     * Create the extension for the given bundle, or null if the bundle is not to be extended.
     *
     * @param bundle the bundle to extend
     * @return
     * @throws Exception
     */
    protected abstract Extension doCreateExtension(Bundle bundle) throws Exception;

    protected abstract void debug(Bundle bundle, String msg);
    protected abstract void warn(Bundle bundle, String msg, Throwable t);
    protected abstract void error(String msg, Throwable t);

    private void createExtension(final Bundle bundle) {
        try {
            BundleContext bundleContext = bundle.getBundleContext();
            if (bundleContext == null) {
                // The bundle has been stopped in the mean time
                return;
            }
            final Extension extension = doCreateExtension(bundle);
            if (extension == null) {
                // This bundle is not to be extended
                return;
            }
            synchronized (_extensions) {
                if (_extensions.putIfAbsent(bundle, extension) != null) {
                    return;
                }
            }
            if (_synchronous) {
                debug(bundle, "Starting extension synchronously");
                extension.start();
            } else {
                debug(bundle, "Scheduling asynchronous start of extension");
                getExecutors().submit(new Runnable() {
                    public void run() {
                        try {
                            extension.start();
                        } catch (Throwable t) {
                            warn(bundle, "Error starting extension", t);
                        }
                    }
                });
            }
        } catch (Throwable t) {
            warn(bundle, "Error while creating extension", t);
        }
    }

    private void destroyExtension(final Bundle bundle) {
        FutureTask future;
        synchronized (_extensions) {
            debug(bundle, "Starting destruction process");
            future = _destroying.get(bundle);
            if (future == null) {
                final Extension extension = _extensions.remove(bundle);
                if (extension != null) {
                    debug(bundle, "Scheduling extension destruction");
                    future = new FutureTask<Void>(new Runnable() {
                        public void run() {
                            debug(bundle, "Destroying extension");
                            try {
                                extension.destroy();
                            } catch (Throwable t) {
                                warn(bundle, "Error while destroying extension", t);
                            } finally {
                                debug(bundle, "Finished destroying extension");
                                synchronized (_extensions) {
                                    _destroying.remove(bundle);
                                }
                            }
                        }
                    }, null);
                    _destroying.put(bundle, future);
                } else {
                    debug(bundle, "Not an extended bundle or destruction of extension already finished");
                }
            } else {
                debug(bundle, "Destruction already scheduled");
            }
        }
        if (future != null) {
            try {
                debug(bundle, "Waiting for extension destruction");
                future.run();
                future.get();
            } catch (Throwable t) {
                warn(bundle, "Error while destroying extension", t);
            }
        }
    }

}
