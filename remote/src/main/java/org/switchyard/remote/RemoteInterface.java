/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
