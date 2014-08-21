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
     * Start the endpoint.  Multiple calls to start() should be safe and be considered a NOP by
     * implementations.
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
    
    /**
     * Set if remote transaction bridging should be disabled.
     * @param _disableRemoteTransaction true if it disables remote transaction
     * @return this RemoteEndpointPublisher instance (useful for method chaining)
     */
    RemoteEndpointPublisher setDisableRemoteTransaction(boolean _disableRemoteTransaction);
    
    /**
     * Get if remote transaction bridging should be disabled.
     * @return true if it disables remote transaction
     */
    boolean isDisableRemoteTransaction();
}
