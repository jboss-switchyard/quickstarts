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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.security.auth.Subject;

import org.switchyard.Exchange;
import org.switchyard.security.credential.Credential;

/**
 * SecurityContext.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public final class SecurityContext {

    private final Set<Credential> _credentials = Collections.synchronizedSet(new HashSet<Credential>());
    private final Map<String,Subject> _domainsToSubjects = Collections.synchronizedMap(new HashMap<String,Subject>());

    /**
     * Constructs a new SecurityContext.
     */
    public SecurityContext() {}

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
     * Clears any and all contained state.
     */
    public synchronized void clear() {
        synchronized (_credentials) {
            _credentials.clear();
        }
        synchronized (_domainsToSubjects) {
            _domainsToSubjects.clear();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "SecurityContext [credentials=" + _credentials + ", domainsToSubjects=" + _domainsToSubjects + "]";
    }

    /**
     * Gets the security context from the exchange.
     * @param exchange the exchange
     * @return the security context
     */
    public static SecurityContext get(Exchange exchange) {
        if (exchange instanceof SecurityExchange) {
            return ((SecurityExchange)exchange).getSecurityContext();
        }
        throw new IllegalArgumentException("Exchange: " + exchange + " is not a SecurityExchange!");
    }

}
