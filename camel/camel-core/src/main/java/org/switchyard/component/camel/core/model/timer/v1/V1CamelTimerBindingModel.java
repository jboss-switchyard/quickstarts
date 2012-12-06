/*
 * JBoss, Home of Professional Open Source Copyright 2009, Red Hat Middleware
 * LLC, and individual contributors by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.switchyard.component.camel.core.model.timer.v1;

import static org.switchyard.component.camel.core.model.Constants.CORE_NAMESPACE_V1;

import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.switchyard.component.camel.common.QueryString;
import org.switchyard.component.camel.common.model.v1.V1BaseCamelBindingModel;
import org.switchyard.component.camel.core.model.timer.CamelTimerBindingModel;
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
     * Camel endpoint configuration values.
     */
    private static final String NAME = "name";
    private static final String TIME = "time";
    private static final String PATTERN = "pattern";
    private static final String PERIOD = "period";
    private static final String DELAY = "delay";
    private static final String FIXED_RATE = "fixedRate";
    private static final String DAEMON = "daemon";

    // Used for dateTime fields
    private static DateFormat _dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * Create a new CamelTimerBindingModel.
     */
    public V1CamelTimerBindingModel() {
        super(TIMER, CORE_NAMESPACE_V1);

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
    public String getName() {
        return getConfig(NAME);
    }

    @Override
    public V1CamelTimerBindingModel setName(String name) {
        return setConfig(NAME, name);
    }

    @Override
    public Date getTime() {
        return getDateConfig(TIME, _dateFormat);
    }

    @Override
    public V1CamelTimerBindingModel setTime(Date time) {
        return setConfig(TIME, _dateFormat.format(time));
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
