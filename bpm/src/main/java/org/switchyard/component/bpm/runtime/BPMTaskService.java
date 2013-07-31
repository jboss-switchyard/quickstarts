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

import javax.persistence.EntityManagerFactory;

import org.jbpm.services.task.HumanTaskServiceFactory;
import org.jbpm.shared.services.api.JbpmServicesTransactionManager;
import org.jbpm.shared.services.impl.events.JbpmServicesEventListener;
import org.kie.api.runtime.Environment;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.ContentMarshallerContext;
import org.kie.internal.task.api.EventService;
import org.kie.internal.task.api.InternalTaskService;
import org.kie.internal.task.api.UserGroupCallback;
import org.kie.internal.task.api.model.NotificationEvent;
import org.switchyard.component.bpm.transaction.AS7TransactionHelper;
import org.switchyard.component.common.knowledge.util.Environments;

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
         * @param environment the environment
         * @param entityManagerFactory the entity manager factory
         * @param jbpmServicesTransactionManager the jbpm services transaction manager
         * @param userGroupCallback the user group callback
         * @param loader the classloader
         * @return the bpm task service
         */
        public static final BPMTaskService newTaskService(
                Environment environment,
                EntityManagerFactory entityManagerFactory,
                JbpmServicesTransactionManager jbpmServicesTransactionManager,
                UserGroupCallback userGroupCallback,
                ClassLoader loader) {
            InternalTaskService internalTaskService = (InternalTaskService)HumanTaskServiceFactory.newTaskServiceConfigurator()
                    .entityManagerFactory(entityManagerFactory)
                    .transactionManager(jbpmServicesTransactionManager)
                    .userGroupCallback(userGroupCallback)
                    .getTaskService();
            String deploymentId = (String)environment.get(Environments.DEPLOYMENT_ID);
            internalTaskService.addMarshallerContext(deploymentId, new ContentMarshallerContext(environment, loader));
            InvocationHandler invocationHandler = new TaskServiceInvocationHandler(internalTaskService);
            return (BPMTaskService)Proxy.newProxyInstance(BPMTaskService.class.getClassLoader(), new Class[]{BPMTaskService.class}, invocationHandler);
        }

        private static final class TaskServiceInvocationHandler implements InvocationHandler {

            private final InternalTaskService _internalTaskService;

            private TaskServiceInvocationHandler(InternalTaskService internalTaskService) {
                _internalTaskService = internalTaskService;
            }

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Object ret;
                AS7TransactionHelper utx = new AS7TransactionHelper(true);
                try {
                    utx.begin();
                    ret = method.invoke(_internalTaskService, args);
                    utx.commit();
                } catch (Throwable t) {
                    utx.rollback();
                    throw t;
                }
                return ret;
            }

        }

    }

}
