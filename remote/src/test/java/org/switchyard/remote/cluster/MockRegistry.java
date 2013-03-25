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
