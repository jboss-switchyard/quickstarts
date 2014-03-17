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
import org.osgi.framework.BundleContext;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * SimpleExtension.
 */
public abstract class SimpleExtension implements Extension {

    protected final Bundle _bundle;
    protected final BundleContext _bundleContext;
    protected final AtomicBoolean _destroyed = new AtomicBoolean(false);

    /**
     * Create new instance of SimpleExtension.
     * @param bundle bundle
     */
    public SimpleExtension(Bundle bundle) {
        _bundle = bundle;
        _bundleContext = bundle.getBundleContext();
    }

    /**
     * Whether the extension has been destroyed.
     * @return true if the extension has been destroyed, false otherwise.
     */
    public boolean isDestroyed() {
        synchronized (getLock()) {
            return _destroyed.get();
        }
    }

    /**
     * Retrieves the bundle for this extension.
     * @return bundle
     */
    public Bundle getBundle() {
        return _bundle;
    }

    /**
     * Retrieves the bundle context for this extension.
     * @return bundle context
     */
    public BundleContext getBundleContext() {
        return _bundleContext;
    }

    @Override
    public void start() throws Exception {
        synchronized (getLock()) {
            if (_destroyed.get()) {
                return;
            }
            if (_bundle.getState() != Bundle.ACTIVE) {
                return;
            }
            if (_bundle.getBundleContext() != _bundleContext) {
                return;
            }
            doStart();
        }
    }

    @Override
    public void destroy() throws Exception {
        synchronized (getLock()) {
            _destroyed.set(true);
        }
        doDestroy();
    }

    protected Object getLock() {
        return this;
    }

    protected abstract void doStart() throws Exception;

    protected abstract void doDestroy() throws Exception;

}
