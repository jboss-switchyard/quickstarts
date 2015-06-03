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
package org.switchyard.component.common.knowledge.config.model;

import org.switchyard.common.io.resource.ResourceType;
import org.switchyard.component.common.knowledge.LoggerType;
import org.switchyard.component.common.knowledge.annotation.Channel;
import org.switchyard.component.common.knowledge.annotation.Container;
import org.switchyard.component.common.knowledge.annotation.Fault;
import org.switchyard.component.common.knowledge.annotation.Global;
import org.switchyard.component.common.knowledge.annotation.Input;
import org.switchyard.component.common.knowledge.annotation.Listener;
import org.switchyard.component.common.knowledge.annotation.Logger;
import org.switchyard.component.common.knowledge.annotation.Manifest;
import org.switchyard.component.common.knowledge.annotation.Output;
import org.switchyard.component.common.knowledge.annotation.Property;
import org.switchyard.component.common.knowledge.annotation.Resource;
import org.switchyard.component.common.knowledge.annotation.ResourceDetail;
import org.switchyard.component.common.knowledge.config.model.v1.V1ChannelModel;
import org.switchyard.component.common.knowledge.config.model.v1.V1ChannelsModel;
import org.switchyard.component.common.knowledge.config.model.v1.V1ContainerModel;
import org.switchyard.component.common.knowledge.config.model.v1.V1FaultModel;
import org.switchyard.component.common.knowledge.config.model.v1.V1FaultsModel;
import org.switchyard.component.common.knowledge.config.model.v1.V1GlobalModel;
import org.switchyard.component.common.knowledge.config.model.v1.V1GlobalsModel;
import org.switchyard.component.common.knowledge.config.model.v1.V1InputModel;
import org.switchyard.component.common.knowledge.config.model.v1.V1InputsModel;
import org.switchyard.component.common.knowledge.config.model.v1.V1ListenerModel;
import org.switchyard.component.common.knowledge.config.model.v1.V1ListenersModel;
import org.switchyard.component.common.knowledge.config.model.v1.V1LoggerModel;
import org.switchyard.component.common.knowledge.config.model.v1.V1LoggersModel;
import org.switchyard.component.common.knowledge.config.model.v1.V1ManifestModel;
import org.switchyard.component.common.knowledge.config.model.v1.V1OutputModel;
import org.switchyard.component.common.knowledge.config.model.v1.V1OutputsModel;
import org.switchyard.component.common.knowledge.service.SwitchYardServiceChannel;
import org.switchyard.config.model.Scanner;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.ComponentReferenceModel;
import org.switchyard.config.model.composite.InterfaceModel;
import org.switchyard.config.model.composite.v1.V1ComponentReferenceModel;
import org.switchyard.config.model.composite.v1.V1InterfaceModel;
import org.switchyard.config.model.property.PropertiesModel;
import org.switchyard.config.model.property.PropertyModel;
import org.switchyard.config.model.property.v1.V1PropertiesModel;
import org.switchyard.config.model.property.v1.V1PropertyModel;
import org.switchyard.config.model.resource.ResourceDetailModel;
import org.switchyard.config.model.resource.ResourceModel;
import org.switchyard.config.model.resource.ResourcesModel;
import org.switchyard.config.model.resource.v1.V1ResourceDetailModel;
import org.switchyard.config.model.resource.v1.V1ResourceModel;
import org.switchyard.config.model.resource.v1.V1ResourcesModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.config.model.switchyard.SwitchYardNamespace;

