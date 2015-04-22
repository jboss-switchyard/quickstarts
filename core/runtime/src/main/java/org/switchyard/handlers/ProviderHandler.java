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
package org.switchyard.handlers;

import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import org.switchyard.BaseHandler;
import org.switchyard.Exchange;
import org.switchyard.HandlerException;
import org.switchyard.Service;
import org.switchyard.ServiceDomain;
import org.switchyard.ServiceSecurity;
import org.switchyard.security.SecurityServices;
import org.switchyard.security.context.SecurityContext;
import org.switchyard.security.context.SecurityContextManager;
import org.switchyard.security.provider.SecurityProvider;

/**
 * Handler calling service provider.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
public class ProviderHandler extends BaseHandler {

    private final SecurityProvider _securityProvider;
    private final SecurityContextManager _securityContextManager;

    /**
     * Create a new ProviderHandler with the specified ServiceDomain.
     * @param serviceDomain the ServiceDomain
     */
    public ProviderHandler(ServiceDomain serviceDomain) {
        _securityProvider = SecurityServices.getSecurityProvider();
        _securityContextManager = new SecurityContextManager(serviceDomain);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleMessage(final Exchange exchange) throws HandlerException {
        final Service service = exchange.getProvider();
        ServiceSecurity serviceSecurity = service.getServiceMetadata().getSecurity();
        if (serviceSecurity != null) {
            SecurityContext securityContext = _securityContextManager.getContext(exchange);
            PrivilegedExceptionAction<Void> action = new PrivilegedExceptionAction<Void>() {
                public Void run() throws Exception {
                    service.getProviderHandler().handleMessage(exchange);
                    return null;
                }
            };
            try {
                _securityProvider.runAs(serviceSecurity, securityContext, action);
            } catch (Exception e) {
                if (e instanceof PrivilegedActionException) {
                    e = ((PrivilegedActionException)e).getException();
                }
                if (e instanceof HandlerException) {
                    throw (HandlerException)e;
                } else {
                    throw new HandlerException(e);
                }
            }
        } else {
            service.getProviderHandler().handleMessage(exchange);
        }
    }

}
