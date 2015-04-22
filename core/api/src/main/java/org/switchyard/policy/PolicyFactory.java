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

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

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
     * @deprecated use {@link #getPolicy(QName)} instead
     */
    @Deprecated
    public static Policy getPolicy(String name) throws Exception {
        return getPolicy(new QName(name));
    }

    /**
     * Returns policy object from qualified name.
     * @param qname policy qualified name to get
     * @return Policy object
     * @throws Exception failed to create Policy object
     */
    public static Policy getPolicy(final QName qname) throws Exception {
        for (Policy p : _policies) {
            // 1. look for an exact match first
            if (p.getQName().equals(qname)) {
                return p;
            }
            // 2. look for a localName match second for backwards compatibility
            // NOTE: similar logic found in:
            // -    core/api: org.switchyard.policy.PolicyUtil.containsPolicy(Set<Policy>, Policy):boolean
            // - core/config: org.switchyard.config.model.composite.v1.PolicyConfig.hasRequirement(Model, QName):boolean
            // NOTE: NULL_NS_URI works because of no default namespace assumption from parent element made in:
            // - core/config: org.switchyard.config.BaseConfiguration.createAttributeQName(String value):QName
            if (XMLConstants.NULL_NS_URI.equals(qname.getNamespaceURI()) && qname.getLocalPart().equals(p.getQName().getLocalPart())) {
                return p;
            }
        }

        // return Generic Policy instance for the non-built-in policy
        return new Policy() {
            public QName getQName() {
                return qname;
            }
            public String getName() {
                return getQName().getLocalPart();
            }
            public String toString() {
                return getQName().toString();
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
