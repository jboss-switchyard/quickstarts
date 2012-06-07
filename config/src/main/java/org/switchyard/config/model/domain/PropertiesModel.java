/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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
package org.switchyard.config.model.domain;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.switchyard.config.model.Model;

/**
 * The "properties" configuration model.
 */
public interface PropertiesModel extends Model {

    /** The "properties" name. */
    public static final String PROPERTIES = "properties";

    /**
     * Gets the child property models.
     * @return the child property models
     */
    public List<PropertyModel> getProperties();

    /**
     * Adds a child property model.
     * @param property the child property model to add
     * @return this TransformsModel (useful for chaining)
     */
    public PropertiesModel addProperty(PropertyModel property);
    
    /**
     * Fetch a property model by name.
     * @param name name of the property
     * @return property with the specified name, or null if no such property exists
     */
    public PropertyModel getProperty(String name);
    
    /**
     * Removes a child property model.
     * @param propertyName the name of the property
     * @return the removed property or null if the named property was not present
     */
    public PropertyModel removeProperty(String propertyName);

    /**
     * Converts this PropertiesModel to a Properties.
     * @return the Properties
     */
    public Properties toProperties();

    /**
     * Converts this PropertiesModel to a Map<String,String>.
     * @return the Map<String,String>
     */
    public Map<String,String> toMap();

}
