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
package org.switchyard.component.common.knowledge.config.model.v1;

import static org.switchyard.component.common.knowledge.config.model.ContainerModel.CONTAINER;
import static org.switchyard.config.model.resource.ResourcesModel.RESOURCES;

import org.switchyard.common.xml.XMLHelper;
import org.switchyard.component.common.knowledge.config.model.ContainerModel;
import org.switchyard.component.common.knowledge.config.model.ManifestModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.resource.ResourcesModel;

/**
 * The 1st version ManifestModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class V1ManifestModel extends BaseModel implements ManifestModel {

    private ContainerModel _container;
    private ResourcesModel _resources;

    /**
     * Constructs a new V1ManifestModel of the specified namespace.
     * @param namespace the namespace
     */
    public V1ManifestModel(String namespace) {
        super(XMLHelper.createQName(namespace, MANIFEST));
        setModelChildrenOrder(CONTAINER, RESOURCES);
    }

    /**
     * Constructs a new V1ManifestModel with the specified Configuration and Descriptor.
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1ManifestModel(Configuration config, Descriptor desc) {
        super(config, desc);
        setModelChildrenOrder(CONTAINER, RESOURCES);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContainerModel getContainer() {
        if (_container == null) {
            _container = (ContainerModel)getFirstChildModel(CONTAINER);
        }
        return _container;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ManifestModel setContainer(ContainerModel container) {
        setChildModel(container);
        _container = container;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResourcesModel getResources() {
        if (_resources == null) {
            _resources = (ResourcesModel)getFirstChildModel(RESOURCES);
        }
        return _resources;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ManifestModel setResources(ResourcesModel resources) {
        setChildModel(resources);
        _resources = resources;
        return this;
    }

}
