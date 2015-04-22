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
    
    /**
     * Specifies the remote registry used by the load balancing strategy.
     * @param registry remote registry
     */
    void setRegistry(RemoteRegistry registry);
}
