/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.switchyard.component.bpm.runtime;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.jbpm.services.task.HumanTaskServiceFactory;
import org.jbpm.services.task.exception.TaskException;
import org.jbpm.services.task.impl.TaskServiceEntryPointImpl;
import org.jbpm.services.task.rule.TaskRuleService;
import org.jbpm.services.task.utils.ContentMarshallerHelper;
import org.jbpm.shared.services.api.JbpmServicesTransactionManager;
import org.jbpm.shared.services.impl.events.JbpmServicesEventListener;
import org.kie.api.task.model.Content;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.EventService;
import org.kie.internal.task.api.InternalTaskService;
import org.kie.internal.task.api.TaskContentService;
import org.kie.internal.task.api.TaskQueryService;
import org.kie.internal.task.api.UserGroupCallback;
import org.kie.internal.task.api.model.ContentData;
import org.kie.internal.task.api.model.NotificationEvent;
import org.switchyard.common.type.reflect.Access;
import org.switchyard.common.type.reflect.FieldAccess;
import org.switchyard.component.bpm.transaction.AS7TransactionHelper;

/**
 * The BPM task service.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public interface BPMTaskService extends InternalTaskService, EventService<JbpmServicesEventListener<NotificationEvent>,JbpmServicesEventListener<Task>> {

    /**
     * The BPM task service factory.
     */
    public static final class Factory {

        /**
         * Creates a new BPM task service.
         * @param entityManagerFactory the entity manager factory
         * @param jbpmServicesTransactionManager the jbpm services transaction manager
         * @param userGroupCallback the user group callback
         * @param loader the classloader
         * @return the bpm task service
         */
        public static final BPMTaskService newTaskService(
                EntityManagerFactory entityManagerFactory,
                JbpmServicesTransactionManager jbpmServicesTransactionManager,
                UserGroupCallback userGroupCallback,
                ClassLoader loader) {
            InternalTaskService internalTaskService = (InternalTaskService)HumanTaskServiceFactory.newTaskServiceConfigurator()
                    .entityManagerFactory(entityManagerFactory)
                    .transactionManager(jbpmServicesTransactionManager)
                    .userGroupCallback(userGroupCallback)
                    .getTaskService();
            InvocationHandler invocationHandler = new TaskServiceInvocationHandler(internalTaskService, loader);
            return (BPMTaskService)Proxy.newProxyInstance(BPMTaskService.class.getClassLoader(), new Class[]{BPMTaskService.class}, invocationHandler);
        }

        private static final class TaskServiceInvocationHandler implements InvocationHandler {

            private static final Access<TaskContentService> TASK_CONTENT_SERVICE_ACCESS;
            private static final Access<TaskQueryService> TASK_QUERY_SERVICE_ACCESS;
            private static final Access<TaskRuleService> TASK_RULE_SERVICE_ACCESS;
            private static final Method GET_TASK_CONTENT_METHOD;
            static {
                try {
                    TASK_CONTENT_SERVICE_ACCESS = new FieldAccess<TaskContentService>(TaskServiceEntryPointImpl.class, "taskContentService");
                    TASK_QUERY_SERVICE_ACCESS = new FieldAccess<TaskQueryService>(TaskServiceEntryPointImpl.class, "taskQueryService");
                    TASK_RULE_SERVICE_ACCESS = new FieldAccess<TaskRuleService>(TaskServiceEntryPointImpl.class, "taskRuleService");
                    GET_TASK_CONTENT_METHOD = InternalTaskService.class.getDeclaredMethod("getTaskContent", new Class[]{long.class});
                } catch (Throwable t) {
                    throw new RuntimeException("reflection problem during initialization: " + t.getMessage(), t);
                }
            }

            private final InternalTaskService _internalTaskService;
            private final TaskContentService _taskContentService;
            private final TaskQueryService _taskQueryService;
            private final ClassLoader _loader;

            private TaskServiceInvocationHandler(InternalTaskService internalTaskService, ClassLoader loader) {
                _internalTaskService = internalTaskService;
                _taskContentService = TASK_CONTENT_SERVICE_ACCESS.read(internalTaskService);
                _taskQueryService = TASK_QUERY_SERVICE_ACCESS.read(internalTaskService);
                TaskRuleService taskRuleService = TASK_RULE_SERVICE_ACCESS.read(internalTaskService);
                TASK_RULE_SERVICE_ACCESS.write(internalTaskService, new TaskRuleServiceWrapper(taskRuleService, loader));
                _loader = loader;
            }

            @SuppressWarnings("unchecked")
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Object ret;
                AS7TransactionHelper utx = new AS7TransactionHelper(true);
                try {
                    utx.begin();
                    if (method.equals(GET_TASK_CONTENT_METHOD)) {
                        Task taskById = _taskQueryService.getTaskInstanceById((Long)args[0]);
                        Content contentById = _taskContentService.getContentById(taskById.getTaskData().getDocumentContentId());
                        Object unmarshalledObject = ContentMarshallerHelper.unmarshall(contentById.getContent(), null, _loader);
                        if (!(unmarshalledObject instanceof Map)) {
                            throw new IllegalStateException("The Task Content Needs to be a Map in order to use this method and it was: " + unmarshalledObject.getClass());
                        }
                        ret = (Map<String, Object>)unmarshalledObject;
                    } else {
                        ret = method.invoke(_internalTaskService, args);
                    }
                    utx.commit();
                } catch (Throwable t) {
                    utx.rollback();
                    throw t;
                }
                return ret;
            }

            private static final class TaskRuleServiceWrapper implements TaskRuleService {
                private final TaskRuleService _wrapped;
                private final ClassLoader _loader;
                public TaskRuleServiceWrapper(TaskRuleService wrapped, ClassLoader loader) {
                    _wrapped = wrapped;
                    _loader = loader;
                }
                @Override
                public void executeRules(Task task, String userId, ContentData contentData, String scope) throws TaskException {
                    Object params = ContentMarshallerHelper.unmarshall(contentData.getContent(), null, _loader);
                    executeRules(task, userId, params, scope);
                }
                @Override
                public void executeRules(Task task, String userId, Object params, String scope) throws TaskException {
                    _wrapped.executeRules(task, userId, params, scope);
                }
            }

        }

    }

}
