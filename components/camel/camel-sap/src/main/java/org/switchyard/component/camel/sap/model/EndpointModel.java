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
package org.switchyard.component.camel.sap.model;

import org.switchyard.component.camel.common.QueryString;
import org.switchyard.config.model.Model;

/**
 * camel-sap endpoint model.
 */
public interface EndpointModel extends Model {
    /**
     * Gets schema.
     * @return schema
     */
    public String getSchema();

    /**
     * Gets name.
     * @return name
     */
    public String getName();

    /**
     * Sets name.
     * @param name name
     * @return this model (useful for chaining)
     */
    public EndpointModel setName(String name);

    /**
     * Gets base URI string.
     * @param queryString query string
     * @return base URI string
     */
    public StringBuilder createBaseURIString(QueryString queryString);
}
