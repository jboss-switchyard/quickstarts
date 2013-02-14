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
 
package org.switchyard.component.sca;

import javax.xml.namespace.QName;

import org.switchyard.ServiceDomain;

/**
 * Interface for allowing SwitchYard to publish an HTTP endpoint for remote service invocations.
 * Based on HTTP gateway endpoint contract. A single HTTP endpoint is used for all remote service
 * bindings in an instance, so the endpoint has internal dispatch logic to get a given request
 * to the correct service.
 */
public interface RemoteEndpointPublisher {

    /**
     * Publish a remote HTTP endpoint.
     * @param context The web context root where the resource need to be published
     */
    void init(String context);
    
    /**
     * Start the endpoint.
     * @throws Exception failed to start
     */
    void start() throws Exception;
    
    /**
     * Stop the endpoint.
     * @throws Exception failed to stop
     */
    void stop() throws Exception;
    
    /**
     * Register a service with the endpoint.  This allows the endpoint to dispatch inbound
     * requests to a registered service.
     * @param serviceName the name of the service
     * @param domain the domain in which it's registered
     */
    void addService(QName serviceName, ServiceDomain domain);
    
    /**
     * Remove a service endpoint.
     * @param serviceName the service name
     * @param domain the domain in which it's registered
     */
    void removeService(QName serviceName, ServiceDomain domain);
    
    /**
     * Get the service domain for a given registered service.
     * @param serviceName name of the service
     * @return service domain 
     */
    ServiceDomain getDomain(QName serviceName);
    
    /**
     * The endpoint URL for this remote endpoint.
     * @return endpoint URL as a string
     */
    String getAddress();
}
