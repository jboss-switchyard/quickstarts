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
import org.kie.api.event.rule.RuleFlowGroupActivatedEvent;
import org.kie.api.event.rule.WorkingMemoryEventListener;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.KieSession;
import org.kie.internal.event.KnowledgeRuntimeEventManager;
import org.kie.internal.runtime.KnowledgeRuntime;
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
public final class Listeners {

    private static final Class<?>[][] PARMAMETER_TYPES = new Class<?>[][]{
        new Class<?>[]{KieRuntimeEventManager.class},
        new Class<?>[]{KieRuntime.class},
        new Class<?>[]{KnowledgeRuntimeEventManager.class},
        new Class<?>[]{KnowledgeRuntime.class},
        new Class<?>[0]
    };

    /**
     * Registers listeners.
     * @param model the model
     * @param loader the class loader
     * @param runtimeEventManager the runtime event manager
     */
    public static void registerListeners(KnowledgeComponentImplementationModel model, ClassLoader loader, KieRuntimeEventManager runtimeEventManager) {
        if (runtimeEventManager instanceof KieSession) {
            runtimeEventManager.addEventListener(new DefaultAgendaEventListener() {
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
                registerListener(listenerClass, runtimeEventManager);
            }
        }
    }

    private static void registerListener(Class<? extends EventListener> listenerClass, KieRuntimeEventManager runtimeEventManager) {
        Constructor<? extends EventListener> constructor = getConstructor(listenerClass);
        Class<?>[] parameterTypes = constructor != null ? constructor.getParameterTypes() : new Class<?>[0];
        try {
            EventListener listener;
            if (parameterTypes.length == 0) {
                listener = Construction.construct(listenerClass);
                // manual registration
                registerListener(listener, runtimeEventManager);
            } else if (parameterTypes.length == 1) {
                // automatic registration
                listener = Construction.construct(listenerClass, parameterTypes, new Object[]{runtimeEventManager});
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
     * Registers listener.
     * @param listener the listener
     * @param runtimeEventManager the runtime event manager
     */
    public static void registerListener(EventListener listener, KieRuntimeEventManager runtimeEventManager) {
        if (listener instanceof KieBaseEventListener && runtimeEventManager instanceof KieRuntime) {
            ((KieRuntime)runtimeEventManager).getKieBase().addEventListener((KieBaseEventListener)listener);
        }
        if (listener instanceof AgendaEventListener) {
            runtimeEventManager.addEventListener((AgendaEventListener)listener);
        }
        if (listener instanceof WorkingMemoryEventListener) {
            runtimeEventManager.addEventListener((WorkingMemoryEventListener)listener);
        }
        if (listener instanceof ProcessEventListener) {
            runtimeEventManager.addEventListener((ProcessEventListener)listener);
        }
    }

    private Listeners() {}

}
