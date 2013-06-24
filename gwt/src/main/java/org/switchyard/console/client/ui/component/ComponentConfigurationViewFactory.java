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
package org.switchyard.console.client.ui.component;

import org.switchyard.console.client.ui.component.ComponentPresenter.ViewFactory;
import org.switchyard.console.components.client.extension.ComponentProvider;
import org.switchyard.console.components.client.extension.ComponentProviders;
import org.switchyard.console.components.client.ui.ComponentConfigurationPresenter.ComponentConfigurationView;

import com.google.inject.Inject;

/**
 * ComponentConfigurationViewFactory
 * 
 * Factory which delegates creation of ComponentConfigurationView objects to the
 * extension providers.
 * 
 * @author Rob Cernich
 */
public class ComponentConfigurationViewFactory implements ViewFactory {

    private final ComponentProviders _componentProviders;

    /**
     * Create a new ComponentConfigurationViewFactory.
     * 
     * @param componentProviders the ComponentProviders.
     */
    @Inject
    public ComponentConfigurationViewFactory(ComponentProviders componentProviders) {
        _componentProviders = componentProviders;
    }

    @Override
    public ComponentConfigurationView create(String componentName) {
        ComponentProvider provider = _componentProviders.getExtensionProviderByComponentName(componentName);
        if (provider == null) {
            provider = _componentProviders.getDefaultProvider();
        }
        return provider.createConfigurationView();
    }

}
