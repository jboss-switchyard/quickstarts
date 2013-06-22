/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
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
package org.switchyard.component.common.knowledge.config.model;

import org.switchyard.common.io.resource.ResourceType;
import org.switchyard.common.type.reflect.Construction;
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
import org.switchyard.config.model.resource.ResourceModel;
import org.switchyard.config.model.resource.ResourcesModel;
import org.switchyard.config.model.resource.v1.V1ResourceModel;
import org.switchyard.config.model.resource.v1.V1ResourcesModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;

/**
 * KnowledgeSwitchYardScanner.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public abstract class KnowledgeSwitchYardScanner implements Scanner<SwitchYardModel> {

    /** Undefined String value. */
    protected static final String UNDEFINED = "";

    /**
     * Converts channel annotations to channel model.
     * @param channelAnnotations annotations
     * @param namespace namespace
     * @param componentModel model
     * @return channel model
     */
    protected ChannelsModel toChannelsModel(Channel[] channelAnnotations, String namespace, ComponentModel componentModel) {
        if (channelAnnotations == null || channelAnnotations.length == 0) {
            return null;
        }
        ChannelsModel channelsModel = new V1ChannelsModel(namespace);
        for (Channel channelAnnotation : channelAnnotations) {
            ChannelModel channelModel = new V1ChannelModel(namespace);
            Class<? extends org.kie.api.runtime.Channel> clazz = channelAnnotation.value();
            if (Channel.UndefinedChannel.class.isAssignableFrom(clazz)) {
                clazz = SwitchYardServiceChannel.class;
            }
            channelModel.setClazz(clazz);
            String name = channelAnnotation.name();
            if (UNDEFINED.equals(name)) {
                org.kie.api.runtime.Channel c = Construction.construct(clazz);
                if (c instanceof SwitchYardServiceChannel) {
                    SwitchYardServiceChannel sysc = (SwitchYardServiceChannel)c;
                    if (sysc.getName() != null) {
                        name = sysc.getName();
                    }
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
                ComponentReferenceModel componentReferenceModel = new V1ComponentReferenceModel();
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
     * @param namespace namespace
     * @return model
     */
    protected ContainerModel toContainerModel(Container containerAnnotation, String namespace) {
        if (containerAnnotation == null) {
            return null;
        }
        ContainerModel containerModel = new V1ContainerModel(namespace);
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
     * @param namespace namespace
     * @return model
     */
    protected ListenersModel toListenersModel(Listener[] listenerAnnotations, String namespace) {
        if (listenerAnnotations == null || listenerAnnotations.length == 0) {
            return null;
        }
        ListenersModel listenersModel = new V1ListenersModel(namespace);
        for (Listener listenerAnnotation : listenerAnnotations) {
            ListenerModel listenerModel = new V1ListenerModel(namespace);
            listenerModel.setClazz(listenerAnnotation.value());
            listenersModel.addListener(listenerModel);
        }
        return listenersModel;
    }

    /**
     * Converts logger annotations to loggers model.
     * @param loggerAnnotations annotations
     * @param namespace namespace
     * @return model
     */
    protected LoggersModel toLoggersModel(Logger[] loggerAnnotations, String namespace) {
        if (loggerAnnotations == null || loggerAnnotations.length == 0) {
            return null;
        }
        LoggersModel loggersModel = new V1LoggersModel(namespace);
        for (Logger loggerAnnotation : loggerAnnotations) {
            LoggerModel loggerModel = new V1LoggerModel(namespace);
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
     * @param namespace namespace
     * @return model
     */
    protected ManifestModel toManifestModel(Manifest[] manifestAnnotations, String namespace) {
        if (manifestAnnotations == null || manifestAnnotations.length == 0) {
            return null;
        }
        Manifest manifestAnnotation = manifestAnnotations[0];
        ManifestModel manifestModel = new V1ManifestModel(namespace);
        Container[] container = manifestAnnotation.container();
        if (container != null && container.length > 0) {
            manifestModel.setContainer(toContainerModel(container[0], namespace));
        }
        manifestModel.setResources(toResourcesModel(manifestAnnotation.resources(), namespace));
        return manifestModel;
    }

    /**
     * Converts globals to mappings model.
     * @param globals globals
     * @param namespace namespace
     * @return mappings model
     */
    protected GlobalsModel toGlobalsModel(Global[] globals, String namespace) {
        GlobalsModel globalsModel = null;
        if (globals != null) {
            for (Global global : globals) {
                if (global != null) {
                    GlobalModel globalModel = new V1GlobalModel(namespace);
                    String from = global.from();
                    if (!UNDEFINED.equals(from)) {
                        globalModel.setFrom(from);
                    }
                    String to = global.to();
                    if (!UNDEFINED.equals(to)) {
                        globalModel.setTo(to);
                    }
                    if (globalsModel == null) {
                        globalsModel = new V1GlobalsModel(namespace);
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
     * @param namespace namespace
     * @return mappings model
     */
    protected InputsModel toInputsModel(Input[] inputs, String namespace) {
        InputsModel inputsModel = null;
        if (inputs != null) {
            for (Input input : inputs) {
                if (input != null) {
                    InputModel inputModel = new V1InputModel(namespace);
                    String from = input.from();
                    if (!UNDEFINED.equals(from)) {
                        inputModel.setFrom(from);
                    }
                    String to = input.to();
                    if (!UNDEFINED.equals(to)) {
                        inputModel.setTo(to);
                    }
                    if (inputsModel == null) {
                        inputsModel = new V1InputsModel(namespace);
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
     * @param namespace namespace
     * @return mappings model
     */
    protected OutputsModel toOutputsModel(Output[] outputs, String namespace) {
        OutputsModel outputsModel = null;
        if (outputs != null) {
            for (Output output : outputs) {
                if (output != null) {
                    OutputModel outputModel = new V1OutputModel(namespace);
                    String from = output.from();
                    if (!UNDEFINED.equals(from)) {
                        outputModel.setFrom(from);
                    }
                    String to = output.to();
                    if (!UNDEFINED.equals(to)) {
                        outputModel.setTo(to);
                    }
                    if (outputsModel == null) {
                        outputsModel = new V1OutputsModel(namespace);
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
     * @param namespace namespace
     * @return mappings model
     */
    protected FaultsModel toFaultsModel(Fault[] faults, String namespace) {
        FaultsModel faultsModel = null;
        if (faults != null) {
            for (Fault fault : faults) {
                if (fault != null) {
                    FaultModel faultModel = new V1FaultModel(namespace);
                    String from = fault.from();
                    if (!UNDEFINED.equals(from)) {
                        faultModel.setFrom(from);
                    }
                    String to = fault.to();
                    if (!UNDEFINED.equals(to)) {
                        faultModel.setTo(to);
                    }
                    if (faultsModel == null) {
                        faultsModel = new V1FaultsModel(namespace);
                    }
                    faultsModel.addFault(faultModel);
                }
            }
        }
        return faultsModel;
    }

    /**
     * Converts property annotations to properties model.
     * @param propertyAnnotations annotations
     * @param namespace namespace
     * @return model
     */
    protected PropertiesModel toPropertiesModel(Property[] propertyAnnotations, String namespace) {
        if (propertyAnnotations == null || propertyAnnotations.length == 0) {
            return null;
        }
        PropertiesModel propertiesModel = new V1PropertiesModel(namespace);
        for (Property propertyAnnotation : propertyAnnotations) {
            PropertyModel propertyModel = new V1PropertyModel(namespace);
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
     * @param resourceAnnotations annotations
     * @param namespace namespace
     * @return model
     */
    protected ResourcesModel toResourcesModel(Resource[] resourceAnnotations, String namespace) {
        if (resourceAnnotations == null || resourceAnnotations.length == 0) {
            return null;
        }
        ResourcesModel resourcesModel = new V1ResourcesModel(namespace);
        for (Resource resourceAnnotation : resourceAnnotations) {
            ResourceModel resourceModel = new V1ResourceModel(namespace);
            String location = resourceAnnotation.location();
            if (!UNDEFINED.equals(location)) {
                resourceModel.setLocation(location);
            }
            String type = resourceAnnotation.type();
            if (!UNDEFINED.equals(type)) {
                resourceModel.setType(ResourceType.valueOf(type));
            }
            resourcesModel.addResource(resourceModel);
        }
        return resourcesModel;
    }

}
