/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
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
