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
package org.switchyard.security.jboss.provider;


import java.security.AccessController;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.security.acl.Group;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.security.auth.Subject;


import org.jboss.security.RunAs;
import org.jboss.security.RunAsIdentity;
import org.jboss.security.SecurityContextAssociation;
import org.jboss.security.SecurityContextFactory;
import org.jboss.security.identity.Identity;
import org.jboss.security.identity.Role;
import org.jboss.security.identity.RoleGroup;
import org.jboss.security.identity.extensions.CredentialIdentityFactory;
import org.jboss.security.identity.plugins.SimpleRoleGroup;
import org.jboss.security.mapping.MappingResult;
import org.picketlink.identity.federation.bindings.jboss.auth.mapping.STSGroupMappingProvider;
import org.picketlink.identity.federation.bindings.jboss.auth.mapping.STSPrincipalMappingProvider;
import org.picketlink.identity.federation.core.wstrust.auth.AbstractSTSLoginModule;
import org.switchyard.ServiceSecurity;
import org.switchyard.security.context.SecurityContext;
import org.switchyard.security.credential.AssertionCredential;
import org.switchyard.security.jboss.JBossSecurityLogger;
import org.switchyard.security.principal.GroupPrincipal;
import org.switchyard.security.principal.RolePrincipal;
import org.switchyard.security.principal.UserPrincipal;
import org.switchyard.security.provider.DefaultSecurityProvider;
import org.w3c.dom.Element;

