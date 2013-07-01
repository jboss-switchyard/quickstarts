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

import org.kie.api.runtime.Channel;
import org.kie.api.runtime.KieRuntime;
import org.switchyard.ServiceDomain;
import org.switchyard.SwitchYardException;
import org.switchyard.common.type.reflect.Construction;
import org.switchyard.common.xml.XMLHelper;
import org.switchyard.component.common.knowledge.config.model.ChannelModel;
import org.switchyard.component.common.knowledge.config.model.ChannelsModel;
import org.switchyard.component.common.knowledge.config.model.KnowledgeComponentImplementationModel;
import org.switchyard.component.common.knowledge.service.SwitchYardServiceChannel;
import org.switchyard.component.common.knowledge.service.SwitchYardServiceInvoker;

/**
 * Channel functions.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public final class Channels {

    /**
     * Registers channels.
     * @param model the model
     * @param loader the loader
     * @param runtime the runtime
     * @param domain the service domain
     */
    public static void registerChannels(KnowledgeComponentImplementationModel model, ClassLoader loader, KieRuntime runtime, ServiceDomain domain) {
        ChannelsModel channelsModel = model.getChannels();
        if (channelsModel != null) {
            String tns = model.getComponent().getTargetNamespace();
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
                    sysc.setInvoker(new SwitchYardServiceInvoker(domain, tns));
                    sysc.setComponentName(model.getComponent().getQName());
                    sysc.setServiceName(XMLHelper.createQName(channelModel.getReference()));
                    sysc.setOperationName(channelModel.getOperation());
                }
                if (name == null) {
                    throw new SwitchYardException("Could not use null name to register channel: " + channel.getClass().getName());
                }
                runtime.registerChannel(name, channel);
            }
        }
    }

    private Channels() {}

}
