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

import java.security.AccessController;
import java.security.Principal;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.acl.Group;
import java.util.Collections;
import java.util.Set;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.switchyard.ServiceSecurity;
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
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public class DefaultSecurityProvider implements SecurityProvider {

    /**
     * Constructs a new JaasSecurityProvider.
     */
    public DefaultSecurityProvider() {}

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
    public void populate(ServiceSecurity serviceSecurity, SecurityContext securityContext) {
        String securityDomain = serviceSecurity.getSecurityDomain();
        Subject toSubject = securityContext.getSubject(securityDomain);
        Set<SubjectCredential> subjectCredentials = securityContext.getCredentials(SubjectCredential.class);
        for (SubjectCredential subjectCredential : subjectCredentials) {
            Subject fromSubject = subjectCredential.getSubject();
            transfer(fromSubject, toSubject);
        }
    }

    /**
     * Transfers Principals, private credentials, and public credentials from one Subject to another.
     * @param fromSubject the from Subject
     * @param toSubject the to Subject
     */
    protected void transfer(Subject fromSubject, Subject toSubject) {
        if (toSubject != null && fromSubject != null && toSubject != fromSubject && !toSubject.equals(fromSubject)) {
            Set<Principal> toPrincipals = toSubject.getPrincipals();
            Group toRolesGroup = null;
            for (Principal fromPrincipal : fromSubject.getPrincipals()) {
                if (fromPrincipal instanceof Group && GroupPrincipal.ROLES.equals(fromPrincipal.getName())) {
                    Group fromRolesGroup = (Group)fromPrincipal;
                    if (toRolesGroup == null) {
                        toRolesGroup = getRolesGroup(toSubject);
                    }
                    if (toRolesGroup == fromRolesGroup) {
                        continue;
                    }
                    for (Principal fromRole : Collections.list(fromRolesGroup.members())) {
                        RolePrincipal toRole = fromRole instanceof RolePrincipal ? (RolePrincipal)fromRole : new RolePrincipal(fromRole.getName());
                        toRolesGroup.addMember(toRole);
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
    public <T> T runAs(ServiceSecurity serviceSecurity, SecurityContext securityContext, PrivilegedExceptionAction<T> action) throws Exception {
        T t = null;
        final String securityDomain = serviceSecurity.getSecurityDomain();
        final Subject subject = securityContext.getSubject(securityDomain);
        final Principal principal = securityContext.getCallerPrincipal(securityDomain);
        final Group rolesGroup = getRolesGroup(subject);
        RolePrincipal runAsPrincipal = null;
        boolean runAsAdded = false;
        final String runAs = serviceSecurity.getRunAs();
        if (runAs != null) {
            runAsPrincipal = new RolePrincipal(runAs);
            if (!rolesGroup.isMember(runAsPrincipal)) {
                rolesGroup.addMember(runAsPrincipal);
                runAsAdded = true;
            }
        }
        Object previous = null;
        try {
            previous = doPrivileged(new PrivilegedExceptionAction<Object>() {
                public Object run() throws Exception {
                    return setContainerContext(securityDomain, subject, principal, rolesGroup, runAs);
                }
            });
            t = Subject.doAsPrivileged(subject, action, null);
        } finally {
            if (runAsAdded) {
                rolesGroup.removeMember(runAsPrincipal);
            }
            final Object p = previous;
            doPrivileged(new PrivilegedExceptionAction<Object>() {
                public Object run() throws Exception {
                    resetContainerContext(p);
                    return null;
                }
            });
        }
        return t;
    }

    /**
     * Runs a PrivilegedExceptionAction.
     * @param action PrivilegedExceptionAction
     * @return the result of the action
     * @throws Exception if a problem occurs
     */
    private final <T> T doPrivileged(PrivilegedExceptionAction<T> action) throws Exception {
        if (System.getSecurityManager() != null) {
            try {
                return AccessController.doPrivileged(action);
            } catch (PrivilegedActionException pae) {
                throw pae.getException();
            }
        } else {
            return action.run();
        }
    }

    /**
     * Sets a container-specific security context, if necessary.
     * @param securityDomain the security domain
     * @param subject the subject
     * @param principal the principal
     * @param rolesGroup the Roles group
     * @param runAs the run as
     * @return the previous container-specific security context, if it existed
     * @throws Exception if a problem occurs
     */
    protected Object setContainerContext(String securityDomain, Subject subject, Principal principal, Group rolesGroup, String runAs) throws Exception {
        // override in sub-classes
        return null;
    }

    /**
     * Resets a container-specific security context to its previous state, if necessary.
     * @param previous the previous state
     * @throws Exception if a problem occurs
     */
    protected void resetContainerContext(Object previous) throws Exception {
        // override in sub-classes
    }

    /**
     * Gets the Group with the name "Roles" from the specified Subject, creating one if not pre-existent.
     * @param subject the subject
     * @return the "Roles" Group
     */
    private Group getRolesGroup(Subject subject) {
        Group rolesGroup = null;
        Set<Group> groups = subject.getPrincipals(Group.class);
        for (Group group : groups) {
            if (GroupPrincipal.ROLES.equals(group.getName())) {
                rolesGroup = group;
                break;
            }
        }
        if (rolesGroup == null) {
            rolesGroup = new GroupPrincipal(GroupPrincipal.ROLES);
            subject.getPrincipals().add(rolesGroup);
        }
        return rolesGroup;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear(ServiceSecurity serviceSecurity, SecurityContext securityContext) {
        if (serviceSecurity != null) {
            String securityDomain = serviceSecurity.getSecurityDomain();
            if (securityDomain != null) {
                securityContext.clearSubject(securityDomain);
            }
        }
    }

}
