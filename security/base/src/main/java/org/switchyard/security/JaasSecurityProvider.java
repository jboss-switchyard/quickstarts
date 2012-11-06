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

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.apache.log4j.Logger;
import org.switchyard.ServiceSecurity;
import org.switchyard.common.lang.Strings;
import org.switchyard.common.type.reflect.Construction;
import org.switchyard.security.callback.NamePasswordCallbackHandler;
import org.switchyard.security.callback.SwitchYardCallbackHandler;

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
        String domain = Strings.trimToNull(serviceSecurity.getModuleName());
        if (domain == null) {
            domain = "other";
        }
        Subject subject = securityContext.getSubject(domain);
        try {
            new LoginContext(domain, subject, ch).login();
            //SecurityUtil.addRunAs(serviceSecurity.getRunAs(), subject);
            success = true;
        } catch (LoginException le) {
            LOGGER.error("authenticate LoginException: " + le.getMessage(), le);
        }
        return success;
    }

}
