/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.switchyard.internal.handlers;

import java.util.List;

import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.HandlerException;
import org.switchyard.Service;
import org.switchyard.internal.ExchangeImpl;
import org.switchyard.internal.ServiceRegistration;
import org.switchyard.spi.ServiceRegistry;

public class AddressingHandler implements ExchangeHandler {
    
    private ServiceRegistry _registry;
    
    public AddressingHandler(ServiceRegistry registry) {
        _registry = registry;
    }
    
    @Override
    public void handle(Exchange exchange) throws HandlerException {
        
    	// we need to mess with some internal exchange details
    	ExchangeImpl ei = (ExchangeImpl)exchange;
    	
        if (ei.getTarget() == null) {
            // find the receiving channel
            List<Service> serviceList = 
                _registry.getServices(exchange.getService());
            
            if (serviceList.isEmpty()) {
                // could not find a registered service - set the exchange
                throw new HandlerException(
                        "No endpoints for service " + exchange.getService());
            }
            
            // Endpoint selection is arbitrary at the moment
            ServiceRegistration sr = (ServiceRegistration)serviceList.get(0);
            ei.setTarget(sr.getEndpoint());
        }
        
    }

}
