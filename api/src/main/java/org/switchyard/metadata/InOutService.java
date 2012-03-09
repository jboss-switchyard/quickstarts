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

/**
 * Implementation of ServiceInterface with a single ExchangePattern.IN_OUT
 * operation.
 */
public class InOutService extends BaseService {

    /**
     * Creates a new InOutService with a ServiceInterface.DEFAULT_OPERATION
     * operation.
     */
    public InOutService() {
        this(DEFAULT_OPERATION);
    }
    
    /**
     * Creates a new InOutService with the specified operation name.
     * @param operationName name of the operation
     */
    public InOutService(String operationName) {
        super(new InOutOperation(operationName));
    }

    /**
     * Creates a new InOutService with the specified operation metadata.
     * @param operation operation metadata
     */
    public InOutService(InOutOperation operation) {
        super(operation);
    } 
}
