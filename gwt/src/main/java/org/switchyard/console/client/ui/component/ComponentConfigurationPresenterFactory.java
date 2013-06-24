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

import java.util.HashMap;
import java.util.Map;

import org.switchyard.console.client.ui.component.ComponentPresenter.PresenterFactory;
import org.switchyard.console.client.ui.component.ComponentPresenter.ViewFactory;
import org.switchyard.console.components.client.extension.ComponentProvider;
import org.switchyard.console.components.client.extension.ComponentProviders;
import org.switchyard.console.components.client.ui.ComponentConfigurationPresenter;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

/**
 * ComponentConfigurationPresenterFactory
 * 
 * Factory which delegates creation of ComponentConfigurationPresenter objects
 * to the extension providers.
 * 
 * @author Rob Cernich
 */
public class ComponentConfigurationPresenterFactory implements PresenterFactory {

    private final EventBus _eventBus;
    private final ViewFactory _viewFactory;
    private final ComponentProviders _componentProviders;
    private final Map<String, ComponentConfigurationPresenter> _presentersCache = new HashMap<String, ComponentConfigurationPresenter>();

    /**
     * Create a new ComponentConfigurationPresenterFactory.
     * 
     * @param eventBus the EventBus
     * @param viewFactory the ViewFactory
     * @param componentProviders the ComponentProviders
     */
    @Inject
    public ComponentConfigurationPresenterFactory(EventBus eventBus, ViewFactory viewFactory,
            ComponentProviders componentProviders) {
        _eventBus = eventBus;
        _viewFactory = viewFactory;
        _componentProviders = componentProviders;
    }

    @Override
    public ComponentConfigurationPresenter create(String componentName) {
        if (_presentersCache.containsKey(componentName)) {
            return _presentersCache.get(componentName);
        }
        ComponentConfigurationPresenter presenter;
        ComponentProvider provider = _componentProviders.getExtensionProviderByComponentName(componentName);
        if (provider == null) {
            provider = _componentProviders.getDefaultProvider();
        }
        presenter = provider.createConfigurationPresenter(_eventBus, _viewFactory.create(componentName));
        _presentersCache.put(componentName, presenter);
        return presenter;
    }

}
