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

import java.util.HashSet;
import java.util.Set;

import org.switchyard.Exchange;
import org.switchyard.Property;

/**
 * Used to set policy details on a message exchange.  Provided policy represents
 * an assertion that a given policy is satisfied.  Required policy represents a
 * requirement that a specific policy must be provided.  The set of required and
 * provided policies is carried as an exchange-scoped context property.
 */
public final class PolicyUtil {

    private static String PROVIDED_PROPERTY = "org.switchyard.policy.provided";
    private static String REQUIRED_PROPERTY = "org.switchyard.policy.required";
    
    private PolicyUtil() {
        
    }
    
    /**
     * Indicate that a given policy is satisfied for the exchange.
     * @param exchange assert policy on this exchange
     * @param policy the policy to assert
     */
    public static void provide(Exchange exchange, Policy policy) {
        Set<Policy> provided = getPolicies(exchange, PROVIDED_PROPERTY);
        provided.add(policy);
        exchange.getContext().setProperty(PROVIDED_PROPERTY, provided);
    }
    
    /**
     * Returns the set of policies provided for this exchange.
     * @param exchange check policy on this exchange
     * @return set of policies provided; empty set if no policies are provided
     */
    public static Set<Policy> getProvided(Exchange exchange) {
        return getPolicies(exchange, PROVIDED_PROPERTY);
    }
    
    /**
     * Indicates whether a given policy is provided on the exchange.
     * @param exchange check policy on this exchange
     * @param policy the policy to check
     * @return true if the policy is provided, false otherwise
     */
    public static boolean isProvided(Exchange exchange, Policy policy) {
        return containsPolicy(getProvided(exchange), policy);
    }
    
    /**
     * Indicate that a given policy is required for the exchange.
     * @param exchange require policy on this exchange
     * @param policy the policy to require
     */
    public static void require(Exchange exchange, Policy policy) {
        Set<Policy> required = getPolicies(exchange, REQUIRED_PROPERTY);
        required.add(policy);
        exchange.getContext().setProperty(REQUIRED_PROPERTY, required);
    }
    
    /**
     * Returns the set of policies required for this exchange.
     * @param exchange check policy on this exchange
     * @return set of policies required; empty set if no policies are required
     */
    public static Set<Policy> getRequired(Exchange exchange) {
        return getPolicies(exchange, REQUIRED_PROPERTY);
    }
    
    /**
     * Indicates whether a given policy is required on the exchange.
     * @param exchange check policy on this exchange
     * @param policy the policy to check
     * @return true if the policy is required, false otherwise
     */
    public static boolean isRequired(Exchange exchange, Policy policy) {
        return containsPolicy(getRequired(exchange), policy);
    }
    
    @SuppressWarnings("unchecked")
    private static Set<Policy> getPolicies(Exchange exchange, String propertyName) {
        Property intentsProperty = exchange.getContext().getProperty(propertyName);
        Set<Policy> intents = new HashSet<Policy>();
        if (intentsProperty != null) {
            intents.addAll((Set<Policy>)intentsProperty.getValue());
        }
        
        return intents;
    }
    
    // Compares policies by name vs. object identity/hashCode
    private static boolean containsPolicy(Set<Policy> list, Policy target) {
        boolean contains = false;
        for (Policy p : list) {
            if (p.getName().equals(target.getName())) {
                contains = true;
                break;
            }
        }
        
        return contains;
    }
}
