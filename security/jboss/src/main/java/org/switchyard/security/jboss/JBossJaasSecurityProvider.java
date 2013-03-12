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
package org.switchyard.security.jboss;

import javax.security.auth.Subject;

import org.apache.log4j.Logger;
import org.jboss.security.SecurityContextAssociation;
import org.switchyard.ServiceSecurity;
import org.switchyard.security.JaasSecurityProvider;
import org.switchyard.security.SecurityContext;

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
            String securityDomain = getSecurityDomain(serviceSecurity);
            String jb_securityDomain = jb_securityContext.getSecurityDomain();
            if (securityDomain.equals(jb_securityDomain)) {
                Subject subject = securityContext.getSubject(securityDomain);
                Subject jb_subject = jb_securityContext.getUtil().getSubject();
                if (jb_subject != null && subject != jb_subject && !subject.equals(jb_subject)) {
                    subject.getPrincipals().addAll(jb_subject.getPrincipals());
                    subject.getPrivateCredentials().addAll(jb_subject.getPrivateCredentials());
                    subject.getPublicCredentials().addAll(jb_subject.getPublicCredentials());
                }
                return true;
            } else {
                LOGGER.warn(String.format("SwitchYard security domain (%s) does not match JBoss security domain (%s).", securityDomain, jb_securityDomain));
            }
        }
        return false;
    }

}
