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
package org.switchyard.security.principal;

import java.io.Serializable;
import java.security.Principal;
import java.security.acl.Group;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import org.switchyard.security.BaseSecurityMessages;

/**
 * GroupPrincipal.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class GroupPrincipal implements Group, Serializable {

    private static final long serialVersionUID = -909127780618924905L;
    private static final String FORMAT = GroupPrincipal.class.getSimpleName() + "@%s[name=%s, members=%s]";

    /**
     * The "CallerPrincipal" group name.
     */
    public static final String CALLER_PRINCIPAL = "CallerPrincipal";

    /**
     * The "Roles" group name.
     */
    public static final String ROLES = "Roles";

    private final String _name;
    private final Set<Principal> _members = new HashSet<Principal>();

    /**
     * Constructs a GroupPrincipal with the specified name.
     * @param name the specified name
     */
    public GroupPrincipal(String name) {
        if (name == null) {
            throw BaseSecurityMessages.MESSAGES.groupNameCannotBeNull();
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
        return String.format(FORMAT, System.identityHashCode(this), _name, _members);
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
        GroupPrincipal other = (GroupPrincipal)obj;
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
