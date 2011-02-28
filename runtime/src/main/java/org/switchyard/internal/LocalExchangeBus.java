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

import javax.xml.namespace.QName;

import org.switchyard.Exchange;
import org.switchyard.ServiceReference;
import org.switchyard.handlers.HandlerChain;
import org.switchyard.spi.Dispatcher;
import org.switchyard.spi.ExchangeBus;

/**
 * Default endpoint provider.
 */
public class LocalExchangeBus implements ExchangeBus {

    private ConcurrentHashMap<QName, Dispatcher> _dispatchers = 
        new ConcurrentHashMap<QName, Dispatcher>();
    
    @Override
    public synchronized Dispatcher createDispatcher(
            ServiceReference service, HandlerChain handlerChain) {
        Dispatcher dispatcher = new LocalDispatcher(service, handlerChain);
        // service can be null for temp/reply dispatcher
        if (service != null) {
            _dispatchers.put(service.getName(), dispatcher);
        }
        return dispatcher;
    }

    @Override
    public Dispatcher getDispatcher(ServiceReference service) {
        return _dispatchers.get(service.getName());
    }
}

class LocalDispatcher implements Dispatcher {
    private HandlerChain _handlerChain;
    private ServiceReference _service;

    /**
     * Constructor.
     * @param handlerChain handler chain
     */
    LocalDispatcher(final ServiceReference service, final HandlerChain handlerChain) {
        _service = service;
        _handlerChain = handlerChain;
    }

    @Override
    public void dispatch(final Exchange exchange) {
        _handlerChain.handle(exchange);
    }

    @Override
    public ServiceReference getService() {
        return _service;
    }
}
