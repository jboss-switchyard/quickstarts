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

import java.util.ArrayList;
import java.util.List;

import org.kie.api.KieServices;
import org.kie.api.event.KieRuntimeEventManager;
import org.kie.api.logger.KieLoggers;
import org.kie.api.logger.KieRuntimeLogger;
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
                if (log == null) {
                    log = "event";
                }
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
