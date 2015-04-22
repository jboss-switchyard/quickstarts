/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.switchyard.console.components.client.internal;

import java.util.Map;

import org.switchyard.console.components.client.extension.ComponentProvider;

/**
 * ComponentExtensionManager
 * 
 * Provides an object for retrieving ComponentProvider implementations within
 * the console application.
 * 
 * @see ComponentExtension.
 * 
 * @author Rob Cernich
 */
public interface ComponentExtensionManager {

    /**
     * ComponentProviderProxy
     * 
     * A proxy to the underlying ComponentProvider. The proxy also provides
     * runtime access to the information specified within the annotation (e.g.
     * displayName).
     * 
     * @see ComponentExtension.
     * 
     * @author Rob Cernich
     */
    public interface ComponentProviderProxy extends ComponentProvider {
        /**
         * @return the displayName as specified in the annotation.
         */
        public String getDisplayName();
    }

    /**
     * @return all registered component providers.
     */
    public Map<String, ComponentProviderProxy> getExtensionProviders();

    /**
     * @param typeName the activation type.
     * 
     * @return the component provider; null if no provider is registered for
     *         this type.
     */
    public ComponentProviderProxy getExtensionProviderByTypeName(String typeName);

    /**
     * @param componentName the name of the SwitchYard component.
     * 
     * @return the component provider; null if no provider is registered for the
     *         component.
     */
    public ComponentProviderProxy getExtensionProviderByComponentName(String componentName);

}
