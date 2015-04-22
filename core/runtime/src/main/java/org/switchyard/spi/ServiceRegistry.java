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

package org.switchyard.spi;

import java.util.List;

import javax.xml.namespace.QName;

import org.switchyard.Service;
import org.switchyard.ServiceReference;

/**
 * A runtime service registry can be queried for services and references.
 */
public interface ServiceRegistry {

    /**
     * Register a service.
     * @param service service 
     * @return registered service
     */
    Service registerService(Service service);
    /**
     * Unregister the service.
     * @param service service
     */
    void unregisterService(Service service);
    
    /**
     * Register the service reference.
     * @param reference service reference
     */
    void registerServiceReference(ServiceReference reference);
    
    /**
     * Unregister the service reference.
     * @param reference service reference
     */
    void unregisterServiceReference(ServiceReference reference);

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
     * Return the list of service references.
     * @return references
     */
    List<ServiceReference> getServiceReferences();
    
    /**
     * Get a ServiceReference instance for the specified service name.
     * @param serviceName service name
     * @return reference to the specified service or null if a service reference is not registered.
     */
    ServiceReference getServiceReference(QName serviceName);
}
