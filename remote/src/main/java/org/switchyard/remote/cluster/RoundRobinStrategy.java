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

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.namespace.QName;

import org.switchyard.remote.RemoteEndpoint;

/**
 * Round Robin load balancing strategy.  The algorithm used here is pretty naive in that the 
 * index is decoupled from the list of endpoints returned from the remote registry.  This means
 * there's potential for a given endpoint to be "skipped" in a given cycle if endpoints are 
 * removed between selections.  This is not harmful, but it means this implementation does not
 * guarantee completely uniform distribution in the event of cluster topology changes.
 */
public class RoundRobinStrategy extends BaseStrategy {
    
    private ConcurrentMap<QName, AtomicInteger> _endpointIdxs = new ConcurrentHashMap<QName, AtomicInteger>();
    
    /**
     * Create a new RoundRobin strategy.
     */
    public RoundRobinStrategy() {
        super();
    }

    @Override
    public RemoteEndpoint selectEndpoint(QName serviceName) {
        if (getRegistry() == null) {
            return null;
        }
        RemoteEndpoint selectedEp = null;
        List<RemoteEndpoint> eps = getRegistry().getEndpoints(serviceName);
        if (!eps.isEmpty()) {
            _endpointIdxs.putIfAbsent(serviceName, new AtomicInteger(0));
            AtomicInteger idx = _endpointIdxs.get(serviceName);
            synchronized (idx) {
                idx.set(idx.get() % eps.size());
                selectedEp = eps.get(idx.getAndIncrement());
            }
        }
        
        return selectedEp;
    }

}
