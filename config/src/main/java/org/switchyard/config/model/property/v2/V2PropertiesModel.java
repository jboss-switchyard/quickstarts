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
package org.switchyard.config.model.property.v2;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.switchyard.common.io.pull.PropertiesPuller;
import org.switchyard.common.io.pull.PropertiesPuller.PropertiesType;
import org.switchyard.common.io.pull.Puller.PathType;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.property.PropertiesModel;
import org.switchyard.config.model.property.v1.V1PropertiesModel;

/**
 * A version 2 PropertiesModel. NOTE: Like other config models, this one is NOT thread-safe!
 */
public class V2PropertiesModel extends V1PropertiesModel {

    private Properties _loadProperties = null;

    /**
     * Creates a new PropertiesModel in the specified namespace.
     * @param namespace the specified namespace
     */
    public V2PropertiesModel(String namespace) {
        super(namespace);
        loadProperies();
    }

    /**
     * Creates a new PropertiesModel with the specified configuration and descriptor.
     * @param config the configuration
     * @param desc the descriptor
     */
    public V2PropertiesModel(Configuration config, Descriptor desc) {
        super(config, desc);
        loadProperies();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLoad() {
        return getModelAttribute("load");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PropertiesModel setLoad(String load) {
        setModelAttribute("load", load);
        loadProperies();
        return this;
    }

    private void loadProperies() {
        _loadProperties = null;
        String propsPath = getLoad();
        if (propsPath != null) {
            PropertiesType propsType = propsPath.endsWith(".xml") ? PropertiesType.XML : PropertiesType.PROPERTIES;
            PropertiesPuller propsPuller = new PropertiesPuller(propsType);
            Properties props = propsPuller.pullPath(propsPath, getClass(), PathType.values());
            if (props != null) {
                _loadProperties = props;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPropertyValue(String name) {
        // defined properties (via super) always win
        String prop = super.getPropertyValue(name);
        if (prop == null && name != null && _loadProperties != null) {
            prop = _loadProperties.getProperty(name);
        }
        return prop;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Properties toProperties() {
        Properties properties = new Properties();
        if (_loadProperties != null) {
            for (Object key : _loadProperties.keySet()) {
                String name = (String)key;
                String value = _loadProperties.getProperty(name);
                properties.setProperty(name, value);
            }
        }
        // defined properties (via super) always win
        Properties superProperties = super.toProperties();
        if (superProperties != null) {
            for (Object key : superProperties.keySet()) {
                String name = (String)key;
                String value = superProperties.getProperty(name);
                properties.setProperty(name, value);
            }
        }
        return properties;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String,String> toMap() {
        Map<String,String> map = new HashMap<String,String>();
        if (_loadProperties != null) {
            for (Object key : _loadProperties.keySet()) {
                String name = (String)key;
                String value = _loadProperties.getProperty(name);
                map.put(name, value);
            }
        }
        // defined properties (via super) always win
        map.putAll(super.toMap());
        return map;
    }

}
