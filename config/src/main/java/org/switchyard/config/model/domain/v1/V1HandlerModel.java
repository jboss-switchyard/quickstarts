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
import org.switchyard.config.model.domain.HandlerModel;
import org.switchyard.config.model.domain.HandlersModel;

/**
 * Implementation of HandlerModel : v1.
 */
public class V1HandlerModel extends BaseModel implements HandlerModel {

    /**
     * Constructs a new V1PropertyModel.
     */
    public V1HandlerModel() {
        super(new QName(DomainModel.DEFAULT_NAMESPACE, HandlerModel.HANDLER));
    }

    /**
     * Constructs a new V1HandlerModel with the specified Configuration and Descriptor.
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1HandlerModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HandlersModel getHandlers() {
        return (HandlersModel)getModelParent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return getModelAttribute(HandlerModel.NAME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HandlerModel setName(String name) {
        setModelAttribute(HandlerModel.NAME, name);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClassName() {
        return getModelAttribute(HandlerModel.CLASS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HandlerModel setClassName(String className) {
        setModelAttribute(HandlerModel.CLASS, className);
        return this;
    }
}
