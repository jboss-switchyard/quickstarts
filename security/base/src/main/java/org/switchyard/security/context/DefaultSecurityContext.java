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
package org.switchyard.security.context;

import static org.switchyard.security.principal.GroupPrincipal.CALLER_PRINCIPAL;
import static org.switchyard.security.principal.GroupPrincipal.ROLES;

import java.security.Principal;
import java.security.acl.Group;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import javax.security.auth.Subject;

import org.switchyard.security.credential.Credential;
import org.switchyard.security.principal.UserPrincipal;
import org.switchyard.security.system.SystemSecurity;

/**
 * DefaultSecurityContext.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public final class DefaultSecurityContext implements SecurityContext {

    private static final long serialVersionUID = -5672423874298035845L;
    private static final String FORMAT = DefaultSecurityContext.class.getSimpleName() + "@%s[systemUUID=%s, expirationMillis=%s, credentials=%s, securityDomainsToSubjects=%s]";

    private final UUID _systemUUID;
    private final long _expirationMillis; // this is not included in equals() and hashCode()
    private final Set<Credential> _credentials = Collections.synchronizedSet(new LinkedHashSet<Credential>());
    private final Map<String, Subject> _securityDomainsToSubjects = Collections.synchronizedMap(new TreeMap<String, Subject>());

    /**
     * Constructs a new DefaultSecurityContext with no timeout.
     */
    DefaultSecurityContext() {
        this(null, null);
    }

    /**
     * Constructs a new DefaultSecurityContext with the specified system UUID and timeout in milliseconds.
     * @param systemUUID the system UUID
     * @param timeoutMillis the timeout in milliseconds
     */
    DefaultSecurityContext(UUID systemUUID, Long timeoutMillis) {
        _systemUUID = systemUUID != null ? systemUUID : SystemSecurity.DEFAULT.getUUID();
        long em = 0;
        if (timeoutMillis != null) {
            long tm = timeoutMillis.longValue();
            if (tm > 0) {
                em = System.currentTimeMillis() + tm;
            }
        }
        _expirationMillis = em;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(UUID systemUUID) {
        return (_systemUUID.equals(systemUUID)) && (_expirationMillis == 0 || _expirationMillis > System.currentTimeMillis());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Credential> getCredentials() {
        return _credentials;
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
     * {@inheritDoc}
     */
    @Override
    public synchronized void clearCredentials() {
        synchronized (_credentials) {
            _credentials.clear();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Subject getSubject(String securityDomain) {
        return getSubject(securityDomain, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized Subject getSubject(String securityDomain, boolean create) {
        Subject subject = _securityDomainsToSubjects.get(securityDomain);
        if (subject == null && create) {
            subject = new Subject();
            _securityDomainsToSubjects.put(securityDomain, subject);
        }
        return subject;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void clearSubject(String securityDomain) {
        synchronized (_securityDomainsToSubjects) {
            _securityDomainsToSubjects.remove(securityDomain);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Principal getCallerPrincipal(String securityDomain) {
        Principal callerPrincipal = null;
        Subject subject = getSubject(securityDomain, false);
        if (subject != null) {
            outerLoop : for (Principal principal : subject.getPrincipals()) {
                if (principal instanceof Group) {
                    Group group = (Group)principal;
                    if (group.getName().equalsIgnoreCase(CALLER_PRINCIPAL)) {
                        Enumeration<? extends Principal> members = group.members();
                        while (members.hasMoreElements()) {
                            callerPrincipal = members.nextElement();
                            break outerLoop;
                        }
                    }
                } else if (callerPrincipal == null && principal instanceof UserPrincipal) {
                    callerPrincipal = principal;
                }
            }
        }
        return callerPrincipal;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCallerInRole(String roleName, String securityDomain) {
        Subject subject = getSubject(securityDomain, false);
        if (subject != null) {
            for (Principal principal : subject.getPrincipals()) {
                if (principal instanceof Group) {
                    Group group = (Group)principal;
                    if (group.getName().equalsIgnoreCase(ROLES)) {
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
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format(FORMAT, System.identityHashCode(this), _systemUUID, _expirationMillis, _credentials, _securityDomainsToSubjects);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_systemUUID == null) ? 0 : _systemUUID.hashCode());
        result = prime * result + ((_credentials == null) ? 0 : _credentials.hashCode());
        result = prime * result + ((_securityDomainsToSubjects == null) ? 0 : _securityDomainsToSubjects.hashCode());
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
        DefaultSecurityContext other = (DefaultSecurityContext)obj;
        if (_systemUUID == null) {
            if (other._systemUUID != null) {
                return false;
            }
        } else if (!_systemUUID.equals(other._systemUUID)) {
            return false;
        }
        if (_credentials == null) {
            if (other._credentials != null) {
                return false;
            }
        } else if (!_credentials.equals(other._credentials)) {
            return false;
        }
        if (_securityDomainsToSubjects == null) {
            if (other._securityDomainsToSubjects != null) {
                return false;
            }
        } else if (!_securityDomainsToSubjects.equals(other._securityDomainsToSubjects)) {
            return false;
        }
        return true;
    }

}
