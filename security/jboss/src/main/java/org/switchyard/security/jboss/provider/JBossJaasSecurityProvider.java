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

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.security.auth.Subject;

import org.jboss.security.SecurityContextAssociation;
import org.jboss.security.identity.Role;
import org.jboss.security.identity.RoleGroup;
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
import org.switchyard.security.provider.JaasSecurityProvider;
import org.w3c.dom.Element;

/**
 * JBossJaasSecurityProvider.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public class JBossJaasSecurityProvider extends JaasSecurityProvider {

    static {
        // Here to trigger fallback usage of the JaasSecurityProvider in the static initializer
        // block of SecurityProvider, if the org.picketbox:picketbox dependency is not available.
        SecurityContextAssociation.getSecurityContext();
    }

    /**
     * Constructs a new JBossJaasSecurityProvider.
     */
    public JBossJaasSecurityProvider() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean propagate(ServiceSecurity serviceSecurity, SecurityContext securityContext) {
        String sy_securityDomain = serviceSecurity.getSecurityDomain();
        Subject sy_subject = securityContext.getSubject(sy_securityDomain);
        org.jboss.security.SecurityContext jb_securityContext = SecurityContextAssociation.getSecurityContext();
        if (jb_securityContext != null) {
            // propagate from pre-authenticated container context
            String jb_securityDomain = jb_securityContext.getSecurityDomain();
            if (sy_securityDomain.equals(jb_securityDomain)) {
                Subject jb_subject = jb_securityContext.getUtil().getSubject();
                transfer(jb_subject, sy_subject);
                return true;
            } 
        } else {
            // propagate from pre-verified federated assertion
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
                        return true;
                    }
                }
            }

        }
        return super.propagate(serviceSecurity, securityContext);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean clear(ServiceSecurity serviceSecurity, SecurityContext securityContext) {
        boolean success = super.clear(serviceSecurity, securityContext);
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
            success = false;
        }
        return success;
    }

}
