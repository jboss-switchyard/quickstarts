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
package org.switchyard.component.bpm.jbpm;

import org.drools.runtime.KnowledgeRuntime;
import org.switchyard.component.bpm.drools.DroolsTaskHandler;
import org.switchyard.component.bpm.task.Task;
import org.switchyard.component.bpm.task.TaskManager;

/**
 * Wraps a jBPM {@link org.jbpm.process.workitem.wsht.CommandBasedWSHumanTaskHandler CommandBasedWSHumanTaskHandler}.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class CommandBasedWSHumanTaskHandler extends DroolsTaskHandler {

    /**
     * The default name for this TaskHandler.
     */
    public static final String HUMAN_TASK = "Human Task";

    private org.jbpm.process.workitem.wsht.CommandBasedWSHumanTaskHandler _wshth;

    /**
     * Constructs a new CommandBasedWSHumanTaskHandler with the default name.
     */
    public CommandBasedWSHumanTaskHandler() {
        super(HUMAN_TASK);
    }

    /**
     * Constructs a new CommandBasedWSHumanTaskHandler with the specified name.
     * @param name the specified name
     */
    public CommandBasedWSHumanTaskHandler(String name) {
        super(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void executeTask(Task task, TaskManager taskManager) {
        connect();
        super.executeTask(task, taskManager);
        taskManager.completeTask(task.getId(), task.getResults());
        disconnect();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void abortTask(Task task, TaskManager taskManager) {
        connect();
        super.abortTask(task, taskManager);
        taskManager.abortTask(task.getId());
        disconnect();
    }

    private void connect() {
        KnowledgeRuntime kruntime = (KnowledgeRuntime)getProcessRuntime();
        _wshth = new org.jbpm.process.workitem.wsht.CommandBasedWSHumanTaskHandler(kruntime) {
            @Override
            public void connect() {
                boolean ready = false;
                int attempts = 0;
                while (!ready) {
                    try {
                        setClient(null);
                        super.connect();
                        ready = true;
                    } catch (Throwable t) {
                        try {
                            dispose();
                            Thread.sleep(1000);
                        } catch (Throwable ignore) {
                            // here to keep checkstyle happy ("Must have at least one statement.")
                            ignore.getMessage();
                        }
                        attempts++;
                        ready = attempts > 9;
                    }
                }
            }
        };
        setWorkItemHandler(_wshth);
    }

    private void disconnect() {
        if (_wshth != null) {
            try {
                _wshth.dispose();
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                _wshth = null;
                setWorkItemHandler(null);
            }
        }
    }

}
