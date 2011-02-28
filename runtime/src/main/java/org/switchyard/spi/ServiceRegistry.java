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

package org.switchyard.spi;

import java.util.List;

import javax.xml.namespace.QName;

import org.switchyard.Service;
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
    Service registerService(QName serviceName, ServiceInterface serviceInterface,
            Endpoint endpoint, HandlerChain handlers, ServiceDomain domain);
    /**
     * Unregister the service.
     * @param service service
     */
    void unregisterService(Service service);

    /**
     * Return the list of services.
     * @return services
     */
    List<Service> getServices();
    /**
     * Get the list of services for the specified service name.
     * @param serviceName service name
     * @return services
     */
    List<Service> getServices(QName serviceName);
    /**
     * Get the list of services for the specified domain name.
     * @param domainName domain name
     * @return services
     */
    List<Service> getServicesForDomain(String domainName);
}
