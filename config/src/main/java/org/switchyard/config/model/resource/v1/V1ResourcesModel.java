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
package org.switchyard.config.model.resource.v1;

import static org.switchyard.config.model.resource.ResourceModel.RESOURCE;
import static org.switchyard.config.model.switchyard.SwitchYardModel.DEFAULT_NAMESPACE;

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
     * Creates a new ResourcesModel in the default namespace.
     */
    public V1ResourcesModel() {
        this(DEFAULT_NAMESPACE);
    }

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
