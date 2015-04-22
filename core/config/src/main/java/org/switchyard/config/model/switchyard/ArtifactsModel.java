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

import java.util.List;

import org.switchyard.config.model.Model;
/**
 * The "artifacts" configuration model.
 */
public interface ArtifactsModel extends Model {

    /** The "artifacts" name. */
    public static final String ARTIFACTS = "artifacts";

    /**
     * Gets the parent switchyard model.
     * @return the parent switchyard model
     */
    public SwitchYardModel getSwitchYard();

    /**
     * Gets the child artifact models.
     * @return the child artifact models
     */
    public List<ArtifactModel> getArtifacts();

    /**
     * Adds a child artifact model.
     * @param artifact the child artifact model to add
     * @return this ArtifactsModel (useful for chaining)
     */
    public ArtifactsModel addArtifact(ArtifactModel artifact);
    
    /**
     * Fetch a artifact model by name.
     * @param name name of the artifact
     * @return artifact with the specified name, or null if no such artifact exists
     */
    public ArtifactModel getArtifact(String name);
    
    /**
     * Removes a child artifact model.
     * @param artifactName the name of the artifact
     * @return the removed artifact or null if the named artifact was not present
     */
    public ArtifactModel removeArtifact(String artifactName);
}
