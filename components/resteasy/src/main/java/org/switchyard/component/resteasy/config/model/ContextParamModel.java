/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.switchyard.component.resteasy.config.model;

import org.switchyard.config.model.Model;

/**
 * The "contextParam" configuration model.
 */
public interface ContextParamModel extends Model {

    /** The "contextParam" name. */
    public static final String CONTEXT_PARAM = "contextParam";

    /**
     * Gets the parent contextParams model.
     * @return the parent contextParams model.
     */
    public ContextParamsModel getContextParamsModel();

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
    public ContextParamModel setName(String name);

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
    public ContextParamModel setValue(String value);

}
