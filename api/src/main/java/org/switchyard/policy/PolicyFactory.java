/*
 * JBoss, Home of Professional Open Source Copyright 2009, Red Hat Middleware
 * LLC, and individual contributors by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.switchyard.policy;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
    public static Policy getPolicy(String name) throws Exception {
        for (Policy p : _policies) {
            if (p.getName().equals(name)) {
                return p;
            }
        }
        throw new Exception("Invalid policy name: '" + name + "' doesn't exist.");
    }
}
