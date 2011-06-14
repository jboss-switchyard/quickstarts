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
package org.switchyard.component.bpm.drools;

import java.util.Map;

import org.drools.runtime.process.ProcessRuntime;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkflowProcessInstance;
import org.switchyard.component.bpm.task.Task;
import org.switchyard.component.bpm.task.TaskState;

/**
 * A Drools Task implementation.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class DroolsTask implements Task {

    private final ProcessRuntime _processRuntime;
    private final WorkItem _workItem;

    /**
     * Constructs a new DroolsTask.
     * @param processRuntime the specified ProcessRuntime
     * @param workItem the wrapped WorkItem
     */
    public DroolsTask(ProcessRuntime processRuntime, WorkItem workItem) {
        _processRuntime = processRuntime;
        _workItem = workItem;
    }

    ProcessRuntime getProcessRuntime() {
        return _processRuntime;
    }

    WorkItem getWorkItem() {
        return _workItem;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getId() {
        return Long.valueOf(_workItem.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return _workItem.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TaskState getState() {
        int state = _workItem.getState();
        switch (state) {
            case WorkItem.PENDING:
                return TaskState.PENDING;
            case WorkItem.ACTIVE:
                return TaskState.ACTIVE;
            case WorkItem.COMPLETED:
                return TaskState.COMPLETED;
            case WorkItem.ABORTED:
                return TaskState.ABORTED;
            default:
                return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getParameter(String name) {
        return _workItem.getParameter(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> getParameters() {
        return _workItem.getParameters();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getResult(String name) {
        return _workItem.getResult(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> getResults() {
        return _workItem.getResults();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getProcessInstanceId() {
        return Long.valueOf(_workItem.getProcessInstanceId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getProcessInstanceVariable(String name) {
        return getProcessInstance().getVariable(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Task setProcessInstanceVariable(String name, Object value) {
        getProcessInstance().setVariable(name, value);
        return this;
    }

    private WorkflowProcessInstance getProcessInstance() {
        return (WorkflowProcessInstance)_processRuntime.getProcessInstance(_workItem.getProcessInstanceId());
    }

}
