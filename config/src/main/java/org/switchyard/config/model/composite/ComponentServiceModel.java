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

package org.switchyard.config.model.composite;

import java.util.Set;

import org.switchyard.config.model.NamedModel;

/**
 * The "component/service" model.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public interface ComponentServiceModel extends NamedModel {

    /** The "service"  name. */
    public static final String SERVICE = CompositeServiceModel.SERVICE;

    /** The "security" name. */
    public static final String SECURITY = "security";

    /**
     * Gets the parent component model.
     * @return the parent component model
     */
    public ComponentModel getComponent();

    /**
     * Gets the child component service interface model.
     * @return the child component service interface model
     */
    public InterfaceModel getInterface();

    /**
     * Sets the child component service interface model.
     * @param interfaze child component service interface model
     * @return this ComponentServiceModel (useful for chaining)
     */
    public ComponentServiceModel setInterface(InterfaceModel interfaze);
    
    /**
     * Gets the name of the security section to respect.
     * @return the name of the security section to respect
     */
    public String getSecurity();
    
    /**
     * Sets the name of the security section to respect.
     * @param security the name of the security section to respect
     * @return this ComponentServiceModel (useful for chaining)
     */
    public ComponentServiceModel setSecurity(String security);
    
    /**
     * Adds the specified policy identifier to the set of required policy 
     * intents for the service.
     * @param policyName name of the required policy
     */
    public void addPolicyRequirement(String policyName);
    
    /**
     * Returns the set of policy intents required for this service.
     * @return set of policy intents required for the service.
     */
    public Set<String> getPolicyRequirements();
    
    /**
     * Indicates whether a given policy is required for this service.
     * @param policyName name of the policy to check
     * @return true if the policy is required, false otherwise
     */
    public boolean hasPolicyRequirement(String policyName);

}
