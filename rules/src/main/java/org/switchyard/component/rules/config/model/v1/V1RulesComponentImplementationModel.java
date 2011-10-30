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
import org.switchyard.component.rules.config.model.RulesActionModel;
import org.switchyard.component.rules.config.model.RulesComponentImplementationModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.composite.v1.V1ComponentImplementationModel;
import org.switchyard.config.model.resource.ResourceModel;

/**
 * A "rules" implementation of a ComponentImplementationModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class V1RulesComponentImplementationModel extends V1ComponentImplementationModel implements RulesComponentImplementationModel {

    private List<RulesActionModel> _rulesActions = new ArrayList<RulesActionModel>();
    private AuditModel _audit;
    private List<ResourceModel> _resources = new ArrayList<ResourceModel>();

    /**
     * Default constructor for application use.
     */
    public V1RulesComponentImplementationModel() {
        super(RULES, DEFAULT_NAMESPACE);
        setModelChildrenOrder(RulesActionModel.ACTION, AuditModel.AUDIT, ResourceModel.RESOURCE);
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
        for (Configuration resource_config : config.getChildren(ResourceModel.RESOURCE)) {
            ResourceModel resource = (ResourceModel)readModel(resource_config);
            if (resource != null) {
                _resources.add(resource);
            }
        }
        setModelChildrenOrder(RulesActionModel.ACTION, AuditModel.AUDIT, ResourceModel.RESOURCE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isStateful() {
        String stateful = getModelAttribute("stateful");
        return stateful != null ? Boolean.valueOf(stateful) : false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RulesComponentImplementationModel setStateful(boolean stateful) {
        setModelAttribute("stateful", String.valueOf(stateful));
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAgent() {
        String agent = getModelAttribute("agent");
        return agent != null ? Boolean.valueOf(agent) : false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RulesComponentImplementationModel setAgent(boolean agent) {
        setModelAttribute("agent", String.valueOf(agent));
        return this;
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
    public AuditModel getAudit() {
        if (_audit == null) {
            _audit = (AuditModel)getFirstChildModelStartsWith(AuditModel.AUDIT);
        }
        return _audit;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RulesComponentImplementationModel setAudit(AuditModel audit) {
        setChildModel(audit);
        _audit = audit;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ResourceModel> getResources() {
        return Collections.unmodifiableList(_resources);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RulesComponentImplementationModel addResource(ResourceModel resource) {
        addChildModel(resource);
        _resources.add(resource);
        return this;
    }

}
