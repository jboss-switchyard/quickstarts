/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2010-2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more 
 * details. You should have received a copy of the GNU Lesser General Public 
 * License, v.2.1 along with this distribution; if not, write to the Free 
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  
 * 02110-1301, USA.
 */

package org.switchyard.internal;

import java.util.Set;

import javax.xml.namespace.QName;

import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.ServiceDomain;
import org.switchyard.ServiceMetadata;
import org.switchyard.ServiceReference;
import org.switchyard.SwitchYardException;
import org.switchyard.event.ReferenceUnregistrationEvent;
import org.switchyard.metadata.ServiceInterface;
import org.switchyard.metadata.ServiceMetadataBuilder;
import org.switchyard.metadata.ServiceOperation;
import org.switchyard.policy.Policy;
import org.switchyard.policy.PolicyUtil;
import org.switchyard.spi.Dispatcher;

/**
 * A reference to a service registered in a SwitchYard domain.  The reference
 * is a logical representation of a service endpoint, which can be mapped to
 * multiple service instances compatible with the service reference metadata.
 */
public class ServiceReferenceImpl implements ServiceReference {

    private QName _name;
    private ServiceInterface _interface;
    private DomainImpl _domain;
    private ExchangeHandler _handler;
    private Dispatcher _dispatcher;
    private QName _targetServiceName;
    private ServiceMetadata _metadata;
    
    /**
     * Creates a new reference to a service.
     * @param name name of the service reference
     * @param serviceInterface the service interface
     * @param domain domain in which the service is used 
     * @param metadata service metadata
     */
    public ServiceReferenceImpl(QName name,
            ServiceInterface serviceInterface,
            DomainImpl domain,
            ServiceMetadata metadata) {
        this(name, serviceInterface, domain, null, metadata);
    }

    /**
     * Creates a new reference to a service.
     * @param name name of the service reference
     * @param serviceInterface the service interface
     * @param domain domain in which the service is used
     * @param handler handler used to process reply faults/messages
     * @param metadata service metadata
     */
    public ServiceReferenceImpl(QName name,
            ServiceInterface serviceInterface,
            DomainImpl domain,
            ExchangeHandler handler,
            ServiceMetadata metadata) {
        
        _name = name;
        _interface = serviceInterface;
        _handler = handler;
        _domain = domain;
        _targetServiceName = name;
        _metadata = metadata != null ? metadata : ServiceMetadataBuilder.create().build();
    }
    
    @Override
    public Exchange createExchange() {
        return createExchange(_handler);
    }
    @Override
    public Exchange createExchange(ExchangeHandler handler) {
        Set<ServiceOperation> operations = _interface.getOperations();
        if (operations.size() == 0) {
            throw new SwitchYardException(
                    "No operations in interface for service: " + _name);
        } else if (operations.size() > 1) {
            throw new SwitchYardException("Operation name required - "
                    + "multiple operations on service interface: " + _name);
        }

        return createExchange(operations.iterator().next().getName(), handler);
    }
    
    @Override
    public Exchange createExchange(String operation) {
        return createExchange(operation, _handler);
    }

    @Override
    public Exchange createExchange(String operation, ExchangeHandler handler) {
        ServiceOperation op = _interface.getOperation(operation);
        if (op == null) {
            // try for a default operation
            if (ServiceInterface.DEFAULT_TYPE.equals(_interface.getType())) {
                op = _interface.getOperations().iterator().next();
            } else {
                throw new SwitchYardException("Operation " + operation + " does not exist for service " + _name);
            }
        }

        Exchange ex = _dispatcher.createExchange(handler, op.getExchangePattern());
        ex.consumer(this, op);

        for (Policy policy : _metadata.getRequiredPolicies()) {
            PolicyUtil.require(ex, policy);
        }
        for (Policy policy : _metadata.getProvidedPolicies()) {
            PolicyUtil.provide(ex, policy);
        }
        return ex;
    }

    @Override
    public ServiceInterface getInterface() {
        return _interface;
    }

    @Override
    public QName getName() {
        return _name;
    }
    
    @Override
    public void unregister() {
        _domain.getServiceRegistry().unregisterServiceReference(this);
        _domain.getEventPublisher().publish(new ReferenceUnregistrationEvent(this));
    }

    /**
     * Specifies the exchange handler to use to process reply messages and faults.
     * @param handler exchange handler
     * @return this ServiceReference instance
     */
    public ServiceReferenceImpl setHandler(ExchangeHandler handler) {
        _handler = handler;
        return this;
    }
    
    /**
     * The domain in which this service reference is registered.
     * @return service domain which created this service reference
     */
    @Override
    public ServiceDomain getDomain() {
        return _domain;
    }
    
    /*
    @Override
    public ServiceSecurity getSecurity() {
        return _domain != null ? _domain.getServiceSecurity(_securityName) : null;
    }
    */
    
    /**
     * Specifies the exchange bus dispatcher to use for this reference.
     * @param dispatcher the exchange dispatcher
     * @return this ServiceReference instance
     */
    public ServiceReferenceImpl setDispatcher(Dispatcher dispatcher) {
        _dispatcher = dispatcher;
        return this;
    }

    @Override
    public void wire(QName serviceName) {
        _targetServiceName = serviceName;
    }
    
    @Override
    public QName getTargetServiceName() {
        return _targetServiceName;
    }
    

    @Override
    public ServiceMetadata getServiceMetadata() {
        return _metadata;
    }
    
    @Override
    public String toString() {
        return "ServiceReference [name=" + _name + ", interface=" + _interface + ", domain=" + _domain + "]";
    }

}
