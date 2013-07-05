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
     * Returns whether the policy type passed in parameter is supported by this policy.
     * @param type policy type
     * @return policy type
     */
    public boolean supports(PolicyType type);
    
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
