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
package org.switchyard.security.callback.handler;

import java.util.Map;
import java.util.Set;

import javax.security.auth.callback.CallbackHandler;

import org.switchyard.security.BaseSecurityMessages;
import org.switchyard.security.credential.Credential;

/**
 * SwitchYardCallbackHandler.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public abstract class SwitchYardCallbackHandler implements CallbackHandler {

    private Map<String, String> _properties;
    private Set<Credential> _credentials;

    /**
     * Gets the Properties.
     * @return the Properties
     */
    public Map<String, String> getProperties() {
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
        Map<String, String> properties = getProperties();
        if (properties == null) {
            if (required) {
                throw BaseSecurityMessages.MESSAGES.propertiesNotSet();
            }
        } else {
            String value = properties.get(name);
            if (value == null && required) {
                throw BaseSecurityMessages.MESSAGES.propertyNotSet(name);
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
    public SwitchYardCallbackHandler setProperties(Map<String, String> properties) {
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
