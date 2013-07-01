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
package org.switchyard.component.soap.config.model.v1;

import static org.switchyard.component.soap.config.model.SOAPBindingModel.DEFAULT_NAMESPACE;

import org.switchyard.component.soap.config.model.EndpointConfigModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * The 1st version InterceptorModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public class V1EndpointConfigModel extends BaseModel implements EndpointConfigModel {

    private static final String CONFIG_FILE = "configFile";
    private static final String CONFIG_NAME = "configName";

    /**
     * Creates a new InterceptorModel.
     */
    public V1EndpointConfigModel() {
        super(ENDPOINT_CONFIG, DEFAULT_NAMESPACE);
    }

    /**
     * Creates a new EndpointConfigModel with the specified configuration and descriptor.
     * @param config the configuration
     * @param desc the descriptor
     */
    public V1EndpointConfigModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getConfigFile() {
        return getModelAttribute(CONFIG_FILE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EndpointConfigModel setConfigFile(String configFile) {
        setModelAttribute(CONFIG_FILE, configFile);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getConfigName() {
        return getModelAttribute(CONFIG_NAME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EndpointConfigModel setConfigName(String configName) {
        setModelAttribute(CONFIG_NAME, configName);
        return this;
    }

}
