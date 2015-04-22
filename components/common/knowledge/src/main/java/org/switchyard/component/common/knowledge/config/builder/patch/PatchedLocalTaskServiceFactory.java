/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.switchyard.component.common.knowledge.config.builder.patch;

import org.jbpm.runtime.manager.impl.factory.LocalTaskServiceFactory;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.task.TaskService;

/**
 * PatchedLocalTaskServiceFactory.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
public class PatchedLocalTaskServiceFactory extends LocalTaskServiceFactory {

    /**
     * Creates a new PatchedLocalTaskServiceFactory.
     * @param runtimeEnvironment the RuntimeEnvironment
     */
    public PatchedLocalTaskServiceFactory(RuntimeEnvironment runtimeEnvironment) {
        super(runtimeEnvironment);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TaskService newTaskService() {
        TaskService taskService;
        try {
            taskService = super.newTaskService();
        } catch (IllegalStateException ise) {
            // There is a bug in TaskDeadlinesServiceImpl.initialize where there is no
            // TaskPersistenceContext available on initialization, so it always fails the
            // first time. We don't need theTaskDeadlinesServiceImpl to work the first time,
            // so we try again, since by then the initialization is already done.
            taskService = super.newTaskService();
        }
        return taskService;
    }

}
