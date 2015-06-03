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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.switchyard.Context;
import org.switchyard.Property;
import org.switchyard.Scope;
import org.switchyard.serial.graph.AccessType;
import org.switchyard.serial.graph.Strategy;

/**
 * Composite context holds multiple scopes mapped to context instances.
 */
@Strategy(access=AccessType.FIELD)
public class CompositeContext implements Context {

    private Map<Scope, Context> _contexts = new HashMap<Scope, Context>();
    
    /**
     * Create a new composite context with empty EXCHANGE and MESSAGE contexts.
     */
    public CompositeContext() {
        _contexts.put(Scope.EXCHANGE, new DefaultContext(Scope.EXCHANGE));
        _contexts.put(Scope.MESSAGE, new DefaultContext(Scope.MESSAGE));
    }

    /**
     * Attach given context to specified scope.
     * 
     * @param scope Scope of context.
     * @param context Context instance.
     */
    public void setContext(Scope scope, Context context) {
        _contexts.put(scope, context);
    }

    @Override
    public void mergeInto(Context context) {
        for (Entry<Scope, Context> entry : _contexts.entrySet()) {
            entry.getValue().mergeInto(context);
        }
    }

    @Override
    public Property getProperty(String name) {
        Property property = getProperty(name, Scope.MESSAGE);
        return property == null ? getProperty(name, Scope.EXCHANGE) : property;
    }

    @Override
    public Property getProperty(String name, Scope scope) {
        if (!_contexts.containsKey(scope)) {
            return null;
        }
        return _contexts.get(scope).getProperty(name);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getPropertyValue(String name) {
        Property property = getProperty(name);
        return property == null ? null : (T) property.getValue();
    }

    @Override
    public Set<Property> getProperties() {
        HashSet<Property> properties = new HashSet<Property>();
        for (Context ctx : _contexts.values()) {
            properties.addAll(ctx.getProperties());
        }
        return properties;
    }

    @Override
    public Set<Property> getProperties(Scope scope) {
        if (_contexts.containsKey(scope)) {
            return _contexts.get(scope).getProperties();
        }
        return null;
    }

    @Override
    public Set<Property> getProperties(String label) {
        HashSet<Property> properties = new HashSet<Property>();
        for (Context ctx : _contexts.values()) {
            properties.addAll(ctx.getProperties(label));
        }
        return properties;
    }

    @Override
    public void removeProperty(Property property) {
        for (Entry<Scope, Context> entry : _contexts.entrySet()) {
            if (entry.getKey() == property.getScope()) {
                entry.getValue().removeProperty(property);
            }
        }
    }

    @Override
    public void removeProperties() {
        for (Context ctx : _contexts.values()) {
            ctx.removeProperties();
        }
    }

    @Override
    public void removeProperties(Scope scope) {
        if (_contexts.containsKey(scope)) {
            _contexts.get(scope).removeProperties();
        }
    }

    @Override
    public void removeProperties(String label) {
        for (Context ctx : _contexts.values()) {
            ctx.removeProperties(label);
        }
    }

    @Override
    public Property setProperty(String name, Object val) {
        return setProperty(name, val, Scope.MESSAGE);
    }

    @Override
    public Property setProperty(String name, Object val, Scope scope) {
        if (_contexts.containsKey(scope)) {
            return _contexts.get(scope).setProperty(name, val);
        }
        throw new IllegalArgumentException(scope.name());
    }

    @Override
    public Context setProperties(Set<Property> properties) {
        for (Property property : properties) {
            setProperty(property.getName(), property.getValue(), property.getScope());
        }
        return this;
    }

}
