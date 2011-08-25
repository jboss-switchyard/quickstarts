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

import static org.switchyard.component.bpm.common.ProcessConstants.MESSAGE_CONTENT_NAME;
import static org.switchyard.component.bpm.common.ProcessConstants.PROCESS_DEFINITION;
import static org.switchyard.component.bpm.common.ProcessConstants.PROCESS_DEFINITION_TYPE;
import static org.switchyard.component.bpm.common.ProcessConstants.PROCESS_ID;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.switchyard.common.io.resource.Resource;
import org.switchyard.common.io.resource.ResourceType;
import org.switchyard.common.io.resource.SimpleResource;
import org.switchyard.component.bpm.config.model.BpmComponentImplementationModel;
import org.switchyard.component.bpm.config.model.ProcessActionModel;
import org.switchyard.component.bpm.config.model.TaskHandlerModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.composite.v1.V1ComponentImplementationModel;
import org.switchyard.config.model.resource.ResourceModel;

/**
 * A "bpm" implementation of a ComponentImplementationModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class V1BpmComponentImplementationModel extends V1ComponentImplementationModel implements BpmComponentImplementationModel {

    private List<ProcessActionModel> _processActions = new ArrayList<ProcessActionModel>();
    private List<TaskHandlerModel> _taskHandlers = new ArrayList<TaskHandlerModel>();
    private List<ResourceModel> _resources = new ArrayList<ResourceModel>();

    /**
     * Default constructor for application use.
     */
    public V1BpmComponentImplementationModel() {
        super(BPM, DEFAULT_NAMESPACE);
        setModelChildrenOrder(ProcessActionModel.PROCESS_ACTION, TaskHandlerModel.TASK_HANDLER, ResourceModel.RESOURCE);
    }

    /**
     * Constructor for Marshaller use (ie: V1BpmMarshaller).
     *
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1BpmComponentImplementationModel(Configuration config, Descriptor desc) {
        super(config, desc);
        for (Configuration processAction_config : config.getChildren(ProcessActionModel.PROCESS_ACTION)) {
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
        for (Configuration resource_config : config.getChildren(ResourceModel.RESOURCE)) {
            ResourceModel resource = (ResourceModel)readModel(resource_config);
            if (resource != null) {
                _resources.add(resource);
            }
        }
        setModelChildrenOrder(ProcessActionModel.PROCESS_ACTION, TaskHandlerModel.TASK_HANDLER, ResourceModel.RESOURCE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Resource getProcessDefinition() {
        String pd = getModelAttribute(PROCESS_DEFINITION);
        if (pd != null) {
            String pdt = getModelAttribute(PROCESS_DEFINITION_TYPE);
            return new SimpleResource(pd, pdt != null ? ResourceType.valueOf(pdt) : null);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BpmComponentImplementationModel setProcessDefinition(Resource processDefinition) {
        setModelAttribute(PROCESS_DEFINITION, processDefinition != null ? processDefinition.getLocation() : null);
        ResourceType pdt = processDefinition != null ? processDefinition.getType() : null;
        setModelAttribute(PROCESS_DEFINITION_TYPE, pdt != null ? pdt.getName() : null);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getProcessId() {
        return getModelAttribute(PROCESS_ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BpmComponentImplementationModel setProcessId(String processId) {
        setModelAttribute(PROCESS_ID, processId);
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
    public BpmComponentImplementationModel setMessageContentName(String messageContentName) {
        setModelAttribute(MESSAGE_CONTENT_NAME, messageContentName);
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
    public BpmComponentImplementationModel addProcessAction(ProcessActionModel processAction) {
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
    public BpmComponentImplementationModel addTaskHandler(TaskHandlerModel taskHandler) {
        addChildModel(taskHandler);
        _taskHandlers.add(taskHandler);
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
    public BpmComponentImplementationModel addResource(ResourceModel resource) {
        addChildModel(resource);
        _resources.add(resource);
        return this;
    }

}
