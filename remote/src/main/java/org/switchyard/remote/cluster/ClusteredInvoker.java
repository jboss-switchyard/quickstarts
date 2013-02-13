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

import java.io.IOException;

import org.switchyard.Exchange;
import org.switchyard.exception.SwitchYardException;
import org.switchyard.remote.RemoteEndpoint;
import org.switchyard.remote.RemoteInvoker;
import org.switchyard.remote.RemoteMessage;
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
    public void invoke(Exchange exchange) throws SwitchYardException {
        RemoteEndpoint ep = _loadBalancer.selectEndpoint(exchange.getProvider().getName());
        if (ep == null) {
            throw new SwitchYardException("No remote endpoints found for service " 
                    + exchange.getProvider().getName());
        }
        new HttpInvoker(ep.getEndpoint()).invoke(exchange);
    }

    @Override
    public RemoteMessage invoke(RemoteMessage request) throws IOException {
        RemoteEndpoint ep = _loadBalancer.selectEndpoint(request.getService());
        if (ep == null) {
            throw new SwitchYardException("No remote endpoints found for service " + request.getService());
        }
        return new HttpInvoker(ep.getEndpoint()).invoke(request);
    }
    
}
