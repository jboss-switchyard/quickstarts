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

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.security.auth.Subject;

import org.switchyard.security.credential.Credential;

/**
 * SecurityContext.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public final class SecurityContext implements Serializable {

    private static final long serialVersionUID = -3501287028169238578L;
    private static final ThreadLocal<SecurityContext> THREAD_LOCAL = new ThreadLocal<SecurityContext>();

    private final Set<Credential> _credentials = Collections.synchronizedSet(new HashSet<Credential>());
    private final Map<String,Subject> _domainsToSubjects = Collections.synchronizedMap(new HashMap<String,Subject>());

    private SecurityContext() {}

    /**
     * Gets the Credentials.
     * @return the Credentials
     */
    public Set<Credential> getCredentials() {
        return _credentials;
    }

    /**
     * Gets the Credentials that match the specified type.
     * @param <T> the specified type
     * @param clazz the specified class
     * @return the Credentials that match the specified type
     */
    public <T extends Credential> Set<T> getCredentials(Class<T> clazz) {
        Set<T> matches = new HashSet<T>();
        for (Credential credential : getCredentials()) {
            if (credential != null && clazz.isAssignableFrom(credential.getClass())) {
                matches.add(clazz.cast(credential));
            }
        }
        return matches;
    }

    /**
     * Gets the Subject for the specified domain.
     * @param domain the specified domain
     * @return the Subject for the specified domain
     */
    public synchronized Subject getSubject(String domain) {
        Subject subject = _domainsToSubjects.get(domain);
        if (subject == null) {
            subject = new Subject();
            _domainsToSubjects.put(domain, subject);
        }
        return subject;
    }

    /*
    public boolean isCallerInRole(String roleName, String domain) {
        for (Principal principal : getSubject(domain).getPrincipals()) {
            if (principal instanceof Group) {
                Group group = (Group)principal;
                if (group.getName().equalsIgnoreCase(ROLES_GROUP_NAME)) {
                    Enumeration<? extends Principal> roles = group.members();
                    while (roles.hasMoreElements()) {
                        Principal role = roles.nextElement();
                        if (role.getName().equals(roleName)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    */

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "SecurityContext [credentials=" + _credentials + ", domainsToSubjects=" + _domainsToSubjects + "]";
    }

    /**
     * Gets the ThreadLocal SecurityContext, creating it doesn't already exist.
     * @return the ThreadLocal SecurityContext
     */
    public static SecurityContext get() {
        return get(true);
    }

    /**
     * Gets the ThreadLocal SecurityContext, optionally creating if it doesn't already exist.
     * @param create should it be created if it doesn't already exist?
     * @return the ThreadLocal SecurityContext
     */
    public synchronized static SecurityContext get(boolean create) {
        SecurityContext securityCtx = THREAD_LOCAL.get();
        if (securityCtx == null && create) {
            securityCtx = new SecurityContext();
            THREAD_LOCAL.set(securityCtx);
        }
        return securityCtx;
    }

    /**
     * Clears the ThreadLocal holding the SecurityContext.
     */
    public static void clear() {
        THREAD_LOCAL.set(null);
    }

}
