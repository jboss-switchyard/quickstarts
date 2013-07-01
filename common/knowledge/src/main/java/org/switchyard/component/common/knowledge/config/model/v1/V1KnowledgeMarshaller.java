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

import static org.switchyard.component.common.knowledge.config.model.ChannelModel.CHANNEL;
import static org.switchyard.component.common.knowledge.config.model.ChannelsModel.CHANNELS;
import static org.switchyard.component.common.knowledge.config.model.ContainerModel.CONTAINER;
import static org.switchyard.component.common.knowledge.config.model.FaultModel.FAULT;
import static org.switchyard.component.common.knowledge.config.model.FaultsModel.FAULTS;
import static org.switchyard.component.common.knowledge.config.model.GlobalModel.GLOBAL;
import static org.switchyard.component.common.knowledge.config.model.GlobalsModel.GLOBALS;
import static org.switchyard.component.common.knowledge.config.model.InputModel.INPUT;
import static org.switchyard.component.common.knowledge.config.model.InputsModel.INPUTS;
import static org.switchyard.component.common.knowledge.config.model.ListenerModel.LISTENER;
import static org.switchyard.component.common.knowledge.config.model.ListenersModel.LISTENERS;
import static org.switchyard.component.common.knowledge.config.model.LoggerModel.LOGGER;
import static org.switchyard.component.common.knowledge.config.model.LoggersModel.LOGGERS;
import static org.switchyard.component.common.knowledge.config.model.ManifestModel.MANIFEST;
import static org.switchyard.component.common.knowledge.config.model.OperationsModel.OPERATIONS;
import static org.switchyard.component.common.knowledge.config.model.OutputModel.OUTPUT;
import static org.switchyard.component.common.knowledge.config.model.OutputsModel.OUTPUTS;
import static org.switchyard.config.model.property.PropertiesModel.PROPERTIES;
import static org.switchyard.config.model.property.PropertyModel.PROPERTY;
import static org.switchyard.config.model.resource.ResourceModel.RESOURCE;
import static org.switchyard.config.model.resource.ResourcesModel.RESOURCES;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.Model;
import org.switchyard.config.model.composite.v1.V1CompositeMarshaller;
import org.switchyard.config.model.property.v1.V1PropertiesModel;
import org.switchyard.config.model.property.v1.V1PropertyModel;
import org.switchyard.config.model.resource.v1.V1ResourceModel;
import org.switchyard.config.model.resource.v1.V1ResourcesModel;

/**
 * A CompositeMarshaller which can also create knowledge models.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class V1KnowledgeMarshaller extends V1CompositeMarshaller {

    /**
     * Required constructor called via reflection.
     *
     * @param desc the Descriptor
     */
    public V1KnowledgeMarshaller(Descriptor desc) {
        super(desc);
    }

    /**
     * Reads in the Configuration, looking for various knowledge models.
     * If not found, it falls back to the super class (V1CompositeMarshaller).
     *
     * @param config the Configuration
     * @return the Model
     */
    @Override
    public Model read(Configuration config) {
        String name = config.getName();
        Descriptor desc = getDescriptor();
        if (CHANNELS.equals(name)) {
            return new V1ChannelsModel(config, desc);
        } else if (CHANNEL.equals(name)) {
            return new V1ChannelModel(config, desc);
        } else if (LISTENERS.equals(name)) {
            return new V1ListenersModel(config, desc);
        } else if (LISTENER.equals(name)) {
            return new V1ListenerModel(config, desc);
        } else if (LOGGERS.equals(name)) {
            return new V1LoggersModel(config, desc);
        } else if (LOGGER.equals(name)) {
            return new V1LoggerModel(config, desc);
        } else if (MANIFEST.equals(name)) {
            return new V1ManifestModel(config, desc);
        } else if (CONTAINER.equals(name)) {
            return new V1ContainerModel(config, desc);
        } else if (RESOURCES.equals(name)) {
            return new V1ResourcesModel(config, desc);
        } else if (RESOURCE.equals(name)) {
            return new V1ResourceModel(config, desc);
        } else if (OPERATIONS.equals(name)) {
            return new V1OperationsModel(config, desc);
        } else if (GLOBALS.equals(name)) {
            return new V1GlobalsModel(config, desc);
        } else if (GLOBAL.equals(name)) {
            return new V1GlobalModel(config, desc);
        } else if (INPUTS.equals(name)) {
            return new V1InputsModel(config, desc);
        } else if (INPUT.equals(name)) {
            return new V1InputModel(config, desc);
        } else if (OUTPUTS.equals(name)) {
            return new V1OutputsModel(config, desc);
        } else if (OUTPUT.equals(name)) {
            return new V1OutputModel(config, desc);
        } else if (FAULTS.equals(name)) {
            return new V1FaultsModel(config, desc);
        } else if (FAULT.equals(name)) {
            return new V1FaultModel(config, desc);
        } else if (PROPERTIES.equals(name)) {
            return new V1PropertiesModel(config, desc);
        } else if (PROPERTY.equals(name)) {
            return new V1PropertyModel(config, desc);
        }
        return super.read(config);
    }

}
