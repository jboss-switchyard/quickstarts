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
