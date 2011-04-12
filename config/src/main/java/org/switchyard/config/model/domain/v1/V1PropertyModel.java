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

import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.domain.DomainModel;
import org.switchyard.config.model.domain.PropertyModel;

/**
 * Implementation of PropertyModel : v1.
 */
public class V1PropertyModel extends BaseModel implements PropertyModel {

    /**
     * Constructs a new V1PropertyModel.
     */
    public V1PropertyModel() {
        super(new QName(DomainModel.DEFAULT_NAMESPACE, PropertyModel.PROPERTY));
    }

    /**
     * Constructs a new V1PropertyModel with the specified Configuration and Descriptor.
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1PropertyModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DomainModel getDomain() {
        return (DomainModel)getModelParent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return getModelAttribute(PropertyModel.NAME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PropertyModel setName(String name) {
        setModelAttribute(PropertyModel.NAME, name);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getValue() {
        return getModelAttribute(PropertyModel.VALUE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PropertyModel setValue(String value) {
        setModelAttribute(PropertyModel.VALUE, value);
        return this;
    }

}
