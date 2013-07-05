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
package org.switchyard.internal;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.switchyard.ServiceSecurity;

/**
 * DefaultServiceSecurity.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public class DefaultServiceSecurity implements ServiceSecurity {

    private static final String FORMAT = DefaultServiceSecurity.class.getSimpleName() + "[name=%s, callbackHandler=%s, properties=%s, rolesAllowed=%s, runAs=%s, securityDomain=%s]";

    private String _name;
    private Class<?> _callbackHandler;
    private Map<String,String> _properties = new LinkedHashMap<String,String>();
    private Set<String> _rolesAllowed = new LinkedHashSet<String>();
    private String _runAs;
    private String _securityDomain;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        if (_name != null) {
            return _name;
        }
        return DEFAULT_NAME;
    }

    /**
     * Sets the name.
     * @param name the name
     * @return this instance (useful for chaining)
     */
    public DefaultServiceSecurity setName(String name) {
        _name = name;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getCallbackHandler() {
        return _callbackHandler;
    }

    /**
     * Sets the CallbackHandler class. 
     * @param callbackHandler the CallbackHandler class
     * @return this instance (useful for chaining)
     */
    public DefaultServiceSecurity setCallbackHandler(Class<?> callbackHandler) {
        _callbackHandler = callbackHandler;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String,String> getProperties() {
        return _properties;
    }

    /**
     * Sets the properties.
     * @param properties the properties
     * @return this instance (useful for chaining)
     */
    public DefaultServiceSecurity setProperties(Map<String,String> properties) {
        _properties.clear();
        if (properties != null) {
            _properties.putAll(properties);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getRolesAllowed() {
        return _rolesAllowed;
    }

    /**
     * Sets the roles allowed.
     * @param rolesAllowed the roles allowed
     * @return this instance (useful for chaining)
     */
    public DefaultServiceSecurity setRolesAllowed(Set<String> rolesAllowed) {
        _rolesAllowed.clear();
        if (rolesAllowed != null) {
            _rolesAllowed.addAll(rolesAllowed);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRunAs() {
        return _runAs;
    }

    /**
     * Sets the run as.
     * @param runAs the run as
     * @return this instance (useful for chaining)
     */
    public DefaultServiceSecurity setRunAs(String runAs) {
        _runAs = runAs;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSecurityDomain() {
        if (_securityDomain != null) {
            return _securityDomain;
        }
        return DEFAULT_SECURITY_DOMAIN;
    }

    /**
     * Sets the security domain.
     * @param securityDomain the security domain
     * @return this instance (useful for chaining)
     */
    public DefaultServiceSecurity setSecurityDomain(String securityDomain) {
        _securityDomain = securityDomain;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format(FORMAT, getName(), _callbackHandler, _properties, _rolesAllowed, _runAs, getSecurityDomain());
    }

}
