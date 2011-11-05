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
