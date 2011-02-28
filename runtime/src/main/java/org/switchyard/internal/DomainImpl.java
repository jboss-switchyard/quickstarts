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
import org.switchyard.ExchangePhase;
import org.switchyard.Service;
import org.switchyard.ServiceDomain;
import org.switchyard.handlers.HandlerChain;
import org.switchyard.handlers.TransformHandler;
import org.switchyard.metadata.ExchangeContract;
import org.switchyard.metadata.InOutService;
import org.switchyard.metadata.ServiceInterface;
import org.switchyard.spi.Endpoint;
import org.switchyard.spi.EndpointProvider;
import org.switchyard.spi.ServiceRegistry;
import org.switchyard.transform.TransformerRegistry;

/**
 * Implementation of ServiceDomain.
 */
public class DomainImpl implements ServiceDomain {

    private final QName _name;
    private final DefaultHandlerChain _defaultHandlers;
    private final ServiceRegistry _registry;
    private final EndpointProvider _endpointProvider;
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
            EndpointProvider endpointProvider,
            TransformerRegistry transformerRegistry) {

        _name = name;
        _registry = registry;
        _endpointProvider  = endpointProvider;
        _transformerRegistry = transformerRegistry;

        // Build out the system handlers chain.  It would be cleaner if we
        // handled this via config.
        _defaultHandlers = new DefaultHandlerChain();
        _defaultHandlers.addFirst("transformation", new TransformHandler(_transformerRegistry));
    }

    @Override
    public Exchange createExchange(Service service, ExchangeContract contract) {
        return createExchange(service, contract, null);
    }



    @Override
    public Exchange createExchange(
            Service service, ExchangeContract contract, ExchangeHandler handler) {
        // Determine the endpoints used for exchange delivery
        Endpoint inputEndpoint = _endpointProvider.getEndpoint(
                getEndpointName(service.getName(), ExchangePhase.IN));
        Endpoint outputEndpoint = null;

        if (handler != null) {
            HandlerChain replyChain = _defaultHandlers.clone();
            replyChain.addLast("replyHandler", handler);
            outputEndpoint = _endpointProvider.createEndpoint(
                    getEndpointName(service.getName(), ExchangePhase.OUT), replyChain);
        }

        // create the exchange
        ExchangeImpl exchange = new ExchangeImpl(service, contract, inputEndpoint, outputEndpoint);
        return exchange;
    }

    @Override
    public Service registerService(QName serviceName, ExchangeHandler handler) {
        return registerService(serviceName, handler, null);
    }

    @Override
    public Service registerService(QName serviceName, ExchangeHandler handler,
            ServiceInterface metadata) {
        HandlerChain handlers = _defaultHandlers.clone();
        handlers.addLast("provider", handler);
        Endpoint ep = _endpointProvider.createEndpoint(
                getEndpointName(serviceName, ExchangePhase.IN), handlers);
        // If no service interface is provided, we default to InOutService
        if (metadata != null) {
            return _registry.registerService(serviceName, metadata, ep, handlers, this);
        } else {
            return _registry.registerService(serviceName, new InOutService(), ep, handlers, this);
        }
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
    public Service getService(QName serviceName) {
        List<Service> services = _registry.getServices(serviceName);
        return services.isEmpty() ? null : services.get(0);
    }
    
    /**
     * Returns an endpoint name based on the domain name, service name, and 
     * exchange phase.
     */
    private String getEndpointName(QName serviceName, ExchangePhase phase) {
        return _name + ":" + serviceName + ":" + phase.toString();
    }
}
