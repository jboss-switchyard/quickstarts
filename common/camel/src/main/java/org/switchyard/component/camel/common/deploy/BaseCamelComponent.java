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
package org.switchyard.component.camel.common.deploy;

import java.util.List;

import org.switchyard.ServiceDomain;
import org.switchyard.common.camel.SwitchYardCamelContext;
import org.switchyard.deploy.Activator;
import org.switchyard.deploy.BaseComponent;

/**
 * Base class for camel binding components.
 */
public abstract class BaseCamelComponent extends BaseComponent {

    /**
     * Default constructor.
     * 
     * @param name Component name.
     * @param types Names of supported elements.
     */
    protected BaseCamelComponent(String name, String ... types) {
        super(types);
        setName(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Activator createActivator(ServiceDomain domain) {
        List<String> activationTypes = getActivationTypes();
        String[] types = activationTypes.toArray(new String[activationTypes.size()]);

        SwitchYardCamelContext camelContext = (SwitchYardCamelContext) domain
            .getProperty(SwitchYardCamelContext.CAMEL_CONTEXT_PROPERTY);
        BaseCamelActivator activator = createActivator(camelContext, types);
        activator.setServiceDomain(domain);
        activator.setEnvironment(getConfig());
        return activator;
    }

    /**
     * Creates activator for component.
     * 
     * @param camelContext Camel context.
     * @param types Activation types supported by component.
     * @return Activator instance.
     */
    protected abstract BaseCamelActivator createActivator(SwitchYardCamelContext camelContext, String ... types);

}

