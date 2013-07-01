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

import static org.switchyard.component.bpm.config.model.BPMComponentImplementationModel.DEFAULT_NAMESPACE;
import static org.switchyard.component.bpm.config.model.WorkItemHandlerModel.WORK_ITEM_HANDLER;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import org.switchyard.component.bpm.config.model.WorkItemHandlerModel;
import org.switchyard.component.bpm.config.model.WorkItemHandlersModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * A version 1 WorkItemHandlersModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class V1WorkItemHandlersModel extends BaseModel implements WorkItemHandlersModel {

    private List<WorkItemHandlerModel> _workItemHandlers = new ArrayList<WorkItemHandlerModel>();

    /**
     * Creates a new WorkItemHandlersModel.
     */
    public V1WorkItemHandlersModel() {
        super(new QName(DEFAULT_NAMESPACE, WORK_ITEM_HANDLERS));
        setModelChildrenOrder(WORK_ITEM_HANDLER);
    }

    /**
     * Creates a new WorkItemHandlersModel with the specified configuration and descriptor.
     * @param config the configuration
     * @param desc the descriptor
     */
    public V1WorkItemHandlersModel(Configuration config, Descriptor desc) {
        super(config, desc);
        for (Configuration workItemHandler_config : config.getChildren(WORK_ITEM_HANDLER)) {
            WorkItemHandlerModel workItemHandler = (WorkItemHandlerModel)readModel(workItemHandler_config);
            if (workItemHandler != null) {
                _workItemHandlers.add(workItemHandler);
            }
        }
        setModelChildrenOrder(WORK_ITEM_HANDLER);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<WorkItemHandlerModel> getWorkItemHandlers() {
        return Collections.unmodifiableList(_workItemHandlers);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkItemHandlersModel addWorkItemHandler(WorkItemHandlerModel workItemHandler) {
        addChildModel(workItemHandler);
        _workItemHandlers.add(workItemHandler);
        return this;
    }

}
