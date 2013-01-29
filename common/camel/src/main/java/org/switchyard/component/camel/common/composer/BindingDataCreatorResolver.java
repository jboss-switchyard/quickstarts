/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.component.camel.common.composer;

import org.apache.camel.CamelContext;
import org.apache.camel.NoFactoryAvailableException;
import org.apache.camel.spi.FactoryFinder;

/**
 * Resolver which allows to plug-in custom {@link BindingDataCreator} injection.
 */
public class BindingDataCreatorResolver {

    private final static String BINDING_DATA_CREATOR_RESOURCE_PATH = "META-INF/services/org/switchyard/component/camel/";
    private FactoryFinder _bindingDataCreatorFactory;

    /**
     * Try resolve binding creator for given endpoint name.
     * 
     * @param name Name of endpoint, eg. DirectEndpoint.
     * @param context Camel context instance.
     * @return Dedicated BindingDataCreator or default if none found.
     */
    public BindingDataCreator<?> resolveBindingCreator(String name, CamelContext context) {
        Class<?> type = null;
        try {
            if (_bindingDataCreatorFactory == null) {
                _bindingDataCreatorFactory = context.getFactoryFinder(BINDING_DATA_CREATOR_RESOURCE_PATH);
            }
            type = _bindingDataCreatorFactory.findClass(name);
        } catch (NoFactoryAvailableException e) {
            e.getMessage(); // ignore
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid URI, no BindingDataCreator registered for scheme: " + name, e);
        }

        if (type != null) {
            if (BindingDataCreator.class.isAssignableFrom(type)) {
                return (BindingDataCreator<?>) context.getInjector().newInstance(type);
            } else {
                throw new IllegalArgumentException("Resolving binding data creator for endpoint of type : " + name
                    + " detected type conflict: Not a BindingDataCreator implementation. Found: " + type.getName());
            }
        }
        return new DefaultBindingDataCreator();
    }

}
