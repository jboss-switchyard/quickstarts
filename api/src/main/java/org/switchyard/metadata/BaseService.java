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

import java.util.HashSet;
import java.util.Set;

/**
 * Base implementation of ServiceInterface.  Specific interface types such as
 * Java and WSDL will extend this class or provide their own implementation of
 * ServiceInterface.
 */
public class BaseService implements ServiceInterface {
    
    // The interface type
    private String _type;
    // The interface operations
    private Set<ServiceOperation> _operations;
    
    /**
     * Create a new ServiceInterface with the specified operation.  The default
     * interface type, ServiceInterface.DEFAULT_TYPE, is assumed.
     * @param operation interface operation
     */
    public BaseService(ServiceOperation operation) {
        this(operation, ServiceInterface.DEFAULT_TYPE);
    }
    
    /**
     * Create a new ServiceInterface with the specified operations.  The default
     * interface type, ServiceInterface.DEFAULT_TYPE, is assumed.
     * @param operations interface operations
     */
    public BaseService(Set<ServiceOperation> operations) {
        this(operations, ServiceInterface.DEFAULT_TYPE);
    }
    
    /**
     * Create a new ServiceInterface with the specified operation and type.
     * @param operation interface operation
     * @param type interface type
     */
    public BaseService(ServiceOperation operation, String type) {
        _operations = new HashSet<ServiceOperation>(1);
        _operations.add(operation);
        _type = type;
    }
    
    /**
     * Create a new ServiceInterface with the specified operations and type.
     * @param operations interface operations
     * @param type interface type
     */
    public BaseService(Set<ServiceOperation> operations, String type) {
        _operations = operations;
        _type = type;
    }

    /**
     * @param name operation name
     * @return service operation
     * @see ServiceInterface
     */
    @Override
    public ServiceOperation getOperation(String name) {
        ServiceOperation operation = null;
        for (ServiceOperation op : _operations) {
            // the '==' covers null equality
            if (op.getName() == name || op.getName().equals(name)) {
                operation = op;
                break;
            }
        }
        return operation;
    }

    /**
     * @return set of service operations
     * @see ServiceInterface
     */
    @Override
    public Set<ServiceOperation> getOperations() {
        return new HashSet<ServiceOperation>(_operations);
    }

    /**
     * @return interface type
     * @see ServiceInterface
     */
    @Override
    public String getType() {
        return _type;
    }
}
