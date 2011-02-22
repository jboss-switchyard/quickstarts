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

import javax.xml.namespace.QName;

import org.switchyard.ExchangePattern;

/**
 * Default implementation of ServiceOperation for ExchangePattern.IN_ONLY 
 * operations.
 */
public class InOnlyOperation extends BaseInvocationContract implements ServiceOperation {
    
    // The operation name
    private String _operationName;

    /**
     * Create a new instance of an InOnly operation with a default message name.
     * @param operationName the name of the operation
     */
    public InOnlyOperation(String operationName) {
        super(null, null, null);
        _operationName = operationName;
    }
    
    /**
     * Create a new instance of an InOnly operation with a specific message name.
     * @param operationName the name of the operation
     * @param inputName the name of the input message
     */
    public InOnlyOperation(String operationName, QName inputName) {
        super(inputName, null, null);
        _operationName = operationName;
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
     * @return operation name
     * @see ServiceOperation
     */
    @Override
    public String getName() {
        return _operationName; 
    }

    @Override
    public String toString() {
        return _operationName + "{IN_ONLY}";
    }
}
