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
package org.switchyard.security.provider;

import java.util.Set;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.switchyard.ServiceSecurity;
import org.switchyard.common.lang.Strings;
import org.switchyard.common.type.reflect.Construction;
import org.switchyard.security.BaseSecurityLogger;
import org.switchyard.security.callback.handler.NamePasswordCallbackHandler;
import org.switchyard.security.callback.handler.SwitchYardCallbackHandler;
import org.switchyard.security.context.SecurityContext;
import org.switchyard.security.principal.GroupPrincipal;
import org.switchyard.security.principal.RolePrincipal;

/**
 * JaasSecurityProvider.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class JaasSecurityProvider implements SecurityProvider {

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
        String securityDomain = serviceSecurity.getSecurityDomain();
        Subject subject = securityContext.getSubject(securityDomain);
        try {
            new LoginContext(securityDomain, subject, ch).login();
            success = true;
        } catch (LoginException le) {
            BaseSecurityLogger.ROOT_LOGGER.authenticateLoginException(le.getMessage(), le);
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
            RolePrincipal runAsRole = new RolePrincipal(runAs);
            String securityDomain = serviceSecurity.getSecurityDomain();
            Subject subject = securityContext.getSubject(securityDomain);
            Set<GroupPrincipal> groups = subject.getPrincipals(GroupPrincipal.class);
            if (groups.isEmpty()) {
                GroupPrincipal rolesGroup = new GroupPrincipal(GroupPrincipal.ROLES);
                rolesGroup.addMember(runAsRole);
                subject.getPrincipals().add(rolesGroup);
                success = true;
            } else {
                for (GroupPrincipal group : groups) {
                    if (GroupPrincipal.ROLES.equals(group.getName())) {
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
        String securityDomain = serviceSecurity.getSecurityDomain();
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
        if (serviceSecurity != null) {
            String securityDomain = serviceSecurity.getSecurityDomain();
            if (securityDomain != null) {
                securityContext.clearSubject(securityDomain);
            }
        }
        return true;
    }

}
