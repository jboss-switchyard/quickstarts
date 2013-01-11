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
package org.switchyard.component.bpm.config.model.v1;

import static org.switchyard.component.bpm.config.model.WorkItemHandlersModel.WORK_ITEM_HANDLERS;
import static org.switchyard.component.common.knowledge.config.model.ActionsModel.ACTIONS;
import static org.switchyard.component.common.knowledge.config.model.ChannelsModel.CHANNELS;
import static org.switchyard.component.common.knowledge.config.model.ListenersModel.LISTENERS;
import static org.switchyard.component.common.knowledge.config.model.LoggersModel.LOGGERS;
import static org.switchyard.component.common.knowledge.config.model.ManifestModel.MANIFEST;
import static org.switchyard.config.model.property.PropertiesModel.PROPERTIES;

import org.switchyard.component.bpm.config.model.BPMComponentImplementationModel;
import org.switchyard.component.bpm.config.model.WorkItemHandlersModel;
import org.switchyard.component.common.knowledge.config.model.v1.V1KnowledgeComponentImplementationModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * A "bpm" implementation of a KnowledgeComponentImplementationModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class V1BPMComponentImplementationModel extends V1KnowledgeComponentImplementationModel implements BPMComponentImplementationModel {

    private WorkItemHandlersModel _workItemHandlers;

    /**
     * Default constructor for application use.
     */
    public V1BPMComponentImplementationModel() {
        super(BPM, DEFAULT_NAMESPACE);
        setModelChildrenOrder(ACTIONS, CHANNELS, LISTENERS, LOGGERS, MANIFEST, PROPERTIES, WORK_ITEM_HANDLERS);
    }

    /**
     * Constructor for Marshaller use (ie: V1BPMMarshaller).
     *
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1BPMComponentImplementationModel(Configuration config, Descriptor desc) {
        super(config, desc);
        setModelChildrenOrder(ACTIONS, CHANNELS, LISTENERS, LOGGERS, MANIFEST, PROPERTIES, WORK_ITEM_HANDLERS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPersistent() {
        String p = getModelAttribute("persistent");
        return p != null ? Boolean.parseBoolean(p) : false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BPMComponentImplementationModel setPersistent(boolean persistent) {
        setModelAttribute("persistent", String.valueOf(persistent));
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getProcessId() {
        return getModelAttribute("processId");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BPMComponentImplementationModel setProcessId(String processId) {
        setModelAttribute("processId", processId);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkItemHandlersModel getWorkItemHandlers() {
        if (_workItemHandlers == null) {
            _workItemHandlers = (WorkItemHandlersModel)getFirstChildModel(WORK_ITEM_HANDLERS);
        }
        return _workItemHandlers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BPMComponentImplementationModel setWorkItemHandlers(WorkItemHandlersModel workItemHandlers) {
        setChildModel(workItemHandlers);
        _workItemHandlers = workItemHandlers;
        return this;
    }

}
