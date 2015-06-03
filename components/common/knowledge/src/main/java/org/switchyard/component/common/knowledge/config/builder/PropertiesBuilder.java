/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.switchyard.component.common.knowledge.config.builder;

import java.util.Properties;

import org.switchyard.component.common.knowledge.config.model.KnowledgeComponentImplementationModel;
import org.switchyard.config.model.property.PropertiesModel;

/**
 * PropertiesBuilder.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
public class PropertiesBuilder extends KnowledgeBuilder {

    private Properties _defaultProperties;
    private Properties _modelProperties;
    private Properties _overrideProperties;

    /**
     * Creates a new PropertiesBuilder.
     */
    public PropertiesBuilder() {}

    /**
     * Creates a new PropertiesBuilder.
     * @param propertiesModel sets the model properties
     */
    public PropertiesBuilder(PropertiesModel propertiesModel) {
        setModelProperties(propertiesModel);
    }

    /**
     * Sets the default properties.
     * @param defaultProperties the default properties
     * @return this PropertiesBuilder
     */
    public PropertiesBuilder setDefaultProperties(Properties defaultProperties) {
        _defaultProperties = defaultProperties;
        return this;
    }

    /**
     * Sets the model properties.
     * @param propertiesModel the properties model
     * @return this PropertiesBuilder
     */
    public PropertiesBuilder setModelProperties(PropertiesModel propertiesModel) {
        _modelProperties = propertiesModel != null ? propertiesModel.toProperties() : null;
        return this;
    }

    /**
     * Sets the override properties.
     * @param overrideProperties the override properties
     * @return this PropertisBuilder
     */
    public PropertiesBuilder setOverrideProperties(Properties overrideProperties) {
        _overrideProperties = overrideProperties;
        return this;
    }

    /**
     * Builds a Properties.
     * @return a Properties
     */
    public Properties build() {
        Properties buildProperties = new Properties();
        merge(_defaultProperties, buildProperties);
        merge(_modelProperties, buildProperties);
        merge(_overrideProperties, buildProperties);
        return buildProperties;
    }

    private void merge(Properties fromProperties, Properties toProperties) {
        if (fromProperties != null && toProperties != null) {
            for (Object key : fromProperties.keySet()) {
                String name = (String)key;
                String value = fromProperties.getProperty(name);
                if (value != null) {
                    toProperties.put(name, value);
                }
            }
        }
    }

    /**
     * Creates a PropertiesBuilder.
     * @param implementationModel implementationModel
     * @return a PropertiesBuilder
     */
    public static PropertiesBuilder builder(KnowledgeComponentImplementationModel implementationModel) {
        PropertiesModel propertiesModel = null;
        if (implementationModel != null) {
            propertiesModel = implementationModel.getProperties();
        }
        return new PropertiesBuilder(propertiesModel);
    }

}
