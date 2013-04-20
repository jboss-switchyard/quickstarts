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
package org.switchyard.security.jboss.spi;

import javax.security.auth.Subject;

import org.jboss.security.SecurityContextAssociation;
import org.switchyard.ServiceSecurity;
import org.switchyard.security.SecurityContext;
import org.switchyard.security.spi.JaasSecurityProvider;
import org.switchyard.security.jboss.JBossSecurityLogger;

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
        org.jboss.security.SecurityContext jb_securityContext = SecurityContextAssociation.getSecurityContext();
        if (jb_securityContext != null) {
            String sy_securityDomain = serviceSecurity.getSecurityDomain();
            String jb_securityDomain = jb_securityContext.getSecurityDomain();
            if (sy_securityDomain.equals(jb_securityDomain)) {
                Subject sy_subject = securityContext.getSubject(sy_securityDomain);
                Subject jb_subject = jb_securityContext.getUtil().getSubject();
                if (jb_subject != null && sy_subject != jb_subject && !sy_subject.equals(jb_subject)) {
                    sy_subject.getPrincipals().addAll(jb_subject.getPrincipals());
                    sy_subject.getPrivateCredentials().addAll(jb_subject.getPrivateCredentials());
                    sy_subject.getPublicCredentials().addAll(jb_subject.getPublicCredentials());
                }
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
