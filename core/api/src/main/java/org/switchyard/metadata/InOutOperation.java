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
public class InOutOperation implements ServiceOperation {
    
    /**
     * Default name of the input message.
     */
    public static final String INPUT_MESSAGE = "in";
    /**
     *  Default name of the output message.
     */
    public static final String OUTPUT_MESSAGE = "out";

    // operation name
    private String _operationName;
    // input message name
    private String _inputName;
    // output message name
    private String _outputName;

    /**
     * Create a new instance of an InOut operation with a default message name.
     * @param operationName the name of the operation
     */
    public InOutOperation(String operationName) {
        this(operationName, INPUT_MESSAGE, OUTPUT_MESSAGE);
    }
    
    /**
     * Create a new instance of an InOut operation with the specified message name.
     * @param operationName the name of the operation
     * @param inputName input message name
     * @param outputName output message name
     */
    public InOutOperation(String operationName, String inputName, String outputName) {
        _operationName = operationName;
        _inputName = inputName;
        _outputName = outputName;
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
        return _outputName;
    }
}
