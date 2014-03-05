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
package org.switchyard.component.camel.common.composer;

import org.apache.camel.CamelContext;
import org.apache.camel.NoFactoryAvailableException;
import org.apache.camel.spi.FactoryFinder;
import org.switchyard.component.camel.common.CommonCamelMessages;

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
            throw CommonCamelMessages.MESSAGES.invalidURINoBindingDataCreatorRegisteredForScheme(name, e);
        }

        if (type != null) {
            if (BindingDataCreator.class.isAssignableFrom(type)) {
                return (BindingDataCreator<?>) context.getInjector().newInstance(type);
            } else {
                throw CommonCamelMessages.MESSAGES.resolvingBindingDataCreatorForEndpointOfType(name, type.getName());
            }
        }
        return new DefaultBindingDataCreator();
    }

}
