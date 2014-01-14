/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

    @Override
    public String toString() {
        return String.format("BaseServiceInterface [type=%s, operations=%s]", _type, _operations);
    }
    
    protected void setType(String type) {
        _type = type;
    }

    protected void setOperations(Set<ServiceOperation> operations) {
        _operations = operations;
    }
}
