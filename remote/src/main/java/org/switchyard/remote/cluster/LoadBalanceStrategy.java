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
package org.switchyard.remote.cluster;

import javax.xml.namespace.QName;

import org.switchyard.remote.RemoteEndpoint;
import org.switchyard.remote.RemoteRegistry;

/**
 * Represents the contract for endpoint selection strategy in a SwitchYard cluster.  A 
 * strategy implementation is initialized with information on remote endpoints 
 * (e.g. a reference to RemoteRegistry) and selects a service endpoint based on available
 * endpoints and its load balance strategy (e.g. RoundRobin, FirstAvailable, etc.).
 */
public interface LoadBalanceStrategy {
    /**
     * Select an endpoint based on the list of available endpoints and the implementation's
     * load balance strategy.
     * @param serviceName service name for lookup
     * @return selected service endpoint or null if no endpoint was located
     */
    RemoteEndpoint selectEndpoint(QName serviceName);
    
    /**
     * Returns a reference to the remote registry used by the load balancing strategy.
     * @return remote registry
     */
    RemoteRegistry getRegistry();
}
