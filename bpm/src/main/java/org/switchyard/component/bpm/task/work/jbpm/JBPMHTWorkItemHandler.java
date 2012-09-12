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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.drools.marshalling.ObjectMarshallingStrategy;
import org.drools.marshalling.ObjectMarshallingStrategy.Context;
import org.drools.marshalling.impl.ClassObjectMarshallingStrategyAcceptor;
import org.drools.marshalling.impl.SerializablePlaceholderResolverStrategy;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.KnowledgeRuntime;
import org.jbpm.eventmessaging.EventResponseHandler;
import org.jbpm.eventmessaging.Payload;
import org.jbpm.process.workitem.wsht.GenericHTWorkItemHandler;
import org.jbpm.process.workitem.wsht.MinaHTWorkItemHandler;
import org.jbpm.task.Content;
import org.jbpm.task.Status;
import org.jbpm.task.Task;
import org.jbpm.task.event.TaskCompletedEvent;
import org.jbpm.task.event.TaskEvent;
import org.jbpm.task.event.TaskEventKey;
import org.jbpm.task.event.TaskFailedEvent;
import org.jbpm.task.event.TaskSkippedEvent;
import org.jbpm.task.service.responsehandlers.AbstractBaseResponseHandler;
import org.jbpm.task.utils.ContentMarshallerContext;
import org.jbpm.task.utils.ContentMarshallerHelper;
import org.jbpm.task.utils.MarshalledContentWrapper;
import org.switchyard.common.type.Classes;
import org.switchyard.common.type.reflect.Access;
import org.switchyard.common.type.reflect.FieldAccess;

/**
 * Extends a jBPM {@link org.jbpm.process.workitem.wsht.MinaHTWorkItemHandler MinaHTWorkItemHandler}.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2012 Red Hat Inc.
 */
public class JBPMHTWorkItemHandler extends MinaHTWorkItemHandler {

    private static final Logger LOGGER = Logger.getLogger(JBPMHTWorkItemHandler.class);

    /**
     * Package-private constructor.
     * @param kruntime the KnowledgeRuntime
     * @param loader the ClassLoader
     */
    JBPMHTWorkItemHandler(KnowledgeRuntime kruntime) {
        super(kruntime);
    }

