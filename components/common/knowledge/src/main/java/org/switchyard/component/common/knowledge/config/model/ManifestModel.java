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
package org.switchyard.component.common.knowledge.config.model;

import org.switchyard.config.model.Model;
import org.switchyard.config.model.resource.ResourcesModel;

/**
 * A Manifest Model.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public interface ManifestModel extends Model {

    /** The "manifest" name. */
    public static final String MANIFEST = "manifest";

    /**
     * Gets the child container model.
     * @return the child container model
     */
    public ContainerModel getContainer();

    /**
     * Sets the child container model.
     * @param container the child container model
     * @return this ManifestModel (useful for chaining)
     */
    public ManifestModel setContainer(ContainerModel container);

    /**
     * Gets the child resources model.
     * @return the child resources model
     */
    public ResourcesModel getResources();

    /**
     * Sets the child resources model.
     * @param resources the child resources model
     * @return this ManifestModel (useful for chaining)
     */
    public ManifestModel setResources(ResourcesModel resources);

    /**
     * Gets the child remote model.
     * @return the child remote model
     */
    public RemoteModel getRemote();

    /**
     * Sets the child remote model.
     * @param remote the child remote model
     * @return this ManifestModel (useful for chaining)
     */
    public ManifestModel setRemote(RemoteModel remote);

}
