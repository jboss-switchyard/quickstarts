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
package org.switchyard.deploy.osgi;

import org.switchyard.deploy.Component;

/**
 * ComponentRegistry.
 */
public interface ComponentRegistry {

    Component getComponent(String type);

    /**
     * Add a new Listener to be called when namespace handlers are registerd or unregistered.
     * @param listener the listener to register
     */
    void addListener(Listener listener);

    /**
     * Remove a previously registered Listener.
     * @param listener the listener to unregister
     */
    void removeListener(Listener listener);

    /**
     * Destroy this registry.
     */
    void destroy();

    /**
     * Interface used to listen to registered or unregistered namespace handlers.
     * @see ComponentRegistry#addListener(org.switchyard.deploy.osgi.ComponentRegistry.Listener)
     * @see ComponentRegistry#removeListener(org.switchyard.deploy.osgi.ComponentRegistry.Listener)
     */
    public interface Listener {

        /**
         * Called when a Component has been registered for the specified type.
         * @param type the component activation type
         */
        void componentRegistered(String type);

        /**
         * Called when a Component has been unregistered for the specified type.
         * @param type the component activation type
         */
        void componentUnregistered(String type);

    }

}
