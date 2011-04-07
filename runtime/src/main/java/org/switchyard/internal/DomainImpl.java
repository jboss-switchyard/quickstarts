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

import java.util.List;

import javax.xml.namespace.QName;

import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.ServiceDomain;
import org.switchyard.ServiceReference;
import org.switchyard.handlers.HandlerChain;
import org.switchyard.handlers.TransformHandler;
import org.switchyard.metadata.ExchangeContract;
import org.switchyard.metadata.InOutService;
import org.switchyard.metadata.ServiceInterface;
import org.switchyard.spi.Dispatcher;
import org.switchyard.spi.ExchangeBus;
import org.switchyard.spi.Service;
import org.switchyard.spi.ServiceRegistry;
import org.switchyard.transform.TransformerRegistry;

/**
 * Implementation of ServiceDomain.
 */
public class DomainImpl implements ServiceDomain {

    private final QName _name;
    private final DefaultHandlerChain _defaultHandlers;
    private final ServiceRegistry _registry;
    private final ExchangeBus _exchangeBus;
    private final TransformerRegistry _transformerRegistry;

    /**
     * Constructor.
     * @param name name
     * @param registry registry
     * @param exchangeBus message exchange bus
     * @param transformerRegistry transformerRegistry
     */
    public DomainImpl(QName name,
            ServiceRegistry registry,
            ExchangeBus exchangeBus,
            TransformerRegistry transformerRegistry) {

        _name = name;
        _registry = registry;
        _exchangeBus  = exchangeBus;
        _transformerRegistry = transformerRegistry;

        // Build out the system handlers chain.  It would be cleaner if we
        // handled this via config.
        _defaultHandlers = new DefaultHandlerChain();
        _defaultHandlers.addFirst("transformation", new TransformHandler(_transformerRegistry));
    }

    @Override
    public Exchange createExchange(ServiceReference service, ExchangeContract contract) {
        return createExchange(service, contract, null);
    }



    @Override
    public Exchange createExchange(
            ServiceReference service, ExchangeContract contract, ExchangeHandler handler) {
        Dispatcher dispatcher = _exchangeBus.getDispatcher(service);
        HandlerChain replyChain = null;
        
        if (handler != null) {
            replyChain = _defaultHandlers.copy();
            replyChain.addLast("replyHandler", handler);
        }

        // create the exchange
        ExchangeImpl exchange = new ExchangeImpl(service.getName(), contract,
                dispatcher, _transformerRegistry, replyChain);
        return exchange;
    }

    @Override
    public ServiceReference registerService(QName serviceName, ExchangeHandler handler) {
        return registerService(serviceName, handler, null);
    }

    @Override
    public ServiceReference registerService(QName serviceName, ExchangeHandler handler,
            ServiceInterface metadata) {
        // If no service interface is provided, we default to InOutService
        if (metadata == null) {
            metadata = new InOutService();
        }
        // Create the service reference
        ServiceReference reference = new ServiceReferenceImpl(serviceName, metadata, this);
        // Add a handler chain with the provider at the end
        HandlerChain handlers = _defaultHandlers.copy();
        handlers.addLast("provider", handler);
        Dispatcher ep = _exchangeBus.createDispatcher(reference, handlers, _transformerRegistry);
        
        // register the service
        return _registry.registerService(reference, ep, this).getReference();
    }

    @Override
    public QName getName() {
        return _name;
    }

    @Override
    public TransformerRegistry getTransformerRegistry() {
        return _transformerRegistry;
    }
    
    @Override
    public ServiceReference getService(QName serviceName) {
        List<Service> services = _registry.getServices(serviceName);
        return services.isEmpty() ? null : services.get(0).getReference();
    }
}
