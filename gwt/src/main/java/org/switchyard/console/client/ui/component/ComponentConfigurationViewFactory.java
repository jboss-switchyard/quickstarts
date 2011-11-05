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
