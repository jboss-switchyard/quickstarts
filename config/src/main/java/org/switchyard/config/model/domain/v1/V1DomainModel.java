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
import org.switchyard.config.model.BaseNamedModel;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.domain.DomainModel;
import org.switchyard.config.model.domain.HandlersModel;
import org.switchyard.config.model.domain.PropertiesModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.config.model.transform.TransformsModel;

/**
 * Implementation of DomainModel : v1.
 */
public class V1DomainModel extends BaseNamedModel implements DomainModel {
    
    private PropertiesModel _properties;
    private HandlersModel _handlers;
    
    /**
     * Constructs a new V1DomainModel.
     */
    public V1DomainModel() {
        super(new QName(SwitchYardModel.DEFAULT_NAMESPACE, DomainModel.DOMAIN));
        setModelChildrenOrder(TransformsModel.TRANSFORMS, PropertiesModel.PROPERTIES, HandlersModel.HANDLERS);
    }

    /**
     * Constructs a new V1DomainModel with the specified Configuration and Descriptor.
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1DomainModel(Configuration config, Descriptor desc) {
        super(config, desc);
        setModelChildrenOrder(TransformsModel.TRANSFORMS, PropertiesModel.PROPERTIES, HandlersModel.HANDLERS);
    }
    
    @Override
    public SwitchYardModel getSwitchYard() {
        return (SwitchYardModel)getModelParent();
    }

    @Override
    public synchronized HandlersModel getHandlers() {
        if (_handlers == null) {
            _handlers = (HandlersModel)getFirstChildModelStartsWith(HandlersModel.HANDLERS);
        }
        return _handlers;
    }

    @Override
    public synchronized PropertiesModel getProperties() {
        if (_properties == null) {
            _properties = (PropertiesModel)getFirstChildModelStartsWith(PropertiesModel.PROPERTIES);
        }
        return _properties;
    }

    @Override
    public DomainModel setProperties(PropertiesModel properties) {
        setChildModel(properties);
        _properties = properties;
        return this;
    }

    @Override
    public DomainModel setHandlers(HandlersModel handlers) {
        setChildModel(handlers);
        _handlers = handlers;
        return this;
    }
    
}
