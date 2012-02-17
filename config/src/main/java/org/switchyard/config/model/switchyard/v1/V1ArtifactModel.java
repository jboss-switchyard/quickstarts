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