/**
 * KnowledgeSwitchYardScanner.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public abstract class KnowledgeSwitchYardScanner implements Scanner<SwitchYardModel> {

    /** Undefined String value. */
    protected static final String UNDEFINED = "";

    /**
     * Converts channel annotations to a channels model.
     * @param channelAnnotations channelAnnotations
     * @param knowledgeNamespace knowledgeNamespace
     * @param componentModel componentModel
     * @param switchyardNamespace switchyardNamespace
     * @return a channels model
     */
    protected ChannelsModel toChannelsModel(Channel[] channelAnnotations, KnowledgeNamespace knowledgeNamespace, ComponentModel componentModel, SwitchYardNamespace switchyardNamespace) {
        if (channelAnnotations == null || channelAnnotations.length == 0) {
            return null;
        }
        ChannelsModel channelsModel = new V1ChannelsModel(knowledgeNamespace.uri());
        for (Channel channelAnnotation : channelAnnotations) {
            ChannelModel channelModel = new V1ChannelModel(knowledgeNamespace.uri());
            Class<? extends org.kie.api.runtime.Channel> clazz = channelAnnotation.value();
            if (Channel.UndefinedChannel.class.isAssignableFrom(clazz)) {
                clazz = SwitchYardServiceChannel.class;
            }
            channelModel.setClazz(clazz);
            String name = channelAnnotation.name();
            if (UNDEFINED.equals(name)) {
                if (SwitchYardServiceChannel.class.isAssignableFrom(clazz)) {
                    name = SwitchYardServiceChannel.SERVICE;
                }
            }
            if (UNDEFINED.equals(name)) {
                name = clazz.getSimpleName();
            }
            channelModel.setName(name);
            String operation = channelAnnotation.operation();
            if (!UNDEFINED.equals(operation)) {
                channelModel.setOperation(operation);
            }
            String reference = channelAnnotation.reference();
            if (!UNDEFINED.equals(reference)) {
                channelModel.setReference(reference);
                ComponentReferenceModel componentReferenceModel = new V1ComponentReferenceModel(switchyardNamespace.uri());
                componentReferenceModel.setName(reference);
                Class<?> interfaze = channelAnnotation.interfaze();
                if (!Channel.UndefinedInterface.class.isAssignableFrom(interfaze)) {
                    InterfaceModel interfaceModel = new V1InterfaceModel(InterfaceModel.JAVA);
                    interfaceModel.setInterface(interfaze.getName());
                    componentReferenceModel.setInterface(interfaceModel);
                    componentModel.addReference(componentReferenceModel);
                }
            }
            channelsModel.addChannel(channelModel);
        }
        return channelsModel;
    }

    /**
     * Converts container annotation to container model
     * @param containerAnnotation annotation
     * @param knowledgeNamespace knowledgeNamespace
     * @return model
     */
    protected ContainerModel toContainerModel(Container containerAnnotation, KnowledgeNamespace knowledgeNamespace) {
        if (containerAnnotation == null) {
            return null;
        }
        ContainerModel containerModel = new V1ContainerModel(knowledgeNamespace.uri());
        String baseName = containerAnnotation.baseName();
        if (!UNDEFINED.equals(baseName)) {
            containerModel.setBaseName(baseName);
        }
        String releaseId = containerAnnotation.releaseId();
        if (!UNDEFINED.equals(releaseId)) {
            containerModel.setReleaseId(releaseId);
        }
        boolean scan = containerAnnotation.scan();
        if (scan) {
            containerModel.setScan(scan);
        }
        long scanInterval = containerAnnotation.scanInterval();
        if (scanInterval > 0) {
            containerModel.setScanInterval(Long.valueOf(scanInterval));
        }
        String sessionName = containerAnnotation.sessionName();
        if (!UNDEFINED.equals(sessionName)) {
            containerModel.setSessionName(sessionName);
        }
        return containerModel;
    }

    /**
     * Converts listener annotations to listeners model.
     * @param listenerAnnotations annotations
     * @param knowledgeNamespace knowledgeNamespace
     * @return model
     */
    protected ListenersModel toListenersModel(Listener[] listenerAnnotations, KnowledgeNamespace knowledgeNamespace) {
        if (listenerAnnotations == null || listenerAnnotations.length == 0) {
            return null;
        }
        ListenersModel listenersModel = new V1ListenersModel(knowledgeNamespace.uri());
        for (Listener listenerAnnotation : listenerAnnotations) {
            ListenerModel listenerModel = new V1ListenerModel(knowledgeNamespace.uri());
            listenerModel.setClazz(listenerAnnotation.value());
            listenersModel.addListener(listenerModel);
        }
        return listenersModel;
    }

    /**
     * Converts logger annotations to loggers model.
     * @param loggerAnnotations annotations
     * @param knowledgeNamespace knowledgeNamespace
     * @return model
     */
    protected LoggersModel toLoggersModel(Logger[] loggerAnnotations, KnowledgeNamespace knowledgeNamespace) {
        if (loggerAnnotations == null || loggerAnnotations.length == 0) {
            return null;
        }
        LoggersModel loggersModel = new V1LoggersModel(knowledgeNamespace.uri());
        for (Logger loggerAnnotation : loggerAnnotations) {
            LoggerModel loggerModel = new V1LoggerModel(knowledgeNamespace.uri());
            int interval = loggerAnnotation.interval();
            if (interval > -1) {
                loggerModel.setInterval(interval);
            }
            String log = loggerAnnotation.log();
            if (!UNDEFINED.equals(log)) {
                loggerModel.setLog(log);
            }
            LoggerType loggerType = loggerAnnotation.type();
            if (!LoggerType.THREADED_FILE.equals(loggerType)) {
                loggerModel.setType(loggerType);
            }
            loggersModel.addLogger(loggerModel);
        }
        return loggersModel;
    }

    /**
     * Converts manifest annotations to manifest model.
     * @param manifestAnnotations annotations
     * @param knowledgeNamespace knowledgeNamespace
     * @return model
     */
    protected ManifestModel toManifestModel(Manifest[] manifestAnnotations, KnowledgeNamespace knowledgeNamespace) {
        if (manifestAnnotations == null || manifestAnnotations.length == 0) {
            return null;
        }
        Manifest manifestAnnotation = manifestAnnotations[0];
        ManifestModel manifestModel = new V1ManifestModel(knowledgeNamespace.uri());
        Container[] container = manifestAnnotation.container();
        if (container != null && container.length > 0) {
            manifestModel.setContainer(toContainerModel(container[0], knowledgeNamespace));
        }
        manifestModel.setResources(toResourcesModel(manifestAnnotation.resources(), knowledgeNamespace));
        return manifestModel;
    }

    /**
     * Converts globals to mappings model.
     * @param globals globals
     * @param knowledgeNamespace knowledgeNamespace
     * @return mappings model
     */
    protected GlobalsModel toGlobalsModel(Global[] globals, KnowledgeNamespace knowledgeNamespace) {
        GlobalsModel globalsModel = null;
        if (globals != null) {
            for (Global global : globals) {
                if (global != null) {
                    GlobalModel globalModel = new V1GlobalModel(knowledgeNamespace.uri());
                    String from = global.from();
                    if (!UNDEFINED.equals(from)) {
                        globalModel.setFrom(from);
                    }
                    String to = global.to();
                    if (!UNDEFINED.equals(to)) {
                        globalModel.setTo(to);
                    }
                    if (globalsModel == null) {
                        globalsModel = new V1GlobalsModel(knowledgeNamespace.uri());
                    }
                    globalsModel.addGlobal(globalModel);
                }
            }
        }
        return globalsModel;
    }

    /**
     * Converts inputs to mappings model.
     * @param inputs inputs
     * @param knowledgeNamespace knowledgeNamespace
     * @return mappings model
     */
    protected InputsModel toInputsModel(Input[] inputs, KnowledgeNamespace knowledgeNamespace) {
        InputsModel inputsModel = null;
        if (inputs != null) {
            for (Input input : inputs) {
                if (input != null) {
                    InputModel inputModel = new V1InputModel(knowledgeNamespace.uri());
                    String from = input.from();
                    if (!UNDEFINED.equals(from)) {
                        inputModel.setFrom(from);
                    }
                    String to = input.to();
                    if (!UNDEFINED.equals(to)) {
                        inputModel.setTo(to);
                    }
                    if (inputsModel == null) {
                        inputsModel = new V1InputsModel(knowledgeNamespace.uri());
                    }
                    inputsModel.addInput(inputModel);
                }
            }
        }
        return inputsModel;
    }

    /**
     * Converts outputs to mappings model.
     * @param outputs outputs
     * @param knowledgeNamespace knowledgeNamespace
     * @return mappings model
     */
    protected OutputsModel toOutputsModel(Output[] outputs, KnowledgeNamespace knowledgeNamespace) {
        OutputsModel outputsModel = null;
        if (outputs != null) {
            for (Output output : outputs) {
                if (output != null) {
                    OutputModel outputModel = new V1OutputModel(knowledgeNamespace.uri());
                    String from = output.from();
                    if (!UNDEFINED.equals(from)) {
                        outputModel.setFrom(from);
                    }
                    String to = output.to();
                    if (!UNDEFINED.equals(to)) {
                        outputModel.setTo(to);
                    }
                    if (outputsModel == null) {
                        outputsModel = new V1OutputsModel(knowledgeNamespace.uri());
                    }
                    outputsModel.addOutput(outputModel);
                }
            }
        }
        return outputsModel;
    }

    /**
     * Converts faults to mappings model.
     * @param faults faults
     * @param knowledgeNamespace knowledgeNamespace
     * @return mappings model
     */
    protected FaultsModel toFaultsModel(Fault[] faults, KnowledgeNamespace knowledgeNamespace) {
        FaultsModel faultsModel = null;
        if (faults != null) {
            for (Fault fault : faults) {
                if (fault != null) {
                    FaultModel faultModel = new V1FaultModel(knowledgeNamespace.uri());
                    String from = fault.from();
                    if (!UNDEFINED.equals(from)) {
                        faultModel.setFrom(from);
                    }
                    String to = fault.to();
                    if (!UNDEFINED.equals(to)) {
                        faultModel.setTo(to);
                    }
                    if (faultsModel == null) {
                        faultsModel = new V1FaultsModel(knowledgeNamespace.uri());
                    }
                    faultsModel.addFault(faultModel);
                }
            }
        }
        return faultsModel;
    }

    /**
     * Converts property annotations to properties model.
     * @param propertyAnnotations propertyAnnotations
     * @param knowledgeNamespace knowledgeNamespace
     * @return model
     */
    protected PropertiesModel toPropertiesModel(Property[] propertyAnnotations, KnowledgeNamespace knowledgeNamespace) {
        if (propertyAnnotations == null || propertyAnnotations.length == 0) {
            return null;
        }
        PropertiesModel propertiesModel = new V1PropertiesModel(knowledgeNamespace.uri());
        for (Property propertyAnnotation : propertyAnnotations) {
            PropertyModel propertyModel = new V1PropertyModel(knowledgeNamespace.uri());
            String name = propertyAnnotation.name();
            if (!UNDEFINED.equals(name)) {
                propertyModel.setName(name);
            }
            String value = propertyAnnotation.value();
            if (!UNDEFINED.equals(value)) {
                propertyModel.setValue(value);
            }
            propertiesModel.addProperty(propertyModel);
        }
        return propertiesModel;
    }

    /**
     * Converts resource annotations to resources model.
     * @param resourceAnnotations resourceAnnotations
     * @param knowledgeNamespace knowledgeNamespace
     * @return model
     */
    protected ResourcesModel toResourcesModel(Resource[] resourceAnnotations, KnowledgeNamespace knowledgeNamespace) {
        if (resourceAnnotations == null || resourceAnnotations.length == 0) {
            return null;
        }
        ResourcesModel resourcesModel = new V1ResourcesModel(knowledgeNamespace.uri());
        for (Resource resourceAnnotation : resourceAnnotations) {
            ResourceModel resourceModel = new V1ResourceModel(knowledgeNamespace.uri());
            String location = resourceAnnotation.location();
            if (!UNDEFINED.equals(location)) {
                resourceModel.setLocation(location);
            }
            String type = resourceAnnotation.type();
            if (!UNDEFINED.equals(type)) {
                resourceModel.setType(ResourceType.valueOf(type));
            }
            ResourceDetailModel resourceDetailModel = toResourceDetailModel(resourceAnnotation.detail(), knowledgeNamespace);
            if (resourceDetailModel != null) {
                resourceModel.setDetail(resourceDetailModel);
            }
            resourcesModel.addResource(resourceModel);
        }
        return resourcesModel;
    }

    private ResourceDetailModel toResourceDetailModel(ResourceDetail[] resourceDetailAnnotations, KnowledgeNamespace knowledgeNamespace) {
        if (resourceDetailAnnotations == null || resourceDetailAnnotations.length == 0) {
            return null;
        }
        ResourceDetailModel resourceDetailModel = null;
        for (ResourceDetail resourceDetailAnnotation : resourceDetailAnnotations) {
            resourceDetailModel = new V1ResourceDetailModel(knowledgeNamespace.uri());
            String inputType = resourceDetailAnnotation.inputType();
            if (!UNDEFINED.equals(inputType)) {
                resourceDetailModel.setInputType(inputType);
            }
            String worksheetName = resourceDetailAnnotation.worksheetName();
            if (!UNDEFINED.equals(worksheetName)) {
                resourceDetailModel.setWorksheetName(worksheetName);
            }
            /* SWITCHYARD-1662
            boolean usingExternalTypes = resourceDetailAnnotation.usingExternalTypes();
            resourceDetailModel.setUsingExternalTypes(usingExternalTypes);
            */
            break;
        }
        return resourceDetailModel;
    }

}
