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

import java.io.IOException;

import javax.xml.namespace.QName;

import org.switchyard.ExchangePattern;
import org.switchyard.io.Serialization.AccessType;
import org.switchyard.io.Serialization.CoverageType;
import org.switchyard.io.Serialization.Factory;
import org.switchyard.io.Serialization.Strategy;
import org.switchyard.metadata.InOnlyOperation.InOnlyOperationFactory;

/**
 * Default implementation of ServiceOperation for ExchangePattern.IN_ONLY 
 * operations.
 */
@Strategy(access=AccessType.FIELD, coverage=CoverageType.INCLUSIVE, factory=InOnlyOperationFactory.class)
public class InOnlyOperation extends BaseInvocationContract implements ServiceOperation {
    
    // The operation name
    private String _operationName;

    private InOnlyOperation() {}

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
    
    /**
     * The serialization factory for InOnlyOperation.
     */
    public static final class InOnlyOperationFactory implements Factory<InOnlyOperation> {
        @Override
        public InOnlyOperation create(Class<InOnlyOperation> type) throws IOException {
            return new InOnlyOperation();
        }
    }
}
