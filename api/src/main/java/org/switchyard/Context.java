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

import java.util.Map;

/**
 * Holds contextual information around the exchange of messages between a
 * service consumer and provider.  There is a distinct {@code Context} instance
 * for each context {@link Scope}.
 */
public interface Context {

    /**
     * Retrieves the value of the named property within this context.
     * @param name property name
     * @return value of the property in this context or null if the property
     * does not exist
     */
    Object getProperty(String name);

    /**
     * Returns a map containing all properties in this context. The returned
     * map is a shallow copy of the property set in this context, so
     * modifications to the map are not reflected in the underlying context.
     * @return map containing all properties in this context.  If there are no
     * properties in this context, an empty map is returned.
     */
    Map<String, Object> getProperties();

    /**
     * Test for whether the named property is present in this context.
     * @param name name of the context property
     * @return true if the property exists, false otherwise
     */
    boolean hasProperty(String name);

    /**
     * Removes the named property from this context.
     * @param name name of the property to remove
     * @return the value of the removed property or null if the property did
     * not exist
     */
    Object removeProperty(String name);

    /**
     * Sets the named context property with the specified value.  If the context
     * property does not exist already, it is added.  If the property already
     * exists, the value of the property is replaced.  If the specified value
     * is null, the property is removed from the context.
     * @param name name of the property to set
     * @param val the value to set for the property
     */
    void setProperty(String name, Object val);
}
