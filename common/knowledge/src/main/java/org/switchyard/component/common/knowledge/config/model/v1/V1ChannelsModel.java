/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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
package org.switchyard.component.common.knowledge.config.model.v1;

import static org.switchyard.component.common.knowledge.config.model.ChannelModel.CHANNEL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import org.switchyard.component.common.knowledge.config.model.ChannelModel;
import org.switchyard.component.common.knowledge.config.model.ChannelsModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * A version 1 ChannelsModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class V1ChannelsModel extends BaseModel implements ChannelsModel {

    private List<ChannelModel> _channels = new ArrayList<ChannelModel>();

    /**
     * Creates a new ChannelsModel in the specified namespace.
     * @param namespace the specified namespace
     */
    public V1ChannelsModel(String namespace) {
        super(new QName(namespace, CHANNELS));
        setModelChildrenOrder(CHANNEL);
    }

    /**
     * Creates a new ChannelsModel with the specified configuration and descriptor.
     * @param config the configuration
     * @param desc the descriptor
     */
    public V1ChannelsModel(Configuration config, Descriptor desc) {
        super(config, desc);
        for (Configuration channel_config : config.getChildren(CHANNEL)) {
            ChannelModel channel = (ChannelModel)readModel(channel_config);
            if (channel != null) {
                _channels.add(channel);
            }
        }
        setModelChildrenOrder(CHANNEL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<ChannelModel> getChannels() {
        return Collections.unmodifiableList(_channels);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ChannelsModel addChannel(ChannelModel channel) {
        addChildModel(channel);
        _channels.add(channel);
        return this;
    }

}
