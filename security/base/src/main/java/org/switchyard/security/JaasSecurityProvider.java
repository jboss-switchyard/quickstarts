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

import java.util.Set;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.apache.log4j.Logger;
import org.switchyard.ServiceSecurity;
import org.switchyard.common.lang.Strings;
import org.switchyard.common.type.reflect.Construction;
import org.switchyard.security.callback.handler.NamePasswordCallbackHandler;
import org.switchyard.security.callback.handler.SwitchYardCallbackHandler;
import org.switchyard.security.principal.Group;
import org.switchyard.security.principal.Role;

/**
 * JaasSecurityProvider.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class JaasSecurityProvider extends SecurityProvider {

    private static final Logger LOGGER = Logger.getLogger(JaasSecurityProvider.class);

    /**
     * Constructs a new JaasSecurityProvider.
     */
    public JaasSecurityProvider() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean authenticate(ServiceSecurity serviceSecurity, SecurityContext securityContext) {
        boolean success = false;
        Class<?> ch_clazz = serviceSecurity.getCallbackHandler();
        if (ch_clazz == null) {
            ch_clazz = NamePasswordCallbackHandler.class;
        }
        CallbackHandler ch = (CallbackHandler)Construction.construct(ch_clazz);
        if (ch instanceof SwitchYardCallbackHandler) {
            SwitchYardCallbackHandler sych = (SwitchYardCallbackHandler)ch;
            sych.setProperties(serviceSecurity.getProperties());
            sych.setCredentials(securityContext.getCredentials());
        }
        String securityDomain = getSecurityDomain(serviceSecurity);
        Subject subject = securityContext.getSubject(securityDomain);
        try {
            new LoginContext(securityDomain, subject, ch).login();
            success = true;
        } catch (LoginException le) {
            LOGGER.error("authenticate LoginException: " + le.getMessage(), le);
        }
        return success;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean propagate(ServiceSecurity serviceSecurity, SecurityContext securityContext) {
        // Override in sub-class if desired.
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addRunAs(ServiceSecurity serviceSecurity, SecurityContext securityContext) {
        boolean success = true;
        String runAs = Strings.trimToNull(serviceSecurity.getRunAs());
        if (runAs != null) {
            success = false;
            Role runAsRole = new Role(runAs);
            String securityDomain = getSecurityDomain(serviceSecurity);
            Subject subject = securityContext.getSubject(securityDomain);
            Set<Group> groups = subject.getPrincipals(Group.class);
            if (groups.isEmpty()) {
                Group rolesGroup = new Group(Group.ROLES);
                rolesGroup.addMember(runAsRole);
                subject.getPrincipals().add(rolesGroup);
                success = true;
            } else {
                for (Group group : groups) {
                    if (Group.ROLES.equals(group.getName())) {
                        group.addMember(runAsRole);
                        success = true;
                    }
                }
            }
        }
        return success;
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
        String securityDomain = getSecurityDomain(serviceSecurity);
        for (String roleName : rolesAllowed) {
            boolean isInRole = securityContext.isCallerInRole(roleName, securityDomain);
            if (isInRole) {
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean clear(ServiceSecurity serviceSecurity, SecurityContext securityContext) {
        securityContext.clear();
        return true;
    }

    /**
     * Gets the security domain from the ServiceSecurity.
     * @param serviceSecurity the ServiceSecurity
     * @return the security domain
     */
    protected String getSecurityDomain(ServiceSecurity serviceSecurity) {
        String securityDomain = Strings.trimToNull(serviceSecurity.getModuleName());
        if (securityDomain == null) {
            securityDomain = "other";
        }
        return securityDomain;
    }

}