/**
 * JBossSecurityProvider.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public class JBossSecurityProvider extends DefaultSecurityProvider {

    static {
        // Here to trigger fallback usage of a different SecurityProvider (or DefaultSecurityProvider),
        // if the org.picketbox:picketbox dependency is not available.
        SecurityContextAssociation.getSecurityContext();
    }

    /**
     * Constructs a new JBossSecurityProvider.
     */
    public JBossSecurityProvider() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void populate(ServiceSecurity serviceSecurity, SecurityContext securityContext) {
        String sy_securityDomain = serviceSecurity.getSecurityDomain();
        Subject sy_subject = securityContext.getSubject(sy_securityDomain);
        org.jboss.security.SecurityContext jb_securityContext = SecurityContextAssociation.getSecurityContext();
        if (jb_securityContext != null) {
            // populate from pre-authenticated container context
            String jb_securityDomain = jb_securityContext.getSecurityDomain();
            if (!sy_securityDomain.equals(jb_securityDomain)) {
                    pushSubjectContext(sy_securityDomain);
            }
            Subject jb_subject = jb_securityContext.getUtil().getSubject();
            transfer(jb_subject, sy_subject);
        } else {
            // populate from pre-verified federated assertion
            Set<AssertionCredential> assertionCredentials = securityContext.getCredentials(AssertionCredential.class);
            for (AssertionCredential assertionCredential : assertionCredentials) {
                Element assertionElement = assertionCredential.getAssertion();
                if (assertionElement != null) {
                    Subject sts_subject = new Subject();
                    boolean sts_mapped = false;
                    Map<String, Object> contextMap = new HashMap<String, Object>();
                    contextMap.put(AbstractSTSLoginModule.SHARED_TOKEN, assertionElement);
                    STSPrincipalMappingProvider principalMapper = new STSPrincipalMappingProvider();
                    principalMapper.init(contextMap);
                    MappingResult<Principal> principalResult = new MappingResult<Principal>();
                    principalMapper.setMappingResult(principalResult);
                    principalMapper.performMapping(contextMap, null);
                    Principal principal = principalResult.getMappedObject();
                    if (principal != null) {
                        sts_subject.getPrincipals().add(new UserPrincipal(principal.getName()));
                        sts_mapped = true;
                    }
                    STSGroupMappingProvider rolesMapper = new STSGroupMappingProvider();
                    rolesMapper.init(contextMap);
                    MappingResult<RoleGroup> rolesResult = new MappingResult<RoleGroup>();
                    rolesMapper.setMappingResult(rolesResult);
                    rolesMapper.performMapping(contextMap, null);
                    RoleGroup roleGroup = rolesResult.getMappedObject();
                    if (roleGroup != null) {
                        GroupPrincipal roles = null;
                        for (Role role : roleGroup.getRoles()) {
                            if (roles == null) {
                                roles = new GroupPrincipal(GroupPrincipal.ROLES);
                            }
                            roles.addMember(new RolePrincipal(role.getRoleName()));
                        }
                        if (roles != null) {
                            sts_subject.getPrincipals().add(roles);
                            sts_mapped = true;
                        }
                    }
                    if (sts_mapped) {
                        transfer(sts_subject, sy_subject);
                    }
                }
            }
        }
        super.populate(serviceSecurity, securityContext);
    }

    /**
    * Create new security context for the specified domain, transferring the
    * current subject, principal and credentials into the new domain.
    *
    * @param domain the domain
    */ 
    public void pushSubjectContext(final String domain) {
        AccessController.doPrivileged(new PrivilegedAction<Void>() {

            public Void run() {
                org.jboss.security.SecurityContext oldSecurityContext = SecurityContextAssociation.getSecurityContext();
                org.jboss.security.SecurityContext securityContext = createSecurityContext(domain);
                setSecurityContextOnAssociation(securityContext);
                securityContext.getUtil().createSubjectInfo(oldSecurityContext.getUtil().getUserPrincipal(), oldSecurityContext.getUtil().getCredential(), oldSecurityContext.getUtil().getSubject());
                return null;
            }
        });
    }

    /**
     * Create a JBoss Security Context with the given security domain name
     *
     * @param domain the security domain name (such as "other" )
     * @return an instanceof {@code SecurityContext}
     */
    private static org.jboss.security.SecurityContext createSecurityContext(final String domain) {
        return AccessController.doPrivileged(new PrivilegedAction<org.jboss.security.SecurityContext>() {

            @Override
            public org.jboss.security.SecurityContext run() {
                try {
                    return SecurityContextFactory.createSecurityContext(domain);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    /**
     * Set the {@code SecurityContext} on the {@code SecurityContextAssociation}
     *
     * @param sc the security context
     */
    private static void setSecurityContextOnAssociation(final org.jboss.security.SecurityContext sc) {
        AccessController.doPrivileged(new PrivilegedAction<Void>() {

            @Override
            public Void run() {
                SecurityContextAssociation.setSecurityContext(sc);
                return null;
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object setContainerContext(String securityDomain, Subject subject, Principal principal, Group rolesGroup, String runAs) throws Exception {
        Set<String> roles = new HashSet<String>();
        if (runAs != null) {
            roles.add(runAs);
        }
        if (rolesGroup != null) {
            for (Principal role : Collections.list(rolesGroup.members())) {
                if (runAs == null) {
                    runAs = role.getName();
                }
                roles.add(role.getName());
            }
        }
        RunAs new_jb_runAs = runAs != null ? new RunAsIdentity(runAs, principal != null ? principal.getName() : null, roles) : null;
        org.jboss.security.SecurityContext old_jb_securityContext = SecurityContextAssociation.getSecurityContext();
        if (old_jb_securityContext == null) {
            Set<Object> credentials = subject.getPrivateCredentials();
            Object credential = !credentials.isEmpty() ? credentials.iterator().next() : null;
            org.jboss.security.SecurityContext new_jb_securityContext = SecurityContextFactory.createSecurityContext(securityDomain);
            SecurityContextAssociation.setSecurityContext(new_jb_securityContext);
            if (rolesGroup != null) {
                RoleGroup roleGroup = new SimpleRoleGroup(rolesGroup);
                Identity identity = CredentialIdentityFactory.createIdentity(principal, credential, roleGroup);
                new_jb_securityContext.getUtil().createSubjectInfo(identity, subject);
                new_jb_securityContext.getSubjectInfo().setRoles(roleGroup);
            } else {
                Identity identity = CredentialIdentityFactory.createIdentity(principal, credential);
                new_jb_securityContext.getUtil().createSubjectInfo(identity, subject);
            }
            if (new_jb_runAs != null) {
                new_jb_securityContext.setOutgoingRunAs(new_jb_runAs);
            }
            return new JBossContainerContext(null, null, null);
        } else {
            RoleGroup old_jb_roleGroup = old_jb_securityContext.getSubjectInfo().getRoles();
            if (rolesGroup != null) {
                old_jb_securityContext.getSubjectInfo().setRoles(new SimpleRoleGroup(rolesGroup));
            }
            RunAs old_jb_runAs = old_jb_securityContext.getOutgoingRunAs();
            if (new_jb_runAs != null) {
                old_jb_securityContext.setOutgoingRunAs(new_jb_runAs);
            }
            return new JBossContainerContext(old_jb_securityContext, old_jb_roleGroup, old_jb_runAs);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void resetContainerContext(Object previous) throws Exception {
        JBossContainerContext old_jb = (JBossContainerContext)previous;
        if (old_jb._securityContext == null) {
            SecurityContextAssociation.clearSecurityContext();
        } else {
            SecurityContextAssociation.setSecurityContext(old_jb._securityContext);
            old_jb._securityContext.getSubjectInfo().setRoles(old_jb._roleGroup);
            old_jb._securityContext.setOutgoingRunAs(old_jb._runAs);
        }
    }

    private static final class JBossContainerContext {
        private final org.jboss.security.SecurityContext _securityContext;
        private final RoleGroup _roleGroup;
        private final RunAs _runAs;
        private JBossContainerContext(org.jboss.security.SecurityContext securityContext, RoleGroup roleGroup, RunAs runAs) {
            this._securityContext = securityContext;
            this._roleGroup = roleGroup;
            this._runAs = runAs;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear(ServiceSecurity serviceSecurity, SecurityContext securityContext) {
        super.clear(serviceSecurity, securityContext);
        try {
            org.jboss.security.SecurityContext jb_securityContext = SecurityContextAssociation.getSecurityContext();
            if (jb_securityContext != null) {
                String sy_securityDomain = serviceSecurity.getSecurityDomain();
                String jb_securityDomain = jb_securityContext.getSecurityDomain();
                if (sy_securityDomain.equals(jb_securityDomain)) {
                    SecurityContextAssociation.clearSecurityContext();
                }
            }
        } catch (Throwable t) {
            JBossSecurityLogger.ROOT_LOGGER.clearSecurityContextAssociation(t);
        }
    }

}
