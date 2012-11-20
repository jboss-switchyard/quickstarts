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

import org.kie.builder.ReleaseId;
import org.switchyard.common.xml.XMLHelper;
import org.switchyard.component.common.knowledge.config.model.ContainerModel;
import org.switchyard.component.common.knowledge.util.Containers;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * AThe 1st version ContainerModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class V1ContainerModel extends BaseModel implements ContainerModel {

    /**
     * Constructs a new V1ContainerModel of the specified namespace.
     * @param namespace the namespace
     */
    public V1ContainerModel(String namespace) {
        super(XMLHelper.createQName(namespace, CONTAINER));
    }

    /**
     * Constructs a new V1ContainerModel with the specified Configuration and Descriptor.
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1ContainerModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getBaseName() {
        return getModelAttribute("baseName");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContainerModel setBaseName(String baseName) {
        setModelAttribute("baseName", baseName);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReleaseId getReleaseId() {
        return Containers.toReleaseId(getModelAttribute("releaseId"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContainerModel setReleaseId(ReleaseId releaseId) {
        String rid = releaseId != null ? releaseId.toExternalForm() : null;
        setModelAttribute("releaseId", rid);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSessionName() {
        return getModelAttribute("sessionName");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContainerModel setSessionName(String sessionName) {
        setModelAttribute("sessionName", sessionName);
        return this;
    }

}
