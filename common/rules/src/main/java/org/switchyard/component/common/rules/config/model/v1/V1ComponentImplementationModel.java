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
package org.switchyard.component.common.rules.config.model.v1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.switchyard.component.common.rules.ClockType;
import org.switchyard.component.common.rules.EventProcessingType;
import org.switchyard.component.common.rules.config.model.AuditModel;
import org.switchyard.component.common.rules.config.model.ComponentImplementationModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.Marshaller;
import org.switchyard.config.model.resource.ResourceModel;

/**
 * A "rules" implementation of a ComponentImplementationModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public abstract class V1ComponentImplementationModel extends org.switchyard.config.model.composite.v1.V1ComponentImplementationModel implements ComponentImplementationModel {

    private AuditModel _audit;
    private List<ResourceModel> _resources = new ArrayList<ResourceModel>();

    /**
     * Constructs a new V1ComponentImplementationModel of the specified "type".
     * @param type the "type" of ComponentImplementationModel
     */
    public V1ComponentImplementationModel(String type) {
        super(type);
    }

    /**
     * Constructs a new V1ComponentImplementationModel of the specified "type", and in the specified namespace.
     * @param type the "type" of ComponentImplementationModel
     * @param namespace the namespace
     */
    public V1ComponentImplementationModel(String type, String namespace) {
        super(type, namespace);
    }

    /**
     * Constructs a new V1ComponentImplementationModel with the specified Configuration and Descriptor.
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1ComponentImplementationModel(Configuration config, Descriptor desc) {
        super(config, desc);
        ClassLoader modelLoader = V1ComponentImplementationModel.class.getClassLoader();
        for (Configuration resource_config : config.getChildren(ResourceModel.RESOURCE)) {
            Marshaller marsh = desc.getMarshaller(resource_config.getQName().getNamespaceURI(), modelLoader);
            ResourceModel resource = (ResourceModel)marsh.read(resource_config);
            if (resource != null) {
                _resources.add(resource);
            }
        }
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
    public V1ComponentImplementationModel setAgent(boolean agent) {
        setModelAttribute("agent", String.valueOf(agent));
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClockType getClock() {
        String clock = getModelAttribute("clock");
        return clock != null ? ClockType.valueOf(clock) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ComponentImplementationModel setClock(ClockType clock) {
        setModelAttribute("clock", clock != null ? clock.name() : null);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EventProcessingType getEventProcessing() {
        String eventProcessing = getModelAttribute("eventProcessing");
        return eventProcessing != null ? EventProcessingType.valueOf(eventProcessing) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ComponentImplementationModel setEventProcessing(EventProcessingType eventProcessing) {
        setModelAttribute("eventProcessing", eventProcessing != null ? eventProcessing.name() : null);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getMaxThreads() {
        String maxThreads = getModelAttribute("maxThreads");
        return maxThreads != null ? Integer.valueOf(maxThreads) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ComponentImplementationModel setMaxThreads(Integer maxThreads) {
        setModelAttribute("maxThreads", maxThreads != null ? String.valueOf(maxThreads) : null);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean getMultithreadEvaluation() {
        String multithreadEvaluation = getModelAttribute("multithreadEvaluation");
        return multithreadEvaluation != null ? Boolean.valueOf(multithreadEvaluation) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ComponentImplementationModel setMultithreadEvaluation(Boolean multithreadEvaluation) {
        setModelAttribute("multithreadEvaluation", multithreadEvaluation != null ? String.valueOf(multithreadEvaluation) : null);
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
    public V1ComponentImplementationModel setAudit(AuditModel audit) {
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
    public V1ComponentImplementationModel addResource(ResourceModel resource) {
        addChildModel(resource);
        _resources.add(resource);
        return this;
    }

}
