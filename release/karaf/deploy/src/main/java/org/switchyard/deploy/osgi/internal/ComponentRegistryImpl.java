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

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.switchyard.deploy.Component;
import org.switchyard.deploy.osgi.ComponentRegistry;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * ComponentRegistryImpl.
 */
public class ComponentRegistryImpl implements ComponentRegistry, ServiceTrackerCustomizer<Component, Component> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NamespaceHandlerRegistryImpl.class);

    // The bundle context is thread safe
    private final BundleContext _bundleContext;

    // The service tracker is thread safe
    private final ServiceTracker<Component, Component> _tracker;

    // List of listeners
    private final List<Listener> _listeners = new CopyOnWriteArrayList<Listener>();

    // List of components
    private final List<Component> _components = new CopyOnWriteArrayList<Component>();

    /**
     * Create a new ComponentRegistryImpl.
     * @param bundleContext bundleContext
     */
    public ComponentRegistryImpl(BundleContext bundleContext) {
        _bundleContext = bundleContext;
        _tracker = new ServiceTracker<Component, Component>(bundleContext, Component.class, this);
        _tracker.open();
    }

    @Override
    public Component getComponent(String type) {
        for (Component component : _components) {
            if (component.getActivationTypes().contains(type)) {
                return component;
            }
        }
        return null;
    }

    @Override
    public void addListener(Listener listener) {
        _listeners.add(listener);
    }

    @Override
    public void removeListener(Listener listener) {
        _listeners.remove(listener);
    }

    @Override
    public Component addingService(ServiceReference<Component> reference) {
        Component component = _bundleContext.getService(reference);
        registerComponent(component);
        return component;
    }

    @Override
    public void modifiedService(ServiceReference<Component> reference, Component component) {
        unregisterComponent(component);
        registerComponent(component);
    }

    @Override
    public void removedService(ServiceReference<Component> reference, Component component) {
        unregisterComponent(component);
    }

    @Override
    public void destroy() {
        _tracker.close();
    }

    private void registerComponent(Component component) {
        _components.add(component);
        for (Listener listener : _listeners) {
            for (String type : component.getActivationTypes()) {
                listener.componentRegistered(type);
            }
        }
    }

    private void unregisterComponent(Component component) {
        for (Listener listener : _listeners) {
            for (String type : component.getActivationTypes()) {
                listener.componentUnregistered(type);
            }
        }
        component.destroy();
        _components.remove(component);
    }

}
