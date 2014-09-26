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

package org.switchyard.config.model.composite.v1;

import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.Model;

/**
 * Provides methods to help with Policy configuration on SCA config models.
 */
public final class PolicyConfig {

    /** The "requires" name. */
    public static final String REQUIRES = "requires";

    private PolicyConfig() {}

    /**
     * Get the content of the requires attribute as a set of strings.
     * @param model the model to query for required policy
     * @return set of policy requirements; empty set if nothing found
     */
    public static Set<QName> getRequirements(Model model) {
        return model.getModelConfiguration().getAttributeAsQNames(REQUIRES, " ");
    }

    /**
     * Set the content of the requires attribute for a given config model.
     * @param model the model to set policy requirements on.
     * @param requirements the set of policy requirements.
     */
    public static void setRequirements(Model model, Set<QName> requirements) {
        Configuration config = model.getModelConfiguration();
        if (requirements == null || requirements.isEmpty()) {
            config.setAttribute(REQUIRES, null);
            return;
        }
        StringBuilder requires = new StringBuilder();
        for (QName req : requirements) {
            requires.append(" ");
            String ns = req.getNamespaceURI();
            if (XMLConstants.DEFAULT_NS_PREFIX.equals(ns)) {
                requires.append(req.getLocalPart());
            } else {
                String pfx = config.lookupPrefix(ns);
                if (pfx != null) {
                    requires.append(pfx + ":" + req.getLocalPart());
                } else {
                    // SCA cvc-datatype-valid.1.2.1 XSD doesn't allow for {namespaceURI}localPart format
                    //requires.append(req.toString());
                    requires.append(req.getLocalPart());
                }
            }
        }
        config.setAttribute(REQUIRES, requires.toString().trim());
    }

    /**
     * Adds a policy requirement to the existing list of requirements for a given config model.
     * @param model the model to set policy requirements on.
     * @param qname the policy requirement to add
     */
    public static void addRequirement(Model model, QName qname) {
        Set<QName> requires = getRequirements(model);
        requires.add(qname);
        setRequirements(model, requires);
    }

    /**
     * If a config model contains the given policy requirement.
     * @param model the model to check for the policy requirement
     * @param qname the policy requirement to check for
     * @return whether the policy requirement exists on the config model
     */
    public static boolean hasRequirement(Model model, QName qname) {
        Set<QName> requirements = getRequirements(model);
        for (QName requirement : requirements) {
            // 1. look for an exact match first
            if (requirement.equals(qname)) {
                return true;
            }
            // 2. look for a localName match second for backwards compatibility
            // NOTE: similar logic found in:
            // -   core/api: org.switchyard.policy.PolicyFactory.getPolicy(QName):Policy
            // -   core/api: org.switchyard.policy.PolicyUtil.containsPolicy(Set<Policy>, Policy):boolean
            // NOTE: NULL_NS_URI works because of no default namespace assumption from parent element made in:
            // - core/config: org.switchyard.config.BaseConfiguration.createAttributeQName(String value):QName
            if (XMLConstants.NULL_NS_URI.equals(requirement.getNamespaceURI()) && requirement.getLocalPart().equals(qname.getLocalPart())) {
                return true;
            }
        }
        return false;
    }
}
