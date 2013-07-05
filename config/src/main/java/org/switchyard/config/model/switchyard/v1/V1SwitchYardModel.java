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

import org.switchyard.common.property.CompoundPropertyResolver;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseNamedModel;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.composite.CompositeModel;
import org.switchyard.config.model.domain.DomainModel;
import org.switchyard.config.model.property.PropertiesModel;
import org.switchyard.config.model.switchyard.ArtifactsModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.config.model.transform.TransformsModel;
import org.switchyard.config.model.validate.ValidatesModel;

/**
 * A version 1 SwitchYardModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class V1SwitchYardModel extends BaseNamedModel implements SwitchYardModel {

    private CompositeModel _composite;
    private ArtifactsModel _artifacts;
    private TransformsModel _transforms;
    private ValidatesModel _validates;
    private DomainModel _domain;

    /**
     * Constructs a new V1SwitchYardModel.
     */
    public V1SwitchYardModel() {
        super(new QName(SwitchYardModel.DEFAULT_NAMESPACE, SwitchYardModel.SWITCHYARD));
        setModelChildrenOrder(CompositeModel.COMPOSITE, TransformsModel.TRANSFORMS, ValidatesModel.VALIDATES, DomainModel.DOMAIN, ArtifactsModel.ARTIFACTS);
        setDomainPropertyResolver();
    }

    /**
     * Constructs a new V1SwitchYardModel with the specified Configuration and Descriptor.
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1SwitchYardModel(Configuration config, Descriptor desc) {
        super(config, desc);
        setModelChildrenOrder(CompositeModel.COMPOSITE, TransformsModel.TRANSFORMS, ValidatesModel.VALIDATES, DomainModel.DOMAIN, ArtifactsModel.ARTIFACTS);
        setDomainPropertyResolver();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompositeModel getComposite() {
        if (_composite == null) {
            _composite = (CompositeModel)getFirstChildModelStartsWith(CompositeModel.COMPOSITE);
        }
        return _composite;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SwitchYardModel setComposite(CompositeModel composite) {
        setChildModel(composite);
        _composite = composite;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TransformsModel getTransforms() {
        if (_transforms == null) {
            _transforms = (TransformsModel)getFirstChildModelStartsWith(TransformsModel.TRANSFORMS);
        }
        return _transforms;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SwitchYardModel setTransforms(TransformsModel transforms) {
        setChildModel(transforms);
        _transforms = transforms;
        return this;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ValidatesModel getValidates() {
        if (_validates == null) {
            _validates = (ValidatesModel)getFirstChildModelStartsWith(ValidatesModel.VALIDATES);
        }
        return _validates;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SwitchYardModel setValidates(ValidatesModel validatesModel) {
        setChildModel(validatesModel);
        _validates = validatesModel;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArtifactsModel getArtifacts() {
        if (_artifacts == null) {
            _artifacts = (ArtifactsModel)getFirstChildModelStartsWith(ArtifactsModel.ARTIFACTS);
        }
        return _artifacts;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SwitchYardModel setArtifacts(ArtifactsModel artifactsModel) {
        setChildModel(artifactsModel);
        _artifacts = artifactsModel;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DomainModel getDomain() {
        if (_domain == null) {
            _domain = (DomainModel)getFirstChildModelStartsWith(DomainModel.DOMAIN);
        }
        return _domain;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SwitchYardModel setDomain(DomainModel domain) {
        setChildModel(domain);
        _domain = domain;
        setDomainPropertyResolver();
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDomainPropertyResolver() {
        DomainModel domain = getDomain();
        if (domain != null) {
            PropertiesModel properties = domain.getProperties();
            if (properties != null) {
                Configuration config = getModelConfiguration();
                config.setPropertyResolver(CompoundPropertyResolver.compact(config.getPropertyResolver(), properties));
            }
        }
        if (_composite != null) {
            _composite.setCompositePropertyResolver();
        }
    }

}
