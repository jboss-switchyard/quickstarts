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
package org.switchyard.admin.base;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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

}
