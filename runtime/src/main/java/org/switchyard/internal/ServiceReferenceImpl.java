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

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.ServiceDomain;
import org.switchyard.ServiceReference;
import org.switchyard.exception.SwitchYardException;
import org.switchyard.metadata.ServiceInterface;
import org.switchyard.metadata.ServiceOperation;
import org.switchyard.policy.ExchangePolicy;
import org.switchyard.policy.Policy;

/**
 * A reference to a service registered in a SwitchYard domain.  The reference
 * is a logical representation of a service endpoint, which can be mapped to
 * multiple service instances compatible with the service reference metadata.
 */
public class ServiceReferenceImpl implements ServiceReference {

    private QName _name;
    private ServiceInterface _interface;
    private DomainImpl _domain;
    private List<Policy> _provides;
    private ExchangeHandler _handler;

    /**
     * Creates a new reference to a service.
     * @param name name of the service reference
     * @param serviceInterface the service interface
     * @param provides list of policies provided by this reference
     * @param handler handler used to process reply faults/messages
     * @param domain domain in which the service is used 
     */
    public ServiceReferenceImpl(QName name, 
            ServiceInterface serviceInterface, 
            List<Policy> provides,
            ExchangeHandler handler,
            DomainImpl domain) {
        
        _name = name;
        _interface = serviceInterface;
        _handler = handler;
        _domain = domain;
        
        if (provides != null) {
            _provides = provides;
        } else {
            _provides = Collections.emptyList();
        }
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
        if (_interface.getOperation(operation) == null) {
            throw new SwitchYardException("Invalid operation " + operation 
                    + " for service " + _name);
        } 
        
        Exchange ex = _domain.createExchange(this, operation, handler);
        for (Policy policy : _provides) {
            ExchangePolicy.provide(ex, policy);
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
    public List<Policy> getProvidedPolicy() {
        return Collections.unmodifiableList(_provides);
    }
    
    @Override
    public void unregister() {
        _domain.unregisterServiceReference(this);
    }

    
    /**
     * The domain in which this service reference is registered.
     * @return service domain which created this service reference
     */
    public ServiceDomain getDomain() {
        return _domain;
    }

}
