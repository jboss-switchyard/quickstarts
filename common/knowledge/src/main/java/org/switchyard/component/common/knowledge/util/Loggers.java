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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.text.StrSubstitutor;
import org.kie.KieServices;
import org.kie.event.KieRuntimeEventManager;
import org.kie.logger.KieLoggers;
import org.kie.logger.KieRuntimeLogger;
import org.switchyard.common.lang.Strings;
import org.switchyard.component.common.knowledge.LoggerType;
import org.switchyard.component.common.knowledge.config.model.KnowledgeComponentImplementationModel;
import org.switchyard.component.common.knowledge.config.model.LoggerModel;
import org.switchyard.component.common.knowledge.config.model.LoggersModel;
import org.switchyard.component.common.knowledge.session.KnowledgeDisposal;

/**
 * Logger functions.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public final class Loggers {

    /**
     * Registers loggers for disposal.
     * @param model the model
     * @param loader the class loader
     * @param runtimeEventManager the runtime event manager
     * @return the disposal
     */
    public static KnowledgeDisposal registerLoggersForDisposal(KnowledgeComponentImplementationModel model, ClassLoader loader, KieRuntimeEventManager runtimeEventManager) {
        final List<KieRuntimeLogger> loggers = registerLoggers(model, loader, runtimeEventManager);
        return Disposals.newDisposal(loggers);
    }

    /**
     * Registers loggers.
     * @param model the model
     * @param loader the class loader
     * @param runtimeEventManager the runtime event manager
     * @return the loggers
     */
    public static List<KieRuntimeLogger> registerLoggers(KnowledgeComponentImplementationModel model, ClassLoader loader, KieRuntimeEventManager runtimeEventManager) {
        List<KieRuntimeLogger> loggers = new ArrayList<KieRuntimeLogger>();
        registerLoggers(model, loader, runtimeEventManager, loggers);
        return loggers;
    }

    /**
     * Registers loggers.
     * @param model the model
     * @param loader the class loader
     * @param runtimeEventManager the runtime event manager
     * @param loggers the loggers
     */
    public static void registerLoggers(KnowledgeComponentImplementationModel model, ClassLoader loader, KieRuntimeEventManager runtimeEventManager, List<KieRuntimeLogger> loggers) {
        LoggersModel loggersModel = model.getLoggers();
        if (loggersModel != null) {
            KieLoggers kieLoggers = KieServices.Factory.get().getLoggers();
            for (LoggerModel loggerModel : loggersModel.getLoggers()) {
                LoggerType loggerType = loggerModel.getType();
                if (loggerType == null) {
                    loggerType = LoggerType.THREADED_FILE;
                }
                String log = Strings.trimToNull(loggerModel.getLog());
                log = log != null ? StrSubstitutor.replaceSystemProperties(log) : "event";
                final KieRuntimeLogger logger;
                switch (loggerType) {
                    case CONSOLE:
                        logger = kieLoggers.newConsoleLogger(runtimeEventManager);
                        break;
                    case FILE:
                        logger = kieLoggers.newFileLogger(runtimeEventManager, log);
                        break;
                    case THREADED_FILE:
                        Integer interval = loggerModel.getInterval();
                        if (interval == null) {
                            interval = Integer.valueOf(1000);
                        }
                        logger = kieLoggers.newThreadedFileLogger(runtimeEventManager, log, interval);
                        break;
                    default:
                        logger = null;
                        break;
                }
                if (logger != null) {
                    loggers.add(logger);
                }
            }
        }
    }

    private Loggers() {}

}
