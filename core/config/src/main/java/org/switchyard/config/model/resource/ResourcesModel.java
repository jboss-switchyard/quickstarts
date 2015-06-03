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
package org.switchyard.config.model.resource;

import java.util.List;

import org.switchyard.config.model.Model;

/**
 * The "resources" configuration model.
 */
public interface ResourcesModel extends Model {

    /** The "resources" name. */
    public static final String RESOURCES = "resources";

    /**
     * Gets the child resource models.
     * @return the child resource models
     */
    public List<ResourceModel> getResources();

    /**
     * Adds a child resource model.
     * @param resource the child resource model
     * @return this ResourcesModel (useful for chaining)
     */
    public ResourcesModel addResource(ResourceModel resource);

}
