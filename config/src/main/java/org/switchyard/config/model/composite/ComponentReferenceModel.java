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

package org.switchyard.config.model.composite;

import java.util.Set;

import org.switchyard.config.model.NamedModel;

/**
 * The "component/reference" configuration model.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public interface ComponentReferenceModel extends NamedModel {

    /** The "reference" name. */
    public static final String REFERENCE = "reference";

    /** The "multiplicity" name. */
    public static final String MULTIPLICITY = "multiplicity";

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
    public ComponentReferenceInterfaceModel getInterface();

    /**
     * Sets the child component reference interface model.
     * @param interfaze the child component reference interface model
     * @return this ComponentReferenceModel (useful for chaining)
     */
    public ComponentReferenceModel setInterface(ComponentReferenceInterfaceModel interfaze);
    

    /**
     * Adds the specified policy identifier to the set of required policy 
     * intents for the reference.
     * @param policyName name of the required policy
     */
    public void addPolicyRequirement(String policyName);
    
    /**
     * Returns the set of policy intents required for this reference.
     * @return set of policy intents required for the reference.
     */
    public Set<String> getPolicyRequirements();
    
    /**
     * Indicates whether a given policy is required for this reference.
     * @param policyName name of the policy to check
     * @return true if the policy is required, false otherwise
     */
    public boolean hasPolicyRequirement(String policyName);

}
