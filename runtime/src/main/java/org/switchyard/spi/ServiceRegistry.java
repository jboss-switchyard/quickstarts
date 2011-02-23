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

package org.switchyard.spi;

import java.util.List;

import javax.xml.namespace.QName;

import org.switchyard.ServiceReference;
import org.switchyard.ServiceDomain;
import org.switchyard.handlers.HandlerChain;
import org.switchyard.metadata.ServiceInterface;

/**
 * A runtime service registry can be queried
 * for services by service name, domain name, or
 * all the services can be returned.
 */
public interface ServiceRegistry {

    /**
     * Register a service.
     * @param serviceName service name
     * @param serviceInterface service interface
     * @param endpoint endpoint
     * @param handlers handlers
     * @param domain domain
     * @return Service
     */
    ServiceReference registerService(QName serviceName, ServiceInterface serviceInterface,
            Endpoint endpoint, HandlerChain handlers, ServiceDomain domain);
    /**
     * Unregister the service.
     * @param service service
     */
    void unregisterService(ServiceReference service);

    /**
     * Return the list of services.
     * @return services
     */
    List<ServiceReference> getServices();
    /**
     * Get the list of services for the specified service name.
     * @param serviceName service name
     * @return services
     */
    List<ServiceReference> getServices(QName serviceName);
    /**
     * Get the list of services for the specified domain name.
     * @param domainName domain name
     * @return services
     */
    List<ServiceReference> getServicesForDomain(String domainName);
}
