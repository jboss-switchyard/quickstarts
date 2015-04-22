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
package org.switchyard.component.common.knowledge.config.items;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.kie.api.event.kiebase.KieBaseEventListener;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.task.TaskLifeCycleEventListener;
import org.kie.internal.runtime.manager.InternalRuntimeManager;

/**
 * NoopExtendedRegisterableItemsFactory.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
public class NoopExtendedRegisterableItemsFactory implements ExtendedRegisterableItemsFactory {

    private InternalRuntimeManager _manager;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AgendaEventListener> getAgendaEventListeners(RuntimeEngine runtime) {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> getGlobals(RuntimeEngine runtime) {
        return Collections.emptyMap();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ProcessEventListener> getProcessEventListeners(RuntimeEngine runtime) {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RuleRuntimeEventListener> getRuleRuntimeEventListeners(RuntimeEngine runtime) {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TaskLifeCycleEventListener> getTaskListeners() {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {
        return Collections.emptyMap();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<KieBaseEventListener> getKieBaseEventListeners(RuntimeEngine runtime) {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InternalRuntimeManager getRuntimeManager() {
        return _manager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRuntimeManager(InternalRuntimeManager manager) {
        _manager = manager;
    }

}
