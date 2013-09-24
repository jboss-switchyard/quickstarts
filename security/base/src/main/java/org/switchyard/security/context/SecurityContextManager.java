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
package org.switchyard.security.context;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

import javax.crypto.SealedObject;

import org.switchyard.Exchange;
import org.switchyard.Property;
import org.switchyard.Scope;
import org.switchyard.ServiceDomain;
import org.switchyard.label.BehaviorLabel;
import org.switchyard.security.credential.Credential;
import org.switchyard.security.crypto.PrivateCrypto;
import org.switchyard.security.service.SecureServiceDomain;
import org.switchyard.security.service.ServiceDomainSecurity;
import org.switchyard.security.system.SystemSecurity;

/**
 * SecurityContextManager.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public final class SecurityContextManager {

    private static final String FORMAT = SecurityContextManager.class.getSimpleName() + "@%s[systemSecurity=%s]";
    private static final String EXCHANGE_PROPERTY = SecurityContext.class.getName();
    private static final ThreadLocal<SecurityContext> THREAD_LOCAL = new InheritableThreadLocal<SecurityContext>();

    private SystemSecurity _systemSecurity = null;

    /**
     * Creates a new SecurityContextManager for the specified ServiceDomain.
     * @param serviceDomain the ServiceDomain
     */
    public SecurityContextManager(ServiceDomain serviceDomain) {
        if (serviceDomain instanceof SecureServiceDomain) {
            ServiceDomainSecurity serviceDomainSecurity = ((SecureServiceDomain)serviceDomain).getServiceDomainSecurity();
            if (serviceDomainSecurity != null) {
                _systemSecurity = serviceDomainSecurity.getSystemSecurity();
            }
        }
        if (_systemSecurity == null) {
            _systemSecurity = SystemSecurity.DEFAULT;
        }
    }

    /**
     * Gets the security context from the exchange.
     * @param exchange the exchange
     * @return the security context
     */
    public SecurityContext getContext(Exchange exchange) {
        return getContext(exchange, true);
    }

    /**
     * Gets the security context from the exchange.
     * @param exchange the exchange
     * @param create create a new security context if one does not already exist in the exchange or it is not still valid
     * @return the security context
     */
    public SecurityContext getContext(Exchange exchange, boolean create) {
        SecurityContext securityContext = null;
        Property property = exchange.getContext().getProperty(EXCHANGE_PROPERTY, Scope.EXCHANGE);
        if (property != null) {
            Object object = property.getValue();
            if (object instanceof SecurityContext) {
                securityContext = (SecurityContext)object;
            } else if (object instanceof SealedObject) {
                PrivateCrypto privateCrypto = _systemSecurity.getPrivateCrypto();
                if (privateCrypto == null) {
                    throw new IllegalStateException("privateCrypto == null");
                }
                securityContext = (SecurityContext)privateCrypto.unseal((SealedObject)object);
            } else if (object != null) {
                throw new IllegalArgumentException(object.getClass().getName() + " != " + EXCHANGE_PROPERTY);
            }
        }
        UUID systemUUID = _systemSecurity.getUUID();
        if ((securityContext == null || !securityContext.isValid(systemUUID)) && create) {
            Long timeoutMillis = _systemSecurity.getSecurityContextTimeoutMillis();
            securityContext = new DefaultSecurityContext(systemUUID, timeoutMillis);
        }
        return securityContext;
    }

    /**
     * Sets the security context on the exchange.
     * @param exchange the exchange
     * @param securityContext the security context
     */
    public void setContext(Exchange exchange, SecurityContext securityContext) {
        THREAD_LOCAL.set(securityContext);
        Serializable object = securityContext;
        if (object != null) {
            PrivateCrypto privateCrypto = _systemSecurity.getPrivateCrypto();
            if (privateCrypto != null) {
                object = privateCrypto.seal(object);
            }
        }
        Property property = exchange.getContext().setProperty(EXCHANGE_PROPERTY, object, Scope.EXCHANGE);
        if (property != null) {
            property.addLabels(BehaviorLabel.TRANSIENT.label());
        }
    }

    /**
     * Adds credentials to the security context in the exchange.
     * @param exchange the exchange
     * @param credentials the credentials
     */
    public void addCredentials(Exchange exchange, Set<Credential> credentials) {
        if (credentials != null && credentials.size() > 0) {
            SecurityContext securityContext = getContext(exchange);
            securityContext.getCredentials().addAll(credentials);
            setContext(exchange, securityContext);
        }
    }

    /**
     * Propagates the security context from a thread-local to the destination exchange.
     * @param toExchange the destination exchange
     */
    public void propagateContext(Exchange toExchange) {
        SecurityContext securityContext = THREAD_LOCAL.get();
        setContext(toExchange, securityContext);
    }

    /**
     * Propagates the security context from one exchange to another.
     * @param fromExchange the source exchange
     * @param toExchange the destination exchange
     */
    public void propagateContext(Exchange fromExchange, Exchange toExchange) {
        setContext(toExchange, getContext(fromExchange, false));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format(FORMAT, System.identityHashCode(this), _systemSecurity);
    }

}
