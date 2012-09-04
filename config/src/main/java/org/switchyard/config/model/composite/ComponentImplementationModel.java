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

import org.switchyard.config.model.TypedModel;

/**
 * The "component/implementation" configuration model.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public interface ComponentImplementationModel extends TypedModel {

    /** The "implementation" name. */
    public static final String IMPLEMENTATION = "implementation";

    /**
     * Gets the parent component model.
     * @return the parent component model
     */
    public ComponentModel getComponent();

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
