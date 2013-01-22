/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.security.principal;

import java.io.Serializable;
import java.security.Principal;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * Group.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class Group implements java.security.acl.Group, Serializable {

    private static final long serialVersionUID = -909127780618924905L;

    /**
     * The "Roles" group name.
     */
    public static final String ROLES = "Roles";

    private final String _name;
    private final Set<Principal> _members = new HashSet<Principal>();

    /**
     * Constructs a Group with the specified name.
     * @param name the specified name
     */
    public Group(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Group name cannot be null");
        }
        _name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return _name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addMember(Principal user) {
        return _members.add(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isMember(Principal member) {
        return _members.contains(member);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Enumeration<? extends Principal> members() {
        return Collections.enumeration(_members);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeMember(Principal user) {
        return _members.remove(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Group [name=" + _name + ", members=" + _members + "]";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_members == null) ? 0 : _members.hashCode());
        result = prime * result + ((_name == null) ? 0 : _name.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Group other = (Group)obj;
        if (_members == null) {
            if (other._members != null) {
                return false;
            }
        } else if (!_members.equals(other._members)) {
            return false;
        }
        if (_name == null) {
            if (other._name != null) {
                return false;
            }
        } else if (!_name.equals(other._name)) {
            return false;
        }
        return true;
    }

}
