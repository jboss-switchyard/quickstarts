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
