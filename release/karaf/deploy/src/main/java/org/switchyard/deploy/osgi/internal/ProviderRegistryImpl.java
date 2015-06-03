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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;
import org.switchyard.common.util.ProviderRegistry;

/**
 * ProviderRegistryImpl.
 */
public class ProviderRegistryImpl implements ProviderRegistry.Registry, BundleTrackerCustomizer<Bundle> {

    public static final long DEFAULT_TIMEOUT = 0l;
    public static final String TIMEOUT = "org.switchyard.providers.timeout";

    private Map<String, List<Callable<Class>>> _factories;

    private ReadWriteLock _lock = new ReentrantReadWriteLock();
    private final BundleContext _bundleContext;
    private final BundleTracker<Bundle> _tracker;

    private ConcurrentMap<Long, Map<String, Callable<Class>>> allFactories = new ConcurrentHashMap<Long, Map<String, Callable<Class>>>();

    /**
     * Create a new instance of ProviderRegistryImpl.
     * @param bundleContext bundleContext
     */
    public ProviderRegistryImpl(BundleContext bundleContext) {
        _bundleContext = bundleContext;
        _tracker = new BundleTracker<Bundle>(bundleContext, Bundle.ACTIVE, this);
        _tracker.open();
    }

    @Override
    public <T> T getProvider(Class<T> clazz) {
        try {
            T provider = null;
            // Attempt to resolve via META-INF/services first
            Class<? extends T> pvdClass = locate(clazz);
            if (pvdClass != null) {
                provider = pvdClass.newInstance();
            } else {
                // Not found in META-INF/services, try OSGi Service Registry
                ServiceReference<T> ref = _bundleContext.getServiceReference(clazz);
                if (ref != null) {
                    provider = _bundleContext.getService(ref);
                }
            }
            return provider;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> List<T> getProviders(Class<T> clazz) {
        try {
            List<T> providers = new ArrayList<T>();
            List<Class<? extends T>> pvds = locateAll(clazz);
            for (Class<? extends T> pvd : pvds) {
                providers.add(pvd.newInstance());
            }
            return providers;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void destroy() {
        _tracker.close();
    }

    @Override
    public Bundle addingBundle(Bundle bundle, BundleEvent event) {
        register(bundle);
        return bundle;
    }

    @Override
    public void modifiedBundle(Bundle bundle, BundleEvent event, Bundle object) {
    }

    @Override
    public void removedBundle(Bundle bundle, BundleEvent event, Bundle object) {
        unregister(bundle.getBundleId());
    }

    public void register(String id, Callable<Class> factory) {
        _lock.writeLock().lock();
        try {
            if (_factories == null) {
                _factories = new HashMap<String, List<Callable<Class>>>();
            }
            List<Callable<Class>> l = _factories.get(id);
            if (l ==  null) {
                l = new ArrayList<Callable<Class>>();
                _factories.put(id, l);
            }
            l.add(0, factory);
            synchronized (_lock) {
                _lock.notifyAll();
            }
        } finally {
            _lock.writeLock().unlock();
        }
    }


    public void unregister(String id, Callable<Class> factory) {
        _lock.writeLock().lock();
        try {
            if (_factories != null) {
                List<Callable<Class>> l = _factories.get(id);
                if (l != null) {
                    l.remove(factory);
                }
            }
        } finally {
            _lock.writeLock().unlock();
        }
    }

    public <T> Class<? extends T> locate(Class<T> factoryId) {
        return locate(factoryId, factoryId.getName());
    }

    private static long getTimeout() {
        long timeout = DEFAULT_TIMEOUT;
        try {
            String prop = System.getProperty(TIMEOUT);
            if (prop != null) {
                timeout = Long.parseLong(prop);
            }
        } catch (Throwable t) { }
        return timeout;
    }

    public <T> Class<? extends T> locate(Class<T> factoryClass, String factoryId) {
        long timeout = getTimeout();
        if (timeout <= 0) {
            return doLocate(factoryClass, factoryId);
        }
        long t0 = System.currentTimeMillis();
        long t1 = t0;
        while (t1 - t0 < timeout) {
            Class<? extends T> impl = doLocate(factoryClass, factoryId);
            if (impl != null) {
                return impl;
            }
            synchronized (_lock) {
                try {
                    _lock.wait(timeout - (t1 - t0));
                } catch (InterruptedException e) {
                    return null;
                }
            }
            t1 = System.currentTimeMillis();
        }
        return null;
    }

    private <T> Class<? extends T> doLocate(Class<T> factoryClass, String factoryId) {
        _lock.readLock().lock();
        try {
            if (_factories != null) {
                List<Callable<Class>> l = _factories.get(factoryId);
                if (l != null && !l.isEmpty()) {
                    // look up the System property first
                    String factoryClassName = System.getProperty(factoryId);
                    try {
                        for (Callable<Class> i : l) {
                            Class c = null;
                            try {
                                c = i.call();
                            } catch (Exception ex) {
                                // do nothing here
                            }
                            if (c != null && factoryClass == c.getClassLoader().loadClass(factoryClass.getName())
                                    && (factoryClassName == null || c.getName().equals(factoryClassName)))
                            {
                                return c;
                            }
                        }
                    } catch (Exception ex) {
                        // do nothing here
                    }
                }
            }
            return null;
        } finally {
            _lock.readLock().unlock();
        }
    }

    public <T> List<Class<? extends T>> locateAll(Class<T> factoryId) {
        return locateAll(factoryId, factoryId.getName());
    }

    public <T> List<Class<? extends T>> locateAll(Class<T> factoryClass, String factoryId) {
        _lock.readLock().lock();
        try {
            List<Class<? extends T>> classes = new ArrayList<Class<? extends T>>();
            if (_factories != null) {
                List<Callable<Class>> l = _factories.get(factoryId);
                if (l != null) {
                    for (Callable<Class> i : l) {
                        try {
                            Class c = i.call();
                            if (c != null && factoryClass.isAssignableFrom(c)) {
                                classes.add(c);
                            }
                        } catch (Exception e) {
                        }
                    }
                }
            }
            return classes;
        } finally {
            _lock.readLock().unlock();
        }
    }

    protected void register(final Bundle bundle) {
        Map<String, Callable<Class>> map = allFactories.get(bundle.getBundleId());
        Enumeration<URL> e = bundle.findEntries("META-INF/services/", "*", false);
        if (e != null) {
            while (e.hasMoreElements()) {
                final URL u = e.nextElement();
                final String url = u.toString();
                if (url.endsWith("/")) {
                    continue;
                }
                final String factoryId = url.substring(url.lastIndexOf("/") + 1);
                if (map == null) {
                    map = new HashMap<String, Callable<Class>>();
                    allFactories.put(bundle.getBundleId(), map);
                }
                map.put(factoryId, new BundleFactoryLoader(factoryId, u, bundle));
            }
        }
        if (map != null) {
            for (Map.Entry<String, Callable<Class>> entry : map.entrySet()) {
                register(entry.getKey(), entry.getValue());
            }
        }
    }

    protected void unregister(long bundleId) {
        Map<String, Callable<Class>> map = allFactories.remove(bundleId);
        if (map != null) {
            for (Map.Entry<String, Callable<Class>> entry : map.entrySet()) {
                unregister(entry.getKey(), entry.getValue());
            }
        }
    }

    private class BundleFactoryLoader implements Callable<Class> {
        private final String factoryId;
        private final URL u;
        private final Bundle bundle;
        private volatile Class<?> clazz;

        public BundleFactoryLoader(String factoryId, URL u, Bundle bundle) {
            this.factoryId = factoryId;
            this.u = u;
            this.bundle = bundle;
        }

        public Class call() throws Exception {
            try {
                if (clazz == null){
                    synchronized (this) {
                        if (clazz == null){
                            BufferedReader br = new BufferedReader(new InputStreamReader(u.openStream(), "UTF-8"));
                            try {
                                String factoryClassName = br.readLine();
                                while (factoryClassName != null) {
                                    factoryClassName = factoryClassName.trim();
                                    if (factoryClassName.charAt(0) != '#') {
                                        clazz = bundle.loadClass(factoryClassName);
                                        return clazz;
                                    }
                                    factoryClassName = br.readLine();
                                }
                            } finally {
                                br.close();
                            }
                        }
                    }
                }
                return clazz;
            } catch (Exception e) {
                throw e;
            } catch (Error e) {
                throw e;
            }
        }

        @Override
        public String toString() {
            return u.toString();
        }

        @Override
        public int hashCode() {
            return u.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof BundleFactoryLoader) {
                return u.equals(((BundleFactoryLoader) obj).u);
            } else {
                return false;
            }
        }
    }
}