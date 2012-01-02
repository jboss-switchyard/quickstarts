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
package org.switchyard.component.bpm.config.model.v1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.switchyard.common.io.resource.Resource;
import org.switchyard.common.io.resource.ResourceType;
import org.switchyard.common.io.resource.SimpleResource;
import org.switchyard.component.bpm.ProcessConstants;
import org.switchyard.component.bpm.config.model.BPMComponentImplementationModel;
import org.switchyard.component.bpm.config.model.ProcessActionModel;
import org.switchyard.component.bpm.config.model.TaskHandlerModel;
import org.switchyard.component.common.rules.config.model.AuditModel;
import org.switchyard.component.common.rules.config.model.v1.V1ComponentImplementationModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.resource.ResourceModel;

/**
 * A "bpm" implementation of a ComponentImplementationModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class V1BPMComponentImplementationModel extends V1ComponentImplementationModel implements BPMComponentImplementationModel {

    private List<ProcessActionModel> _processActions = new ArrayList<ProcessActionModel>();
    private List<TaskHandlerModel> _taskHandlers = new ArrayList<TaskHandlerModel>();

    /**
     * Default constructor for application use.
     */
    public V1BPMComponentImplementationModel() {
        super(BPM, DEFAULT_NAMESPACE);
        setModelChildrenOrder(ProcessActionModel.ACTION, AuditModel.AUDIT, TaskHandlerModel.TASK_HANDLER, ResourceModel.RESOURCE);
    }

    /**
     * Constructor for Marshaller use (ie: V1BPMMarshaller).
     *
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1BPMComponentImplementationModel(Configuration config, Descriptor desc) {
        super(config, desc);
        for (Configuration processAction_config : config.getChildren(ProcessActionModel.ACTION)) {
            ProcessActionModel processAction = (ProcessActionModel)readModel(processAction_config);
            if (processAction != null) {
                _processActions.add(processAction);
            }
        }
        for (Configuration taskHandler_config : config.getChildren(TaskHandlerModel.TASK_HANDLER)) {
            TaskHandlerModel taskHandler = (TaskHandlerModel)readModel(taskHandler_config);
            if (taskHandler != null) {
                _taskHandlers.add(taskHandler);
            }
        }
        setModelChildrenOrder(ProcessActionModel.ACTION, AuditModel.AUDIT, TaskHandlerModel.TASK_HANDLER, ResourceModel.RESOURCE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Resource getProcessDefinition() {
        String pd = getModelAttribute(ProcessConstants.PROCESS_DEFINITION);
        if (pd != null) {
            String pdt = getModelAttribute(ProcessConstants.PROCESS_DEFINITION_TYPE);
            return new SimpleResource(pd, pdt != null ? ResourceType.valueOf(pdt) : null);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BPMComponentImplementationModel setProcessDefinition(Resource processDefinition) {
        setModelAttribute(ProcessConstants.PROCESS_DEFINITION, processDefinition != null ? processDefinition.getLocation() : null);
        ResourceType pdt = processDefinition != null ? processDefinition.getType() : null;
        setModelAttribute(ProcessConstants.PROCESS_DEFINITION_TYPE, pdt != null ? pdt.getName() : null);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getProcessId() {
        return getModelAttribute(ProcessConstants.PROCESS_ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BPMComponentImplementationModel setProcessId(String processId) {
        setModelAttribute(ProcessConstants.PROCESS_ID, processId);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessageContentInName() {
        return getModelAttribute(ProcessConstants.MESSAGE_CONTENT_IN_NAME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BPMComponentImplementationModel setMessageContentInName(String messageContentInName) {
        setModelAttribute(ProcessConstants.MESSAGE_CONTENT_IN_NAME, messageContentInName);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessageContentOutName() {
        return getModelAttribute(ProcessConstants.MESSAGE_CONTENT_OUT_NAME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BPMComponentImplementationModel setMessageContentOutName(String messageContentOutName) {
        setModelAttribute(ProcessConstants.MESSAGE_CONTENT_OUT_NAME, messageContentOutName);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ProcessActionModel> getProcessActions() {
        return Collections.unmodifiableList(_processActions);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BPMComponentImplementationModel addProcessAction(ProcessActionModel processAction) {
        addChildModel(processAction);
        _processActions.add(processAction);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TaskHandlerModel> getTaskHandlers() {
        return Collections.unmodifiableList(_taskHandlers);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BPMComponentImplementationModel addTaskHandler(TaskHandlerModel taskHandler) {
        addChildModel(taskHandler);
        _taskHandlers.add(taskHandler);
        return this;
    }

}
