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

import org.switchyard.common.xml.XMLHelper;
import org.switchyard.component.common.knowledge.config.model.ContainerModel;
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
    public String getReleaseId() {
        return getModelAttribute("releaseId");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContainerModel setReleaseId(String releaseId) {
        setModelAttribute("releaseId", releaseId);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isScan() {
        String scan = getModelAttribute("scan");
        return scan != null ? Boolean.parseBoolean(scan) : false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContainerModel setScan(boolean scan) {
        setModelAttribute("scan", String.valueOf(scan));
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getScanInterval() {
        String scanInterval = getModelAttribute("scanInterval");
        return scanInterval != null ? Long.valueOf(scanInterval) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContainerModel setScanInterval(Long scanInterval) {
        String si = scanInterval != null ? scanInterval.toString() : null;
        setModelAttribute("scanInterval", si);
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
