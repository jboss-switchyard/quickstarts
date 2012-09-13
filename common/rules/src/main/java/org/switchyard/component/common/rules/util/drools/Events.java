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
package org.switchyard.component.common.rules.util.drools;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

import org.apache.log4j.Logger;
import org.drools.WorkingMemoryEventManager;
import org.drools.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.event.AgendaEventListener;
import org.drools.event.KnowledgeRuntimeEventManager;
import org.drools.event.RuleBaseEventListener;
import org.drools.event.WorkingMemoryEventListener;
import org.drools.event.knowledgebase.KnowledgeBaseEventListener;
import org.drools.event.process.ProcessEventListener;
import org.drools.impl.KnowledgeBaseImpl;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.impl.StatelessKnowledgeSessionImpl;
import org.drools.reteoo.ReteooWorkingMemoryInterface;
import org.drools.runtime.process.InternalProcessRuntime;
import org.switchyard.common.type.reflect.Construction;
import org.switchyard.component.common.rules.config.model.ComponentImplementationModel;
import org.switchyard.component.common.rules.config.model.EventListenerModel;

/**
 * Drools Event Utilities.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public final class Events {

    private static final Logger LOGGER = Logger.getLogger(Events.class);

    /**
     * Adds EventListeners specified in the ComponentImplementationConfig to the provided KnowledgeRuntimeEventManager.
     * If the EventListeners provide a Constructor taking a single KnowledgeRuntimeEventManager as a parameter,
     * that is used and it is assumed it will do the work of registering itself.  Otherwise, a no-arg constructor is
     * assumed, and this method will do the work of registering the EventListener for each of the respected interfaces
     * it implements. Those interfaces being:
     * <ul>
     * <li>{@link org.drools.event.WorkingMemoryEventListener}</li>
     * <li>{@link org.drools.event.rule.WorkingMemoryEventListener}</li>
     * <li>{@link org.drools.event.AgendaEventListener}</li>
     * <li>{@link org.drools.event.rule.AgendaEventListener}</li>
     * <li>{@link org.drools.event.RuleBaseEventListener}</li>
     * <li>{@link org.drools.event.knowledgebase.KnowledgeBaseEventListener}</li>
     * <li>{@link org.drools.event.process.ProcessEventListener}</li>
     * </ul>
     * @param cic the ComponentImplementationConfig
     * @param krem the KnowledgeRuntimeEventManager
     * @return the List of EventListeners that were created and added
     */
    public static final List<EventListener> addEventListeners(ComponentImplementationConfig cic, KnowledgeRuntimeEventManager krem) {
        List<EventListener> list = new ArrayList<EventListener>();
        if (cic != null) {
            ComponentImplementationModel cim = cic.getModel();
            if (cim != null) {
                for (EventListenerModel elm : cim.getEventListeners()) {
                    ClassLoader loader = cic.getLoader();
                    if (loader == null) {
                        loader = Events.class.getClassLoader();
                    }
                    Class<? extends EventListener> elc = elm.getClazz(loader);
                    if (elc != null) {
                        EventListener el;
                        try {
                            @SuppressWarnings("unused")
                            Constructor<? extends EventListener> cnstr = elc.getDeclaredConstructor(KnowledgeRuntimeEventManager.class);
                            el = Construction.construct(elc, new Class[]{KnowledgeRuntimeEventManager.class}, new Object[]{krem});
                        } catch (NoSuchMethodException nsme1) {
                            try {
                                @SuppressWarnings("unused")
                                Constructor<? extends EventListener> cnstr = elc.getDeclaredConstructor();
                                el = Construction.construct(elc);
                                registerEventListener(el, krem);
                            } catch (NoSuchMethodException nsme2) {
                                el = null;
                            }
                        }
                        if (el != null) {
                            list.add(el);
                        } else {
                            LOGGER.error("Could not find appropriate constructor in class " + elc.getName());
                        }
                    }
                }
            }
        }
        return list;
    }

    private static final void registerEventListener(EventListener el, KnowledgeRuntimeEventManager krem) {
        if (krem instanceof StatefulKnowledgeSessionImpl) {
            StatefulKnowledgeSessionImpl sksi = (StatefulKnowledgeSessionImpl)krem;
            WorkingMemoryEventManager wmem = sksi.session;
            if (el instanceof WorkingMemoryEventListener) {
                wmem.addEventListener((WorkingMemoryEventListener)el);
            } else if (el instanceof org.drools.event.rule.WorkingMemoryEventListener) {
                krem.addEventListener((org.drools.event.rule.WorkingMemoryEventListener)el);
            }
            if (el instanceof AgendaEventListener) {
                wmem.addEventListener((AgendaEventListener)el);
            } else if (el instanceof org.drools.event.rule.AgendaEventListener) {
                krem.addEventListener((org.drools.event.rule.AgendaEventListener)el);
            }
            if (el instanceof RuleBaseEventListener) {
                wmem.addEventListener((RuleBaseEventListener)el);
            } else if (el instanceof KnowledgeBaseEventListener) {
                sksi.getKnowledgeBase().addEventListener((KnowledgeBaseEventListener)el);
            }
            if (el instanceof ProcessEventListener) {
                InternalProcessRuntime processRuntime = sksi.session.getProcessRuntime();
                if (processRuntime != null) {
                    processRuntime.addEventListener((ProcessEventListener)el);
                }
            }
        } else if (krem instanceof StatelessKnowledgeSessionImpl) {
            StatelessKnowledgeSessionImpl sksi = (StatelessKnowledgeSessionImpl)krem;
            if (el instanceof WorkingMemoryEventListener) {
                sksi.workingMemoryEventSupport.addEventListener((WorkingMemoryEventListener)el);
            } else if (el instanceof org.drools.event.rule.WorkingMemoryEventListener) {
                krem.addEventListener((org.drools.event.rule.WorkingMemoryEventListener)el);
            }
            if (el instanceof AgendaEventListener) {
                sksi.agendaEventSupport.addEventListener((AgendaEventListener)el);
            } else if (el instanceof org.drools.event.rule.AgendaEventListener) {
                krem.addEventListener((org.drools.event.rule.AgendaEventListener)el);
            }
            if (el instanceof RuleBaseEventListener) {
                sksi.getRuleBase().addEventListener((RuleBaseEventListener)el);
            } else if (el instanceof KnowledgeBaseEventListener) {
                new KnowledgeBaseImpl(sksi.getRuleBase()).addEventListener((KnowledgeBaseEventListener)el);
            }
            if (el instanceof ProcessEventListener) {
                sksi.processEventSupport.addEventListener((ProcessEventListener)el);
            }
        } else if (krem instanceof CommandBasedStatefulKnowledgeSession) {
            CommandBasedStatefulKnowledgeSession cbsks = (CommandBasedStatefulKnowledgeSession)krem;
            ReteooWorkingMemoryInterface rwmi = ((StatefulKnowledgeSessionImpl)((KnowledgeCommandContext)cbsks.getCommandService().getContext()).getStatefulKnowledgesession()).session;
            if (el instanceof WorkingMemoryEventListener) {
                rwmi.addEventListener((WorkingMemoryEventListener)el);
            } else if (el instanceof org.drools.event.rule.WorkingMemoryEventListener) {
                krem.addEventListener((org.drools.event.rule.WorkingMemoryEventListener)el);
            }
            if (el instanceof AgendaEventListener) {
                rwmi.addEventListener((AgendaEventListener)el);
            } else if (el instanceof org.drools.event.rule.AgendaEventListener) {
                krem.addEventListener((org.drools.event.rule.AgendaEventListener)el);
            }
            if (el instanceof RuleBaseEventListener) {
                rwmi.addEventListener((RuleBaseEventListener)el);
            } else if (el instanceof KnowledgeBaseEventListener) {
                cbsks.getKnowledgeBase().addEventListener((KnowledgeBaseEventListener)el);
            }
            if (el instanceof ProcessEventListener) {
                InternalProcessRuntime ipr = rwmi.getProcessRuntime();
                if (ipr != null) {
                    ipr.addEventListener((ProcessEventListener)el);
                }
            }
        }
    }

    private Events() {}

}
