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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.switchyard.ArtifactModel;
import org.switchyard.config.model.switchyard.ArtifactsModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;

/**
 * A version 1 ArtifactsModel.
 */
public class V1ArtifactsModel extends BaseModel implements ArtifactsModel {

    private List<ArtifactModel> _artifacts = new ArrayList<ArtifactModel>();

    /**
     * Constructs a new V1ArtifactsModel.
     * @param namespace namespace
     */
    public V1ArtifactsModel(String namespace) {
        super(new QName(namespace, ArtifactsModel.ARTIFACTS));
        setModelChildrenOrder(ArtifactModel.ARTIFACT);
    }

    /**
     * Constructs a new V1ArtifactsModel with the specified Configuration and Descriptor.
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1ArtifactsModel(Configuration config, Descriptor desc) {
        super(config, desc);
        for (Configuration artifact_config : config.getChildrenStartsWith(ArtifactModel.ARTIFACT)) {
            ArtifactModel artifact = (ArtifactModel)readModel(artifact_config);
            if (artifact != null) {
                _artifacts.add(artifact);
            }
        }
        setModelChildrenOrder(ArtifactModel.ARTIFACT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SwitchYardModel getSwitchYard() {
        return (SwitchYardModel)getModelParent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<ArtifactModel> getArtifacts() {
        return Collections.unmodifiableList(_artifacts);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized ArtifactsModel addArtifact(ArtifactModel artifact) {
        addChildModel(artifact);
        _artifacts.add(artifact);
        return this;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ArtifactModel getArtifact(String name) {
        ArtifactModel artifact = null;
        for (ArtifactModel a : _artifacts) {
            if (a.getName().equals(name)) {
                artifact = a;
                break;
            }
        }
        return artifact;
    }

    @Override
    public ArtifactModel removeArtifact(String artifactName) {
        ArtifactModel removed = null;
        
        for (ArtifactModel artifact : _artifacts) {
            if (artifact.getName().equals(artifactName)) {
                removed = artifact;
                _artifacts.remove(artifact);
            }
        }
        return removed;
    }

}
