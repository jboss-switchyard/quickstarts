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

import java.util.Set;

/**
 * Holds contextual information around the exchange of messages between a
 * service consumer and provider.
 */
public interface Context {

    /**
     * Merges the properties in this Context instance into the specified Context instance, 
     * adding missing values and overwriting existing values.  A shallow copy of 
     * context property values is performed, so values that are references will be shared
     * between the two contexts.
     * <br><br>
     * Properties with a label of Labels.TRANSIENT are not included in the merge.
     * @param context context properties are copied from this context
     */
    void mergeInto(Context context);

    /**
     * Retrieves the named property within this context, regardless
     * of the scope.
     * @param name property name
     * @return value of the property in this context or null if the property
     * does not exist
     */
    Property getProperty(String name);
    
    /**
     * Retrieves the value of the named property with the given scope in this
     * context.
     * @param name property name
     * @param scope property scope
     * @return value of the property in this context or null if the property
     * does not exist
     */
    Property getProperty(String name, Scope scope);
    
    /**
     * Retrieves the value of the named property within this context, regardless
     * of the scope. This is a convenience method equivalent to 
     * <code> getProperty(name).getValue() </code>.
     * @param name property name
     * @return value of the property in this context or null if the property
     * does not exist
     * @param <T> Expected type of property.
     */
    <T> T getPropertyValue(String name);
    
    /**
     * Returns the set of all properties in this context. The returned
     * set is a shallow copy of the properties set in this context, so
     * modifications to the map are not reflected in the underlying context.
     * @return set containing all properties in this context.  If there are no
     * properties in this context, an empty set is returned.
     */
    Set<Property> getProperties();
    
    /**
     * Returns the set of all properties in this context in the specified scope. The 
     * returned set is a shallow copy of the properties set in this context, so
     * modifications to the map are not reflected in the underlying context.
     * @param scope scope from which properties will be retrieved
     * @return set containing all properties in this context for the specified
     * scope.  If there are no properties in the scope, an empty set is returned.
     */
    Set<Property> getProperties(Scope scope);
    
    /**
     * Get all properties with a given label.
     * @param label the label each property must have
     * @return set of properties with the specified label
     */
    Set<Property> getProperties(String label);

    /**
     * Removes the named property from this context.
     * @param property property to remove
     * not exist
     */
    void removeProperty(Property property);
    
    /**
     * Removes all properties from this context.
     */
    void removeProperties();
    
    /**
     * Removes properties from the specified scope from this context.
     * @param scope scope of the properties to remove
     */
    void removeProperties(Scope scope);
    
    /**
     * Remove all properties with a given label.
     * @param label the label each property must have
     */
    void removeProperties(String label);

    /**
     * Sets the named context property with the specified value.  If the context
     * property does not exist already, it is added.  If the property already
     * exists, the value of the property is replaced. 
     * @param name name of the property to set
     * @param val the value to set for the property
     * @return a reference to the set Property
     */
    Property setProperty(String name, Object val);
    
    /**
     * Sets the named context property with the specified value in a specific
     * scope.  If the context property does not exist already, it is added.  
     * If the property already exists, the value of the property is replaced.  
     * If the specified value is null, the property is removed from the context.
     * @param name name of the property to set
     * @param val the value to set for the property
     * @param scope scope of the property to add
     * @return a reference to the set Property
     */
    Property setProperty(String name, Object val, Scope scope);
    
    /**
     * Adds the set of properties to this context.
     * @param properties set of properties to add
     * @return a reference to the updated Context
     */
    Context setProperties(Set<Property> properties);
}
