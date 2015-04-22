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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.api.event.kiebase.KieBaseEventListener;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.manager.RegisterableItemsFactory;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.task.TaskLifeCycleEventListener;
import org.kie.internal.runtime.manager.InternalRegisterableItemsFactory;
import org.kie.internal.runtime.manager.InternalRuntimeManager;

/**
 * CompoundRegisterableItemsFactory.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
public class CompoundRegisterableItemsFactory implements ExtendedRegisterableItemsFactory {

    private final InternalRegisterableItemsFactory _rootFactory;
    private final List<RegisterableItemsFactory> _factories;

    /**
     * Creates a new CompoundRegisterableItemsFactory.
     * @param rootFactory the root InternalRegisterableItemsFactory
     * @param factories any additional RegisterableItemsFactory instances
     */
    public CompoundRegisterableItemsFactory(InternalRegisterableItemsFactory rootFactory, RegisterableItemsFactory... factories) {
        this(rootFactory, (factories != null ? Arrays.asList(factories) : null));
    }

    /**
     * Creates a new CompoundRegisterableItemsFactory.
     * @param rootFactory the root InternalRegisterableItemsFactory
     * @param factories any additional RegisterableItemsFactory instances
     */
    public CompoundRegisterableItemsFactory(InternalRegisterableItemsFactory rootFactory, Collection<RegisterableItemsFactory> factories) {
        _rootFactory = rootFactory;
        _factories = new ArrayList<RegisterableItemsFactory>();
        if (factories != null) {
            for (RegisterableItemsFactory f : factories) {
                if (f != null && f != rootFactory && f != this && !_factories.contains(f)) {
                    _factories.add(f);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AgendaEventListener> getAgendaEventListeners(RuntimeEngine runtime) {
        List<AgendaEventListener> list = new ArrayList<AgendaEventListener>();
        list.addAll(_rootFactory.getAgendaEventListeners(runtime));
        for (RegisterableItemsFactory f : _factories) {
            list.addAll(f.getAgendaEventListeners(runtime));
        }
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> getGlobals(RuntimeEngine runtime) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.putAll(_rootFactory.getGlobals(runtime));
        for (RegisterableItemsFactory f : _factories) {
            map.putAll(f.getGlobals(runtime));
        }
        return map;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ProcessEventListener> getProcessEventListeners(RuntimeEngine runtime) {
        List<ProcessEventListener> list = new ArrayList<ProcessEventListener>();
        list.addAll(_rootFactory.getProcessEventListeners(runtime));
        for (RegisterableItemsFactory f : _factories) {
            list.addAll(f.getProcessEventListeners(runtime));
        }
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RuleRuntimeEventListener> getRuleRuntimeEventListeners(RuntimeEngine runtime) {
        List<RuleRuntimeEventListener> list = new ArrayList<RuleRuntimeEventListener>();
        list.addAll(_rootFactory.getRuleRuntimeEventListeners(runtime));
        for (RegisterableItemsFactory f : _factories) {
            list.addAll(f.getRuleRuntimeEventListeners(runtime));
        }
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TaskLifeCycleEventListener> getTaskListeners() {
        List<TaskLifeCycleEventListener> list = new ArrayList<TaskLifeCycleEventListener>();
        list.addAll(_rootFactory.getTaskListeners());
        for (RegisterableItemsFactory f : _factories) {
            list.addAll(f.getTaskListeners());
        }
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {
        Map<String, WorkItemHandler> map = new HashMap<String, WorkItemHandler>();
        map.putAll(_rootFactory.getWorkItemHandlers(runtime));
        for (RegisterableItemsFactory f : _factories) {
            map.putAll(f.getWorkItemHandlers(runtime));
        }
        return map;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<KieBaseEventListener> getKieBaseEventListeners(RuntimeEngine runtime) {
        List<KieBaseEventListener> list = new ArrayList<KieBaseEventListener>();
        for (RegisterableItemsFactory f : _factories) {
            if (f instanceof ExtendedRegisterableItemsFactory) {
                ExtendedRegisterableItemsFactory e = (ExtendedRegisterableItemsFactory)f;
                list.addAll(e.getKieBaseEventListeners(runtime));
            }
        }
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InternalRuntimeManager getRuntimeManager() {
        return _rootFactory.getRuntimeManager();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRuntimeManager(InternalRuntimeManager manager) {
        _rootFactory.setRuntimeManager(manager);
    }

}
