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

import static org.switchyard.config.model.switchyard.SwitchYardModel.DEFAULT_NAMESPACE;

import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.property.PropertiesModel;
import org.switchyard.config.model.property.PropertyModel;

/**
 * Implementation of PropertyModel : v1.
 */
public class V1PropertyModel extends BaseModel implements PropertyModel {

    /**
     * Creates a new PropertyModel in the default namespace.
     */
    public V1PropertyModel() {
        this(DEFAULT_NAMESPACE);
    }

    /**
     * Creates a new PropertyModel in the specified namespace.
     * @param namespace the specified namespace
     */
    public V1PropertyModel(String namespace) {
        super(new QName(namespace, PROPERTY));
    }

    /**
     * Creates a new PropertyModel with the specified configuration and descriptor.
     * @param config the configuration
     * @param desc the descriptor
     */
    public V1PropertyModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PropertiesModel getProperties() {
        return (PropertiesModel)getModelParent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return getModelAttribute("name");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PropertyModel setName(String name) {
        setModelAttribute("name", name);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getValue() {
        return getModelAttribute("value");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PropertyModel setValue(String value) {
        setModelAttribute("value", value);
        return this;
    }

}
