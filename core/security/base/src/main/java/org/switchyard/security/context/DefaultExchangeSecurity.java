/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.switchyard.security.context;

import java.security.Principal;

import javax.security.auth.Subject;

import org.switchyard.Exchange;
import org.switchyard.ExchangeSecurity;
import org.switchyard.ServiceDomain;
import org.switchyard.ServiceSecurity;
import org.switchyard.security.SecurityMetadata;

/**
 * Default ExchangeSecurity implementation.
 * 
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
public class DefaultExchangeSecurity implements ExchangeSecurity {

    private final Exchange _exchange;
    private final String _securityDomain;
    private final SecurityContextManager _securityContextManager;

    /**
     * Creates a new ExchangeUtil for the specified Exchange.
     * @param exchange the Exchange
     */
    public DefaultExchangeSecurity(Exchange exchange) {
        _exchange = exchange;
        SecurityMetadata securityMetadata = SecurityMetadata.getSecurityMetadata(exchange);
        ServiceSecurity serviceSecurity = securityMetadata.getServiceSecurity();
        _securityDomain = serviceSecurity != null ? serviceSecurity.getSecurityDomain() : null;
        ServiceDomain serviceDomain = securityMetadata.getServiceDomain();
        _securityContextManager = serviceDomain != null ? new SecurityContextManager(serviceDomain) : null;
    }

    /**
     * Gets the security context. <i>This method is not exposed in the public API.</i>
     * @return the security context
     */
    public SecurityContext getSecurityContext() {
        if (_securityContextManager != null) {
            return _securityContextManager.getContext(_exchange);
        }
        return null;
    }

    /**
     * Gets the subject. <i>This method is not exposed in the public API.</i>
     * @return the subject
     */
    public Subject getSubject() {
        if (_securityContextManager != null) {
            return _securityContextManager.getContext(_exchange).getSubject(_securityDomain);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Principal getCallerPrincipal() {
        if (_securityContextManager != null) {
            return _securityContextManager.getContext(_exchange).getCallerPrincipal(_securityDomain);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCallerInRole(String roleName) {
        if (_securityContextManager != null) {
            return _securityContextManager.getContext(_exchange).isCallerInRole(roleName, _securityDomain);
        }
        return false;
    }

}
