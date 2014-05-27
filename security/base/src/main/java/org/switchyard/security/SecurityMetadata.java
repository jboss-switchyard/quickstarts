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
package org.switchyard.security;

import org.switchyard.Exchange;
import org.switchyard.Service;
import org.switchyard.ServiceDomain;
import org.switchyard.ServiceReference;
import org.switchyard.ServiceSecurity;

/**
 * SecurityMetadata.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
public final class SecurityMetadata {

    private final Source _source;
    private final ServiceDomain _serviceDomain;
    private final ServiceSecurity _serviceSecurity;

    private SecurityMetadata(Source source, ServiceDomain serviceDomain, ServiceSecurity serviceSecurity) {
        _source = source;
        _serviceDomain = serviceDomain;
        _serviceSecurity = serviceSecurity;
    }

    /**
     * If the source is the provider Service.
     * @return if true
     */
    public boolean isProvider() {
        return _source == Source.PROVIDER;
    }

    /**
     * If the source is the consumer ServiceReference.
     * @return if true
     */
    public boolean isConsumer() {
        return _source == Source.CONSUMER;
    }

    /**
     * Gets the ServiceDomain.
     * @return the ServiceDomain
     */
    public ServiceDomain getServiceDomain() {
        return _serviceDomain;
    }

    /**
     * Gets the ServiceSecurity.
     * @return the ServiceSecurity
     */
    public ServiceSecurity getServiceSecurity() {
        return _serviceSecurity;
    }

    /**
     * Gets the ServiceDomain from the Exchange.
     * @param exchange the Exchange
     * @return the ServiceDomain
     */
    public static final ServiceDomain getServiceDomain(Exchange exchange) {
        return getSecurityMetadata(exchange).getServiceDomain();
    }

    /**
     * Gets the ServiceSecurity from the Exchange.
     * @param exchange the Exchange
     * @return the ServiceSecurity
     */
    public static final ServiceSecurity getServiceSecurity(Exchange exchange) {
        return getSecurityMetadata(exchange).getServiceSecurity();
    }

    /**
     * Gets the SecurityMetadata from the Exchange.
     * @param exchange the Exchange
     * @return the SecurityMetadata
     */
    public static final SecurityMetadata getSecurityMetadata(Exchange exchange) {
        Source source = null;
        ServiceDomain serviceDomain = null;
        ServiceSecurity serviceSecurity = null;
        Service service = exchange.getProvider();
        if (service != null) {
            source = Source.PROVIDER;
            serviceDomain = service.getDomain();
            serviceSecurity = service.getServiceMetadata().getSecurity();
        }
        if (serviceSecurity == null) {
            ServiceReference serviceReference = exchange.getConsumer();
            if (serviceReference != null) {
                source = Source.CONSUMER;
                serviceDomain = serviceReference.getDomain();
                serviceSecurity = serviceReference.getServiceMetadata().getSecurity();
            }
        }
        return new SecurityMetadata(source, serviceDomain, serviceSecurity);
    }

    private static enum Source {
        PROVIDER,
        CONSUMER;
    }

}
