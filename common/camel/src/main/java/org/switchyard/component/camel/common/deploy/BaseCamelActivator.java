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

import org.switchyard.common.camel.SwitchYardCamelContext;
import org.switchyard.config.Configuration;
import org.switchyard.deploy.BaseActivator;

/**
 * Activator for handling camel bindings on both, service and reference, sides.
 * 
 * @author Lukasz Dywicki
 */
public class BaseCamelActivator extends BaseActivator {

    private Configuration _environment;
    private SwitchYardCamelContext _camelContext;

    protected BaseCamelActivator(SwitchYardCamelContext context, String ... types) {
        super(types);
        _camelContext = context;
    }

    /**
     * Specify environment configuration for binding.
     * 
     * @param config Environment settings.
     */
    public void setEnvironment(Configuration config) {
        _environment = config;
    }

    /**
     * Gets the {@ling Configuration} used by this Activator.
     * 
     * @return Configuration of the Activator.
     */
    public Configuration getEnvironment() {
        return _environment;
    }

    /**
     * Gets the {@link SwitchYardCamelContext} used by this Activator.
     * 
     * @return The CamelContext used by this Activator.
     */
    public SwitchYardCamelContext getCamelContext() {
        return _camelContext;
    }

}
