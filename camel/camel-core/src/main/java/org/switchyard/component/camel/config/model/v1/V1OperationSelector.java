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

import javax.xml.namespace.QName;

import org.switchyard.component.camel.config.model.OperationSelector;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * Version 1 implementation of {@link OperationSelector}. 
 * 
 * @author Daniel Bevenius
 *
 */
public class V1OperationSelector extends OperationSelector {

    /**
     * Create a new OperationSelector.
     */
    public V1OperationSelector() {
        super(new QName(DEFAULT_NAMESPACE, OPERATION_SELECTOR));
    }
    
    protected V1OperationSelector(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OperationSelector setNamespace(String namespace) {
        setModelAttribute(NAMESPACE, namespace);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNamespace() {
        return getModelAttribute(NAMESPACE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OperationSelector setOperationName(String operationName) {
        setModelAttribute(OPERATION_NAME, operationName);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getOperationName() {
        return getModelAttribute(OPERATION_NAME);
    }

}
