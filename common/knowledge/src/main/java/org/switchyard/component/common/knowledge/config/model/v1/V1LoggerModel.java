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
import org.switchyard.component.common.knowledge.LoggerType;
import org.switchyard.component.common.knowledge.config.model.LoggerModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseNamedModel;
import org.switchyard.config.model.Descriptor;

/**
 * The 1st version ChannelModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class V1LoggerModel extends BaseNamedModel implements LoggerModel {

    /**
     * Creates a new MappingModel in the specified namespace.
     * @param namespace the namespace
     */
    public V1LoggerModel(String namespace) {
        super(XMLHelper.createQName(namespace, LOGGER));
    }

    /**
     * Creates a new MappingModel with the specified configuration and descriptor.
     * @param config the configuration
     * @param desc the descriptor
     */
    public V1LoggerModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getInterval() {
        String i = getModelAttribute("interval");
        return i != null ? Integer.valueOf(i) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LoggerModel setInterval(Integer interval) {
        String i = interval != null ? interval.toString() : null;
        setModelAttribute("interval", i);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLog() {
        return getModelAttribute("log");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LoggerModel setLog(String log) {
        setModelAttribute("log", log);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LoggerType getType() {
        String t = getModelAttribute("type");
        return t != null ? LoggerType.valueOf(t) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LoggerModel setType(LoggerType type) {
        String t = type != null ? type.name() : null;
        setModelAttribute("type", t);
        return this;
    }

}
