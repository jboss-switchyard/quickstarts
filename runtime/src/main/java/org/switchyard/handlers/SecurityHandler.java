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
import org.switchyard.Service;
import org.switchyard.ServiceReference;
import org.switchyard.ServiceSecurity;
import org.switchyard.security.SecurityContext;
import org.switchyard.security.credential.ConfidentialityCredential;
import org.switchyard.security.credential.PrincipalCredential;
import org.switchyard.security.spi.SecurityProvider;

/**
 * A security ExchangeHandler implementation.
 * 
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public class SecurityHandler extends BaseHandler {

    private final SecurityProvider _securityProvider;

    /**
     * Constructs a SecurityHandler.
     */
    public SecurityHandler() {
        _securityProvider = SecurityProvider.instance();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleMessage(Exchange exchange) throws HandlerException {
        ServiceSecurity serviceSecurity = getServiceSecurity(exchange);
        if (serviceSecurity != null) {
            SecurityContext securityContext = SecurityContext.get(exchange);
            if (IN.equals(exchange.getPhase())) {
                if (isRequired(exchange, CONFIDENTIALITY) && !isProvided(exchange, CONFIDENTIALITY)) {
                    if (isConfidentialityProvided(securityContext)) {
                        provide(exchange, CONFIDENTIALITY);
                    }
                }
                boolean success = false;
                if (isRequired(exchange, CLIENT_AUTHENTICATION) && !isProvided(exchange, CLIENT_AUTHENTICATION)) {
                    if (isClientAuthenticationProvided(securityContext)) {
                        provide(exchange, CLIENT_AUTHENTICATION);
                        success = true;
                    } else {
                        boolean authenticated = _securityProvider.authenticate(serviceSecurity, securityContext);
                        if (authenticated) {
                            provide(exchange, CLIENT_AUTHENTICATION);
                            success = true;
                        }
                    }
                } else {
                    success = true;
                }
                if (success) {
                    _securityProvider.propagate(serviceSecurity, securityContext);
                    _securityProvider.addRunAs(serviceSecurity, securityContext);
                }
                if (isRequired(exchange, AUTHORIZATION) && !isProvided(exchange, AUTHORIZATION)) {
                    if (isAuthorizationProvided(serviceSecurity, securityContext)) {
                        provide(exchange, AUTHORIZATION);
                    }
                }
            } else {
                securityContext.clearCredentials();
                _securityProvider.clear(serviceSecurity, securityContext);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleFault(Exchange exchange) {
        ServiceSecurity serviceSecurity = getServiceSecurity(exchange);
        if (serviceSecurity != null) {
            SecurityContext securityContext = SecurityContext.get(exchange);
            securityContext.clearCredentials();
            _securityProvider.clear(serviceSecurity, securityContext);
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

    private boolean isAuthorizationProvided(ServiceSecurity serviceSecurity, SecurityContext securityContext) {
        return _securityProvider.checkRolesAllowed(serviceSecurity, securityContext);
    }

    private ServiceSecurity getServiceSecurity(Exchange exchange) {
        ServiceSecurity serviceSecurity = null;
        Service service = exchange.getProvider();
        if (service != null) {
            serviceSecurity = service.getServiceMetadata().getSecurity();
        }
        if (serviceSecurity == null) {
            ServiceReference serviceReference = exchange.getConsumer();
            if (serviceReference != null) {
                serviceSecurity = serviceReference.getServiceMetadata().getSecurity();
            }
        }
        return serviceSecurity;
    }

}
