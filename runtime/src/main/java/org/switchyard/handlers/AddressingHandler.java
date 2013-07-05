/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.switchyard.handlers;

import java.util.List;

import org.switchyard.BaseHandler;
import org.switchyard.Exchange;
import org.switchyard.ExchangePhase;
import org.switchyard.HandlerException;
import org.switchyard.Service;
import org.switchyard.ServiceDomain;
import org.switchyard.SwitchYardException;
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
        for (Policy policy : service.getServiceMetadata().getRequiredPolicies()) {
            PolicyUtil.require(exchange, policy);
        }
    }

}
