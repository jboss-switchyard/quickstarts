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
package org.switchyard.config.model.domain.v1;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseNamedModel;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.domain.DomainModel;
import org.switchyard.config.model.domain.PropertyModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;

/**
 * Implementation of DomainModel : v1.
 */
public class V1DomainModel extends BaseNamedModel implements DomainModel {

    private Map<String, String> _properties = new HashMap<String, String>();
    
    /**
     * Constructs a new V1DomainModel.
     */
    public V1DomainModel() {
        super(new QName(DomainModel.DEFAULT_NAMESPACE, DomainModel.DOMAIN));
        setModelChildrenOrder(PropertyModel.PROPERTY);
    }

    /**
     * Constructs a new V1DomainModel with the specified Configuration and Descriptor.
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1DomainModel(Configuration config, Descriptor desc) {
        super(config, desc);
        for (Configuration property_config : config.getChildren(PropertyModel.PROPERTY)) {
            PropertyModel property = (PropertyModel)readModel(property_config);
            if (property != null) {
                _properties.put(property.getName(), property.getValue());
            }
        }
        setModelChildrenOrder(PropertyModel.PROPERTY);
    }
    
    @Override
    public SwitchYardModel getSwitchYard() {
        return (SwitchYardModel)getModelParent();
    }

    @Override
    public DomainModel setProperty(String name, String value) {
        PropertyModel property = new V1PropertyModel().setName(name).setValue(value);
        addChildModel(property);
        _properties.put(name, value);
        return this;
    }

    @Override
    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(_properties);
    }

    @Override
    public String getProperty(String name) {
        return _properties.get(name);
    }
}
