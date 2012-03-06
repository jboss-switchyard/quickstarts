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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.HashMap;
import java.util.Map;

import org.drools.SystemEventListenerFactory;
import org.drools.runtime.KnowledgeRuntime;
import org.jbpm.eventmessaging.EventResponseHandler;
import org.jbpm.eventmessaging.Payload;
import org.jbpm.task.Content;
import org.jbpm.task.Status;
import org.jbpm.task.Task;
import org.jbpm.task.event.TaskCompletedEvent;
import org.jbpm.task.event.TaskEvent;
import org.jbpm.task.event.TaskEventKey;
import org.jbpm.task.event.TaskFailedEvent;
import org.jbpm.task.event.TaskSkippedEvent;
import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.TaskClientHandler.GetContentResponseHandler;
import org.jbpm.task.service.TaskClientHandler.GetTaskResponseHandler;
import org.jbpm.task.service.mina.MinaTaskClientConnector;
import org.jbpm.task.service.mina.MinaTaskClientHandler;
import org.jbpm.task.service.responsehandlers.AbstractBaseResponseHandler;
import org.switchyard.common.type.Classes;
import org.switchyard.common.type.reflect.FieldAccess;

/**
 * Wraps a jBPM {@link org.jbpm.process.workitem.wsht.CommandBasedWSHumanTaskHandler CommandBasedWSHumanTaskHandler}.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class CommandBasedWSHumanTaskHandler extends WSHumanTaskHandler {

    /**
     * Constructs a new CommandBasedWSHumanTaskHandler with the default name.
     */
    public CommandBasedWSHumanTaskHandler() {
        super();
    }

    /**
     * Constructs a new CommandBasedWSHumanTaskHandler with the specified name.
     * @param name the specified name
     */
    public CommandBasedWSHumanTaskHandler(String name) {
        super(name);
    }

    @Override
    protected Connector newConnector(KnowledgeRuntime kruntime) {
        final TCCLCommandBasedWSHumanTaskHandler handler = new TCCLCommandBasedWSHumanTaskHandler(kruntime, getLoader());
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

    private static final class TCCLCommandBasedWSHumanTaskHandler extends org.jbpm.process.workitem.wsht.CommandBasedWSHumanTaskHandler {

        private final KnowledgeRuntime _session;
        private final ClassLoader _loader;
        private TaskClient _client = null;

        private TCCLCommandBasedWSHumanTaskHandler(KnowledgeRuntime session, ClassLoader loader) {
            super(session);
            _session = session;
            _loader = loader;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void connect() {
            if (_client == null) {
                _client = new TaskClient(new MinaTaskClientConnector("org.drools.process.workitem.wsht.WSHumanTaskHandler", new MinaTaskClientHandler(SystemEventListenerFactory.getSystemEventListener())));
                boolean connected = _client.connect("127.0.0.1", 9123);
                if (!connected) {
                    throw new IllegalArgumentException("Could not connect task client");
                }
                TaskEventKey key = new TaskEventKey(TaskCompletedEvent.class, -1);
                EventResponseHandler eventResponseHandler = proxyHandler(new TaskCompletedHandler(), _loader);
                _client.registerForEvent(key, false, eventResponseHandler);
                key = new TaskEventKey(TaskFailedEvent.class, -1);
                _client.registerForEvent(key, false, eventResponseHandler);
                key = new TaskEventKey(TaskSkippedEvent.class, -1);
                _client.registerForEvent(key, false, eventResponseHandler);
                new FieldAccess<TaskClient>(org.jbpm.process.workitem.wsht.CommandBasedWSHumanTaskHandler.class, "client").write(this, _client);
            }
        }

        private class TaskCompletedHandler extends AbstractBaseResponseHandler implements EventResponseHandler {

            public void execute(Payload payload) {
                TaskEvent event = ( TaskEvent ) payload.get();
                long taskId = event.getTaskId();
                GetTaskResponseHandler getTaskResponseHandler = new GetCompletedTaskResponseHandler();
                _client.getTask(taskId, getTaskResponseHandler);
            }

            public boolean isRemove() {
                return false;
            }

        }

        private class GetCompletedTaskResponseHandler extends AbstractBaseResponseHandler implements GetTaskResponseHandler {

            public void execute(Task task) {
                long workItemId = task.getTaskData().getWorkItemId();
                if (task.getTaskData().getStatus() == Status.Completed) {
                    String userId = task.getTaskData().getActualOwner().getId();
                    Map<String, Object> results = new HashMap<String, Object>();
                    results.put("ActorId", userId);
                    long contentId = task.getTaskData().getOutputContentId();
                    if (contentId != -1) {
                        GetContentResponseHandler getContentResponseHandler = new GetResultContentResponseHandler(task, results);
                        _client.getContent(contentId, getContentResponseHandler);
                    } else {
                        _session.getWorkItemManager().completeWorkItem(workItemId, results);
                    }
                } else {
                    _session.getWorkItemManager().abortWorkItem(workItemId);
                }
            }

        }

        private class GetResultContentResponseHandler extends AbstractBaseResponseHandler implements GetContentResponseHandler {

            private final Task _task;
            private final Map<String, Object> _results;

            public GetResultContentResponseHandler(Task task, Map<String, Object> results) {
                _task = task;
                _results = results;
            }

            public void execute(Content content) {
                ByteArrayInputStream bis = new ByteArrayInputStream(content.getContent());
                ObjectInputStream in;
                try {
                    in = new ObjectInputStream(bis) {
                        // This override is the whole point of copying so much code. :(
                        @Override
                        protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
                            Class<?> clazz = Classes.forName(desc.getName(), getClass());
                            return clazz != null ? clazz : super.resolveClass(desc);
                        }
                    };
                    Object result = in.readObject();
                    in.close();
                    _results.put("Result", result);
                    if (result instanceof Map) {
                        Map<?, ?> map = (Map<?, ?>)result;
                        for (Map.Entry<?, ?> entry : map.entrySet()) {
                            if (entry.getKey() instanceof String) {
                                _results.put((String) entry.getKey(), entry.getValue());
                            }
                        }
                    }
                    _session.getWorkItemManager().completeWorkItem(_task.getTaskData().getWorkItemId(), _results);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

        }

    }

}
