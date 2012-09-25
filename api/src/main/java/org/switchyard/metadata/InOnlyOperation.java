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
 * Default implementation of ServiceOperation for ExchangePattern.IN_ONLY 
 * operations.
 */
public class InOnlyOperation extends BaseServiceOperation {
    
    protected InOnlyOperation() {
        super(ExchangePattern.IN_OUT);
    }

    /**
     * Create a new instance of an InOnly operation with a default message name.
     * @param operationName the name of the operation
     */
    public InOnlyOperation(String operationName) {
        super(ExchangePattern.IN_ONLY, operationName, null, null, null);
    }
    
    /**
     * Create a new instance of an InOnly operation with a specific message name.
     * @param operationName the name of the operation
     * @param inputName the name of the input message
     */
    public InOnlyOperation(String operationName, QName inputName) {
        super(ExchangePattern.IN_ONLY, operationName, inputName, null, null);
    }

}
