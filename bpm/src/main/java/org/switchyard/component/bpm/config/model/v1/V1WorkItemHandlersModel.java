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
