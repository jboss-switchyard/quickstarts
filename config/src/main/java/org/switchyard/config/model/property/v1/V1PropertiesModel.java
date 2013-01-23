/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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
package org.switchyard.config.model.property.v1;

import static org.switchyard.config.model.property.PropertyModel.PROPERTY;
import static org.switchyard.config.model.switchyard.SwitchYardModel.DEFAULT_NAMESPACE;

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
     * Creates a new PropertiesModel in the default namespace.
     */
    public V1PropertiesModel() {
        this(DEFAULT_NAMESPACE);
    }

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
    public String resolveProperty(String key) {
        PropertyModel property = getProperty(key);
        return property != null ? property.getValue() : null;
    }

}
