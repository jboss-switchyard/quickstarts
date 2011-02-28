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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.switchyard.Context;

/**
 * Base context implementation.
 */
public class DefaultContext implements Context {
    private final ConcurrentHashMap<String, Object> _properties =
        new ConcurrentHashMap<String, Object>();

    @Override
    public Object getProperty(final String name) {
        return _properties.get(name);
    }

    @Override
    public Map<String, Object> getProperties() {
        // create a shallow copy to prevent against direct modification of
        // underlying context map
        return new HashMap<String, Object>(_properties);
    }

    @Override
    public boolean hasProperty(final String name) {
        return _properties.containsKey(name);
    }

    @Override
    public Object removeProperty(final String name) {
        return _properties.remove(name);
    }

    @Override
    public void setProperty(final String name, final Object val) {
        if (name != null) {
            if (val != null) {
                _properties.put(name, val);
            } else {
                _properties.remove(name);
            }
        }
    }
}
