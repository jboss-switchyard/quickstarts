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
package org.switchyard.console.components.client.extension;

import java.util.Map;

import org.switchyard.console.components.client.internal.ComponentExtensionManager;

import com.google.inject.Inject;

/**
 * ComponentProviders
 * 
 * A wrapper which provides access to ComponentProvider implementations
 * contributed using the ComponentExtension annotation, as well as default
 * provider for use when a specified provider is not available.
 * 
 * @author Rob Cernich
 */
public class ComponentProviders implements ComponentExtensionManager {

    private final ComponentProvider _defaultProvider;
    private final ComponentExtensionManager _extensionManager;

    /**
     * Create a new ComponentProviders.
     * 
     * @param defaultProvider the default ComponentProvider.
     * @param extensionManager the ComponentExtensionManager.
     */
    @Inject
    public ComponentProviders(DefaultComponentProvider defaultProvider, ComponentExtensionManager extensionManager) {
        _defaultProvider = defaultProvider;
        _extensionManager = extensionManager;
    }

    @Override
    public Map<String, ComponentProviderProxy> getExtensionProviders() {
        return _extensionManager.getExtensionProviders();
    }

    @Override
    public ComponentProviderProxy getExtensionProviderByTypeName(String typeName) {
        return _extensionManager.getExtensionProviderByTypeName(typeName);
    }

    @Override
    public ComponentProviderProxy getExtensionProviderByComponentName(String componentName) {
        return _extensionManager.getExtensionProviderByComponentName(componentName);
    }

    /**
     * @return the default ComponentProvider.
     */
    public ComponentProvider getDefaultProvider() {
        return _defaultProvider;
    }
}
