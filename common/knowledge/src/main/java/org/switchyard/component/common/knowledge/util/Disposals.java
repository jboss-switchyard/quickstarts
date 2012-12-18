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

import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.apache.log4j.Logger;
import org.kie.agent.KnowledgeAgent;
import org.kie.builder.KieScanner;
import org.kie.logger.KieRuntimeLogger;
import org.kie.runtime.KieSession;
import org.switchyard.component.common.knowledge.session.KnowledgeDisposal;

/**
 * Disposal functions.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public final class Disposals {

    private static final Logger LOGGER = Logger.getLogger(Disposals.class);

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
                    LOGGER.warn("problem closing EntityManagerFactory: " + t.getMessage());
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
                            LOGGER.warn("problem closing KieRuntimeLogger: " + t.getMessage());
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
                    LOGGER.warn("problem stoppping KieScanner: " + t.getMessage());
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
                    LOGGER.warn("problem disposing KieSession: " + t.getMessage());
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
                    LOGGER.warn("problem disposing KnowledgeAgent: " + t.getMessage());
                }
            }
        };
    }

    private Disposals() {}

}
