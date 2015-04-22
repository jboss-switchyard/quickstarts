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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.switchyard.remote.RemoteEndpoint;
import org.switchyard.remote.RemoteRegistry;

/**
 * Dummy registry which allows a single service with multiple endpoints.
 */
public class MockRegistry implements RemoteRegistry {
    
    private Map<QName, List<RemoteEndpoint>> endpoints = new HashMap<QName, List<RemoteEndpoint>>();

    @Override
    public void addEndpoint(RemoteEndpoint endpoint) {
        List<RemoteEndpoint> eps = endpoints.get(endpoint.getServiceName());
        if (eps == null) {
            endpoints.put(endpoint.getServiceName(),
                    new ArrayList<RemoteEndpoint>());
            endpoints.get(endpoint.getServiceName()).add(endpoint);
        } else {
            endpoints.get(endpoint.getServiceName()).add(endpoint);
        }
    }

    @Override
    public void removeEndpoint(RemoteEndpoint endpoint) {
        endpoints.remove(endpoint.getServiceName());
    }

    @Override
    public List<RemoteEndpoint> getEndpoints(QName serviceName) {
        List<RemoteEndpoint> eps = endpoints.get(serviceName);
        if (eps == null) {
            return new ArrayList<RemoteEndpoint>();
        }
        return eps;
    }

}
