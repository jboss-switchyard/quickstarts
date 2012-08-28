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
package org.switchyard.component.common.selector.config.model.v1;

import org.switchyard.component.common.selector.config.model.BindingModel;
import org.switchyard.component.common.selector.config.model.OperationSelectorModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.Marshaller;

/**
 * A version 1 BindingModel which supports OperationSelector.
 */
public abstract class V1BindingModel extends org.switchyard.config.model.composite.v1.V1BindingModel implements BindingModel {

    private OperationSelectorModel _operationSelectorModel;
    
    /**
     * Constructs a new V1CommonSelectorModel of the specified "type".
     * @param type the "type" of BindingModel
     */
    protected V1BindingModel(String type) {
        super(type);
    }
    
    /**
     * Constructs a new V1CommonSelectorBindingModel of the specified "type" with the specified namespace.
     * @param type the "type" of BindingModel
     * @param namespace binding namespace
     */
    protected V1BindingModel(String type, String namespace) {
        super(type, namespace);
    }

    /**
     * Constructs a new V1CommonSelectorBindingModel with the specified Configuration and Descriptor.
     * @param config the Configuration
     * @param desc the Descriptor
     */
    protected V1BindingModel(Configuration config, Descriptor desc) {
        super(config, desc);
        ClassLoader modelLoader = V1BindingModel.class.getClassLoader();
        desc.addDefaultProperties(modelLoader);
        Configuration selectorConfig = config.getFirstChildStartsWith(OperationSelectorModel.OPERATION_SELECTOR);
        if (selectorConfig != null) {
            Marshaller marsh = desc.getMarshaller(selectorConfig.getQName().getNamespaceURI(), modelLoader);
            _operationSelectorModel = OperationSelectorModel.class.cast(marsh.read(selectorConfig));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OperationSelectorModel getOperationSelector() {
        return _operationSelectorModel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V1BindingModel setOperationSelector(OperationSelectorModel model) {
        setChildModel(model);
        _operationSelectorModel = model;
        return this;
    }
}
