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
package org.switchyard.policy;

/**
 * Base contract for all policy types.  Every policy has a string literal 
 * representation which is used to identify the policy in application 
 * configuration (e.g. "suspendsTransaction").
 * 
 * @author Keith Babo &copy; 2011 Red Hat Inc.
 */
public interface Policy {

    /** policy type. */
    public enum PolicyType {INTERACTION, IMPLEMENTATION};
    
    /**
     * Returns the string identifier for the policy.
     * @return policy name
     */
    public String getName();

    /**
     * Returns the policy type.
     * @return policy type
     */
    public PolicyType getType();
    
    /**
     * Returns whether the policy passed in parameter is compatible with this or not.
     * @param target policy to check compatibility
     * @return true if compatible
     */
    public boolean isCompatibleWith(Policy target);
    
    /**
     * Returns a policy which is a dependency of this Policy.
     * @return a policy dependency
     */
    public Policy getPolicyDependency();
}
