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
package org.switchyard.component.bpm.config.model.v1;

import org.switchyard.component.bpm.config.model.BPMComponentImplementationModel;
import org.switchyard.component.common.knowledge.config.model.KnowledgeComponentImplementationModel;
import org.switchyard.component.common.knowledge.config.model.v1.V1KnowledgeComponentImplementationModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * A "bpm" implementation of a KnowledgeComponentImplementationModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class V1BPMComponentImplementationModel extends V1KnowledgeComponentImplementationModel implements BPMComponentImplementationModel {

    /**
     * Default constructor for application use.
     * @param namespace namespace
     */
    public V1BPMComponentImplementationModel(String namespace) {
        super(BPM, namespace);
    }

    /**
     * Constructor for Marshaller use (ie: V1BPMMarshaller).
     *
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1BPMComponentImplementationModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPersistent() {
        String p = getModelAttribute("persistent");
        return p != null ? Boolean.parseBoolean(p) : false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KnowledgeComponentImplementationModel setPersistent(boolean persistent) {
        setModelAttribute("persistent", String.valueOf(persistent));
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getProcessId() {
        return getModelAttribute("processId");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KnowledgeComponentImplementationModel setProcessId(String processId) {
        setModelAttribute("processId", processId);
        return this;
    }

}
