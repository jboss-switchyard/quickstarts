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
import org.switchyard.component.common.knowledge.config.model.MappingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * The 1st version MappingModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public abstract class V1MappingModel extends BaseModel implements MappingModel {

    /**
     * Creates a new V1MappingModel in the specified namespace and localName.
     * @param namespace the specified namespace
     * @param localName the specified localName
     */
    public V1MappingModel(String namespace, String localName) {
        super(XMLHelper.createQName(namespace, localName));
    }

    /**
     * Creates a new V1MappingModel with the specified configuration and descriptor.
     * @param config the configuration
     * @param desc the descriptor
     */
    public V1MappingModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFrom() {
        return getModelAttribute("from");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MappingModel setFrom(String from) {
        setModelAttribute("from", from);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTo() {
        return getModelAttribute("to");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MappingModel setTo(String to) {
        setModelAttribute("to", to);
        return this;
    }

}
