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
        String requires = it.next();
        while (it.hasNext()) {
            requires += " " + it.next();
        }
        
        model.getModelConfiguration().setAttribute(REQUIRES, requires);
    }
}
