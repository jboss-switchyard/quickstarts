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
package org.switchyard.remote;

import java.util.HashSet;
import java.util.Set;

import org.switchyard.metadata.BaseService;
import org.switchyard.metadata.ServiceInterface;
import org.switchyard.metadata.ServiceOperation;

/**
 * Remotable contract definition.
 */
public class RemoteInterface extends BaseService {
    
    /**
     * The "remote" type.
     */
    public static final String TYPE = "remote";
    
    /**
     * Create a new RemoteInterface with no operations.
     */
    public RemoteInterface() {
        super(new HashSet<ServiceOperation>(), TYPE);
    }
    
    /**
     * Create a new RemoteInterface with the specified operation and type.
     * @param operations the operation set
     * @param type the interface type
     */
    public RemoteInterface(Set<ServiceOperation> operations, String type) {
        super(operations, type);
    }
    
    /**
     * Creates a RemoteInterface representation from an existing contract.
     * @param contract the existing interface contract
     * @return RemoteInterface representation of the specified contract
     */
    public static RemoteInterface fromInterface(ServiceInterface contract) {
        return new RemoteInterface(contract.getOperations(), TYPE);
    }
    
}
