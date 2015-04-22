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

import org.switchyard.common.type.Classes;
import org.switchyard.component.common.knowledge.config.model.ChannelModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseNamedModel;
import org.switchyard.config.model.Descriptor;

/**
 * The 1st version ChannelModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class V1ChannelModel extends BaseNamedModel implements ChannelModel {

    /**
     * Creates a new ChannelModel in the specified namespace.
     * @param namespace the namespace
     */
    public V1ChannelModel(String namespace) {
        super(namespace, CHANNEL);
    }

    /**
     * Creates a new ChannelModel with the specified configuration and descriptor.
     * @param config the configuration
     * @param desc the descriptor
     */
    public V1ChannelModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getClazz(ClassLoader loader) {
        String c = getModelAttribute("class");
        return c != null ? Classes.forName(c, loader) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ChannelModel setClazz(Class<?> clazz) {
        String c = clazz != null ? clazz.getName() : null;
        setModelAttribute("class", c);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getOperation() {
        return getModelAttribute("operation");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ChannelModel setOperation(String operation) {
        setModelAttribute("operation", operation);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getReference() {
        return getModelAttribute("reference");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ChannelModel setReference(String reference) {
        setModelAttribute("reference", reference);
        return this;
    }

}
