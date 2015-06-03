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
package org.switchyard.admin.base;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.switchyard.Exchange;
import org.switchyard.admin.Application;
import org.switchyard.admin.ComponentService;
import org.switchyard.admin.ServiceOperation;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.ComponentServiceModel;
import org.switchyard.config.model.composite.InterfaceModel;

/**
 * BaseComponentService
 * 
 * Base implementation for {@link ComponentService}.
 * 
 * @author Rob Cernich
 */
public abstract class ComponentServiceWithOperations extends BaseComponentService implements ComponentService {

    private List<ServiceOperation> _operations;

    /**
     * Create a new BaseComponentService from a config model.
     * 
     * @param serviceConfig the component service configuration
     * @param componentConfig the component configuration
     * @param application the switchyard application
     */
    public ComponentServiceWithOperations(ComponentServiceModel serviceConfig, ComponentModel componentConfig, Application application) {
        super(serviceConfig, componentConfig, application);

        _operations = new LinkedList<ServiceOperation>();
        for (org.switchyard.metadata.ServiceOperation so : getInterfaceOperations(serviceConfig.getInterface())) {
            _operations.add(new BaseServiceOperation(so.getName(), so.getExchangePattern().name(), so.getInputType(), so.getOutputType(), so.getFaultType()));
        }
    }

    @Override
    public List<ServiceOperation> getServiceOperations() {
        return Collections.unmodifiableList(_operations);
    }

    @Override
    public ServiceOperation getServiceOperation(String operation) {
        for (ServiceOperation serviceOperation : _operations) {
            if (serviceOperation.getName().equals(operation)) {
                return serviceOperation;
            }
        }
        return null;
    }

    /**
     * Template method for children, should return list of operations supported
     * by interface model.
     * 
     * @param interfaceModel Interface definition for this service.
     * @return Supported operations.
     */
    protected abstract Collection<org.switchyard.metadata.ServiceOperation> getInterfaceOperations(InterfaceModel interfaceModel);

    @Override
    public void resetMessageMetrics() {
        for (final ServiceOperation operation : _operations) {
            operation.resetMessageMetrics();
        }
        super.resetMessageMetrics();
    }

    @Override
    public void recordMetrics(Exchange exchange) {
        final String operationName = exchange.getContract().getProviderOperation().getName();
        if (operationName != null) {
            for (final ServiceOperation operation : _operations) {
                if (operationName.equals(operation.getName())) {
                    operation.recordMetrics(exchange);
                    break;
                }
            }
        }
        super.recordMetrics(exchange);
    }

}
