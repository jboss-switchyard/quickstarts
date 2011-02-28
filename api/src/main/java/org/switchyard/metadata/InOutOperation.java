/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
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

package org.switchyard.metadata;

import javax.xml.namespace.QName;

import org.switchyard.ExchangePattern;

/**
 * Default implementation of ServiceOperation for ExchangePattern.IN_OUT
 * operations.
 */
public class InOutOperation extends BaseInvocationContract implements ServiceOperation {
    
    // operation name
    private String _operationName;

    /**
     * Create a new instance of an InOut operation with a default message name.
     * @param operationName the name of the operation
     */
    public InOutOperation(String operationName) {
        this(operationName, null, null, null);
    }
    
    /**
     * Create a new instance of an InOut operation with the specified message name.
     * @param operationType the name of the operation
     * @param inputType input message name
     * @param outputType output message name
     */
    public InOutOperation(String operationType, QName inputType, QName outputType) {
        super(inputType, outputType, null);
        _operationName = operationType;
    }

    /**
     * Create a new instance of an InOut operation with the specified message name.
     * @param operationType the name of the operation
     * @param inputType input message name
     * @param outputType output message name
     * @param faultType fault message name
     */
    public InOutOperation(String operationType, QName inputType, QName outputType, QName faultType) {
        super(inputType, outputType, faultType);
        _operationName = operationType;
    }

    /**
     * @return ExchangePattern.IN_OUT.
     * @see ServiceOperation
     */
    @Override
    public ExchangePattern getExchangePattern() {
        return ExchangePattern.IN_OUT;
    }

    /**
     * @return operation name
     * @see ServiceOperation
     */
    @Override
    public String getName() {
        return _operationName; 
    }

    @Override
    public String toString() {
        return _operationName + "{IN_OUT}";
    }
}
