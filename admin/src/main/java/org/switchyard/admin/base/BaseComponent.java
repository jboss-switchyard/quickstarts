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

package org.switchyard.admin.base;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.switchyard.admin.Component;

/**
 * BaseComponent
 * 
 * Basic implementation for Component.
 */
public class BaseComponent implements Component {

    private Map<String, String> _properties;
    private String _name;
    private Set<String> _types;

    /**
     * Create a new BaseComponent.
     * 
     * @param name the name of the component.
     * @param types the type of the component.
     * @param properties the configuration properties of this component.
     */
    public BaseComponent(String name, Collection<String> types, Map<String, String> properties) {
        _name = name;
        _types = new HashSet<String>();
        if (types != null) {
            _types.addAll(types);
        }
        _properties = new HashMap<String, String>();
        if (properties != null) {
            _properties.putAll(properties);
        }
    }

    @Override
    public Map<String, String> getProperties() {
        return _properties;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public Set<String> getTypes() {
        return _types;
    }
}
