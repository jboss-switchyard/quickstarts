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
package org.switchyard;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * MockContext.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class MockContext implements Context {

    private final Map<String,Property> _exchangeProperties = Collections.synchronizedMap(new HashMap<String,Property>());
    private final Map<String,Property> _messageProperties = Collections.synchronizedMap(new HashMap<String,Property>());

    public MockContext() {}
    
    private Map<String,Property> getPropertiesMap(Scope scope) {
        switch (scope) {
            case MESSAGE:
                return _messageProperties;
            case EXCHANGE:
            default:
                return _exchangeProperties;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Property getProperty(String name) {
        return getProperty(name, Scope.EXCHANGE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Property getProperty(String name, Scope scope) {
        return getPropertiesMap(scope).get(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getPropertyValue(String name) {
        Property property = getProperty(name);
        return property != null ? (T) property.getValue() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Property> getProperties() {
        return getProperties(Scope.EXCHANGE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Property> getProperties(Scope scope) {
        Set<Property> properties = new HashSet<Property>();
        for (Property property : getPropertiesMap(scope).values()) {
            properties.add(property);
        }
        return properties;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeProperty(Property property) {
        getPropertiesMap(Scope.EXCHANGE).remove(property.getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeProperties() {
        removeProperties(Scope.EXCHANGE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeProperties(Scope scope) {
        getPropertiesMap(scope).clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Property setProperty(String name, Object val) {
        return setProperty(name, val, Scope.EXCHANGE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Property setProperty(String name, Object val, Scope scope) {
        Map<String,Property> properties = getPropertiesMap(scope);
        if (val != null) {
            Property p = new MockProperty(name, val, scope);
            properties.put(name, p);
            return p;
        } else {
            properties.remove(name);
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Context setProperties(Set<Property> properties) {
        for (Property property : properties) {
            setProperty(property.getName(), property.getValue(), property.getScope());
        }
        return this;
    }

    @Override
    public void mergeInto(Context context) {
        // brute force with no bells since this is only a mock object
        for (Property property : _exchangeProperties.values()) {
            context.setProperty(property.getName(), property.getValue(), Scope.EXCHANGE);
        }
        for (Property property : _messageProperties.values()) {
            context.setProperty(property.getName(), property.getValue(), Scope.MESSAGE);
        }
    }

    @Override
    public Set<Property> getProperties(String label) {
        Set<Property> props = new HashSet<Property>();
        for (Property p : getProperties()) {
            if (p.hasLabel(label)) {
                props.add(p);
            }
        }
        return props;
    }

    @Override
    public void removeProperties(String label) {
        for (Property p : getProperties()) {
            if (p.hasLabel(label)) {
                removeProperty(p);
            }
        }
    }

}
