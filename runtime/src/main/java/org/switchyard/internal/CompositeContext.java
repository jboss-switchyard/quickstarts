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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.switchyard.Context;
import org.switchyard.Property;
import org.switchyard.Scope;

/**
 * Composite context keep multiple scopes maped to context instances.
 */
public class CompositeContext implements Context {

    private Map<Scope, Context> _contexts = new HashMap<Scope, Context>();

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
    public Context copy() {
        CompositeContext ctx = new CompositeContext();
        for (Entry<Scope, Context> entry : _contexts.entrySet()) {
            ctx.setContext(entry.getKey(), entry.getValue().copy());
        }
        return ctx;
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
