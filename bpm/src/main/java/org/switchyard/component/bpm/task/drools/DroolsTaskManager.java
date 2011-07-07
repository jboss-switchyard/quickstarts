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
package org.switchyard.component.bpm.task.drools;

import java.util.Map;

import org.drools.runtime.process.ProcessRuntime;
import org.drools.runtime.process.WorkItemManager;
import org.switchyard.component.bpm.task.TaskHandler;
import org.switchyard.component.bpm.task.TaskManager;

/**
 * A Drools TaskManager implementation.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class DroolsTaskManager implements TaskManager {

    private final ProcessRuntime _processRuntime;
    private final WorkItemManager _workItemManager;

    /**
     * Constructs a new DroolsTaskManager.
     * @param processRuntime the ProcessRuntime
     * @param workItemManager the wrapped WorkItemManager
     */
    public DroolsTaskManager(ProcessRuntime processRuntime, WorkItemManager workItemManager) {
        _processRuntime = processRuntime;
        _workItemManager = workItemManager;
    }

    ProcessRuntime getProcessRuntime() {
        return _processRuntime;
    }

    WorkItemManager getWorkItemManager() {
        return _workItemManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void completeTask(Long id, Map<String, Object> results) {
        _workItemManager.completeWorkItem(id.longValue(), results);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void abortTask(Long id) {
        _workItemManager.abortWorkItem(id.longValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerTaskHandler(String taskName, TaskHandler taskHandler) {
        _workItemManager.registerWorkItemHandler(taskName, new DroolsWorkItemHandler(_processRuntime, taskHandler));
    }

}
