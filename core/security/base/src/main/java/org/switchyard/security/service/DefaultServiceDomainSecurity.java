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
package org.switchyard.security.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.switchyard.ServiceSecurity;
import org.switchyard.security.system.SystemSecurity;

/**
 * DefaultServiceDomainSecurity.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public class DefaultServiceDomainSecurity implements ServiceDomainSecurity {

    private static final String FORMAT = DefaultServiceDomainSecurity.class.getSimpleName() + "@%s[serviceSecurities=%s, systemSecurity=%s]";

    private final Map<String, ServiceSecurity> _serviceSecurities;
    private final SystemSecurity _systemSecurity;

    /**
     * Constructs a new DefaultServiceDomainSecurity with defaults for ServiceSecurity(s) and SystemSecurity.
     */
    public DefaultServiceDomainSecurity() {
        this(null, null);
    }

    /**
     * Constructs a new DefaultServiceDomainSecurity with the specified ServiceSecurity(s) and SystemSecurity.
     * @param serviceSecurities the ServiceSecurity(s)
     * @param systemSecurity the SystemSecurity
     */
    public DefaultServiceDomainSecurity(Map<String, ServiceSecurity> serviceSecurities, SystemSecurity systemSecurity) {
        Map<String, ServiceSecurity> tmpServiceSecurities = new HashMap<String, ServiceSecurity>();
        if (serviceSecurities != null) {
            tmpServiceSecurities.putAll(serviceSecurities);
        }
        if (!tmpServiceSecurities.containsKey(ServiceSecurity.DEFAULT_NAME)) {
            tmpServiceSecurities.put(ServiceSecurity.DEFAULT_NAME, new DefaultServiceSecurity());
        }
        _serviceSecurities = Collections.unmodifiableMap(tmpServiceSecurities);
        _systemSecurity = systemSecurity != null ? systemSecurity : SystemSecurity.DEFAULT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ServiceSecurity getServiceSecurity(String name) {
        ServiceSecurity serviceSecurity = name != null ? _serviceSecurities.get(name) : null;
        if (serviceSecurity == null) {
            serviceSecurity = _serviceSecurities.get(ServiceSecurity.DEFAULT_NAME);
        }
        return serviceSecurity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SystemSecurity getSystemSecurity() {
        return _systemSecurity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format(FORMAT, System.identityHashCode(this), _serviceSecurities, _systemSecurity);
    }

}
