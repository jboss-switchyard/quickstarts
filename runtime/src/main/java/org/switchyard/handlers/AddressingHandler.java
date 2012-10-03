/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.handlers;

import java.util.List;

import org.switchyard.BaseHandler;
import org.switchyard.Exchange;
import org.switchyard.ExchangePhase;
import org.switchyard.HandlerException;
import org.switchyard.Service;
import org.switchyard.ServiceDomain;
import org.switchyard.exception.SwitchYardException;
import org.switchyard.metadata.ServiceOperation;
import org.switchyard.policy.Policy;
import org.switchyard.policy.PolicyUtil;

/**
 * The AddressingHandler resolves service instances based on a service reference.
 */
public class AddressingHandler extends BaseHandler {
    
    private ServiceDomain _domain;
    
    /**
     * Create a new AddressingHandler for the specified domain.
     * @param domain services available for routing
     */
    public AddressingHandler(ServiceDomain domain) {
        _domain = domain;
    }

    @Override
    public void handleMessage(Exchange exchange) throws HandlerException {
        // only set the provider on the 'IN' phase
        if (ExchangePhase.IN != exchange.getPhase()) {
            return;
        }
        
        // is a provider already set?
        if (exchange.getProvider() != null) {
            return;
        }
        
        List<Service> services = _domain.getServices(exchange.getConsumer().getTargetServiceName());
        if (services == null || services.isEmpty()) {
            throw new SwitchYardException("No registered service found for " + exchange.getConsumer().getName());
        }

        // At this stage, just pick the first service implementation we find and go with
        // it.  In the future, it would be nice if we could make this pluggable.
        Service service = services.get(0);
        ServiceOperation consumerOp = exchange.getContract().getConsumerOperation();
        ServiceOperation providerOp = service.getInterface().getOperation(consumerOp.getName());
        
        if (providerOp == null) {
            // try for a default operation
            if (service.getInterface().getOperations().size() == 1) {
                providerOp = service.getInterface().getOperations().iterator().next();
            } else {
                throw new HandlerException("Operation " + consumerOp.getName() 
                    + " is not included in interface for service: " + service.getName());
            }
        }
        
        // set provider contract and details on exchange
        exchange.provider(service, providerOp);
        for (Policy policy : service.getRequiredPolicies()) {
            PolicyUtil.require(exchange, policy);
        }
    }

}
