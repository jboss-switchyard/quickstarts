/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.security.jboss.spi;

import javax.security.auth.Subject;

import org.apache.log4j.Logger;
import org.jboss.security.SecurityContextAssociation;
import org.switchyard.ServiceSecurity;
import org.switchyard.security.SecurityContext;
import org.switchyard.security.spi.JaasSecurityProvider;

/**
 * JBossJaasSecurityProvider.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public class JBossJaasSecurityProvider extends JaasSecurityProvider {

    private static final Logger LOGGER = Logger.getLogger(JBossJaasSecurityProvider.class);

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
            LOGGER.error("Problem clearing SecurityContextAssociation: " + t.getMessage(), t);
            success = false;
        }
        return success;
    }

}
