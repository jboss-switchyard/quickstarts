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

import javax.xml.namespace.QName;

import org.kie.runtime.Channel;
import org.kie.runtime.KieRuntime;
import org.switchyard.ServiceDomain;
import org.switchyard.ServiceReference;
import org.switchyard.common.type.reflect.Construction;
import org.switchyard.common.xml.XMLHelper;
import org.switchyard.component.common.knowledge.channel.SwitchYardChannel;
import org.switchyard.component.common.knowledge.channel.SwitchYardServiceChannel;
import org.switchyard.component.common.knowledge.config.model.ChannelModel;
import org.switchyard.component.common.knowledge.config.model.ChannelsModel;
import org.switchyard.component.common.knowledge.config.model.KnowledgeComponentImplementationModel;

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
                String channelName = channelModel.getName();
                if (channelName != null) {
                    Class<? extends Channel> channelClass = channelModel.getClazz(loader);
                    if (channelClass == null) {
                        channelClass = SwitchYardServiceChannel.class;
                    }
                    Channel channel = Construction.construct(channelClass);
                    if (channel instanceof SwitchYardChannel) {
                        SwitchYardChannel syc = (SwitchYardChannel)channel;
                        syc.setOperation(channelModel.getOperation());
                        QName qname = XMLHelper.createQName(tns, channelModel.getReference());
                        ServiceReference reference = domain.getServiceReference(qname);
                        syc.setReference(reference);
                    }
                    runtime.registerChannel(channelName, channel);
                }
            }
        }
    }

    private Channels() {}

}
