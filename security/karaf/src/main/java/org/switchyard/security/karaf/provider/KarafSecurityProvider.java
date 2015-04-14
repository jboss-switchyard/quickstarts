/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.switchyard.security.karaf.provider;

import java.security.Principal;
import java.util.Enumeration;
import java.util.Set;

import javax.security.auth.Subject;

import org.apache.karaf.jaas.boot.ProxyLoginModule;
import org.apache.karaf.jaas.boot.principal.GroupPrincipal;
import org.switchyard.ServiceSecurity;
import org.switchyard.security.context.SecurityContext;
import org.switchyard.security.provider.DefaultSecurityProvider;

// TODO: Auto-generated Javadoc
/**
 * KarafSecurityProvider.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
public class KarafSecurityProvider extends DefaultSecurityProvider {

    static {
        // Here to trigger fallback usage of a different SecurityProvider (or DefaultSecurityProvider),
        // if the org.apache.karaf.jaas:org.apache.karaf.jaas.modules dependency is not available.
        new ProxyLoginModule();
    }

    /**
     * Constructs a new KarafSecurityProvider.
     */
    public KarafSecurityProvider() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void populate(ServiceSecurity serviceSecurity, SecurityContext securityContext) {
        super.populate(serviceSecurity, securityContext);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear(ServiceSecurity serviceSecurity, SecurityContext securityContext) {
        super.clear(serviceSecurity, securityContext);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkRolesAllowed(ServiceSecurity serviceSecurity, SecurityContext securityContext) {
        Set<String> rolesAllowed = serviceSecurity.getRolesAllowed();
        if (rolesAllowed.isEmpty()) {
            return true;
        }
        String securityDomain = serviceSecurity.getSecurityDomain();
        for (String roleName : rolesAllowed) {
            boolean isInRole = this.isCallerInRole(securityContext, roleName, securityDomain);
            if (isInRole) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if is caller in role.
     *
     * @param securityContext
     *            the security context
     * @param roleName
     *            the role name
     * @param securityDomain
     *            the security domain
     * @return true, if is caller in role
     */
    public boolean isCallerInRole(SecurityContext securityContext, String roleName, String securityDomain) {
        Subject subject = securityContext.getSubject(securityDomain, false);
        if (subject != null) {
            for (Principal principal : subject.getPrincipals()) {
                if (principal instanceof GroupPrincipal) {
                    if (principal.getName().equalsIgnoreCase(roleName)) {
                        return true;
                    }
                } else if (principal instanceof org.switchyard.security.principal.GroupPrincipal) {
                    Enumeration<? extends Principal> e = ((org.switchyard.security.principal.GroupPrincipal) principal).members();
                    while (e.hasMoreElements()) {
                        Principal p = e.nextElement();
                        if (p.getName().equals(roleName)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