    private void registerTaskEvents() {
        TaskCompletedHandler eventResponseHandler = new TaskCompletedHandler();
        TaskEventKey key = new TaskEventKey(TaskCompletedEvent.class, -1);
        getClient().registerForEvent(key, false, eventResponseHandler);
        eventHandlers.put(key, eventResponseHandler);
        key = new TaskEventKey(TaskFailedEvent.class, -1);
        getClient().registerForEvent(key, false, eventResponseHandler);
        eventHandlers.put(key, eventResponseHandler);
        key = new TaskEventKey(TaskSkippedEvent.class, -1);
        getClient().registerForEvent(key, false, eventResponseHandler);
        eventHandlers.put(key, eventResponseHandler);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connect() {
        if (getClient() != null) {
            if (!isConnected()) {
                boolean connected = getClient().connect(getIpAddress(), getPort());
                Access<Boolean> connectedAccess = new FieldAccess<Boolean>(GenericHTWorkItemHandler.class, "connected");
                connectedAccess.write(this, Boolean.valueOf(connected));
                if (!isConnected()) {
                    throw new IllegalArgumentException("Could not connect task client: on ip: " + getIpAddress() + " - port: " + getPort());
                }
                registerTaskEvents();
            }
        }
    }

    private final class TaskCompletedHandler extends AbstractBaseResponseHandler implements EventResponseHandler {

        public void execute(Payload payload) {
            TaskEvent event = (TaskEvent)payload.get();
            final long taskId = event.getTaskId();
            if (isLocal()) {
                handleCompletedTask(taskId);
            } else {
                Runnable runnable = new Runnable() {
                    public void run() {
                        handleCompletedTask(taskId);
                    }
                };
                new Thread(runnable).start();
            }
        }

        public boolean isRemove() {
            return false;
        }

        public void handleCompletedTask(long taskId) {
            Task task = getClient().getTask(taskId);
            long workItemId = task.getTaskData().getWorkItemId();
            if (task.getTaskData().getStatus() == Status.Completed) {
                String userId = task.getTaskData().getActualOwner().getId();
                Map<String, Object> results = new HashMap<String, Object>();
                results.put("ActorId", userId);
                long contentId = task.getTaskData().getOutputContentId();
                if (contentId != -1) {
                    Content content = getClient().getContent(contentId);
                    Object result = unmarshall(task.getTaskData().getDocumentType(), content.getContent(), marshallerContext, session.getEnvironment());
                    results.put("Result", result);
                    if (result instanceof Map) {
                        Map<?, ?> map = (Map<?, ?>)result;
                        for (Map.Entry<?, ?> entry : map.entrySet()) {
                            if (entry.getKey() instanceof String) {
                                results.put((String)entry.getKey(), entry.getValue());
                            }
                        }
                    }
                    session.getWorkItemManager().completeWorkItem(task.getTaskData().getWorkItemId(), results);
                } else {
                    session.getWorkItemManager().completeWorkItem(workItemId, results);
                }
            } else {
                session.getWorkItemManager().abortWorkItem(workItemId);
            }
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static Object unmarshall(String type, byte[] content, ContentMarshallerContext marshallerContext, Environment env) {
        ObjectMarshallingStrategy[] strats = null;
        if (env != null && env.get(EnvironmentName.OBJECT_MARSHALLING_STRATEGIES) != null) {
            strats = (ObjectMarshallingStrategy[]) env.get(EnvironmentName.OBJECT_MARSHALLING_STRATEGIES);
        } else if (!marshallerContext.getStrategies().isEmpty()) {
              strats = (ObjectMarshallingStrategy[]) marshallerContext.getStrategies().toArray();
        } else {
            strats = new ObjectMarshallingStrategy[1];
            strats[0] = new SerializablePlaceholderResolverStrategy(ClassObjectMarshallingStrategyAcceptor.DEFAULT);
        }
        Object data = null;
        ObjectMarshallingStrategy selectedStrat = null;
        for (ObjectMarshallingStrategy strat : strats) {
            if (strat.getClass().getCanonicalName().equals(type)) {
                selectedStrat = strat;
            }
        }
        Context context = marshallerContext.strategyContext.get(selectedStrat.getClass());
        try {
            if (marshallerContext.isUseMarshal()) {
                data = selectedStrat.unmarshal(context, null, content, ContentMarshallerHelper.class.getClassLoader());
            } else {
                ByteArrayInputStream bs = new ByteArrayInputStream(content);
                ObjectInputStream oIn = new ObjectInputStream(bs) {
                    @Override
                    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
                        Class<?> clazz = Classes.forName(desc.getName(), getClass());
                        return clazz != null ? clazz : super.resolveClass(desc);
                    }
                };
                data = selectedStrat.read(oIn);
                oIn.close();
                bs.close();
            }
            if (data instanceof Map) {
                ByteArrayInputStream bs = null;
                ObjectInputStream oIn = null;
                Map localData = new HashMap();
                for (Object key : ((Map)data).keySet()) {
                    MarshalledContentWrapper value = (MarshalledContentWrapper) ((Map)data).get(key);
                    Object unmarshalledObj = null;
                    for (ObjectMarshallingStrategy strat : strats) {
                        if (strat.getClass().getCanonicalName().equals(value.getMarshaller())) {
                            selectedStrat = strat;
                        }
                    }
                    context = marshallerContext.strategyContext.get(selectedStrat.getClass());
                    if (marshallerContext.isUseMarshal()) {
                        unmarshalledObj = selectedStrat.unmarshal(context, null, value.getContent(), ContentMarshallerHelper.class.getClassLoader());
                    } else {
                        bs = new ByteArrayInputStream(value.getContent());
                        oIn = new ObjectInputStream(bs) {
                            @Override
                            protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
                                Class<?> clazz = Classes.forName(desc.getName(), getClass());
                                return clazz != null ? clazz : super.resolveClass(desc);
                            }
                        };
                        unmarshalledObj = selectedStrat.read(oIn);
                        oIn.close();
                        bs.close();
                    }
                    localData.put(key, unmarshalledObj);
                }
                data = localData;
            }
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
        } catch (ClassNotFoundException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        return data;
    }

}
