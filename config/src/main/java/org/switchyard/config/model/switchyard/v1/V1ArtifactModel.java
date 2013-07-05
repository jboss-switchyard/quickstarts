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
package org.switchyard.config.model.switchyard.v1;

import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.switchyard.ArtifactModel;
import org.switchyard.config.model.switchyard.ArtifactsModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;

/**
 * Implementation of ArtifactModel : v1.
 */
public class V1ArtifactModel extends BaseModel implements ArtifactModel {

    /**
     * Constructs a new V1ArtifactModel.
     */
    public V1ArtifactModel() {
        super(new QName(SwitchYardModel.DEFAULT_NAMESPACE, ArtifactModel.ARTIFACT));
    }

    /**
     * Constructs a new V1ArtifactModel with the specified Configuration and Descriptor.
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1ArtifactModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArtifactsModel getArtifacts() {
        return (ArtifactsModel)getModelParent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return getModelAttribute(ArtifactModel.NAME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArtifactModel setName(String name) {
        setModelAttribute(ArtifactModel.NAME, name);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getURL() {
        return getModelAttribute(ArtifactModel.URL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArtifactModel setURL(String url) {
        setModelAttribute(ArtifactModel.URL, url);
        return this;
    }

}
