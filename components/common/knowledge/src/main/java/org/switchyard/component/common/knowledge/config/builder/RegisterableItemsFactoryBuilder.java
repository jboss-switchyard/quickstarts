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
package org.switchyard.component.common.knowledge.config.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EventListener;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.runtime.manager.impl.RuntimeEngineImpl;
import org.kie.api.event.KieRuntimeEventManager;
import org.kie.api.event.kiebase.KieBaseEventListener;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessRuntime;
import org.kie.api.runtime.process.WorkItemHandler;
import org.switchyard.ServiceDomain;
import org.switchyard.component.common.knowledge.config.items.ExtendedRegisterableItemsFactory;
import org.switchyard.component.common.knowledge.config.items.NoopExtendedRegisterableItemsFactory;
import org.switchyard.component.common.knowledge.config.model.KnowledgeComponentImplementationModel;
import org.switchyard.component.common.knowledge.event.EventPublisherProcessEventListener;

/**
 * RegisterableItemsFactoryBuilder.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
public class RegisterableItemsFactoryBuilder extends KnowledgeBuilder {

    private final List<ListenerBuilder> _listenerBuilders;
    private final boolean _bpm;
    private final List<WorkItemHandlerBuilder> _workItemHandlerBuilders;

    /**
     * Creates a new RegisterableItemsFactoryBuilder.
     * @param classLoader classLoader
     * @param serviceDomain serviceDomain
     * @param implementationModel implementationModel
     */
    public RegisterableItemsFactoryBuilder(ClassLoader classLoader, ServiceDomain serviceDomain, KnowledgeComponentImplementationModel implementationModel) {
        super(classLoader, serviceDomain);
        _listenerBuilders = ListenerBuilder.builders(getClassLoader(), implementationModel);
        _bpm = implementationModel != null && "bpm".equals(implementationModel.getType());
        if (_bpm) {
            _workItemHandlerBuilders = WorkItemHandlerBuilder.builders(getClassLoader(), serviceDomain, implementationModel);
        } else {
            _workItemHandlerBuilders = Collections.emptyList();
        }
    }

    /**
     * Builds a ExtendedRegisterableItemsFactory.
     * @return a ExtendedRegisterableItemsFactory
     */
    public ExtendedRegisterableItemsFactory build() {
        return new NoopExtendedRegisterableItemsFactory() {
            private RuntimeEngine _runtime;
            private List<EventListener> _listeners = new ArrayList<EventListener>();
            private synchronized List<EventListener> listeners(RuntimeEngine runtime) {
                if (_runtime != runtime) {
                    _runtime = runtime;
                    _listeners.clear();
                    KieRuntimeEventManager runtimeEventManager = _runtime.getKieSession();
                    for (ListenerBuilder builder : _listenerBuilders) {
                        EventListener listener = builder.build(runtimeEventManager);
                        if (listener != null && !builder.wasAutomaticRegistration()) {
                            _listeners.add(listener);
                        }
                    }
                }
                return new ArrayList<EventListener>(_listeners);
            }
            @Override
            public List<AgendaEventListener> getAgendaEventListeners(RuntimeEngine runtime) {
                List<AgendaEventListener> list = new ArrayList<AgendaEventListener>();
                for (EventListener listener : listeners(runtime)) {
                    if (listener instanceof AgendaEventListener) {
                        list.add((AgendaEventListener)listener);
                    }
                }
                return list;
            }
            @Override
            public List<ProcessEventListener> getProcessEventListeners(RuntimeEngine runtime) {
                List<ProcessEventListener> list = new ArrayList<ProcessEventListener>();
                if (_bpm) {
                    // always add our EventPublisherProcessEventListener for bpm component
                    list.add(new EventPublisherProcessEventListener(getServiceDomain().getEventPublisher()));
                }
                for (EventListener listener : listeners(runtime)) {
                    if (listener instanceof ProcessEventListener) {
                        list.add((ProcessEventListener)listener);
                    }
                }
                return list;
            }
            @Override
            public List<RuleRuntimeEventListener> getRuleRuntimeEventListeners(RuntimeEngine runtime) {
                List<RuleRuntimeEventListener> list = new ArrayList<RuleRuntimeEventListener>();
                for (EventListener listener : listeners(runtime)) {
                    if (listener instanceof RuleRuntimeEventListener) {
                        list.add((RuleRuntimeEventListener)listener);
                    }
                }
                return list;
            }
            @Override
            public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {
                Map<String, WorkItemHandler> map = new LinkedHashMap<String, WorkItemHandler>();
                // wih builder list will always be empty for rules component
                for (WorkItemHandlerBuilder builder : _workItemHandlerBuilders) {
                    ProcessRuntime processRuntime = runtime.getKieSession();
                    RuntimeManager runtimeManager = null;
                    if (runtime instanceof RuntimeEngineImpl) {
                        runtimeManager = ((RuntimeEngineImpl)runtime).getManager();
                    }
                    String name = builder.getWorkItemHandlerName();
                    WorkItemHandler handler = builder.build(processRuntime, runtimeManager);
                    map.put(name, handler);
                }
                return map;
            }
            @Override
            public List<KieBaseEventListener> getKieBaseEventListeners(RuntimeEngine runtime) {
                List<KieBaseEventListener> list = new ArrayList<KieBaseEventListener>();
                for (EventListener listener : listeners(runtime)) {
                    if (listener instanceof KieBaseEventListener) {
                        list.add((KieBaseEventListener)listener);
                    }
                }
                return list;
            }
        };
    }

}
