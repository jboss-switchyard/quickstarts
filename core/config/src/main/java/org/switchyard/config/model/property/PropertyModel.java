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

import org.switchyard.config.model.Model;

/**
 * The "property" configuration model.
 */
public interface PropertyModel extends Model {

    /** The "property" name. */
    public static final String PROPERTY = "property";

    /**
     * Gets the parent properties model.
     * @return the parent properties model.
     */
    public PropertiesModel getProperties();

    /**
     * Gets the name attribute.
     * @return the name attribute
     */
    public String getName();

    /**
     * Sets the name attribute.
     * @param name the name attribute
     * @return this PropertyModel (useful for chaining)
     */
    public PropertyModel setName(String name);

    /**
     * Gets the value attribute.
     * @return the value attribute
     */
    public String getValue();

    /**
     * Sets the value attribute.
     * @param value the value attribute
     * @return this PropertyModel (useful for chaining)
     */
    public PropertyModel setValue(String value);

}
