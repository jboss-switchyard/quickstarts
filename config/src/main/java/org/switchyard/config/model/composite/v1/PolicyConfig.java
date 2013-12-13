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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.switchyard.config.model.Model;

/**
 * Provides methods to help with Policy configuration on SCA config models.
 */
public final class PolicyConfig {

    /** The "requires" name. */
    public static final String REQUIRES = "requires";
    
    private PolicyConfig() {
        
    }
    
    /**
     * Get the content of the requires attribute as a set of strings.
     * @param model the model to query for required policy
     * @return set of policy requirements; empty set if nothing found
     */
    public static Set<String> getRequires(Model model) {
        Set<String> requiredSet = new HashSet<String>();
        String requires = model.getModelConfiguration().getAttribute(REQUIRES);
        if (requires != null) {
            for (String policy : requires.split(" ")) {
                requiredSet.add(policy);
            }
        }
        return requiredSet;
    }
    
    /**
     * Set the content of the requires attribute for a given config model.
     * @param model the model to set policy requirements on.
     * @param requiredSet the set of policy requirements.
     */
    public static void setRequires(Model model, Set<String> requiredSet) {
        if (requiredSet == null || requiredSet.isEmpty()) {
            return;
        }
        Iterator<String> it = requiredSet.iterator();
        StringBuilder requires = new StringBuilder(it.next());
        while (it.hasNext()) {
            requires.append(" " + it.next());
        }
        
        model.getModelConfiguration().setAttribute(REQUIRES, requires.toString());
    }
}
