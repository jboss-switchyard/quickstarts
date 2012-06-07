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
package org.switchyard.security;

/**
 * SecurityUtil.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public final class SecurityUtil {

    private SecurityUtil() {}

    /*
    public static boolean checkRolesAllowed(List<String> rolesAllowed, SecurityContext securityContext, String domain) {
        if (rolesAllowed.isEmpty()) {
            return true;
        }
        for (String roleName : rolesAllowed) {
            boolean isInRole = securityContext.isCallerInRole(roleName, domain);
            if (isInRole) {
                return true;
            }
        }
        return false;
    }

    public static boolean isCallerInRole(Subject subject, Principal role) {
        Set<java.security.acl.Group> groups = subject.getPrincipals(java.security.acl.Group.class);
        for (java.security.acl.Group group : groups) {
            if (group.isMember(role)) {
                return true;
            }
        }
        return false;
    }

    public static void addRunAs(String runAs, Subject subject) {
        if (runAs != null) {
            Role runAsRole = new Role(runAs);
            Set<Group> groups = subject.getPrincipals(Group.class);
            if (groups.isEmpty()) {
                Group group = new Group(ROLES_GROUP_NAME);
                group.addMember(runAsRole);
                subject.getPrincipals().add(group);
            } else {
                for (Group group : groups) {
                    if (ROLES_GROUP_NAME.equals(group.getName())) {
                        group.addMember(runAsRole);
                    }
                }
            }
        }
    }
    */

}
