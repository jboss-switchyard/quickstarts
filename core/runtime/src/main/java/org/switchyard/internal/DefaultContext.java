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

import static org.switchyard.internal.ContextUtil.checkScope;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.switchyard.Context;
import org.switchyard.Property;
import org.switchyard.Scope;
import org.switchyard.serial.graph.AccessType;
import org.switchyard.serial.graph.Strategy;

/**
 * Base context implementation.
 */
@Strategy(access=AccessType.FIELD)
public class DefaultContext implements Context {

    private Scope _scope;
    private final Map<String, Property> _properties;

    /**
     * Create a new DefaultContext instance using the specified property map.
     * @param defaultScope Scope handled by context.
     * @param properties context properties
     */
    public DefaultContext(Scope defaultScope, Map<String, Property> properties) {
        _scope = defaultScope;
        _properties = properties;
    }

    /**
     * Creates new context with given scope.
     * 
     * @param defaultScope Scope handled by context.
     */
    public DefaultContext(Scope defaultScope) {
        this(defaultScope, new HashMap<String, Property>());
    }

    /**
     * Creates new context with exchange scope.
     */
    public DefaultContext() {
        this(Scope.EXCHANGE);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getPropertyValue(String name) {
       Property prop = _properties.get(name);
       if (prop != null) {
           return (T) prop.getValue();
       }
       return null;
    }

    @Override
    public void removeProperties() {
        _properties.clear();
    }

    @Override
    public Context setProperties(Set<Property> properties) {
        for (Property p : properties) {
            _properties.put(p.getName(), p);
        }
        return this;
    }

    @Override
    public Set<Property> getProperties() {
        return new HashSet<Property>(_properties.values());
    }

    @Override
    public void removeProperty(Property property) {
        checkScope(_scope, property.getScope());
        _properties.remove(property.getName());
    }

    @Override
    public Property getProperty(String name) {
        return _properties.get(name);
    }

    @Override
    public Property setProperty(String name, Object val) {
        Property p = new ContextProperty(name, _scope, val);
        _properties.put(p.getName(), p);
        return p;
    }

    @Override
    public void mergeInto(Context context) {
        ContextUtil.copy(this, context);
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

    @Override
    public Property getProperty(String name, Scope scope) {
        checkScope(_scope, scope);
        return _properties.get(name);
    }

    @Override
    public Set<Property> getProperties(Scope scope) {
        checkScope(_scope, scope);
        return getProperties();
    }

    @Override
    public void removeProperties(Scope scope) {
        checkScope(_scope, scope);
        _properties.clear();
    }

    @Override
    public Property setProperty(String name, Object val, Scope scope) {
        checkScope(_scope, scope);
        ContextProperty value = new ContextProperty(name, scope, val);
        _properties.put(name, value);
        return value;
    }

    /**
     * Allows to specify default scope of this context.
     * 
     * @param scope Scope of this context instance.
     */
    public void setScope(Scope scope) {
        this._scope = scope;
    }

    @Override
    public String toString() {
        return _properties.toString();
    }
}
