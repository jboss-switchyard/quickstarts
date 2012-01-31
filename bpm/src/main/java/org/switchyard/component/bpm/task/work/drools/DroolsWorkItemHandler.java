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
import org.switchyard.component.bpm.task.work.TaskHandler;

/**
 * A Drools WorkItemHandler implementation.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class DroolsWorkItemHandler implements WorkItemHandler {

    private final TaskHandler _handler;
    private final DroolsTaskManager _manager;

    /**
     * Constructs a new DroolsWorkItemHandler.
     * @param handler the wrapped TaskHandler
     * @param manager the wrapped TaskManager
     */
    public DroolsWorkItemHandler(TaskHandler handler, DroolsTaskManager manager) {
        _handler = handler;
        _manager = manager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
        _handler.executeTask(new DroolsTask(workItem, _manager), _manager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
        _handler.abortTask(new DroolsTask(workItem, _manager), _manager);
    }

}
