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
package org.switchyard.config.model.property;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.switchyard.common.property.PropertyResolver;
import org.switchyard.config.model.Model;

/**
 * The "properties" configuration model.
 */
public interface PropertiesModel extends Model, PropertyResolver {

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
     * @return this PropertiesModel (useful for chaining)
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
