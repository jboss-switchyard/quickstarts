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
