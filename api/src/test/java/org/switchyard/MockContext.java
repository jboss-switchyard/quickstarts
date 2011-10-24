/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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
    private final Map<String,Property> _inProperties = Collections.synchronizedMap(new HashMap<String,Property>());
    private final Map<String,Property> _outProperties = Collections.synchronizedMap(new HashMap<String,Property>());

    public MockContext() {}

    private Map<String,Property> getPropertiesMap(Scope scope) {
        switch (scope) {
            case IN:
                return _inProperties;
            case OUT:
                return _outProperties;
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
    public Object getPropertyValue(String name) {
        Property property = getProperty(name);
        return property != null ? property.getValue() : null;
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
    public Context setProperty(String name, Object val) {
        setProperty(name, val, Scope.EXCHANGE);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Context setProperty(String name, Object val, Scope scope) {
        Map<String,Property> properties = getPropertiesMap(scope);
        if (val != null) {
            properties.put(name, new MockProperty(name, val, scope));
        } else {
            properties.remove(name);
        }
        return this;
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

}
