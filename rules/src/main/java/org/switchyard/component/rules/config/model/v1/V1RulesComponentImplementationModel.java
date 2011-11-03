/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.component.rules.config.model.v1;

import static org.switchyard.component.rules.common.RulesConstants.MESSAGE_CONTENT_NAME;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.switchyard.component.common.rules.config.model.AuditModel;
import org.switchyard.component.common.rules.config.model.v1.V1ComponentImplementationModel;
import org.switchyard.component.rules.config.model.ChannelModel;
import org.switchyard.component.rules.config.model.RulesActionModel;
import org.switchyard.component.rules.config.model.RulesComponentImplementationModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.resource.ResourceModel;

/**
 * A "rules" implementation of a ComponentImplementationModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class V1RulesComponentImplementationModel extends V1ComponentImplementationModel implements RulesComponentImplementationModel {

    private List<RulesActionModel> _rulesActions = new ArrayList<RulesActionModel>();
    private List<ChannelModel> _channels = new ArrayList<ChannelModel>();

    /**
     * Default constructor for application use.
     */
    public V1RulesComponentImplementationModel() {
        super(RULES, DEFAULT_NAMESPACE);
        setModelChildrenOrder(RulesActionModel.ACTION, AuditModel.AUDIT, ChannelModel.CHANNEL, ResourceModel.RESOURCE);
    }

    /**
     * Constructor for Marshaller use (ie: V1RulesMarshaller).
     *
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1RulesComponentImplementationModel(Configuration config, Descriptor desc) {
        super(config, desc);
        for (Configuration rulesAction_config : config.getChildren(RulesActionModel.ACTION)) {
            RulesActionModel rulesAction = (RulesActionModel)readModel(rulesAction_config);
            if (rulesAction != null) {
                _rulesActions.add(rulesAction);
            }
        }
        for (Configuration channel_config : config.getChildren(ChannelModel.CHANNEL)) {
            ChannelModel channel = (ChannelModel)readModel(channel_config);
            if (channel != null) {
                _channels.add(channel);
            }
        }
        setModelChildrenOrder(RulesActionModel.ACTION, AuditModel.AUDIT, ChannelModel.CHANNEL, ResourceModel.RESOURCE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessageContentName() {
        return getModelAttribute(MESSAGE_CONTENT_NAME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RulesComponentImplementationModel setMessageContentName(String messageContentName) {
        setModelAttribute(MESSAGE_CONTENT_NAME, messageContentName);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RulesActionModel> getRulesActions() {
        return Collections.unmodifiableList(_rulesActions);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RulesComponentImplementationModel addRulesAction(RulesActionModel rulesAction) {
        addChildModel(rulesAction);
        _rulesActions.add(rulesAction);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ChannelModel> getChannels() {
        return Collections.unmodifiableList(_channels);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RulesComponentImplementationModel addChannel(ChannelModel channel) {
        addChildModel(channel);
        _channels.add(channel);
        return this;
    }

}
