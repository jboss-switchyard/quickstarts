/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
