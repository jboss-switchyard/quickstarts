/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.switchyard.metadata;

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
    public InOutOperation(String operationType, String inputType, String outputType) {
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
    public InOutOperation(String operationType, String inputType, String outputType, String faultType) {
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
