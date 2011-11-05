/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.console.client.ui.component;

import java.util.HashMap;
import java.util.Map;

import org.switchyard.console.client.ui.component.ComponentPresenter.PresenterFactory;
import org.switchyard.console.client.ui.component.ComponentPresenter.ViewFactory;
import org.switchyard.console.components.client.extension.ComponentProvider;
import org.switchyard.console.components.client.extension.ComponentProviders;
import org.switchyard.console.components.client.ui.ComponentConfigurationPresenter;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;

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
