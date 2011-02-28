/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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

package org.switchyard.internal;

import java.util.concurrent.ConcurrentHashMap;

import org.switchyard.Exchange;
import org.switchyard.handlers.HandlerChain;
import org.switchyard.spi.Endpoint;
import org.switchyard.spi.EndpointProvider;

/**
 * Default endpoint provider.
 */
public class DefaultEndpointProvider implements EndpointProvider {

    private ConcurrentHashMap<String, Endpoint> _endpoints = 
        new ConcurrentHashMap<String, Endpoint>();
    
    @Override
    public synchronized Endpoint createEndpoint(String name, HandlerChain handlerChain) {
        Endpoint endpoint = new DefaultEndpoint(name, handlerChain);
        _endpoints.put(name, endpoint);
        return endpoint;
    }

    @Override
    public Endpoint getEndpoint(String name) {
        return _endpoints.get(name);
    }
}

class DefaultEndpoint implements Endpoint {
    private HandlerChain _handlerChain;
    private String _name;

    /**
     * Constructor.
     * @param handlerChain handler chain
     */
    DefaultEndpoint(final String name, final HandlerChain handlerChain) {
        _name = name;
        _handlerChain = handlerChain;
    }

    @Override
    public void send(final Exchange exchange) {
        _handlerChain.handle(exchange);
    }

    @Override
    public String getName() {
        return _name;
    }
}
