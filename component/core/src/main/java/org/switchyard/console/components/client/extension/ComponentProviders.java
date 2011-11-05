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
