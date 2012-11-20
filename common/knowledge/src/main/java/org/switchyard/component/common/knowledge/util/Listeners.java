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

import org.drools.WorkingMemoryEventManager;
import org.drools.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.event.AgendaEventListener;
import org.drools.event.RuleBaseEventListener;
import org.drools.event.WorkingMemoryEventListener;
import org.drools.impl.KnowledgeBaseImpl;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.impl.StatelessKnowledgeSessionImpl;
import org.drools.reteoo.ReteooWorkingMemoryInterface;
import org.drools.runtime.process.InternalProcessRuntime;
import org.kie.event.KieRuntimeEventManager;
import org.kie.event.kiebase.KieBaseEventListener;
import org.kie.event.process.ProcessEventListener;
import org.kie.event.rule.DefaultAgendaEventListener;
import org.kie.event.rule.RuleFlowGroupActivatedEvent;
import org.kie.runtime.KieSession;
import org.kie.runtime.StatefulKnowledgeSession;
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
                Class<? extends EventListener> listenerClass = listenerModel.getClazz(loader);
                EventListener listener;
                try {
                    @SuppressWarnings("unused")
                    Constructor<? extends EventListener> cnstr = listenerClass.getDeclaredConstructor(KieRuntimeEventManager.class);
                    // automatic registration
                    listener = Construction.construct(listenerClass, new Class[]{KieRuntimeEventManager.class}, new Object[]{runtimeEventManager});
                } catch (NoSuchMethodException nsme1) {
                    try {
                        @SuppressWarnings("unused")
                        Constructor<? extends EventListener> cnstr = listenerClass.getDeclaredConstructor();
                        listener = Construction.construct(listenerClass);
                        // manual registration
                        registerListener(listener, runtimeEventManager);
                    } catch (NoSuchMethodException nsme2) {
                        listener = null;
                    }
                }
                if (listener == null) {
                    throw new SwitchYardException("Could not find appropriate constructor in class " + listenerClass.getName());
                }
            }
        }
    }

    private static void registerListener(EventListener listener, KieRuntimeEventManager runtimeEventManager) {
        if (runtimeEventManager instanceof StatefulKnowledgeSessionImpl) {
            StatefulKnowledgeSessionImpl sksi = (StatefulKnowledgeSessionImpl)runtimeEventManager;
            WorkingMemoryEventManager wmem = sksi.session;
            if (listener instanceof WorkingMemoryEventListener) {
                wmem.addEventListener((WorkingMemoryEventListener)listener);
            } else if (listener instanceof org.kie.event.rule.WorkingMemoryEventListener) {
                runtimeEventManager.addEventListener((org.kie.event.rule.WorkingMemoryEventListener)listener);
            }
            if (listener instanceof AgendaEventListener) {
                wmem.addEventListener((AgendaEventListener)listener);
            } else if (listener instanceof org.kie.event.rule.AgendaEventListener) {
                runtimeEventManager.addEventListener((org.kie.event.rule.AgendaEventListener)listener);
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
            } else if (listener instanceof org.kie.event.rule.WorkingMemoryEventListener) {
                runtimeEventManager.addEventListener((org.kie.event.rule.WorkingMemoryEventListener)listener);
            }
            if (listener instanceof AgendaEventListener) {
                sksi.agendaEventSupport.addEventListener((AgendaEventListener)listener);
            } else if (listener instanceof org.kie.event.rule.AgendaEventListener) {
                runtimeEventManager.addEventListener((org.kie.event.rule.AgendaEventListener)listener);
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
            ReteooWorkingMemoryInterface rwmi = ((StatefulKnowledgeSessionImpl)((KnowledgeCommandContext)cbsks.getCommandService().getContext()).getStatefulKnowledgesession()).session;
            if (listener instanceof WorkingMemoryEventListener) {
                rwmi.addEventListener((WorkingMemoryEventListener)listener);
            } else if (listener instanceof org.kie.event.rule.WorkingMemoryEventListener) {
                runtimeEventManager.addEventListener((org.kie.event.rule.WorkingMemoryEventListener)listener);
            }
            if (listener instanceof AgendaEventListener) {
                rwmi.addEventListener((AgendaEventListener)listener);
            } else if (listener instanceof org.kie.event.rule.AgendaEventListener) {
                runtimeEventManager.addEventListener((org.kie.event.rule.AgendaEventListener)listener);
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
