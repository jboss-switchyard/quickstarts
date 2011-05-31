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
package org.switchyard.component.camel.config.model;

import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * An implementation of sca:operatorSelector which specifies an 'operationName'
 * attribute which is used to identify the operation on the target service.
 *  
 * @author Daniel Bevenius
 *
 */
public abstract class OperationSelector extends BaseModel {
    /**
     * The name of the 'operationSelector' element.
     */
    public static final String OPERATION_SELECTOR = "operationSelector";
    
    /**
     * The names space for the camel config model.
     */
    public static final String DEFAULT_NAMESPACE = "urn:switchyard-component-camel:config:1.0";
    
    /**
     * The 'operationName' attribute.
     */
    public static final String OPERATION_NAME = "operationName";
    
    protected OperationSelector(Configuration config) {
        super(config);
    }
    
    /**
     * Create a OperationSelector from the specified configuration and descriptor.
     * 
     * @param config The switchyard configuration instance.
     * @param desc The switchyard descriptor instance.
     */
    public OperationSelector(Configuration config, Descriptor desc) {
        super(config, desc);
    }
    
    /**
     * Create a new CamelImplementationModel.
     */
    protected OperationSelector(QName name) {
        super(name);
    }

    /**
     * Gets the operationName attribute from the underlying model.
     * 
     * @return String the content of the operationName attribute.
     */
    public abstract String getOperationName();
    
    /**
     * Sets the operation name attribute on the underlying model.
     * 
     * @param operationName The operation name.
     */
    public abstract void setOperationName(String operationName);
}
