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
package org.switchyard.component.common.knowledge.util;

import org.kie.runtime.Channel;
import org.kie.runtime.KieRuntime;
import org.switchyard.ServiceDomain;
import org.switchyard.common.type.reflect.Construction;
import org.switchyard.common.xml.XMLHelper;
import org.switchyard.component.common.knowledge.config.model.ChannelModel;
import org.switchyard.component.common.knowledge.config.model.ChannelsModel;
import org.switchyard.component.common.knowledge.config.model.KnowledgeComponentImplementationModel;
import org.switchyard.component.common.knowledge.service.SwitchYardServiceChannel;
import org.switchyard.component.common.knowledge.service.SwitchYardServiceInvoker;
import org.switchyard.exception.SwitchYardException;

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
                    sysc.setServiceName(XMLHelper.createQName(channelModel.getReference()));
                    sysc.setServiceOperationName(channelModel.getOperation());
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
