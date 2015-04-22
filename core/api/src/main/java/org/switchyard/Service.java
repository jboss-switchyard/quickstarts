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

package org.switchyard;

import javax.xml.namespace.QName;

import org.switchyard.metadata.ServiceInterface;

/**
 * A service registered with the SwitchYard runtime.
 */
public interface Service {
    
    /**
     * Qualified name of the service.
     * @return service name
     */
    QName getName();
    
    /**
     * Interface metadata for the registered service.
     * @return the service interface
     */
    ServiceInterface getInterface();
    
    /**
     * Unregisters this service from the domain it's registered in.
     */
    void unregister();
    
    /**
     * The domain in which this service is registered.
     * @return service domain which created this service
     */
    ServiceDomain getDomain();
    
    /**
     * Return the exchange handler representing the provider.
     * @return provider's exchange handler
     */
    ExchangeHandler getProviderHandler();
    
    /**
     * Return runtime metadata associated with this service.
     * @return runtime service metadata
     */
    ServiceMetadata getServiceMetadata();
}
