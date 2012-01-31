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
package org.switchyard.component.bpm.task.service.jbpm;

import org.jbpm.task.Status;
import org.jbpm.task.query.TaskSummary;
import org.switchyard.component.bpm.task.service.Task;
import org.switchyard.component.bpm.task.service.TaskStatus;

/**
 * A jBPM task.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class JBPMTask implements Task {

    private TaskSummary _taskSummary;

    /**
     * Creates a new jBPM task.
     * @param taskSummary the wrapped jBPM task summary
     */
    public JBPMTask(TaskSummary taskSummary) {
        _taskSummary = taskSummary;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getId() {
        return Long.valueOf(_taskSummary.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return _taskSummary.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TaskStatus getStatus() {
        Status status = _taskSummary.getStatus();
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

}
