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
package org.switchyard.deploy.osgi;

import org.switchyard.deploy.Component;

import java.net.URI;

/**
 */
public interface ComponentRegistry {

    Component getComponent(String type);

    /**
     * Add a new Listener to be called when namespace handlers are registerd or unregistered
     *
     * @param listener the listener to register
     */
    void addListener(Listener listener);

    /**
     * Remove a previously registered Listener
     *
     * @param listener the listener to unregister
     */
    void removeListener(Listener listener);

    /**
     * Destroy this registry
     */
    void destroy();

    /**
     * Interface used to listen to registered or unregistered namespace handlers.
     *
     * @see ComponentRegistry#addListener(org.switchyard.deploy.osgi.ComponentRegistry.Listener)
     * @see ComponentRegistry#removeListener(org.switchyard.deploy.osgi.ComponentRegistry.Listener)
     */
    public interface Listener {

        /**
         * Called when a Component has been registered for the specified type.
         *
         * @param type the component activation type
         */
        void componentRegistered(String type);

        /**
         * Called when a Component has been unregistered for the specified type.
         *
         * @param type the component activation type
         */
        void componentUnregistered(String type);

    }

}
