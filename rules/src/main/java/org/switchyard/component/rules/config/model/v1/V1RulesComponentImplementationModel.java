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
package org.switchyard.component.rules.config.model.v1;

import static org.switchyard.component.common.knowledge.config.model.ChannelsModel.CHANNELS;
import static org.switchyard.component.common.knowledge.config.model.ListenersModel.LISTENERS;
import static org.switchyard.component.common.knowledge.config.model.LoggersModel.LOGGERS;
import static org.switchyard.component.common.knowledge.config.model.ManifestModel.MANIFEST;
import static org.switchyard.component.common.knowledge.config.model.OperationsModel.OPERATIONS;
import static org.switchyard.config.model.property.PropertiesModel.PROPERTIES;

import org.switchyard.component.common.knowledge.config.model.v1.V1KnowledgeComponentImplementationModel;
import org.switchyard.component.rules.config.model.RulesComponentImplementationModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * A "rules" implementation of a KnowledgeComponentImplementationModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class V1RulesComponentImplementationModel extends V1KnowledgeComponentImplementationModel implements RulesComponentImplementationModel {

    /**
     * Default constructor for application use.
     * @param namespace namespace
     */
    public V1RulesComponentImplementationModel(String namespace) {
        super(RULES, namespace);
        setModelChildrenOrder(CHANNELS, LISTENERS, LOGGERS, MANIFEST, OPERATIONS, PROPERTIES);
    }

    /**
     * Constructor for Marshaller use (ie: V1RulesMarshaller).
     *
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1RulesComponentImplementationModel(Configuration config, Descriptor desc) {
        super(config, desc);
        setModelChildrenOrder(CHANNELS, LISTENERS, LOGGERS, MANIFEST, OPERATIONS, PROPERTIES);
    }

}
