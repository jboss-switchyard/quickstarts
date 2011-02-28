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
     * Default operation name for the default service interface.
     */
    String DEFAULT_OPERATION = "process";

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
