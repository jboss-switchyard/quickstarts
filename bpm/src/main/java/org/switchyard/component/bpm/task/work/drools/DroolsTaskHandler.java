/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
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
package org.switchyard.component.bpm.task.work.drools;

import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.switchyard.component.bpm.task.work.BaseTaskHandler;
import org.switchyard.component.bpm.task.work.Task;
import org.switchyard.component.bpm.task.work.TaskManager;

/**
 * A Drools TaskHandler implementation.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class DroolsTaskHandler extends BaseTaskHandler {

    private WorkItemHandler _workItemHandler;

    /**
     * Constructs a new DroolsTaskHandler.
     */
    public DroolsTaskHandler() {
        super();
    }

    /**
     * Constructs a new DroolsTaskHandler with the specified name.
     * @param name the specified name
     */
    public DroolsTaskHandler(String name) {
        super(name);
    }

    /**
     * Gets the WorkItemHandler.
     * @return the WorkItemHandler
     */
    public WorkItemHandler getWorkItemHandler() {
        return _workItemHandler;
    }

    /**
     * Sets the WorkItemHandler.
     * @param workItemHandler the WorkItemHandler
     * @return this handler (useful for chaining)
     */
    public DroolsTaskHandler setWorkItemHandler(WorkItemHandler workItemHandler) {
        _workItemHandler = workItemHandler;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void executeTask(Task task, TaskManager manager) {
        WorkItem workItem = ((DroolsTask)task).getWorkItem();
        WorkItemManager workItemManager = ((DroolsTaskManager)manager).getProcessRuntime().getWorkItemManager();
        _workItemHandler.executeWorkItem(workItem, workItemManager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void abortTask(Task task, TaskManager manager) {
        WorkItem workItem = ((DroolsTask)task).getWorkItem();
        WorkItemManager workItemManager = ((DroolsTaskManager)manager).getProcessRuntime().getWorkItemManager();
        _workItemHandler.abortWorkItem(workItem, workItemManager);
    }

}
