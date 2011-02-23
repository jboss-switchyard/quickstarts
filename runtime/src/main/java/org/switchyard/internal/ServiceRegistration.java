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

import javax.xml.namespace.QName;

import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.ServiceReference;
import org.switchyard.ServiceDomain;
import org.switchyard.handlers.HandlerChain;
import org.switchyard.metadata.ExchangeContract;
import org.switchyard.metadata.ServiceInterface;
import org.switchyard.spi.Endpoint;
import org.switchyard.spi.ServiceRegistry;

/**
 * ServiceRegistration represents the registration of a service
 * within the registry.
 */
public class ServiceRegistration implements Service {

    private final ServiceRegistry _registry;
    private final ServiceDomain _domain;
    private final Endpoint _endpoint;
    private final QName _serviceName;
    private final HandlerChain _handlers;
    private final ServiceInterface _serviceInterface;

    ServiceRegistration(QName serviceName,
            ServiceInterface serviceInterface,
            Endpoint endpoint,
            HandlerChain handlers,
            ServiceRegistry registry,
            ServiceDomain domain) {

        _serviceName = serviceName;
        _serviceInterface = serviceInterface;
        _endpoint = endpoint;
        _handlers = handlers;
        _registry = registry;
        _domain = domain;
    }

    @Override
    public void unregister() {
        _registry.unregisterService(this);
    }

    @Override
     public Exchange createExchange(ExchangeContract contract) {
         return _domain.createExchange(this, contract);
     }

     @Override
     public Exchange createExchange(ExchangeContract contract, ExchangeHandler handler) {
         return _domain.createExchange(this, contract, handler);
     }

    /**
     * Get the endpoint .
     * @return endpoint
     */
    public Endpoint getEndpoint() {
        return _endpoint;
    }

    /**
     * Get the service domain.
     * @return domain
     */
    public ServiceDomain getDomain() {
        return _domain;
    }

    @Override
    public QName getName() {
        return _serviceName;
    }

    /**
     * Get the handlers.
     * @return handlers
     */
    public HandlerChain getHandlers() {
        return _handlers;
    }

    @Override
    public ServiceInterface getInterface() {
        return _serviceInterface;
    }
}
