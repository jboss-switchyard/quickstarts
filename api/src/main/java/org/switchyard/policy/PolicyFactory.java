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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.switchyard.policy.Policy.PolicyType;

/**
 * A factory class for the policies.
 */
public final class PolicyFactory {

    private PolicyFactory() {}
    
    private static Set<Policy> _policies;

    static {
        _policies = new HashSet<Policy>();
        _policies.addAll(Arrays.asList(TransactionPolicy.values()));
        _policies.addAll(Arrays.asList(SecurityPolicy.values()));
    }
    
    /**
     * Returns policy object from name.
     * @param name policy name to get
     * @return Policy object
     * @throws Exception failed to create Policy object
     */
    public static Policy getPolicy(final String name) throws Exception {
        for (Policy p : _policies) {
            if (p.getName().equals(name)) {
                return p;
            }
        }

        // return Generic Policy instance for the non-built-in policy
        return new Policy() {
            public String getName() {
                return name;
            }
            public boolean supports(PolicyType type) {
                return true;
            }
            public boolean isCompatibleWith(Policy target) {
                return true;
            }
            public Policy getPolicyDependency() {
                return null;
            }
        };
    }
    
    /**
     * Returns a set of all available policies.
     * @return policies
     */
    public static Set<Policy> getAllAvailablePolicies() {
        return Collections.unmodifiableSet(_policies);
    }
    
    /**
     * Returns a set of available interaction policies.
     * @return policies
     */
    public static Set<Policy> getAvailableInteractionPolicies() {
        Set<Policy> interactions = new HashSet<Policy>();
        for (Policy p : _policies) {
            if (p.supports(PolicyType.INTERACTION)) {
                interactions.add(p);
            }
        }
        return Collections.unmodifiableSet(interactions);
    }
    
    /**
     * Returns a set of available implementation policies.
     * @return policies
     */
    public static Set<Policy> getAvailableImplementationPolicies() {
        Set<Policy> implementations = new HashSet<Policy>();
        for (Policy p : _policies) {
            if (p.supports(PolicyType.IMPLEMENTATION)) {
                implementations.add(p);
            }
        }
        return Collections.unmodifiableSet(implementations);
    }
}
