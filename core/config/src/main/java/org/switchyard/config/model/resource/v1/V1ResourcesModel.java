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
package org.switchyard.config.model.resource.v1;

import static org.switchyard.config.model.resource.ResourceModel.RESOURCE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.resource.ResourceModel;
import org.switchyard.config.model.resource.ResourcesModel;

/**
 * A version 1 ResourcesModel.
 */
public class V1ResourcesModel extends BaseModel implements ResourcesModel {

    private List<ResourceModel> _resources = new ArrayList<ResourceModel>();

    /**
     * Creates a new ResourcesModel in the specified namespace.
     * @param namespace the specified namespace
     */
    public V1ResourcesModel(String namespace) {
        super(new QName(namespace, RESOURCES));
        setModelChildrenOrder(RESOURCE);
    }

    /**
     * Creates a new ResourcesModel with the specified configuration and descriptor.
     * @param config the configuration
     * @param desc the descriptor
     */
    public V1ResourcesModel(Configuration config, Descriptor desc) {
        super(config, desc);
        for (Configuration resource_config : config.getChildren(RESOURCE)) {
            ResourceModel resource = (ResourceModel)readModel(resource_config);
            if (resource != null) {
                _resources.add(resource);
            }
        }
        setModelChildrenOrder(RESOURCE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<ResourceModel> getResources() {
        return Collections.unmodifiableList(_resources);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResourcesModel addResource(ResourceModel resource) {
        addChildModel(resource);
        _resources.add(resource);
        return this;
    }

}
