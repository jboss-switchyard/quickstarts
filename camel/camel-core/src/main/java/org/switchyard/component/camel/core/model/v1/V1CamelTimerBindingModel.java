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
package org.switchyard.component.camel.core.model.v1;

import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.switchyard.component.camel.common.QueryString;
import org.switchyard.component.camel.common.model.v1.V1BaseCamelBindingModel;
import org.switchyard.component.camel.core.model.CamelTimerBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * Represents the configuration settings for Camel Timer binding.
 * 
 * @author Mario Antollini
 */
public class V1CamelTimerBindingModel extends V1BaseCamelBindingModel 
    implements CamelTimerBindingModel {

    /**
     * Camel endpoint type.
     */
    public static final String TIMER = "timer";
    
    /**
     * Date format string.
     */
    public static final String DATE_FORMAT_STRING = "yyyy-MM-dd'T'HH:mm:ss";
    
    /**
     * Camel endpoint configuration values.
     */
    private static final String NAME = "name";
    private static final String TIME = "time";
    private static final String PATTERN = "pattern";
    private static final String PERIOD = "period";
    private static final String DELAY = "delay";
    private static final String FIXED_RATE = "fixedRate";
    private static final String DAEMON = "daemon";

    /**
     * Create a new CamelTimerBindingModel.
     * @param namespace namespace
     */
    public V1CamelTimerBindingModel(String namespace) {
        super(TIMER, namespace);

        setModelChildrenOrder(NAME, TIME, PATTERN, PERIOD, DELAY, FIXED_RATE, DAEMON);
    }

    /**
     * Create a CamelTimerBindingModel from the specified configuration and descriptor.
     * 
     * @param config The switchyard configuration instance.
     * @param desc The switchyard descriptor instance.
     */
    public V1CamelTimerBindingModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    @Override
    public String getTimerName() {
        return getConfig(NAME);
    }

    @Override
    public V1CamelTimerBindingModel setTimerName(String name) {
        return setConfig(NAME, name);
    }

    @Override
    public Date getTime() {
        final DateFormat dateConfigFormat = new SimpleDateFormat(DATE_FORMAT_STRING);
        return getDateConfig(TIME, dateConfigFormat);
    }

    @Override
    public V1CamelTimerBindingModel setTime(Date time) {
        final DateFormat timeDateFormat = new SimpleDateFormat(DATE_FORMAT_STRING);
        return setConfig(TIME, timeDateFormat.format(time));
    }

    @Override
    public String getPattern() {
        return getConfig(PATTERN);
    }

    @Override
    public V1CamelTimerBindingModel setPattern(String pattern) {
        return setConfig(PATTERN, pattern);
    }

    @Override
    public Long getPeriod() {
        return getLongConfig(PERIOD);
    }

    @Override
    public V1CamelTimerBindingModel setPeriod(Long period) {
        return setConfig(PERIOD, period);
    }

    @Override
    public Long getDelay() {
        return getLongConfig(DELAY);
    }

    @Override
    public V1CamelTimerBindingModel setDelay(Long delay) {
        return setConfig(DELAY, delay);
    }

    @Override
    public Boolean isFixedRate() {
        return getBooleanConfig(FIXED_RATE);
    }

    @Override
    public V1CamelTimerBindingModel setFixedRate(Boolean fixedRate) {
        return setConfig(FIXED_RATE, fixedRate);
    }

    @Override
    public Boolean isDaemon() {
        return getBooleanConfig(DAEMON);
    }

    @Override
    public V1CamelTimerBindingModel setDaemon(Boolean daemon) {
        return setConfig(DAEMON, daemon);
    }

    @Override
    public URI getComponentURI() {
        // base URI without params
        String uriStr = TIMER + "://" + getConfig(NAME);
        // create query string from config values
        QueryString queryStr = new QueryString();
        traverseConfiguration(getModelConfiguration().getChildren(), queryStr, NAME);

        return URI.create(uriStr.toString() + queryStr);
    }



}
