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
