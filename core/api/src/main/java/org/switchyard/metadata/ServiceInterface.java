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

import java.util.Set;

/**
 * The canonical representation of a service interface in SwitchYard.  Every 
 * service in SwitchYard is registered with exactly one ServiceInterface, 
 * providing information on the type, operations, and input/output messages
 * for the service.  The SwitchYard runtime provides concrete implementations
 * of ServiceInterface for Java and WSDL interface types.  There is also
 * a default type of <code>ServiceInterface.DEFAULT_TYPE</code> which is used
 * to represent services which do not publish a formal contract.
 */
public interface ServiceInterface {

    /**
     * Default interface type used when a concrete interface (Java, WSDL) is
     * not provided by the service.
     */
    String DEFAULT_TYPE = "esb";
    /**
     * Empty string is the default operation name for service interface.
     */
    String DEFAULT_OPERATION = "";

    /**
     * The type of the interface - e.g. java, wsdl, esb, etc.
     * @return the interface type
     */
    String getType();
    
    /**
     * The set of operations available on this service.
     * @return A Set containing operations available on this service.  If the
     * interface contains no operations, the returned Set will be empty and not
     * null.
     */
    Set<ServiceOperation> getOperations();
    
    /**
     * Returns operation details for the specified name from the service interface.
     * @param name operation name
     * @return operation details or null if the named operation is not present
     * on this interface
     */
    ServiceOperation getOperation(String name);
}
