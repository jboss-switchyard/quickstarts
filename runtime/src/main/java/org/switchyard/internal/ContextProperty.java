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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.switchyard.Property;
import org.switchyard.Scope;
import org.switchyard.common.lang.Strings;
import org.switchyard.internal.ContextProperty.ContextPropertyMapper;
import org.switchyard.serial.map.Mappable;
import org.switchyard.serial.map.Mapper;

/**
 * Serializable implementation of <code>Context</code>.
 */
@Mappable(ContextPropertyMapper.class)
public class ContextProperty implements Property {
    
    private String _name;
    private Scope _scope;
    private Object _value;
    private Set<String> _labels = Collections.synchronizedSet(new TreeSet<String>());
    
    // Private ctor used for internal serialization only
    private ContextProperty() {
        
    }
    
    ContextProperty(String name, Scope scope, Object value) {
        if (name == null || scope == null) {
            throw new IllegalArgumentException("Property name and scope must not be null!");
        }
        
        _name = name;
        _scope = scope;
        _value = value;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public Scope getScope() {
        return _scope;
    }

    @Override
    public Object getValue() {
        return _value;
    }
    
    /**
     * Set the value of a context property.
     * @param value value to set
     */
    public void setValue(Object value) {
        _value = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getLabels() {
        return Collections.unmodifiableSet(_labels);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Property addLabels(String... labels) {
        for (String label : labels) {
            label = normalizeLabel(label);
            if (label != null) {
                _labels.add(label);
            }
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Property removeLabels(String... labels) {
        for (String label : labels) {
            label = normalizeLabel(label);
            if (label != null) {
                _labels.remove(label);
            }
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasLabel(String label) {
        label = normalizeLabel(label);
        return label != null && _labels.contains(label);
    }

    private String normalizeLabel(String label) {
        label = Strings.trimToNull(label);
        if (label != null) {
            label = label.toLowerCase();
        }
        return label;
    }

    // NOTE: Labels are intentionally not part of equals(Object) or hashCode().
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ContextProperty)) {
            return false;
        }
        
        ContextProperty comp = (ContextProperty)obj;
        return _name.equals(comp.getName()) 
                && _scope.equals(comp.getScope())
                && (_value == null ? comp.getValue() == null : _value.equals(comp.getValue()));
    }

    // NOTE: Labels are intentionally not part of equals(Object) or hashCode().
    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + _name.hashCode();
        hash = hash * 31 + _scope.hashCode();
        hash = hash * 31 + (_value != null ? _value.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        String labels = Strings.concat(", ", _labels.toArray(new String[_labels.size()]));
        labels = "{" + (labels != null ? labels : "") + "}";
        return ("[name=" + _name + ", scope=" + _scope + ", value=" + _value + ", labels=" + labels + "]");
    }
    
    /**
     * A ContextProperty Mapper.
     */
    public static final class ContextPropertyMapper implements Mapper<ContextProperty> {
        @Override
        public Map<String, Object> toMap(ContextProperty obj) {
            Map<String, Object> map = new LinkedHashMap<String, Object>();
            map.put("name", obj._name);
            map.put("scope", obj._scope);
            map.put("value", obj._value);
            map.put("labels", new TreeSet<String>(obj._labels));
            return map;
        }
        @Override
        public ContextProperty toObject(Map<String, Object> map) {
            ContextProperty obj = new ContextProperty();
            obj._name = (String)map.get("name");
            obj._scope = (Scope)map.get("scope");
            obj._value = map.get("value");
            @SuppressWarnings("unchecked")
            Set<String> labels = (Set<String>)map.get("labels");
            if (labels != null) {
                obj._labels.addAll(labels);
            }
            return obj;
        }
    }
}
