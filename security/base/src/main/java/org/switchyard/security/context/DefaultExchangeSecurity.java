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

import org.switchyard.Exchange;
import org.switchyard.ExchangeSecurity;
import org.switchyard.Service;
import org.switchyard.ServiceDomain;
import org.switchyard.ServiceReference;
import org.switchyard.ServiceSecurity;

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
        ServiceSecurity serviceSecurity = null;
        ServiceDomain serviceDomain = null;
        Service service = exchange.getProvider();
        if (service != null) {
            serviceSecurity = service.getServiceMetadata().getSecurity();
            serviceDomain = service.getDomain();
        }
        if (serviceSecurity == null) {
            ServiceReference serviceReference = exchange.getConsumer();
            if (serviceReference != null) {
                serviceSecurity = serviceReference.getServiceMetadata().getSecurity();
                serviceDomain = serviceReference.getDomain();
            }
        }
        _securityDomain = serviceSecurity != null ? serviceSecurity.getSecurityDomain() : null;
        _securityContextManager = serviceDomain != null ? new SecurityContextManager(serviceDomain) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSecurityDomain() {
        return _securityDomain;
    }

    /*
    public Subject getSubject() {
        if (_securityContextManager != null) {
            return _securityContextManager.getContext(_exchange).getSubject(_securityDomain);
        }
        return null;
    }
    */

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
