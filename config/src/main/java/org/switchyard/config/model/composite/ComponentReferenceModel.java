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

import javax.xml.namespace.QName;

import org.switchyard.config.model.NamedModel;

/**
 * The "component/reference" configuration model.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public interface ComponentReferenceModel extends NamedModel {

    /** The "multiplicity" name. */
    public static final String MULTIPLICITY = CompositeReferenceModel.MULTIPLICITY;

    /** The "reference" name. */
    public static final String REFERENCE = CompositeReferenceModel.REFERENCE;

    /** The "security" name. */
    public static final String SECURITY = "security";

    /**
     * Gets the parent component model.
     * @return the parent component model
     */
    public ComponentModel getComponent();

    /**
     * Gets the multiplicity attribute.
     * @return the multiplicity attribute
     */
    public String getMultiplicity();

    /**
     * Sets the multiplicity attribute.
     * @param multiplicity the multiplicity attribute
     * @return this ComponentReferenceModel (useful for chaining)
     */
    public ComponentReferenceModel setMultiplicity(String multiplicity);

    /**
     * Gets the child component reference interface model.
     * @return the child component reference interface model
     */
    public InterfaceModel getInterface();

    /**
     * Sets the child component reference interface model.
     * @param interfaze the child component reference interface model
     * @return this ComponentReferenceModel (useful for chaining)
     */
    public ComponentReferenceModel setInterface(InterfaceModel interfaze);
    
    /**
     * Gets the name of the security section to respect.
     * @return the name of the security section to respect
     */
    public String getSecurity();
    
    /**
     * Sets the name of the security section to respect.
     * @param security the name of the security section to respect
     * @return this ComponentReferenceModel (useful for chaining)
     */
    public ComponentReferenceModel setSecurity(String security);
    
    /**
     * Adds the specified policy identifier to the set of required policy 
     * intents for the reference.
     * @param policyQName qualified name of the required policy
     */
    public void addPolicyRequirement(QName policyQName);
    
    /**
     * Returns the set of policy intents required for this reference.
     * @return set of policy intents required for the reference.
     */
    public Set<QName> getPolicyRequirements();
    
    /**
     * Indicates whether a given policy is required for this reference.
     * @param policyQName qualified name of the policy to check
     * @return true if the policy is required, false otherwise
     */
    public boolean hasPolicyRequirement(QName policyQName);

}
