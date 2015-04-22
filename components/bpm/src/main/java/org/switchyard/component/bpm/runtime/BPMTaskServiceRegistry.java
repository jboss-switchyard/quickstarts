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

import javax.xml.namespace.QName;

import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.task.TaskService;
import org.switchyard.component.common.knowledge.runtime.KnowledgeRuntimeManager;
import org.switchyard.component.common.knowledge.runtime.KnowledgeRuntimeManagerRegistry;

/**
 * BPMTaskServiceRegistry is <b>DEPRECATED</b>.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 * @deprecated Use {@link KnowledgeRuntimeManagerRegistry} instead.
 */
@Deprecated
public final class BPMTaskServiceRegistry {

    /**
     * Gets a task service.
     * @param serviceDomainName the service domain name
     * @param serviceName the service name
     * @return the task service
     */
    public static final synchronized BPMTaskService getTaskService(QName serviceDomainName, QName serviceName) {
        KnowledgeRuntimeManager runtimeManager = KnowledgeRuntimeManagerRegistry.getRuntimeManager(serviceDomainName, serviceName);
        if (runtimeManager != null) {
            RuntimeEngine runtimeEngine = runtimeManager.getRuntimeEngine();
            if (runtimeEngine != null) {
                final TaskService taskService = runtimeEngine.getTaskService();
                if (taskService != null) {
                    InvocationHandler ih = new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                           return method.invoke(taskService, args);
                        }
                    };
                    return (BPMTaskService)Proxy.newProxyInstance(BPMTaskService.class.getClassLoader(), new Class[]{BPMTaskService.class}, ih);
                }
            }
        }
        return null;
    }

    /**
     * Puts a task service.
     * @param serviceDomainName the service domain name
     * @param serviceName the service name
     * @param taskService the task service
     */
    public static final synchronized void putTaskService(QName serviceDomainName, QName serviceName, BPMTaskService taskService) {
        // deprecated
    }

    /**
     * Removes a task service.
     * @param serviceDomainName the service domain name
     * @param serviceName the service name
     */
    public static final synchronized void removeTaskService(QName serviceDomainName, QName serviceName) {
        // deprecated
    }

    private BPMTaskServiceRegistry() {}

}
