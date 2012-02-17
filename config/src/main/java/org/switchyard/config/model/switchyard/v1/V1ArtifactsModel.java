/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
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
     */
    public V1ArtifactsModel() {
        super(new QName(SwitchYardModel.DEFAULT_NAMESPACE, ArtifactsModel.ARTIFACTS));
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
