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
package org.switchyard.component.bpm.config.model.v1;

import static org.switchyard.component.bpm.config.model.UserGroupCallbackModel.USER_GROUP_CALLBACK;
import static org.switchyard.component.bpm.config.model.WorkItemHandlersModel.WORK_ITEM_HANDLERS;
import static org.switchyard.component.common.knowledge.config.model.ChannelsModel.CHANNELS;
import static org.switchyard.component.common.knowledge.config.model.ListenersModel.LISTENERS;
import static org.switchyard.component.common.knowledge.config.model.LoggersModel.LOGGERS;
import static org.switchyard.component.common.knowledge.config.model.ManifestModel.MANIFEST;
import static org.switchyard.component.common.knowledge.config.model.OperationsModel.OPERATIONS;
import static org.switchyard.config.model.property.PropertiesModel.PROPERTIES;

import org.switchyard.component.bpm.config.model.BPMComponentImplementationModel;
import org.switchyard.component.bpm.config.model.UserGroupCallbackModel;
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

    private UserGroupCallbackModel _userGroupCallback;
    private WorkItemHandlersModel _workItemHandlers;

    /**
     * Default constructor for application use.
     */
    public V1BPMComponentImplementationModel() {
        super(BPM, DEFAULT_NAMESPACE);
        setModelChildrenOrder(CHANNELS, LISTENERS, LOGGERS, MANIFEST, OPERATIONS, PROPERTIES, USER_GROUP_CALLBACK, WORK_ITEM_HANDLERS);
    }

    /**
     * Constructor for Marshaller use (ie: V1BPMMarshaller).
     *
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1BPMComponentImplementationModel(Configuration config, Descriptor desc) {
        super(config, desc);
        setModelChildrenOrder(CHANNELS, LISTENERS, LOGGERS, MANIFEST, OPERATIONS, PROPERTIES, USER_GROUP_CALLBACK, WORK_ITEM_HANDLERS);
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
    public UserGroupCallbackModel getUserGroupCallback() {
        if (_userGroupCallback == null) {
            _userGroupCallback = (UserGroupCallbackModel)getFirstChildModel(USER_GROUP_CALLBACK);
        }
        return _userGroupCallback;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BPMComponentImplementationModel setUserGroupCallback(UserGroupCallbackModel userGroupCallback) {
        setChildModel(userGroupCallback);
        _userGroupCallback = userGroupCallback;
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
