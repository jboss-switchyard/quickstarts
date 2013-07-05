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

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import org.switchyard.Property;
import org.switchyard.Scope;
import org.switchyard.common.lang.Strings;
import org.switchyard.internal.ContextProperty.ContextPropertyFactory;
import org.switchyard.serial.graph.AccessType;
import org.switchyard.serial.graph.BaseFactory;
import org.switchyard.serial.graph.Strategy;

/**
 * Serializable implementation of <code>Context</code>.
 */
@Strategy(access=AccessType.FIELD, factory=ContextPropertyFactory.class)
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
    public Property addLabels(Set<String> labels) {
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
     * The serialization factory for context properties.
     */
    public static final class ContextPropertyFactory extends BaseFactory<ContextProperty> {
        /**
         * {@inheritDoc}
         */
        @Override
        public ContextProperty create(Class<ContextProperty> type) {
            return new ContextProperty();
        }
    }
}
