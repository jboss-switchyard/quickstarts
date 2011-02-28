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

package org.switchyard.internal;

import java.util.List;

import javax.xml.namespace.QName;

import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.ExchangePhase;
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
     * @param endpointProvider endpointProvider
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
        // Determine the endpoints used for exchange delivery
        Dispatcher inputDispatcher = _exchangeBus.getDispatcher(service);
        Dispatcher outputDispatcher = null;

        if (handler != null) {
            HandlerChain replyChain = _defaultHandlers.clone();
            replyChain.addLast("replyHandler", handler);
            outputDispatcher = _exchangeBus.createDispatcher(null, replyChain);
        }

        // create the exchange
        ExchangeImpl exchange = new ExchangeImpl(service, contract, inputDispatcher, outputDispatcher);
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
        HandlerChain handlers = _defaultHandlers.clone();
        handlers.addLast("provider", handler);
        Dispatcher ep = _exchangeBus.createDispatcher(reference, handlers);
        
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
    
    /**
     * Returns an endpoint name based on the domain name, service name, and 
     * exchange phase.
     */
    private String getEndpointName(QName serviceName, ExchangePhase phase) {
        return _name + ":" + serviceName + ":" + phase.toString();
    }
}
