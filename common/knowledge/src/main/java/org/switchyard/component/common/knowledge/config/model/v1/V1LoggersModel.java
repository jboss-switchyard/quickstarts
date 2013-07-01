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
package org.switchyard.component.common.knowledge.config.model.v1;

import static org.switchyard.component.common.knowledge.config.model.LoggerModel.LOGGER;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import org.switchyard.component.common.knowledge.config.model.LoggerModel;
import org.switchyard.component.common.knowledge.config.model.LoggersModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * A version 1 LoggersModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class V1LoggersModel extends BaseModel implements LoggersModel {

    private List<LoggerModel> _loggers = new ArrayList<LoggerModel>();

    /**
     * Creates a new LoggersModel in the specified namespace.
     * @param namespace the specified namespace
     */
    public V1LoggersModel(String namespace) {
        super(new QName(namespace, LOGGERS));
        setModelChildrenOrder(LOGGER);
    }

    /**
     * Creates a new LoggersModel with the specified configuration and descriptor.
     * @param config the configuration
     * @param desc the descriptor
     */
    public V1LoggersModel(Configuration config, Descriptor desc) {
        super(config, desc);
        for (Configuration logger_config : config.getChildren(LOGGER)) {
            LoggerModel logger = (LoggerModel)readModel(logger_config);
            if (logger != null) {
                _loggers.add(logger);
            }
        }
        setModelChildrenOrder(LOGGER);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<LoggerModel> getLoggers() {
        return Collections.unmodifiableList(_loggers);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LoggersModel addLogger(LoggerModel logger) {
        addChildModel(logger);
        _loggers.add(logger);
        return this;
    }

}
