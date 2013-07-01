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
import java.util.Set;
import java.util.TreeSet;

import org.switchyard.common.lang.Strings;


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
        label = Strings.trimToNull(label);
        if (label != null) {
            label = label.toLowerCase();
        }
        return label;
    }

}
