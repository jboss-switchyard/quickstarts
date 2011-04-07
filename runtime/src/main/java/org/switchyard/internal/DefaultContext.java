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

package org.switchyard.internal;

import java.util.Set;

import org.switchyard.Context;
import org.switchyard.Property;
import org.switchyard.Scope;
import org.switchyard.io.Serialization.AccessType;
import org.switchyard.io.Serialization.CoverageType;
import org.switchyard.io.Serialization.Strategy;

/**
 * Base context implementation.
 */
@Strategy(access=AccessType.FIELD, coverage=CoverageType.INCLUSIVE)
public class DefaultContext implements Context {
    
    private ScopedPropertyMap _properties;
    
    /**
     * Create a new DefaultContext instance.
     */
    public DefaultContext() {
        _properties = new ScopedPropertyMap();
    }
    
    /**
     * Create a new DefaultContext instance using the specified property map.
     * @param properties context properties
     */
    DefaultContext(ScopedPropertyMap properties) {
        _properties = properties;
    }

    @Override
    public Property getProperty(String name, Scope scope) {
        return _properties.get(scope, name);
    }

    @Override
    public Object getPropertyValue(String name) {
       Property prop = _properties.get(Scope.EXCHANGE, name);
       if (prop != null) {
           return prop.getValue();
       } else {
           return null;
       }
    }

    @Override
    public void removeProperties() {
        _properties.clear();
    }

    @Override
    public void removeProperties(Scope scope) {
        _properties.clear(scope);
    }

    @Override
    public Context setProperties(Set<Property> properties) {
        for (Property p : properties) {
            _properties.put(p);
        }
        return this;
    }

    @Override
    public Context setProperty(String name, Object val, Scope scope) {
        _properties.put(new ContextProperty(name, scope, val));
        return this;
    }

    @Override
    public Set<Property> getProperties() {
        return _properties.get();
    }
    
    @Override
    public Set<Property> getProperties(Scope scope) {
        return _properties.get(scope);
    }

    @Override
    public void removeProperty(Property property) {
        _properties.remove(property);
    }

    @Override
    public Property getProperty(String name) {
        return _properties.get(Scope.EXCHANGE, name);
    }

    @Override
    public Context setProperty(String name, Object val) {
        _properties.put(new ContextProperty(name, Scope.EXCHANGE, val));
        return this;
    }
    
    /**
     * Provides a shallow copy of the context so that additions and removals
     * to the copied reference are not reflected in the original (and vice
     * versa, natch).
     * @return shallow copy of this context
     */
    public DefaultContext copy() {
        return new DefaultContext(_properties.copy());
    }
}
