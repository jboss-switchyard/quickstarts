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
package org.switchyard.component.soap.config.model;

import org.switchyard.config.model.Model;

/**
 * A EndpointConfig Model.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public interface EndpointConfigModel extends Model {

    /**
     * The endpointConfig XML element.
     */
    public static final String ENDPOINT_CONFIG = "endpointConfig";

    /**
     * Gets the configFile.
     * @return the configFile
     */
    public String getConfigFile();

    /**
     * Sets the configFile.
     * @param configFile the configFile
     * @return this EndpointConfigModel (useful for chaining)
     */
    public EndpointConfigModel setConfigFile(String configFile);

    /**
     * Gets the configName.
     * @return the configName
     */
    public String getConfigName();

    /**
     * Sets the configName.
     * @param configName the configName
     * @return this EndpointConfigModel (useful for chaining)
     */
    public EndpointConfigModel setConfigName(String configName);

}
