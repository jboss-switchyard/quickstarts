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
package org.switchyard.component.common.knowledge.expression;

import static org.switchyard.Scope.MESSAGE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.switchyard.Context;
import org.switchyard.Property;
import org.switchyard.Scope;

/**
 * A Context that is also a Map.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class ContextMap implements Context, Map<String, Object> {

    private final Context _context;
    private final Scope _scope;

    /**
     * Wraps the specified context with Scope.IN for map operations.
     * @param context the specified context
     */
    public ContextMap(Context context) {
        this(context, MESSAGE);
    }

    /**
     * Wraps the specified context with the specified scope for map operations.
     * @param context the specified context
     * @param scope the specified scope for map operations
     */
    public ContextMap(Context context, Scope scope) {
        _context = context;
        _scope = scope;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Property getProperty(String name) {
        return _context.getProperty(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Property getProperty(String name, Scope scope) {
        return _context.getProperty(name, scope);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T getPropertyValue(String name) {
        return _context.getPropertyValue(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Property> getProperties() {
        return _context.getProperties();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Property> getProperties(Scope scope) {
        return _context.getProperties(scope);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeProperty(Property property) {
        _context.removeProperty(property);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeProperties() {
        _context.removeProperties();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeProperties(Scope scope) {
        _context.removeProperties(scope);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Property setProperty(String name, Object val) {
        return _context.setProperty(name, val);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Property setProperty(String name, Object val, Scope scope) {
        return _context.setProperty(name, val, scope);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Context setProperties(Set<Property> properties) {
        _context.setProperties(properties);
        return this;
    }

    @Override
    public Context copy() {
        return _context.copy();
    }

    @Override
    public Set<Property> getProperties(String label) {
        return _context.getProperties(label);
    }

    @Override
    public void removeProperties(String label) {
        _context.removeProperties(label);
    }

    /**
     * {@inheritDoc}
     */   
    @Override
    public int size() {
        return getProperties(_scope).size();
    }

    /**
     * {@inheritDoc}
     */   
    @Override
    public boolean isEmpty() {
        return getProperties(_scope).isEmpty();
    }

    /**
     * {@inheritDoc}
     */   
    @Override
    public boolean containsKey(Object key) {
        return key != null ? getProperty(key.toString(), _scope) != null : false;
    }

    /**
     * {@inheritDoc}
     */   
    @Override
    public boolean containsValue(Object value) {
        if (value != null) {
            for (Property property : getProperties(_scope)) {
                if (value.equals(property.getValue())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */   
    @Override
    public Object get(Object key) {
        if (key != null) {
            Property property = getProperty(key.toString(), _scope);
            if (property != null) {
                return property.getValue();
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */   
    @Override
    public Object put(String key, Object value) {
        return setProperty(key, value, _scope);
    }

    /**
     * {@inheritDoc}
     */   
    @Override
    public Object remove(Object key) {
        if (key != null) {
            Property property = getProperty(key.toString(), _scope);
            if (property != null) {
                removeProperty(property);
                return property.getValue();
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */   
    @Override
    public void putAll(Map<? extends String, ? extends Object> m) {
        for (Entry<? extends String, ? extends Object> entry : m.entrySet()) {
            setProperty(entry.getKey(), entry.getValue(), _scope);
        }
    }

    /**
     * {@inheritDoc}
     */   
    @Override
    public void clear() {
        removeProperties(_scope);
    }

    /**
     * {@inheritDoc}
     */   
    @Override
    public Set<String> keySet() {
        Set<String> keySet = new HashSet<String>();
        for (Property property : getProperties(_scope)) {
            keySet.add(property.getName());
        }
        return keySet;
    }

    /**
     * {@inheritDoc}
     */   
    @Override
    public Collection<Object> values() {
        Collection<Object> values = new ArrayList<Object>();
        for (Property property : getProperties(_scope)) {
            values.add(property.getValue());
        }
        return values;
    }

    /**
     * {@inheritDoc}
     */   
    @Override
    public Set<Entry<String, Object>> entrySet() {
        Map<String, Object> entries = new HashMap<String, Object>();
        for (Property property : getProperties(_scope)) {
            entries.put(property.getName(), property.getValue());
        }
        return entries.entrySet();
    }

}
