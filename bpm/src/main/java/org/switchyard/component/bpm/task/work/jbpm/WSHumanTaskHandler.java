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
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.drools.SystemEventListenerFactory;
import org.drools.runtime.KnowledgeRuntime;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.drools.task.service.ResponseHandler;
import org.jbpm.eventmessaging.EventResponseHandler;
import org.jbpm.eventmessaging.Payload;
import org.jbpm.task.Content;
import org.jbpm.task.Status;
import org.jbpm.task.event.TaskCompletedEvent;
import org.jbpm.task.event.TaskEvent;
import org.jbpm.task.event.TaskEventKey;
import org.jbpm.task.event.TaskFailedEvent;
import org.jbpm.task.event.TaskSkippedEvent;
import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.TaskClientHandler;
import org.jbpm.task.service.TaskClientHandler.GetContentResponseHandler;
import org.jbpm.task.service.TaskClientHandler.GetTaskResponseHandler;
import org.jbpm.task.service.mina.MinaTaskClientConnector;
import org.jbpm.task.service.mina.MinaTaskClientHandler;
import org.jbpm.task.service.responsehandlers.AbstractBaseResponseHandler;
import org.switchyard.common.type.Classes;
import org.switchyard.common.type.reflect.Access;
import org.switchyard.common.type.reflect.FieldAccess;
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

    private static final Class<?>[] RESPONSE_HANDLER_INTERFACES = new Class<?>[] {
        ResponseHandler.class,
        EventResponseHandler.class,
        TaskClientHandler.AddAttachmentResponseHandler.class,
        TaskClientHandler.AddCommentResponseHandler.class,
        TaskClientHandler.AddTaskResponseHandler.class,
        TaskClientHandler.DeleteAttachmentResponseHandler.class,
        TaskClientHandler.DeleteCommentResponseHandler.class,
        TaskClientHandler.GetContentResponseHandler.class,
        TaskClientHandler.GetTaskResponseHandler.class,
        TaskClientHandler.QueryGenericResponseHandler.class,
        TaskClientHandler.SetDocumentResponseHandler.class,
        TaskClientHandler.TaskOperationResponseHandler.class,
        TaskClientHandler.TaskSummaryResponseHandler.class
    };

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
        final TCCLWSHumanTaskHandler handler = new TCCLWSHumanTaskHandler(kruntime, getLoader());
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

    private static final class TCCLWSHumanTaskHandler extends org.jbpm.process.workitem.wsht.WSHumanTaskHandler {

        private final ClassLoader _loader;
        private TaskClient _client = null;
        private WorkItemManager _manager = null;
        private boolean _initialized = false;

        private TCCLWSHumanTaskHandler(KnowledgeRuntime session, ClassLoader loader) {
            super(session);
            _loader = loader;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void connect() {
            if (!_initialized) {
                Access<TaskClient> clientAccess = new FieldAccess<TaskClient>(org.jbpm.process.workitem.wsht.WSHumanTaskHandler.class, "client");
                _client = clientAccess.read(this);
                if (_client == null) {
                    _client = new TaskClient(new MinaTaskClientConnector("org.drools.process.workitem.wsht.WSHumanTaskHandler", new MinaTaskClientHandler(SystemEventListenerFactory.getSystemEventListener())));
                    boolean connected = _client.connect("127.0.0.1", 9123);
                    if (!connected) {
                        throw new IllegalArgumentException("Could not connect task client");
                    }
                }
                TaskEventKey key = new TaskEventKey(TaskCompletedEvent.class, -1);
                _manager = new FieldAccess<WorkItemManager>(WorkItemManager.class, "manager").read(this);
                EventResponseHandler eventResponseHandler = proxyHandler(new TaskCompletedHandler(_manager, _client), _loader);
                _client.registerForEvent(key, false, eventResponseHandler);
                key = new TaskEventKey(TaskFailedEvent.class, -1);
                _client.registerForEvent(key, false, eventResponseHandler);
                key = new TaskEventKey(TaskSkippedEvent.class, -1);
                _client.registerForEvent(key, false, eventResponseHandler);
                clientAccess.write(this, _client);
                _initialized = true;
                new FieldAccess<Boolean>(org.jbpm.process.workitem.wsht.WSHumanTaskHandler.class, "initialized").write(this, _initialized);
            }
        }

    }

    private static class TaskCompletedHandler extends AbstractBaseResponseHandler implements EventResponseHandler {

        private WorkItemManager _manager;
        private TaskClient _client;

        public TaskCompletedHandler(WorkItemManager manager, TaskClient client) {
            _manager = manager;
            _client = client;
        }

        public void execute(Payload payload) {
            TaskEvent event = ( TaskEvent ) payload.get();
            long taskId = event.getTaskId();
            GetTaskResponseHandler getTaskResponseHandler = new GetCompletedTaskResponseHandler(_manager, _client);
            _client.getTask(taskId, getTaskResponseHandler);
        }

        public boolean isRemove() {
            return false;
        }

    }

    private static class GetCompletedTaskResponseHandler extends AbstractBaseResponseHandler implements GetTaskResponseHandler {

        private WorkItemManager _manager;
        private TaskClient _client;

        public GetCompletedTaskResponseHandler(WorkItemManager manager, TaskClient client) {
            _manager = manager;
            _client = client;
        }

        public void execute(org.jbpm.task.Task task) {
            long workItemId = task.getTaskData().getWorkItemId();
            if (task.getTaskData().getStatus() == Status.Completed) {
                String userId = task.getTaskData().getActualOwner().getId();
                Map<String, Object> results = new HashMap<String, Object>();
                results.put("ActorId", userId);
                long contentId = task.getTaskData().getOutputContentId();
                if (contentId != -1) {
                    GetContentResponseHandler getContentResponseHandler = new GetResultContentResponseHandler(_manager, task, results);
                    _client.getContent(contentId, getContentResponseHandler);
                } else {
                    _manager.completeWorkItem(workItemId, results);
                }
            } else {
                _manager.abortWorkItem(workItemId);
            }
        }

    }

    private static class GetResultContentResponseHandler extends AbstractBaseResponseHandler implements GetContentResponseHandler {

        private WorkItemManager _manager;
        private org.jbpm.task.Task _task;
        private Map<String, Object> _results;

        public GetResultContentResponseHandler(WorkItemManager manager, org.jbpm.task.Task task, Map<String, Object> results) {
            _manager = manager;
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
                    Map<?, ?> map = (Map<?, ?>) result;
                    for (Map.Entry<?, ?> entry : map.entrySet()) {
                        if (entry.getKey() instanceof String) {
                            _results.put((String) entry.getKey(), entry.getValue());
                        }
                    }
                }
                _manager.completeWorkItem(_task.getTaskData().getWorkItemId(), _results);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    @SuppressWarnings("unchecked")
    protected static <T extends ResponseHandler> T proxyHandler(T handler, ClassLoader loader) {
        return (T)Proxy.newProxyInstance(loader, RESPONSE_HANDLER_INTERFACES, new TCCLInvocationHandler(handler, loader));
    }

    private static final class TCCLInvocationHandler implements InvocationHandler {

        private final Object _wrapped;
        private final ClassLoader _loader;

        private TCCLInvocationHandler(Object wrapped, ClassLoader loader) {
            _wrapped = wrapped;
            _loader = loader;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            ClassLoader previousLoader = Classes.setTCCL(_loader);
            try {
                return method.invoke(_wrapped, args);
            } finally {
                Classes.setTCCL(previousLoader);
            }
        }

    }

    protected static abstract class Connector {

        private final WorkItemHandler _handler;

        protected Connector(WorkItemHandler handler) {
            _handler = handler;
        }

        protected final WorkItemHandler getWorkItemHandler() {
            return _handler;
        }

        protected final void connect() {
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

        protected abstract void doConnect() throws Exception;

        protected final void dispose() {
            try {
                doDispose();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        protected abstract void doDispose() throws Exception;

    }

}
