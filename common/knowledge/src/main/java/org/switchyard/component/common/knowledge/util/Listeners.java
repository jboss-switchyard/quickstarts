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
package org.switchyard.component.common.knowledge.util;

import java.lang.reflect.Constructor;
import java.util.EventListener;

import org.drools.core.WorkingMemoryEventManager;
import org.drools.core.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.drools.core.event.AgendaEventListener;
import org.drools.core.event.RuleBaseEventListener;
import org.drools.core.event.WorkingMemoryEventListener;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.impl.StatelessKnowledgeSessionImpl;
import org.drools.core.reteoo.ReteooWorkingMemoryInterface;
import org.drools.core.runtime.process.InternalProcessRuntime;
import org.kie.api.event.KieRuntimeEventManager;
import org.kie.api.event.kiebase.KieBaseEventListener;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.event.rule.RuleFlowGroupActivatedEvent;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.KieSession;
import org.kie.internal.event.KnowledgeRuntimeEventManager;
import org.kie.internal.runtime.KnowledgeRuntime;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.switchyard.common.type.reflect.Construction;
import org.switchyard.component.common.knowledge.config.model.KnowledgeComponentImplementationModel;
import org.switchyard.component.common.knowledge.config.model.ListenerModel;
import org.switchyard.component.common.knowledge.config.model.ListenersModel;
import org.switchyard.exception.SwitchYardException;

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
        if (runtimeEventManager instanceof StatefulKnowledgeSession) {
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
                    throw new SwitchYardException("Could not load listener class: " + listenerModel.getModelConfiguration().getAttribute("class"));
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
            throw new SwitchYardException("Could not instantiate listener class: " + listenerClass.getName());
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
        if (runtimeEventManager instanceof StatefulKnowledgeSessionImpl) {
            StatefulKnowledgeSessionImpl sksi = (StatefulKnowledgeSessionImpl)runtimeEventManager;
            WorkingMemoryEventManager wmem = sksi.session;
            if (listener instanceof WorkingMemoryEventListener) {
                wmem.addEventListener((WorkingMemoryEventListener)listener);
            } else if (listener instanceof org.kie.api.event.rule.WorkingMemoryEventListener) {
                runtimeEventManager.addEventListener((org.kie.api.event.rule.WorkingMemoryEventListener)listener);
            }
            if (listener instanceof AgendaEventListener) {
                wmem.addEventListener((AgendaEventListener)listener);
            } else if (listener instanceof org.kie.api.event.rule.AgendaEventListener) {
                runtimeEventManager.addEventListener((org.kie.api.event.rule.AgendaEventListener)listener);
            }
            if (listener instanceof RuleBaseEventListener) {
                wmem.addEventListener((RuleBaseEventListener)listener);
            } else if (listener instanceof KieBaseEventListener) {
                sksi.getKieBase().addEventListener((KieBaseEventListener)listener);
            }
            if (listener instanceof ProcessEventListener) {
                InternalProcessRuntime processRuntime = sksi.session.getProcessRuntime();
                if (processRuntime != null) {
                    processRuntime.addEventListener((ProcessEventListener)listener);
                }
            }
        } else if (runtimeEventManager instanceof StatelessKnowledgeSessionImpl) {
            StatelessKnowledgeSessionImpl sksi = (StatelessKnowledgeSessionImpl)runtimeEventManager;
            if (listener instanceof WorkingMemoryEventListener) {
                sksi.workingMemoryEventSupport.addEventListener((WorkingMemoryEventListener)listener);
            } else if (listener instanceof org.kie.api.event.rule.WorkingMemoryEventListener) {
                runtimeEventManager.addEventListener((org.kie.api.event.rule.WorkingMemoryEventListener)listener);
            }
            if (listener instanceof AgendaEventListener) {
                sksi.agendaEventSupport.addEventListener((AgendaEventListener)listener);
            } else if (listener instanceof org.kie.api.event.rule.AgendaEventListener) {
                runtimeEventManager.addEventListener((org.kie.api.event.rule.AgendaEventListener)listener);
            }
            if (listener instanceof RuleBaseEventListener) {
                sksi.getRuleBase().addEventListener((RuleBaseEventListener)listener);
            } else if (listener instanceof KieBaseEventListener) {
                new KnowledgeBaseImpl(sksi.getRuleBase()).addEventListener((KieBaseEventListener)listener);
            }
            if (listener instanceof ProcessEventListener) {
                sksi.processEventSupport.addEventListener((ProcessEventListener)listener);
            }
        } else if (runtimeEventManager instanceof CommandBasedStatefulKnowledgeSession) {
            CommandBasedStatefulKnowledgeSession cbsks = (CommandBasedStatefulKnowledgeSession)runtimeEventManager;
            ReteooWorkingMemoryInterface rwmi = ((StatefulKnowledgeSessionImpl)((KnowledgeCommandContext)cbsks.getCommandService().getContext()).getKieSession()).session;
            if (listener instanceof WorkingMemoryEventListener) {
                rwmi.addEventListener((WorkingMemoryEventListener)listener);
            } else if (listener instanceof org.kie.api.event.rule.WorkingMemoryEventListener) {
                runtimeEventManager.addEventListener((org.kie.api.event.rule.WorkingMemoryEventListener)listener);
            }
            if (listener instanceof AgendaEventListener) {
                rwmi.addEventListener((AgendaEventListener)listener);
            } else if (listener instanceof org.kie.api.event.rule.AgendaEventListener) {
                runtimeEventManager.addEventListener((org.kie.api.event.rule.AgendaEventListener)listener);
            }
            if (listener instanceof RuleBaseEventListener) {
                rwmi.addEventListener((RuleBaseEventListener)listener);
            } else if (listener instanceof KieBaseEventListener) {
                cbsks.getKieBase().addEventListener((KieBaseEventListener)listener);
            }
            if (listener instanceof ProcessEventListener) {
                InternalProcessRuntime ipr = rwmi.getProcessRuntime();
                if (ipr != null) {
                    ipr.addEventListener((ProcessEventListener)listener);
                }
            }
        }
    }

    private Listeners() {}

}
