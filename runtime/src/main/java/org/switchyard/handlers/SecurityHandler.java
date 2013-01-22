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
package org.switchyard.handlers;

import static org.switchyard.ExchangePhase.IN;
import static org.switchyard.policy.PolicyUtil.isProvided;
import static org.switchyard.policy.PolicyUtil.isRequired;
import static org.switchyard.policy.PolicyUtil.provide;
import static org.switchyard.policy.SecurityPolicy.AUTHORIZATION;
import static org.switchyard.policy.SecurityPolicy.CLIENT_AUTHENTICATION;
import static org.switchyard.policy.SecurityPolicy.CONFIDENTIALITY;

import java.util.Set;

import org.switchyard.BaseHandler;
import org.switchyard.Exchange;
import org.switchyard.HandlerException;
import org.switchyard.ServiceSecurity;
import org.switchyard.security.SecurityContext;
import org.switchyard.security.SecurityProvider;
import org.switchyard.security.credential.ConfidentialityCredential;
import org.switchyard.security.credential.PrincipalCredential;

/**
 * A security ExchangeHandler implementation.
 * 
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class SecurityHandler extends BaseHandler {

    private final SecurityProvider _securityProvider;
    private final ServiceSecurity _serviceSecurity;

    /**
     * Constructs a SecurityHandler with the specified ServiceSecurity.
     * @param serviceSecurity the specified ServiceSecurity
     */
    public SecurityHandler(ServiceSecurity serviceSecurity) {
        _securityProvider = SecurityProvider.instance();
        _serviceSecurity = serviceSecurity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleMessage(Exchange exchange) throws HandlerException {
        if (IN.equals(exchange.getPhase())) {
            SecurityContext securityContext = SecurityContext.get(exchange);
            if (isRequired(exchange, CONFIDENTIALITY) && !isProvided(exchange, CONFIDENTIALITY)) {
                if (isConfidentialityProvided(securityContext)) {
                    provide(exchange, CONFIDENTIALITY);
                }
            }
            if (isRequired(exchange, CLIENT_AUTHENTICATION) && !isProvided(exchange, CLIENT_AUTHENTICATION)) {
                if (isClientAuthenticationProvided(securityContext)) {
                    provide(exchange, CLIENT_AUTHENTICATION);
                } else {
                    boolean success = _securityProvider.authenticate(_serviceSecurity, securityContext);
                    if (success) {
                        provide(exchange, CLIENT_AUTHENTICATION);
                    }
                }
            }
            if (isRequired(exchange, AUTHORIZATION) && !isProvided(exchange, AUTHORIZATION)) {
                if (isAuthorizationProvided(securityContext)) {
                    provide(exchange, AUTHORIZATION);
                }
            }
        }
    }

    private boolean isConfidentialityProvided(SecurityContext securityContext) {
        Set<ConfidentialityCredential> creds = securityContext.getCredentials(ConfidentialityCredential.class);
        for (ConfidentialityCredential cred : creds) {
            if (cred.isConfidential()) {
                return true;
            }
        }
        return false;
    }

    private boolean isClientAuthenticationProvided(SecurityContext securityContext) {
        Set<PrincipalCredential> creds = securityContext.getCredentials(PrincipalCredential.class);
        for (PrincipalCredential cred : creds) {
            if (cred.getPrincipal() != null && cred.isTrusted()) {
                return true;
            }
        }
        return false;
    }

    private boolean isAuthorizationProvided(SecurityContext securityContext) {
        return _securityProvider.checkRolesAllowed(_serviceSecurity, securityContext);
    }

}
