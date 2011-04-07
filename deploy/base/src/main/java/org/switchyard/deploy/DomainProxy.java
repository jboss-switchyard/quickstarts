/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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

package org.switchyard.deploy;

import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.ServiceDomain;
import org.switchyard.ServiceReference;
import org.switchyard.metadata.ExchangeContract;
import org.switchyard.metadata.ServiceInterface;
import org.switchyard.transform.TransformerRegistry;

import javax.xml.namespace.QName;

/**
 * Domain Proxy class.
 * <p/>
 * This class ensures that Services are searched for across all Service Domains (1 domain/app),
 * while Transformer lookup only happens on within the application's ServiceDomain i.e. it
 * ensures that Transformers are scoped around the application.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
class DomainProxy implements ServiceDomain {
    
    private ServiceDomain _domain;
    private ServiceDomainManager _domainManager;

    public DomainProxy(ServiceDomain domain, ServiceDomainManager domainManager) {
        this._domain = domain;
        this._domainManager = domainManager;
    }

    @Override
    public QName getName() {
        return _domain.getName();
    }

    /**
     * Get the proxied ServiceDomain.
     * @return The proxied service domain.
     */
    public ServiceDomain getDomain() {
        return _domain;
    }

    @Override
    public ServiceReference getService(QName serviceName) {
        ServiceReference service = _domain.getService(serviceName);
        if (service == null) {
            // Look for the service across the other domains, excluding the domain
            // associated with this proxy...
            service = _domainManager.findService(serviceName, _domain);
        }
        return service;
    }

    @Override
    public Exchange createExchange(ServiceReference service, ExchangeContract contract) {
        return _domain.createExchange(service, contract);
    }

    @Override
    public Exchange createExchange(ServiceReference service, ExchangeContract contract, ExchangeHandler handler) {
        return _domain.createExchange(service, contract, handler);
    }

    @Override
    public ServiceReference registerService(QName serviceName, ExchangeHandler handler) {
        return _domain.registerService(serviceName, handler);
    }

    @Override
    public ServiceReference registerService(QName serviceName, ExchangeHandler handler, ServiceInterface metadata) {
        return _domain.registerService(serviceName, handler, metadata);
    }

    @Override
    public TransformerRegistry getTransformerRegistry() {
        return _domain.getTransformerRegistry();
    }
}
