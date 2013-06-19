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
 */
public class ComponentRegistryImpl implements ComponentRegistry, ServiceTrackerCustomizer<Component, Component> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NamespaceHandlerRegistryImpl.class);

    // The bundle context is thread safe
    private final BundleContext bundleContext;

    // The service tracker is thread safe
    private final ServiceTracker<Component, Component> tracker;

    // List of listeners
    private final List<Listener> listeners = new CopyOnWriteArrayList<Listener>();

    // List of components
    private final List<Component> components = new CopyOnWriteArrayList<Component>();

    public ComponentRegistryImpl(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
        tracker = new ServiceTracker<Component, Component>(bundleContext, Component.class, this);
        tracker.open();
    }

    @Override
    public Component getComponent(String type) {
        for (Component component : components) {
            if (component.getActivationTypes().contains(type)) {
                return component;
            }
        }
        return null;
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    @Override
    public Component addingService(ServiceReference<Component> reference) {
        Component component = bundleContext.getService(reference);
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

    public void destroy() {
        tracker.close();
    }

    private void registerComponent(Component component) {
        components.add(component);
        for (Listener listener : listeners) {
            for (String type : component.getActivationTypes()) {
                listener.componentRegistered(type);
            }
        }
    }

    private void unregisterComponent(Component component) {
        for (Listener listener : listeners) {
            for (String type : component.getActivationTypes()) {
                listener.componentUnregistered(type);
            }
        }
        components.remove(component);
    }

}
