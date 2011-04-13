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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.switchyard.Property;
import org.switchyard.Scope;
import org.switchyard.io.Serialization.AccessType;
import org.switchyard.io.Serialization.Strategy;

/**
 * A simple container for a set of scoped property maps.  Properties that are 
 * put and get are done stored based on the scope of the property.
 */
@Strategy(access=AccessType.FIELD)
public class ScopedPropertyMap {
    private Map<Scope, Map<String, Property>> _props = 
        new HashMap<Scope, Map<String, Property>>();
    
    /**
     * Creates a new ScopedPropertyMap with one property map for each Scope type.
     */
    public ScopedPropertyMap() {
        for (Scope scope : Scope.values()) {
            _props.put(scope, new HashMap<String, Property>());
        }
    }
    
    /**
     * Adds a property to the map.
     * @param property property to add
     */
    public void put(Property property) {
        _props.get(property.getScope()).put(property.getName(), property);
    }
    
    /**
     * Gets a property from the map.
     * @param scope the scope of the property
     * @param name the name of the property
     * @return the property stored with the given name at the specified scope, or
     * null if no such property exists.
     */
    public Property get(Scope scope, String name) {
        return _props.get(scope).get(name);
    }
    
    /**
     * Get all properties from the specified scope.
     * @param scope scope to grab properties from
     * @return set of all properties in the specified scope
     */
    public Set<Property> get(Scope scope) {
        return new HashSet<Property>(_props.get(scope).values());
    }
    
    /**
     * Get all properties from all scopes.
     * @return a set of all properties.
     */
    public Set<Property> get() {
        HashSet<Property> allProps = new HashSet<Property>();
        for (Scope scope : _props.keySet()) {
            allProps.addAll(get(scope));
        }
        return allProps;
    }
    
    /**
     * Remove the specified property from the map.
     * @param property property to remove
     */
    public void remove(Property property) {
        _props.get(property.getScope()).remove(property.getName());
    }
    
    /**
     * Removes all properties in the specified scope.
     * @param scope scope in which all properties should be removed.
     */
    public void clear(Scope scope) {
        _props.get(scope).clear();
    }

    /**
     * Removes all properties in all scopes.
     */
    public void clear() {
        for (Map map : _props.values()) {
            map.clear();
        }
    }
    
    /**
     * Creates a new ScopedPropertyMap and copies references from all property
     * entries in this map to the new map.
     * @return new shallow copy of this property map
     */
    public ScopedPropertyMap copy() {
        ScopedPropertyMap props = new ScopedPropertyMap();
        for (Map<String, Property> scopedMap : _props.values()) {
            for (Property prop : scopedMap.values()) {
                props.put(prop);
            }
        }
        return props;
    }
}
