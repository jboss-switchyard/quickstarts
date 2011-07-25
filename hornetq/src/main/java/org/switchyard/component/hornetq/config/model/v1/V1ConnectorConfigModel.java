/*
 * JBoss, Home of Professional Open Source Copyright 2009, Red Hat Middleware
 * LLC, and individual contributors by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.switchyard.component.hornetq.config.model.v1;

import javax.xml.namespace.QName;

import org.switchyard.component.hornetq.config.model.HornetQConnectorConfigModel;
import org.switchyard.component.hornetq.config.model.HornetQConstants;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.domain.PropertiesModel;
import org.switchyard.config.model.domain.PropertyModel;

/**
 * 
 * @author Daniel Bevenius
 *
 */
public class V1ConnectorConfigModel extends BaseModel implements HornetQConnectorConfigModel {
    
    /**
     * Constructs a HornetQConnectorConfigModel using the default {@link HornetQConstants#DEFAULT_NAMESPACE}.
     */
    public V1ConnectorConfigModel() {
        super(new QName(HornetQConstants.DEFAULT_NAMESPACE, CONNECTOR));
        setModelChildrenOrder(FACTORY_CLASS_ELEMENT, PropertiesModel.PROPERTIES);
    }

    /**
     * Constructs a HornetQConnectorConfigModel using the passed-in Configuration and 
     * Descriptor.
     * 
     * @param config the SwitchYard configuration.
     * @param desc the {@link Descriptor}.
     */
    public V1ConnectorConfigModel(final Configuration config, final Descriptor desc) {
        super(config, desc);
    }

    @Override
    public String getConnectorName() {
        return getModelAttribute(NAME_ATTRIBUTE);
    }

    @Override
    public HornetQConnectorConfigModel setConnectorName(final String name) {
        setModelAttribute(NAME_ATTRIBUTE, name);
        return this;
    }

    @Override
    public String getConnectorClassName() {
        return getConfigValue(FACTORY_CLASS_ELEMENT);
    }
    
    private String getConfigValue(final String propertyName) {
        final Configuration config = getModelConfiguration().getFirstChild(propertyName);
        return config != null ? config.getValue() : null;
    }

    @Override
    public HornetQConnectorConfigModel setConnectorClassName(final String className) {
        setConfigValue(className, FACTORY_CLASS_ELEMENT);
        return this;
    }
    
    private void setConfigValue(final String value, String propertyName) {
        final Configuration config = getModelConfiguration().getFirstChild(propertyName);
        if (config != null) {
            config.setValue(value);
        } else {
            V1NameValueModel model = new V1NameValueModel(propertyName);
            model.setValue(value);
            setChildModel(model);
        }
    }
    
    @Override
    public HornetQConnectorConfigModel addProperty(final PropertyModel property) {
        addChildModel(property);
        return this;
    }

    @Override
    public PropertiesModel getProperties() {
        return (PropertiesModel) getFirstChildModel(PropertiesModel.PROPERTIES);
    }

}
