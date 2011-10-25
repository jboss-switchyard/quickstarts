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
package org.switchyard.component.camel.config.model.v1;

import org.switchyard.component.camel.config.model.CamelBindingModel;
import org.switchyard.component.camel.config.model.OperationSelector;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.composite.v1.V1BindingModel;

/**
 * Version 1.0 implementation of a {@link CamelBindingModel}.
 * 
 * @author Daniel Bevenius
 */
public abstract class V1BaseCamelBindingModel extends V1BindingModel implements
        CamelBindingModel {

    /**
     * Camel endpoint type.
     */
    public static final String CAMEL = "camel";

    private OperationSelector _operationSelector;

    /**
     * Constructor.
     */
    public V1BaseCamelBindingModel() {
        this(CAMEL);
        setModelChildrenOrder();
    }

    /**
     * 
     * Create a new CamelBindingModel.
     * 
     * @param type
     *            binding type
     */
    public V1BaseCamelBindingModel(String type) {
        super(type, CamelBindingModel.DEFAULT_NAMESPACE);
    }

    /**
     * Create a CamelBindingModel from the specified configuration and
     * descriptor.
     * 
     * @param config
     *            The switchyard configuration instance.
     * @param desc
     *            The switchyard descriptor instance.
     */
    public V1BaseCamelBindingModel(Configuration config, Descriptor desc) {
        super(config, desc);
        setModelChildrenOrder();
    }

    @Override
    public OperationSelector getOperationSelector() {
        if (_operationSelector == null) {
            _operationSelector = (V1OperationSelector) getFirstChildModelStartsWith(OperationSelector.OPERATION_SELECTOR);
        }
        return _operationSelector;
    }

    @Override
    public V1BaseCamelBindingModel setOperationSelector(
            OperationSelector operationSelector) {
        if (_operationSelector == null) {
            setChildModel(operationSelector);
        }
        return this;
    }
}
