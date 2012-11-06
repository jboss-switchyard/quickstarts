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
package org.switchyard.security.callback;

import java.util.Map;
import java.util.Set;

import javax.security.auth.callback.CallbackHandler;

import org.switchyard.security.credential.Credential;

/**
 * SwitchYardCallbackHandler.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public abstract class SwitchYardCallbackHandler implements CallbackHandler {

    private Map<String,String> _properties;
    private Set<Credential> _credentials;

    /**
     * Gets the Properties.
     * @return the Properties
     */
    public Map<String,String> getProperties() {
        return _properties;
    }

    /**
     * Gets a property value with the specified name.
     * @param name the specified name
     * @return the property value, or null if not set
     */
    public String getProperty(String name) {
        return getProperty(name, false);
    }

    /**
     * Gets a property value with the specified name.
     * @param name the specified name
     * @param required if the property is required to have been set
     * @return the property value
     */
    public String getProperty(String name, boolean required) {
        Map<String,String> properties = getProperties();
        if (properties == null) {
            if (required) {
                throw new IllegalStateException("properties not set");
            }
        } else {
            String value = properties.get(name);
            if (value == null && required) {
                throw new IllegalStateException("property [" + name + "] not set");
            }
            return value;
        }
        return null;
    }

    /**
     * Sets the Properties.
     * @param properties the Properties
     * @return this instance (useful for chaining)
     */
    public SwitchYardCallbackHandler setProperties(Map<String,String> properties) {
        _properties = properties;
        return this;
    }

    /**
     * Gets the Credentials.
     * @return the Credentials
     */
    public Set<Credential> getCredentials() {
        return _credentials;
    }

    /**
     * Sets the Credentials.
     * @param credentials the Credentials
     * @return this instance (useful for chaining)
     */
    public SwitchYardCallbackHandler setCredentials(Set<Credential> credentials) {
        _credentials = credentials;
        return this;
    }

}
