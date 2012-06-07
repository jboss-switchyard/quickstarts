/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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

import java.util.Map;

import org.switchyard.ServiceSecurity;

/**
 * DefaultServiceSecurity.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class DefaultServiceSecurity implements ServiceSecurity {

    private String _moduleName;
    private Class<?> _callbackHandler;
    /*
    private String _runAs;
    private Set<String> _rolesAllowed;
    */
    private Map<String,String> _properties;

    /**
     * Constructs a DefaultServiceSecurity.
     */
    public DefaultServiceSecurity() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public String getModuleName() {
        return _moduleName;
    }

    /**
     * Sets the module name.
     * @param moduleName the module name
     * @return this instance (useful for chaining)
     */
    public DefaultServiceSecurity setModuleName(String moduleName) {
        _moduleName = moduleName;
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


    /*
    @Override
    public String getRunAs() {
        return _runAs;
    }

    public DefaultServiceSecurity setRunAs(String runAs) {
        _runAs = runAs;
        return this;
    }

    @Override
    public Set<String> getRolesAllowed() {
        return _rolesAllowed;
    }

    public DefaultServiceSecurity setRolesAllowed(Set<String> rolesAllowed) {
        _rolesAllowed = rolesAllowed;
        return this;
    }
    */

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
        _properties = properties;
        return this;
    }

}
