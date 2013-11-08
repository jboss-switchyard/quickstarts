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
package org.switchyard.component.common.knowledge.util;

import java.lang.reflect.Constructor;
import java.util.EventListener;

import org.kie.api.event.KieRuntimeEventManager;
import org.kie.api.event.kiebase.KieBaseEventListener;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.event.rule.RuleFlowGroupActivatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.event.rule.WorkingMemoryEventListener;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.switchyard.common.type.reflect.Construction;
import org.switchyard.component.common.knowledge.CommonKnowledgeMessages;
import org.switchyard.component.common.knowledge.config.model.KnowledgeComponentImplementationModel;
import org.switchyard.component.common.knowledge.config.model.ListenerModel;
import org.switchyard.component.common.knowledge.config.model.ListenersModel;

/**
 * Listener functions.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
@SuppressWarnings("deprecation")
public final class Listeners {

    private static final Class<?>[][] PARMAMETER_TYPES = new Class<?>[][]{
        new Class<?>[]{KieRuntimeEventManager.class}, // current (kie)
        new Class<?>[]{KieRuntime.class}, // current (kie)
        /* SWITCHYARD-1755
        new Class<?>[]{KnowledgeRuntimeEventManager.class}, // legacy (drools)
        new Class<?>[]{KnowledgeRuntime.class}, // legacy (drools)
        */
        new Class<?>[0]
    };

    /**
     * Registers event listeners with a runtime event manager.
     * @param model the model
     * @param loader the class loader
     * @param manager the runtime event manager
     */
    public static void registerListeners(KnowledgeComponentImplementationModel model, ClassLoader loader, KieRuntimeEventManager manager) {
        if (manager instanceof KieSession) {
            manager.addEventListener(new DefaultAgendaEventListener() {
                @Override
                public void matchCreated(MatchCreatedEvent event) {
                    ((KieSession)event.getKieRuntime()).fireAllRules();
                }
                @Override
                public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
                    ((KieSession)event.getKieRuntime()).fireAllRules();
                }
            });
        }
        ListenersModel listenersModel = model.getListeners();
        if (listenersModel != null) {
            for (ListenerModel listenerModel : listenersModel.getListeners()) {
                @SuppressWarnings("unchecked")
                Class<? extends EventListener> listenerClass = (Class<? extends EventListener>)listenerModel.getClazz(loader);
                if (listenerClass == null) {
                    throw CommonKnowledgeMessages.MESSAGES.couldNotLoadListenerClass(listenerModel.getModelConfiguration().getAttribute("class"));
                }
                registerListener(listenerClass, manager);
            }
        }
    }

    private static void registerListener(Class<? extends EventListener> listenerClass, KieRuntimeEventManager manager) {
        Constructor<? extends EventListener> constructor = getConstructor(listenerClass);
        Class<?>[] parameterTypes = constructor != null ? constructor.getParameterTypes() : new Class<?>[0];
        try {
            EventListener listener;
            if (parameterTypes.length == 0) {
                listener = Construction.construct(listenerClass);
                // manual registration
                registerListener(listener, manager);
            } else if (parameterTypes.length == 1) {
                // automatic registration
                listener = Construction.construct(listenerClass, parameterTypes, new Object[]{manager});
            }
        } catch (Throwable t) {
            throw CommonKnowledgeMessages.MESSAGES.couldNotInstantiateListenerClass(listenerClass.getName());
        }
    }

    private static Constructor<? extends EventListener> getConstructor(Class<? extends EventListener> listenerClass) {
        Constructor<? extends EventListener> constructor = null;
        for (Class<?>[] parameterTypes : PARMAMETER_TYPES) {
            try {
                constructor = listenerClass.getConstructor(parameterTypes);
                if (constructor != null) {
                    break;
                }
            } catch (Throwable t) {
                // keep checkstyle happy ("at least one statement")
                t.getMessage();
            }
        }
        return constructor;
    }

    /**
     * Registers an event listener with a runtime event manager.
     * @param listener the event listener
     * @param manager the runtime event manager
     */
    public static void registerListener(EventListener listener, KieRuntimeEventManager manager) {
        // BASE
        if (listener instanceof KieBaseEventListener) {
            // current (kie)
            if (manager instanceof StatelessKieSession) {
                ((StatelessKieSession)manager).getKieBase().addEventListener((KieBaseEventListener)listener);
            } else if (manager instanceof KieRuntime) { // includes KieSession (stateful and command)
                ((KieRuntime)manager).getKieBase().addEventListener((KieBaseEventListener)listener);
            }
        }/* else if (listener instanceof org.drools.core.event.RuleBaseEventListener) {
            // legacy (drools)
            org.drools.core.event.RuleBaseEventListener droolsListener = (org.drools.core.event.RuleBaseEventListener)listener;
            if (manager instanceof StatelessKnowledgeSessionImpl) {
                ((StatelessKnowledgeSessionImpl)manager).getRuleBase().addEventListener(droolsListener);
            } else if (manager instanceof StatefulKnowledgeSessionImpl) {
                ((StatefulKnowledgeSessionImpl)manager).getInternalWorkingMemory().addEventListener(droolsListener);
            } else if (manager instanceof CommandBasedStatefulKnowledgeSession) {
                getInternalWorkingMemory((CommandBasedStatefulKnowledgeSession)manager).addEventListener(droolsListener);
            }
        }*/
        // AGENDA
        if (listener instanceof AgendaEventListener) {
            // current (kie)
            manager.addEventListener((AgendaEventListener)listener);
        }/* else if (listener instanceof org.drools.core.event.AgendaEventListener) {
            // legacy (drools)
            org.drools.core.event.AgendaEventListener droolsListener = (org.drools.core.event.AgendaEventListener)listener;
            if (manager instanceof StatelessKnowledgeSessionImpl) {
                ((StatelessKnowledgeSessionImpl)manager).addAgendaEventListener(droolsListener);
            } else if (manager instanceof StatefulKnowledgeSessionImpl) {
                ((StatefulKnowledgeSessionImpl)manager).getInternalWorkingMemory().addEventListener(droolsListener);
            } else if (manager instanceof CommandBasedStatefulKnowledgeSession) {
                getInternalWorkingMemory((CommandBasedStatefulKnowledgeSession)manager).addEventListener(droolsListener);
            }
        }*/
        // RULE RUNTIME / WORKING MEMORY
        if (listener instanceof RuleRuntimeEventListener) {
            // current (kie)
            manager.addEventListener((RuleRuntimeEventListener)listener);
        } else if (listener instanceof WorkingMemoryEventListener) {
            // deprecated (kie)
            manager.addEventListener((WorkingMemoryEventListener)listener);
        }/* else if (listener instanceof org.drools.core.event.WorkingMemoryEventListener) {
            // legacy (drools)
            org.drools.core.event.WorkingMemoryEventListener droolsListener = (org.drools.core.event.WorkingMemoryEventListener)listener;
            if (manager instanceof StatelessKnowledgeSessionImpl) {
                ((StatelessKnowledgeSessionImpl)manager).addWorkingMemoryEventListener(droolsListener);
            } else if (manager instanceof StatefulKnowledgeSessionImpl) {
                ((StatefulKnowledgeSessionImpl)manager).getInternalWorkingMemory().addEventListener(droolsListener);
            } else if (manager instanceof CommandBasedStatefulKnowledgeSession) {
                getInternalWorkingMemory((CommandBasedStatefulKnowledgeSession)manager).addEventListener(droolsListener);
            }
        }*/
        // PROCESS
        if (listener instanceof ProcessEventListener) {
            // current (kie)
            manager.addEventListener((ProcessEventListener)listener);
        }   // legacy (drools) - N/A
    }

    /*
    private static InternalWorkingMemory getInternalWorkingMemory(CommandBasedStatefulKnowledgeSession cmdSession) {
        KieSession kieSession = ((KnowledgeCommandContext)cmdSession.getCommandService().getContext()).getKieSession();
        return ((StatefulKnowledgeSessionImpl)kieSession).getInternalWorkingMemory();
    }
    */

    private Listeners() {}

}
