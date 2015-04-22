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

import java.util.List;

import org.switchyard.ServiceDomain;
import org.switchyard.config.Configuration;

/**
 * Components allow components to participate in the deployment and configuration lifecycle of
 * a SwitchYard runtime. Components are initialized during boot strap process at system startup.
 * There is exactly one instance of a particular SwitchYard Component per SwitchYard runtime.
 */
public interface Component {
    /**
     * Creates and configures an Activator associated with this Component.
     * Component instances should attempt to use the domain passed
     * to configure the Activator associated with them.
     * @param domain The SwitchYard Service Domain.
     * @return An Activator instance that will be used by deployments.
     */
    Activator createActivator(ServiceDomain domain);
    
    /**
     * Get the activator types that this Component can create.
     * @return List<String> The activation types that this Component supports.
     */
    List<String> getActivationTypes();
    
    /**
     * Returns the name of the Component instance.
     * @return The name.
     */
    String getName();
    /**
     * Initialize a component based on the supplied environment/global
     * configuration. Component instances should attempt to use the 
     * configuration passed when necessary to configure the Activator
     * associated with them.
     * @param config switchyard environment/global configuration for the component
     */
    void init(Configuration config);
    /**
     * Destroy the specified Component. Component implementations should
     * use this lifecyle method to clean up any resources used by Activators.
     */
    void destroy();
    
    /**
     * @return the configuration used by this component.
     * @see #init(Configuration)
     */
    Configuration getConfig();
    
    /**
     * Inject a resource dependency on Component.
     * 
     * TODO This API should be changed to more generic& configurable way
     * https://issues.jboss.org/browse/SWITCHYARD-833
     * 
     * @param value resource instance
     */
    void addResourceDependency(Object value);
}
