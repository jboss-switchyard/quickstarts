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

import static org.switchyard.policy.PolicyUtil.isProvided;
import static org.switchyard.policy.PolicyUtil.isRequired;
import static org.switchyard.policy.PolicyUtil.provide;
import static org.switchyard.policy.SecurityPolicy.AUTHORIZATION;
import static org.switchyard.policy.SecurityPolicy.CLIENT_AUTHENTICATION;
import static org.switchyard.policy.SecurityPolicy.CONFIDENTIALITY;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.logging.Logger;
import org.switchyard.BaseHandler;
import org.switchyard.Exchange;
import org.switchyard.HandlerException;
import org.switchyard.Service;
import org.switchyard.ServiceDomain;
import org.switchyard.ServiceReference;
import org.switchyard.ServiceSecurity;
import org.switchyard.security.SecurityServices;
import org.switchyard.security.context.SecurityContext;
import org.switchyard.security.context.SecurityContextManager;
import org.switchyard.security.credential.ConfidentialityCredential;
import org.switchyard.security.credential.PrincipalCredential;
import org.switchyard.security.provider.SecurityProvider;

/**
 * A security ExchangeHandler implementation.
 * 
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public class SecurityHandler extends BaseHandler {

    private static final Logger LOGGER = Logger.getLogger(SecurityHandler.class);
    private static final ThreadLocal<AtomicInteger> PROCESS_COUNT = new InheritableThreadLocal<AtomicInteger>();

    private final SecurityContextManager _securityContextManager;
    private final SecurityProvider _securityProvider;
    private final SecurityAction _securityAction;

    /**
     * What the SecurityHandler should do.
     */
    public static enum SecurityAction {
        /** Process security. */
        PROCESS,
        /** Cleanup security. */
        CLEANUP;
    }

    /**
     * Constructs a SecurityHandler for the specified ServiceDomain and SecurityAction.
     * @param serviceDomain the ServiceDomain
     * @param securityAction the SecurityAction
     */
    public SecurityHandler(ServiceDomain serviceDomain, SecurityAction securityAction) {
        _securityContextManager = new SecurityContextManager(serviceDomain);
        _securityProvider = SecurityServices.getSecurityProvider();
        _securityAction = securityAction;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleMessage(Exchange exchange) throws HandlerException {
        ServiceSecurity serviceSecurity = getServiceSecurity(exchange);
        if (serviceSecurity == null) {
            // nothing to do
            return;
        }
        switch (_securityAction) {
            case PROCESS:
                process(exchange, serviceSecurity);
                break;
            case CLEANUP:
                cleanup(exchange, serviceSecurity);
                break;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleFault(Exchange exchange) {
        ServiceSecurity serviceSecurity = getServiceSecurity(exchange);
        if (serviceSecurity == null) {
            // nothing to do
            return;
        }
        try {
            cleanup(exchange, serviceSecurity);
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    private void process(Exchange exchange, ServiceSecurity serviceSecurity) throws HandlerException {
        processCount().incrementAndGet();
        SecurityContext securityContext = _securityContextManager.getContext(exchange);
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
        _securityContextManager.setContext(exchange, securityContext);
    }

    private void cleanup(Exchange exchange, ServiceSecurity serviceSecurity) {
        int pc = processCount().decrementAndGet();
        if (pc < 1) {
            _securityContextManager.setContext(exchange, null);
            processCount().set(0);
        }
        /*
        SecurityContext securityContext = _securityContextManager.getContext(exchange);
        securityContext.clearCredentials();
        _securityProvider.clear(serviceSecurity, securityContext);
        _securityContextManager.setContext(exchange, securityContext);
        */
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

    private static synchronized AtomicInteger processCount() {
        AtomicInteger hic = PROCESS_COUNT.get();
        if (hic == null) {
            hic = new AtomicInteger(0);
            PROCESS_COUNT.set(hic);
        }
        return hic;
    }

}
