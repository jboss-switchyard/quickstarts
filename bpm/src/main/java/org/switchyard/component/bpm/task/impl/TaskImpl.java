/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.component.bpm.task.impl;

import org.jbpm.task.Status;
import org.jbpm.task.query.TaskSummary;
import org.switchyard.component.bpm.task.Task;
import org.switchyard.component.bpm.task.TaskStatus;

/**
 * A jBPM task.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class TaskImpl implements Task {

    private final TaskSummary _wrappedTaskSummary;
    private final org.jbpm.task.Task _wrappedTask;

    /**
     * Creates a new jBPM task.
     * @param taskSummary the jBPM task summary to wrap
     * @param task the jBPM task to wrap
     */
    public TaskImpl(TaskSummary taskSummary, org.jbpm.task.Task task) {
        _wrappedTaskSummary = taskSummary;
        _wrappedTask = task;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getId() {
        return Long.valueOf(_wrappedTaskSummary.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return _wrappedTaskSummary.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TaskStatus getStatus() {
        Status status = _wrappedTaskSummary.getStatus();
        switch (status) {
            case Created:
                return TaskStatus.CREATED;
            case Ready:
                return TaskStatus.READY;
            case Reserved:
                return TaskStatus.RESERVED;
            case InProgress:
                return TaskStatus.IN_PROGRESS;
            case Suspended:
                return TaskStatus.SUSPENDED;
            case Completed:
                return TaskStatus.COMPLETED;
            case Failed:
                return TaskStatus.FAILED;
            case Error:
                return TaskStatus.ERROR;
            case Exited:
                return TaskStatus.EXITED;
            case Obsolete:
                return TaskStatus.OBSOLETE;
            default:
                return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getProcessInstanceId() {
        return _wrappedTaskSummary.getProcessInstanceId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getTaskContentId() {
        return _wrappedTask.getTaskData().getDocumentContentId();
    }

}
