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

package org.switchyard.deploy;

import java.util.Collection;

import javax.xml.namespace.QName;

import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.config.model.composite.ComponentModel;

/**
 * Activators allow components to participate in the deployment lifecycle of
 * a SwitchYard application.  The deployer has a list of activators that map
 * to implementation and binding types.  During deployment, the deployer will 
 * build a list of activators for known types and enlist each activator in the
 * lifecycle of the application.  The deployer takes care of dependency ordering
 * between services, references, and bindings and invokes Activator instances
 * in the appropriate order.
 */
public interface Activator {
    
    /**
     * Activate a reference or service binding.  This is equivalent to a 
     * create/init for the binding and it's configuration.  Validation errors
     * with config should be reported from this method.
     * @param name name of the service or reference
     * @param config binding configuration
     * @return a lifecycle-aware service handler used to process exchanges
     */
    ServiceHandler activateBinding(QName name, BindingModel config);
    /**
     * Activate a service implementation.  This is equivalent to a 
     * create/init for the service implementation and it's configuration.  
     * Validation errors with config should be reported from this method.  Note
     * that if a service component declares multiple services for a single 
     * implementation, this method will be called once for each service on 
     * that implementation.
     * @param name name of the service
     * @param config component configuration
     * @return a lifecycle-aware service handler used to process exchanges
     */
    ServiceHandler activateService(QName name, ComponentModel config);

    /**
     * Deactivate a binding.  This is equivalent to remove/destroy for the 
     * binding.  The handler that was returned from activateBinding() is 
     * passed as a parameter in case it's needed for callback purposes.
     * @param name name of the service or reference binding
     * @param handler the handler returned from activateBinding
     */
    void deactivateBinding(QName name, ServiceHandler handler);
    
    /**
     * Deactivate a service.  This is equivalent to remove/destroy for the 
     * service.  The handler that was returned from activateService() is 
     * passed as a parameter in case it's needed for callback purposes.  Note
     * that if a service component declares multiple services for a single 
     * implementation, this method will be called once for each service on 
     * that implementation.
     * @param name name of the service
     * @param handler the handler returned from activateService
     */
    void deactivateService(QName name, ServiceHandler handler);
    
    /**
     * Whether the activator can handle a given .
     * @param type activation type
     * @return true If this Activator can activate the passed-in type.
     */
    boolean canActivate(String type);
    /**
     * Get the types that this Activator can activate.
     * @return Collection<String> The activation types that this Activator supports.
     */
    Collection<String> getActivationTypes();
    
    /**
     * Destroys an Activator instance. Called during undeploy and allows the 
     * activator to perform cleanup of any lingering resources.
     */
    void destroy();
}
