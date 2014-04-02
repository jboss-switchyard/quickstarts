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

import java.security.Principal;
import java.security.acl.Group;
import java.util.Collections;
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
import org.switchyard.security.credential.SubjectCredential;
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
        String sy_securityDomain = serviceSecurity.getSecurityDomain();
        Subject toSubject = securityContext.getSubject(sy_securityDomain);
        Set<SubjectCredential> subjectCredentials = securityContext.getCredentials(SubjectCredential.class);
        for (SubjectCredential subjectCredential : subjectCredentials) {
            Subject fromSubject = subjectCredential.getSubject();
            transfer(fromSubject, toSubject);
        }
        return true;
    }

    /**
     * Transfers Principals, private credentials, and public credentials from one Subject to another.
     * @param fromSubject the from Subject
     * @param toSubject the to Subject
     */
    protected void transfer(Subject fromSubject, Subject toSubject) {
        if (toSubject != null && fromSubject != null && toSubject != fromSubject && !toSubject.equals(fromSubject)) {
            Set<Principal> toPrincipals = toSubject.getPrincipals();
            Group toRoleGroup = null;
            for (Principal fromPrincipal : fromSubject.getPrincipals()) {
                if (fromPrincipal instanceof Group && GroupPrincipal.ROLES.equals(fromPrincipal.getName())) {
                    Group fromRoleGroup = (Group)fromPrincipal;
                    if (toRoleGroup == null) {
                        toRoleGroup = getRoleGroup(toSubject);
                    }
                    if (toRoleGroup == fromRoleGroup) {
                        continue;
                    }
                    for (Principal fromRole : Collections.list(fromRoleGroup.members())) {
                        RolePrincipal toRole = fromRole instanceof RolePrincipal ? (RolePrincipal)fromRole : new RolePrincipal(fromRole.getName());
                        toRoleGroup.addMember(toRole);
                    }
                } else {
                    toPrincipals.add(fromPrincipal);
                }
            }
            toSubject.getPrivateCredentials().addAll(fromSubject.getPrivateCredentials());
            toSubject.getPublicCredentials().addAll(fromSubject.getPublicCredentials());
        }
    }

    /**
     * Gets the Group with the name "Roles" from the specified Subject, creating one if not pre-existent.
     * @param subject the subject
     * @return the "Roles" Group
     */
    private GroupPrincipal getRoleGroup(Subject subject) {
        GroupPrincipal roleGroup = null;
        Set<GroupPrincipal> groups = subject.getPrincipals(GroupPrincipal.class);
        for (GroupPrincipal group : groups) {
            if (GroupPrincipal.ROLES.equals(group.getName())) {
                roleGroup = group;
                break;
            }
        }
        if (roleGroup == null) {
            roleGroup = new GroupPrincipal(GroupPrincipal.ROLES);
            subject.getPrincipals().add(roleGroup);
        }
        return roleGroup;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addRunAs(ServiceSecurity serviceSecurity, SecurityContext securityContext) {
        String runAs = Strings.trimToNull(serviceSecurity.getRunAs());
        if (runAs != null) {
            String securityDomain = serviceSecurity.getSecurityDomain();
            Subject subject = securityContext.getSubject(securityDomain);
            GroupPrincipal roleGroup = getRoleGroup(subject);
            roleGroup.addMember(new RolePrincipal(runAs));
            return true;
        }
        return false;
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
