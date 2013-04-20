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

import java.io.IOException;

import org.switchyard.remote.RemoteEndpoint;
import org.switchyard.remote.RemoteInvoker;
import org.switchyard.remote.RemoteMessage;
import org.switchyard.remote.RemoteMessages;
import org.switchyard.remote.RemoteRegistry;
import org.switchyard.remote.http.HttpInvoker;

/**
 * Cluster-aware implementation of RemoteInvoker which uses a load balance strategy in 
 * combination with a remote registry to invoke remote services.
 */
public class ClusteredInvoker implements RemoteInvoker {
    
    private LoadBalanceStrategy _loadBalancer;
    
    /**
     * Create a new ClusteredInvoker with the default load balance strategy (RoundRobin).
     * @param registry remote registry
     */
    public ClusteredInvoker(RemoteRegistry registry) {
        this(registry, new RoundRobinStrategy());
    }
    
    /**
     * Create a new ClusteredInvoker with the specified load balance strategy.
     * @param registry remote registry
     * @param loadBalancer load balance strategy
     */
    public ClusteredInvoker(RemoteRegistry registry, LoadBalanceStrategy loadBalancer) {
        _loadBalancer = loadBalancer;
        _loadBalancer.setRegistry(registry);
    }
    
    @Override
    public RemoteMessage invoke(RemoteMessage request) throws IOException {
        RemoteEndpoint ep = _loadBalancer.selectEndpoint(request.getService());
        if (ep == null) {
            throw RemoteMessages.MESSAGES.noRemoteEndpointFound(request.getService().toString());
        }
        return new HttpInvoker(ep.getEndpoint()).invoke(request);
    }
    
}
