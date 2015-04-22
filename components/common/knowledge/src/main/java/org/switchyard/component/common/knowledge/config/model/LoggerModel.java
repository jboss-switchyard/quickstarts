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
package org.switchyard.component.common.knowledge.config.model;

import org.switchyard.component.common.knowledge.LoggerType;
import org.switchyard.config.model.Model;

/**
 * A Channel Model.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public interface LoggerModel extends Model {

    /**
     * The logger XML element.
     */
    public static final String LOGGER = "logger";

    /**
     * Gets the interval attribute.
     * @return the interval attribute
     */
    public Integer getInterval();

    /**
     * Sets the interval attribute.
     * @param interval the interval attribute
     * @return this LoggerModel (useful for chaining)
     */
    public LoggerModel setInterval(Integer interval);

    /**
     * Gets the log attribute.
     * @return the log attribute
     */
    public String getLog();

    /**
     * Sets the log attribute.
     * @param log the log attribute
     * @return this LoggerModel (useful for chaining)
     */
    public LoggerModel setLog(String log);

    /**
     * Gets the type attribute.
     * @return the type attribute
     */
    public LoggerType getType();

    /**
     * Sets the type attribute.
     * @param type the type attribute
     * @return this LoggerModel (useful for chaining)
     */
    public LoggerModel setType(LoggerType type);

}
