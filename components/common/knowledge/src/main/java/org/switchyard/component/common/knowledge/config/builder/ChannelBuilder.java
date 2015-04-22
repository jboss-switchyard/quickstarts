/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.switchyard.component.common.knowledge.config.builder;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.kie.api.runtime.Channel;
import org.switchyard.ServiceDomain;
import org.switchyard.common.type.reflect.Construction;
import org.switchyard.common.xml.XMLHelper;
import org.switchyard.component.common.knowledge.config.model.ChannelModel;
import org.switchyard.component.common.knowledge.config.model.ChannelsModel;
import org.switchyard.component.common.knowledge.config.model.KnowledgeComponentImplementationModel;
import org.switchyard.component.common.knowledge.service.SwitchYardServiceChannel;
import org.switchyard.component.common.knowledge.service.SwitchYardServiceInvoker;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.deploy.ComponentNames;

/**
 * ChannelBuilder.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
public class ChannelBuilder extends KnowledgeBuilder {

    private Class<? extends Channel> _channelClass;
    private String _channelName;
    private QName _serviceName;
    private String _operationName;
    private String _targetNamespace;

    /**
     * Creates a new ChannelBuilder.
     * @param classLoader classLoader
     * @param serviceDomain serviceDomain
     * @param channelModel channelModel
     */
    @SuppressWarnings("unchecked")
    public ChannelBuilder(ClassLoader classLoader, ServiceDomain serviceDomain, ChannelModel channelModel) {
        super(classLoader, serviceDomain);
        if (channelModel != null) {
            ChannelsModel channelsModel = (ChannelsModel)channelModel.getModelParent();
            KnowledgeComponentImplementationModel implementationModel = (KnowledgeComponentImplementationModel)channelsModel.getModelParent();
            ComponentModel componentModel = implementationModel.getComponent();
            QName componentName =  componentModel.getQName();
            _targetNamespace =  componentModel.getTargetNamespace();
            _channelClass = (Class<? extends Channel>)channelModel.getClazz(getClassLoader());
            if (_channelClass == null) {
                _channelClass = SwitchYardServiceChannel.class;
            }
            _channelName = channelModel.getName();
            if (_channelName == null && SwitchYardServiceChannel.class.isAssignableFrom(_channelClass)) {
                _channelName = SwitchYardServiceChannel.SERVICE;
            }
            _serviceName = XMLHelper.createQName(channelModel.getReference());
            if (_serviceName != null && componentName != null) {
                _serviceName = ComponentNames.qualify(componentName, ComponentNames.unqualify(_serviceName));
            }
            _operationName = channelModel.getOperation();
        }
    }

    /**
     * Gets the channel name.
     * @return the channel name
     */
    public String getChannelName() {
        return _channelName;
    }

    /**
     * Builds a Channel.
     * @return a Channel
     */
    public Channel build() {
        Channel channel = null;
        if (_channelClass != null) {
            channel = Construction.construct(_channelClass);
            if (channel instanceof SwitchYardServiceChannel) {
                SwitchYardServiceChannel sysc = (SwitchYardServiceChannel)channel;
                sysc.setServiceName(_serviceName);
                sysc.setOperationName(_operationName);
                sysc.setInvoker(new SwitchYardServiceInvoker(getServiceDomain(), _targetNamespace));
            }
        }
        return channel;
    }

    /**
     * Creates ChannelBuilders.
     * @param classLoader classLoader
     * @param serviceDomain serviceDomain
     * @param implementationModel implementationModel
     * @return ChannelBuilders
     */
    public static List<ChannelBuilder> builders(ClassLoader classLoader, ServiceDomain serviceDomain, KnowledgeComponentImplementationModel implementationModel) {
        List<ChannelBuilder> builders = new ArrayList<ChannelBuilder>();
        if (implementationModel != null) {
            ChannelsModel channelsModel = implementationModel.getChannels();
            if (channelsModel != null) {
                for (ChannelModel channelModel : channelsModel.getChannels()) {
                    if (channelModel != null) {
                        builders.add(new ChannelBuilder(classLoader, serviceDomain, channelModel));
                    }
                }
            }
        }
        return builders;
    }

}
