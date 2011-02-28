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

import javax.xml.namespace.QName;

import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.Service;
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
