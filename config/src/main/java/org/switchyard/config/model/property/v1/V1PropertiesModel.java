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
package org.switchyard.config.model.property.v1;

import static org.switchyard.config.model.property.PropertyModel.PROPERTY;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.property.PropertiesModel;
import org.switchyard.config.model.property.PropertyModel;

/**
 * A version 1 PropertiesModel.
 */
public class V1PropertiesModel extends BaseModel implements PropertiesModel {

    private List<PropertyModel> _properties = new ArrayList<PropertyModel>();

    /**
     * Creates a new PropertiesModel in the specified namespace.
     * @param namespace the specified namespace
     */
    public V1PropertiesModel(String namespace) {
        super(new QName(namespace, PROPERTIES));
        setModelChildrenOrder(PROPERTY);
    }

    /**
     * Creates a new PropertiesModel with the specified configuration and descriptor.
     * @param config the configuration
     * @param desc the descriptor
     */
    public V1PropertiesModel(Configuration config, Descriptor desc) {
        super(config, desc);
        for (Configuration property_config : config.getChildren(PROPERTY)) {
            PropertyModel property = (PropertyModel)readModel(property_config);
            if (property != null) {
                _properties.add(property);
            }
        }
        setModelChildrenOrder(PROPERTY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<PropertyModel> getProperties() {
        return Collections.unmodifiableList(_properties);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized PropertiesModel addProperty(PropertyModel property) {
        addChildModel(property);
        _properties.add(property);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PropertyModel getProperty(String name) {
        PropertyModel property = null;
        for (PropertyModel p : _properties) {
            if (p.getName().equals(name)) {
                property = p;
                break;
            }
        }
        return property;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PropertyModel removeProperty(String propertyName) {
        PropertyModel removed = null;
        
        for (PropertyModel property : _properties) {
            if (property.getName().equals(propertyName)) {
                removed = property;
                _properties.remove(property);
            }
        }
        return removed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Properties toProperties() {
        Properties properties = new Properties();
        for (PropertyModel pm : getProperties()) {
            String name = pm.getName();
            String value = pm.getValue();
            if (name != null && value != null) {
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
        for (PropertyModel pm : getProperties()) {
            String name = pm.getName();
            String value = pm.getValue();
            if (name != null && value != null) {
                map.put(name, value);
            }
        }
        return map;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object resolveProperty(String key) {
        Object value = null;
        if (key != null) {
            PropertyModel property = getProperty(key);
            value = property != null ? property.getValue() : null;
        }
        return value;
    }

}
