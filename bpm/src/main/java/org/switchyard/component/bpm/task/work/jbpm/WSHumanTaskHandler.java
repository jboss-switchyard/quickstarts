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
package org.switchyard.component.bpm.task.work.jbpm;

import org.drools.runtime.KnowledgeRuntime;
import org.drools.runtime.process.WorkItemHandler;
import org.switchyard.component.bpm.task.work.Task;
import org.switchyard.component.bpm.task.work.TaskManager;
import org.switchyard.component.bpm.task.work.drools.DroolsTaskHandler;
import org.switchyard.component.bpm.task.work.drools.DroolsTaskManager;

/**
 * Wraps a jBPM {@link org.jbpm.process.workitem.wsht.WSHumanTaskHandler WSHumanTaskHandler}.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class WSHumanTaskHandler extends DroolsTaskHandler {

    /**
     * The default name for this TaskHandler.
     */
    public static final String HUMAN_TASK = "Human Task";

    private Connector _connector = null;

    /**
     * Constructs a new WSHumanTaskHandler with the default name.
     */
    public WSHumanTaskHandler() {
        super(HUMAN_TASK);
    }

    /**
     * Constructs a new WSHumanTaskHandler with the specified name.
     * @param name the specified name
     */
    public WSHumanTaskHandler(String name) {
        super(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        if (_connector != null) {
            _connector.dispose();
        }
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

    protected synchronized void init(TaskManager manager) {
        if (_connector == null) {
            KnowledgeRuntime kruntime = (KnowledgeRuntime)((DroolsTaskManager)manager).getProcessRuntime();
            _connector = newConnector(kruntime);
            _connector.connect();
            setWorkItemHandler(_connector.getWorkItemHandler());
        }
    }

    protected Connector newConnector(KnowledgeRuntime kruntime) {
        final org.jbpm.process.workitem.wsht.WSHumanTaskHandler handler = new org.jbpm.process.workitem.wsht.WSHumanTaskHandler(kruntime);
        return new Connector(handler) {
            @Override
            public void doConnect() throws Exception {
                handler.connect();
            }
            @Override
            public void doDispose() throws Exception {
                handler.dispose();
            }
        };
    }

    protected static abstract class Connector {

        private final WorkItemHandler _handler;

        public Connector(WorkItemHandler handler) {
            _handler = handler;
        }

        public final WorkItemHandler getWorkItemHandler() {
            return _handler;
        }

        public final void connect() {
            boolean ready = false;
            int attempts = 0;
            Exception exception = null;
            while (!ready) {
                try {
                    doConnect();
                    ready = true;
                } catch (Throwable t) {
                    try {
                        dispose();
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        if (exception == null) {
                            exception = e;
                        }
                    }
                    attempts++;
                    ready = attempts > 9;
                }
            }
            if (exception != null) {
                throw new RuntimeException(exception);
            }
        }

        public abstract void doConnect() throws Exception;

        public final void dispose() {
            try {
                doDispose();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public abstract void doDispose() throws Exception;

    }

}
