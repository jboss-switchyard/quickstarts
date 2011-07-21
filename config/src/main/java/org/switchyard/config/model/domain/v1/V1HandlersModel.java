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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.domain.HandlerModel;
import org.switchyard.config.model.domain.HandlersModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;

/**
 * A version 1 PropertiesModel.
 */
public class V1HandlersModel extends BaseModel implements HandlersModel {

    private List<HandlerModel> _handlers = new ArrayList<HandlerModel>();

    /**
     * Constructs a new V1PropertiesModel.
     */
    public V1HandlersModel() {
        super(new QName(SwitchYardModel.DEFAULT_NAMESPACE, HandlersModel.HANDLERS));
        setModelChildrenOrder(HandlerModel.HANDLER);
    }

    /**
     * Constructs a new V1PropertiesModel with the specified Configuration and Descriptor.
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1HandlersModel(Configuration config, Descriptor desc) {
        super(config, desc);
        for (Configuration handler_config : config.getChildrenStartsWith(HandlerModel.HANDLER)) {
            HandlerModel handler = (HandlerModel)readModel(handler_config);
            if (handler != null) {
                _handlers.add(handler);
            }
        }
        setModelChildrenOrder(HandlerModel.HANDLER);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SwitchYardModel getSwitchYard() {
        return (SwitchYardModel)getModelParent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<HandlerModel> getHandlers() {
        return Collections.unmodifiableList(_handlers);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized HandlersModel addHandler(HandlerModel handler) {
        addChildModel(handler);
        _handlers.add(handler);
        return this;
    }
}
