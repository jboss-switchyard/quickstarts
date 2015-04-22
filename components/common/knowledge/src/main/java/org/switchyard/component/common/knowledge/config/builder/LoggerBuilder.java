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

/**
 * LoggerBuilder.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
public class LoggerBuilder extends KnowledgeBuilder {

    private LoggerType _loggerType;
    private String _log;
    private Integer _interval;

    /**
     * Creates a new LoggerBuilder.
     * @param classLoader classLoader
     * @param loggerModel loggerModel
     */
    public LoggerBuilder(ClassLoader classLoader, LoggerModel loggerModel) {
        super(classLoader);
        if (loggerModel != null) {
            _loggerType = loggerModel.getType();
            _log = Strings.trimToNull(loggerModel.getLog());
            _interval = loggerModel.getInterval();
        }
        if (_loggerType == null) {
            _loggerType = LoggerType.THREADED_FILE;
        }
        if (_log == null) {
            _log = "event";
        }
        if (_interval == null) {
            _interval = Integer.valueOf(1000);
        }
    }

    /**
     * Builds a KieRuntimeLogger.
     * @param runtimeEventManager runtimeEventManager
     * @return a KieRuntimeLogger
     */
    public KieRuntimeLogger build(KieRuntimeEventManager runtimeEventManager) {
        KieRuntimeLogger logger = null;
        if (_loggerType != null && runtimeEventManager != null) {
            KieLoggers loggers = KieServices.Factory.get().getLoggers();
            switch (_loggerType) {
                case CONSOLE:
                    logger = loggers.newConsoleLogger(runtimeEventManager);
                    break;
                case FILE:
                    logger = loggers.newFileLogger(runtimeEventManager, _log);
                    break;
                case THREADED_FILE:
                    logger = loggers.newThreadedFileLogger(runtimeEventManager, _log, _interval);
                    break;
            }
        }
        return logger;
    }

    /**
     * Creates LoggerBuilders.
     * @param classLoader classLoader
     * @param implementationModel implementationModel
     * @return LoggerBuilders
     */
    public static List<LoggerBuilder> builders(ClassLoader classLoader, KnowledgeComponentImplementationModel implementationModel) {
        List<LoggerBuilder> builders = new ArrayList<LoggerBuilder>();
        if (implementationModel != null) {
            LoggersModel loggersModel = implementationModel.getLoggers();
            if (loggersModel != null) {
                for (LoggerModel loggerModel : loggersModel.getLoggers()) {
                    if (loggerModel != null) {
                        builders.add(new LoggerBuilder(classLoader, loggerModel));
                    }
                }
            }
        }
        return builders;
    }

}
