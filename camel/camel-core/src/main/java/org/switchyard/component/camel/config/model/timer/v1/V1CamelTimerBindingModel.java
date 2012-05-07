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

package org.switchyard.component.camel.config.model.timer.v1;

import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.switchyard.component.camel.config.model.QueryString;
import org.switchyard.component.camel.config.model.timer.CamelTimerBindingModel;
import org.switchyard.component.camel.config.model.v1.V1BaseCamelBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;


/**
 * Represents the configuration settings for Camel Timer binding.
 * 
 * @author Mario Antollini
 */
public class V1CamelTimerBindingModel  extends V1BaseCamelBindingModel 
                 implements CamelTimerBindingModel {

    /**
     * Camel endpoint type.
     */
    public static final String TIMER = "timer";
    
    /**
     * Camel endpoint configuration values.
     */
    private static final String NAME                = "name";
    private static final String TIME                = "time";
    private static final String PATTERN             = "pattern";
    private static final String PERIOD              = "period";
    private static final String DELAY               = "delay";
    private static final String FIXED_RATE          = "fixedRate";
    private static final String DAEMON              = "daemon";

    // Used for dateTime fields
    private static DateFormat _dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    
    /**
     * Create a new CamelTimerBindingModel.
     */
    public V1CamelTimerBindingModel() {
        super(TIMER);
        setModelChildrenOrder(
                NAME,
                TIME,
                PATTERN,
                PERIOD,
                DELAY,
                FIXED_RATE,
                DAEMON);
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
    public CamelTimerBindingModel setName(String name) {
        setConfig(NAME, name);
        return this;
    }

    @Override
    public Date getTime() {
        return getDateConfig(TIME, _dateFormat);
    }

    @Override
    public CamelTimerBindingModel setTime(Date time) {
        setConfig(TIME, _dateFormat.format(time));
        return this;
    }

    @Override
    public String getPattern() {
        return getConfig(PATTERN);
    }

    @Override
    public CamelTimerBindingModel setPattern(String pattern) {
        setConfig(PATTERN, pattern);
        return this;
    }

    @Override
    public Long getPeriod() {
        return getLongConfig(PERIOD);
    }

    @Override
    public CamelTimerBindingModel setPeriod(Long period) {
        setConfig(PERIOD, String.valueOf(period));
        return this;
    }

    @Override
    public Long getDelay() {
        return getLongConfig(DELAY);
    }

    @Override
    public CamelTimerBindingModel setDelay(Long delay) {
        setConfig(DELAY, String.valueOf(delay));
        return this;
    }

    @Override
    public Boolean isFixedRate() {
        return getBooleanConfig(FIXED_RATE);
    }

    @Override
    public CamelTimerBindingModel setFixedRate(Boolean fixedRate) {
        setConfig(FIXED_RATE, String.valueOf(fixedRate));
        return this;
    }

    @Override
    public Boolean isDaemon() {
        return getBooleanConfig(DAEMON);
    }

    @Override
    public CamelTimerBindingModel setDaemon(Boolean daemon) {
        setConfig(DAEMON, String.valueOf(daemon));
        return this;
    }

    @Override
    public URI getComponentURI() {
        // base URI without params
        String uriStr = TIMER + "://" + getConfig(NAME);
        // create query string from config values
        QueryString queryStr = new QueryString()
        .add(TIME, getConfig(TIME))
        .add(PATTERN, getConfig(PATTERN))
        .add(PERIOD, getConfig(PERIOD))
        .add(DELAY, getConfig(DELAY))
        .add(FIXED_RATE, getConfig(FIXED_RATE))
        .add(DAEMON, getConfig(DAEMON));

        return URI.create(uriStr.toString() + queryStr);
    }



}
