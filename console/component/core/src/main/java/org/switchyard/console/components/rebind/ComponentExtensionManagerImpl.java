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
package org.switchyard.console.components.rebind;

import java.util.HashMap;
import java.util.Map;

import org.switchyard.console.components.client.extension.ComponentProvider;
import org.switchyard.console.components.client.internal.ComponentExtensionManager;
import org.switchyard.console.components.client.internal.ComponentProviderProxyImpl;

import com.google.gwt.core.client.GWT;

/**
 * ComponentExtensionManagerImpl
 * 
 * This is a template ComponentExtensionManager implementation used to help code
 * the generator.
 * 
 * @author Rob Cernich
 */
public class ComponentExtensionManagerImpl implements ComponentExtensionManager {

    private Map<String, ComponentProviderProxy> _providers = new HashMap<String, ComponentProviderProxy>();
    private Map<String, String> _typeToName = new HashMap<String, String>();

    /**
     * Create a new ComponentExtensionManagerImpl.
     */
    public ComponentExtensionManagerImpl() {
        final Class<?> componentProvider = null;
        _providers.put("componentName", new ComponentProviderProxyImpl("displayName") {
            public ComponentProvider instantiate() {
                return GWT.create(componentProvider);
            }
        });
        _typeToName.put("typeName", "componentName");
    }

    @Override
    public Map<String, ComponentProviderProxy> getExtensionProviders() {
        return _providers;
    }

    @Override
    public ComponentProviderProxy getExtensionProviderByComponentName(String componentName) {
        if (_providers.containsKey(componentName)) {
            return _providers.get(componentName);
        }
        return null;
    }

    @Override
    public ComponentProviderProxy getExtensionProviderByTypeName(String typeName) {
        if (_typeToName.containsKey(typeName)) {
            return getExtensionProviderByComponentName(_typeToName.get(typeName));
        }
        return null;
    }

}
