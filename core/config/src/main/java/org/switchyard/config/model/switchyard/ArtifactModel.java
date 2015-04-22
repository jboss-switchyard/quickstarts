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
package org.switchyard.config.model.switchyard;

import org.switchyard.config.model.Model;

/**
 * The "artifact" configuration model.
 */
public interface ArtifactModel extends Model {

    /** The "artifact" name. */
    public static final String ARTIFACT = "artifact";

    /** The "name" name. */
    public static final String NAME = "name";

    /** The "url" name. */
    public static final String URL = "url";

    /**
     * Gets the name attribute.
     * @return the name attribute
     */
    public String getName();

    /**
     * Sets the name attribute.
     * @param name the name attribute
     * @return this ArtifactModel (useful for chaining)
     */
    public ArtifactModel setName(String name);

    /**
     * Gets the url attribute.
     * @return the url attribute
     */
    public String getURL();

    /**
     * Sets the url attribute.
     * @param url the url attribute
     * @return this ArtifactModel (useful for chaining)
     */
    public ArtifactModel setURL(String url);
    
    /**
     * Gets the parent artifacts model.
     * @return the parent artifacts model.
     */
    public ArtifactsModel getArtifacts();


}
