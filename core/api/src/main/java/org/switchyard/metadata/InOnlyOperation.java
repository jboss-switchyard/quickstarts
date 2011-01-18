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
 * Default implementation of ServiceOperation for ExchangePattern.IN_ONLY 
 * operations.
 */
public class InOnlyOperation implements ServiceOperation {
    
    /**
     *  Default name of the input message.
     */
    public static final String INPUT_MESSAGE = "in";
    
    // The operation name
    private String _operationName;
    // The input message name
    private String _inputName;
    
    /**
     * Create a new instance of an InOnly operation with a default message name.
     * @param operationName the name of the operation
     */
    public InOnlyOperation(String operationName) {
        this(operationName, INPUT_MESSAGE);
    }
    
    /**
     * Create a new instance of an InOnly operation with a specific message name.
     * @param operationName the name of the operation
     * @param inputName the name of the input message
     */
    public InOnlyOperation(String operationName, String inputName) {
        _operationName = operationName;
        _inputName = inputName;
    }

    /**
     * @return ExchangePattern.IN_ONLY.
     * @see ServiceOperation
     */
    @Override
    public ExchangePattern getExchangePattern() {
        return ExchangePattern.IN_ONLY;
    }
    
    /**
     * @return input message name
     * @see ServiceOperation
     */
    @Override
    public String getInputMessage() {
        return _inputName;
    }
    
    /**
     * @return operation name
     * @see ServiceOperation
     */
    @Override
    public String getName() {
        return _operationName; 
    }

    /**
     * @return output message name
     * @see ServiceOperation
     */
    @Override
    public String getOutputMessage() {
        return null;
    }

}
