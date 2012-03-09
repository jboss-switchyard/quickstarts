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

package org.switchyard.config.model.switchyard;

import javax.xml.namespace.QName;

import org.switchyard.config.model.composite.InterfaceModel;

/**
 * An "esb" InterfaceModel.
 */
public interface EsbInterfaceModel extends InterfaceModel {

    /**
     * The "esb" implementation type.
     */
    static final String ESB = "esb";
    
    /**
     * The "inputType" parameter name.
     */
    static final String INPUT_TYPE = "inputType";

    /**
     * The "outputType" parameter name.
     */
    static final String OUTPUT_TYPE = "outputType";
    
    /**
     * The "faultType" parameter name.
     */
    static final String FAULT_TYPE = "faultType";
    
    /**
     * Returns the input type for the interface.
     * @return input type
     */
    QName getInputType();
    
    /**
     * Returns the output type for the interface.
     * @return input type
     */
    QName getOutputType();
    
    /**
     * Returns the fault type for the interface.
     * @return input type
     */
    QName getFaultType();
    
    /**
     * Sets the input type for the interface.
     * @param input input type
     * @return reference to this InterfaceModel
     */
    EsbInterfaceModel setInputType(QName input);
    
    /**
     * Sets the output type for the interface.
     * @param output output type
     * @return reference to this InterfaceModel
     */
    EsbInterfaceModel setOutputType(QName output);
    
    /**
     * Sets the fault type for the interface.
     * @param fault fault type
     * @return reference to this InterfaceModel
     */
    EsbInterfaceModel setFaultType(QName fault);

}
