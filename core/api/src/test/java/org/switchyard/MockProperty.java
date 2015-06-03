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
import java.util.Set;
import java.util.TreeSet;


/**
 * MockProperty.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class MockProperty implements Property {

    private final String _name;
    private final Object _value;
    private final Scope _scope;
    private Set<String> _labels = Collections.synchronizedSet(new TreeSet<String>());

    public MockProperty(String name, Object value, Scope scope) {
        _name = name;
        _value = value;
        _scope = scope;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return _name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getValue() {
        return _value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Scope getScope() {
        return _scope;
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
        if (label != null) {
            label.trim();
            label = label.toLowerCase();
        }
        return label;
    }

}
