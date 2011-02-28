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

import org.switchyard.ServiceDomain;
import org.switchyard.ServiceReference;
import org.switchyard.spi.Dispatcher;
import org.switchyard.spi.Service;
import org.switchyard.spi.ServiceRegistry;

/**
 * ServiceRegistration represents the registration of a service
 * within the registry.
 */
public class ServiceRegistration implements Service {

    private final Dispatcher _endpoint;
    private final ServiceRegistry _registry;
    private final ServiceDomain _domain;
    private final ServiceReference _reference;

    ServiceRegistration(ServiceReference reference,
            Dispatcher endpoint,
            ServiceRegistry registry,
            ServiceDomain domain) {

        _endpoint = endpoint;
        _reference = reference;
        _registry = registry;
        _domain = domain;
    }
    
    @Override
    public ServiceReference getReference() {
        return _reference;
    }

    @Override
    public void unregister() {
        _registry.unregisterService(this);
    }

    /**
     * Get the endpoint .
     * @return endpoint
     */
    @Override
    public Dispatcher getEndpoint() {
        return _endpoint;
    }

    /**
     * Get the service domain.
     * @return domain
     */
    @Override
    public ServiceDomain getDomain() {
        return _domain;
    }

}
