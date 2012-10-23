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
package org.switchyard.component.remote;

import java.util.List;

import org.switchyard.Exchange;
import org.switchyard.HandlerException;
import org.switchyard.component.remote.config.model.RemoteBindingModel;
import org.switchyard.deploy.BaseServiceHandler;
import org.switchyard.remote.RemoteEndpoint;
import org.switchyard.remote.RemoteRegistry;
import org.switchyard.remote.http.HttpInvoker;

/**
 * Handles outbound communication to a remote service endpoint.
 */
public class RemoteServiceHandler extends BaseServiceHandler {
    
    private RemoteBindingModel _config;
    private RemoteRegistry _registry;
    
    /**
     * Create a new RemoteServiceHandler.
     * @param config binding configuration model
     * @param registry registry of remote services
     */
    public RemoteServiceHandler(RemoteBindingModel config, RemoteRegistry registry) {
        _config = config;
        _registry = registry;
    }
    
    @Override
    public void handleMessage(Exchange exchange) throws HandlerException {
        List<RemoteEndpoint> endpoints = _registry.getEndpoints(exchange.getProvider().getName());
        if (endpoints.isEmpty()) {
            throw new HandlerException(
                    "No remote endpoints found for service " + exchange.getProvider().getName());
        }
        
        // Add load-balancing logic here - picking the first one for now
        HttpInvoker invoker = new HttpInvoker(endpoints.get(0).getEndpoint());
        invoker.invoke(exchange);
        
    }
}
