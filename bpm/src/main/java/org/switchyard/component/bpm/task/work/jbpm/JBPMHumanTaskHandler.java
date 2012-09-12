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
package org.switchyard.component.bpm.task.work.jbpm;

import org.drools.runtime.KnowledgeRuntime;
import org.switchyard.component.bpm.task.work.Task;
import org.switchyard.component.bpm.task.work.TaskManager;
import org.switchyard.component.bpm.task.work.drools.DroolsTaskHandler;
import org.switchyard.component.bpm.task.work.drools.DroolsTaskManager;

/**
 * Wraps a {@link org.switchyard.component.bpm.task.work.jbpm.JBPMHTWorkItemHandler JBPMHTWorkItemHandler}.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2012 Red Hat Inc.
 */
public class JBPMHumanTaskHandler extends DroolsTaskHandler {

    /**
     * The default name for this TaskHandler.
     */
    public static final String HUMAN_TASK = "Human Task";

    /**
     * Constructs a new JBPMHumanTaskHandler with the default name.
     */
    public JBPMHumanTaskHandler() {
        super(HUMAN_TASK);
    }

    /**
     * Constructs a new JBPMHumanTaskHandler with the specified name.
     * @param name the specified name
     */
    public JBPMHumanTaskHandler(String name) {
        super(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void executeTask(Task task, TaskManager manager) {
        init(manager);
        super.executeTask(task, manager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void abortTask(Task task, TaskManager manager) {
        init(manager);
        super.abortTask(task, manager);
    }

    private synchronized void init(TaskManager manager) {
        if (getWorkItemHandler() == null) {
            KnowledgeRuntime kruntime = (KnowledgeRuntime)((DroolsTaskManager)manager).getProcessRuntime();
            setWorkItemHandler(new JBPMHTWorkItemHandler(kruntime));
        }
    }
}
