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
package org.switchyard.component.common.knowledge.util;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.kie.api.runtime.Channel;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.switchyard.ServiceDomain;
import org.switchyard.SwitchYardException;
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
 * Channel functions.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public final class Channels {

    /**
     * Registers channels on a stateless session.
     * @param model the model
     * @param loader the loader
     * @param domain the service domain
     * @param stateless the stateless session
     */
    public static void registerChannels(KnowledgeComponentImplementationModel model, ClassLoader loader, ServiceDomain domain, StatelessKieSession stateless) {
        List<NameChannel> ncList = getNameChannels(model, loader, domain);
        for (NameChannel nc : ncList) {
            stateless.registerChannel(nc.getName(), nc.getChannel());
        }
    }

    /**
     * Registers channels on a stateful session.
     * @param model the model
     * @param loader the loader
     * @param domain the service domain
     * @param stateful the stateful session
     */
    public static void registerChannels(KnowledgeComponentImplementationModel model, ClassLoader loader, ServiceDomain domain, KieSession stateful) {
        List<NameChannel> ncList = getNameChannels(model, loader, domain);
        for (NameChannel nc : ncList) {
            stateful.registerChannel(nc.getName(), nc.getChannel());
        }
    }

    private static List<NameChannel> getNameChannels(KnowledgeComponentImplementationModel model, ClassLoader loader, ServiceDomain domain) {
        List<NameChannel> ncList = new ArrayList<NameChannel>();
        ChannelsModel channelsModel = model.getChannels();
        if (channelsModel != null) {
            ComponentModel componentModel = model.getComponent();
            QName componentName =  componentModel.getQName();
            String componentTNS =  componentModel.getTargetNamespace();
            for (ChannelModel channelModel : channelsModel.getChannels()) {
                @SuppressWarnings("unchecked")
                Class<? extends Channel> channelClass = (Class<? extends Channel>)channelModel.getClazz(loader);
                if (channelClass == null) {
                    channelClass = SwitchYardServiceChannel.class;
                }
                Channel channel = Construction.construct(channelClass);
                String name = channelModel.getName();
                if (channel instanceof SwitchYardServiceChannel) {
                    SwitchYardServiceChannel sysc = (SwitchYardServiceChannel)channel;
                    if (name != null) {
                        sysc.setName(name);
                    } else {
                        name = sysc.getName();
                    }
                    QName serviceName = XMLHelper.createQName(channelModel.getReference());
                    if (serviceName != null && componentName != null) {
                        serviceName = ComponentNames.qualify(componentName, ComponentNames.unqualify(serviceName));
                    }
                    sysc.setServiceName(serviceName);
                    sysc.setOperationName(channelModel.getOperation());
                    sysc.setInvoker(new SwitchYardServiceInvoker(domain, componentTNS));
                }
                if (name == null) {
                    throw new SwitchYardException("Could not use null name to register channel: " + channel.getClass().getName());
                }
                ncList.add(new NameChannel(name, channel));
            }
        }
        return ncList;
    }

    private static final class NameChannel {
        private final String _name;
        private final Channel _channel;
        private NameChannel(String name, Channel channel) {
            _name = name;
            _channel = channel;
        }
        private String getName() {
            return _name;
        }
        private Channel getChannel() {
            return _channel;
        }
    }

    private Channels() {}

}
