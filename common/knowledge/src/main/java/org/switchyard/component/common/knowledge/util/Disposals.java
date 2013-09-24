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

import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.kie.api.builder.KieScanner;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.runtime.KieSession;
import org.kie.internal.agent.KnowledgeAgent;
import org.switchyard.component.common.knowledge.CommonKnowledgeLogger;
import org.switchyard.component.common.knowledge.session.KnowledgeDisposal;

/**
 * Disposal functions.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public final class Disposals {

    /**
     * Creates a new EntityManagetFactory disposal.
     * @param entityManagerFactory the EntityManagerFactory
     * @return the disposal
     */
    public static KnowledgeDisposal newDisposal(final EntityManagerFactory entityManagerFactory) {
        return new KnowledgeDisposal() {
            @Override
            public void dispose() {
                try {
                    if (entityManagerFactory != null) {
                        entityManagerFactory.close();
                    }
                } catch (Throwable t) {
                    CommonKnowledgeLogger.ROOT_LOGGER.problemClosingEntityManagerFactory(t.getMessage());
                }
            }
        };
    }

    /**
     * Creates a new KieRuntimeLogger disposal.
     * @param kieRuntimeLoggers the loggers
     * @return the disposal
     */
    public static KnowledgeDisposal newDisposal(final List<KieRuntimeLogger> kieRuntimeLoggers) {
        return new KnowledgeDisposal() {
            @Override
            public void dispose() {
                if (kieRuntimeLoggers != null) {
                    for (KieRuntimeLogger kieRuntimeLogger : kieRuntimeLoggers) {
                        try {
                            if (kieRuntimeLogger != null) {
                                kieRuntimeLogger.close();
                            }
                        } catch (Throwable t) {
                            CommonKnowledgeLogger.ROOT_LOGGER.problemClosingKieRuntimeLogger(t.getMessage());
                        }
                    }
                    kieRuntimeLoggers.clear();
                }
            }
        };
    }

    /**
     * Creates a new KieScanner disposal.
     * @param kieScanner the scanner
     * @return the disposal
     */
    public static KnowledgeDisposal newDisposal(final KieScanner kieScanner) {
        return new KnowledgeDisposal() {
            @Override
            public void dispose() {
                try {
                    if (kieScanner != null) {
                        kieScanner.stop();
                    }
                } catch (Throwable t) {
                    CommonKnowledgeLogger.ROOT_LOGGER.problemStopppingKieScanner(t.getMessage());
                }
            }
        };
    }

    /**
     * Creates a new KieSession disposal.
     * @param kieSession the session
     * @return the disposal
     */
    public static KnowledgeDisposal newDisposal(final KieSession kieSession) {
        return new KnowledgeDisposal() {
            @Override
            public void dispose() {
                try {
                    if (kieSession != null) {
                        try {
                            kieSession.halt();
                        } finally {
                            kieSession.dispose();
                        }
                    }
                } catch (Throwable t) {
                    CommonKnowledgeLogger.ROOT_LOGGER.problemDisposingKieSession(t.getMessage());
                }
            }
        };
    }

    /**
     * Creates a new knowledge agent disposal.
     * @param knowledgeAgent the agent
     * @return the disposal
     */
    public static KnowledgeDisposal newDisposal(final KnowledgeAgent knowledgeAgent) {
        return new KnowledgeDisposal() {
            @Override
            public void dispose() {
                try {
                    if (knowledgeAgent != null) {
                        knowledgeAgent.dispose();
                    }
                } catch (Throwable t) {
                    CommonKnowledgeLogger.ROOT_LOGGER.problemDisposingKnowledgeAgent(t.getMessage());
                }
            }
        };
    }

    private Disposals() {}

}
