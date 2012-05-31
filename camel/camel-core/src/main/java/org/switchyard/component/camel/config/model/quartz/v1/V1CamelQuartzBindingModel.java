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
package org.switchyard.component.camel.config.model.quartz.v1;

import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.camel.util.UnsafeUriCharactersEncoder;
import org.switchyard.component.camel.config.model.QueryString;
import org.switchyard.component.camel.config.model.quartz.CamelQuartzBindingModel;
import org.switchyard.component.camel.config.model.v1.V1BaseCamelBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * Configuration binding for quartz.
 * 
 * @author Lukasz Dywicki
 */
public class V1CamelQuartzBindingModel extends V1BaseCamelBindingModel
    implements CamelQuartzBindingModel {

    /**
     * Camel component prefix.
     */
    public static final String QUARTZ = "quartz";

    private static final String NAME = "name";
    private static final String CRON = "cron";
    private static final String STATEFUL = "stateful";
    private static final String START_TIME = "trigger.startTime";
    private static final String END_TIME = "trigger.endTime";

    // Used for dateTime fields
    private static DateFormat _dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * Create a new CamelDirectBindingModel.
     */
    public V1CamelQuartzBindingModel() {
        super(QUARTZ);
        setModelChildrenOrder(NAME, CRON, STATEFUL, START_TIME, END_TIME);
    }

    /**
     * Create a V1CamelQuartzBindingModel from the specified configuration and descriptor.
     * 
     * @param config The switchyard configuration instance.
     * @param desc The switchyard descriptor instance.
     */
    public V1CamelQuartzBindingModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    @Override
    public String getName() {
        return getConfig(NAME);
    }

    @Override
    public V1CamelQuartzBindingModel setName(String name) {
        return setConfig(NAME, name);
    }

    @Override
    public String getCron() {
        return getConfig(CRON);
    }

    @Override
    public V1CamelQuartzBindingModel setCron(String cron) {
        return setConfig(CRON, cron);
    }

    @Override
    public Boolean isStateful() {
        return getBooleanConfig(STATEFUL);
    }

    @Override
    public V1CamelQuartzBindingModel setStateful(Boolean stateful) {
        return setConfig(STATEFUL, stateful);
    }

    @Override
    public Date getStartTime() {
        return getDateConfig(START_TIME, _dateFormat);
    }

    @Override
    public V1CamelQuartzBindingModel setStartTime(Date startTime) {
        return setConfig(START_TIME, _dateFormat.format(startTime));
    }

    @Override
    public Date getEndTime() {
        return getDateConfig(END_TIME, _dateFormat);
    }

    @Override
    public V1CamelQuartzBindingModel setEndTime(Date endTime) {
        return setConfig(END_TIME, _dateFormat.format(endTime));
    }

    @Override
    public URI getComponentURI() {
        Configuration modelConfiguration = getModelConfiguration();
        List<Configuration> children = modelConfiguration.getChildren();

        String baseUri = QUARTZ + "://" + getName();

        QueryString queryStr = new QueryString();
        traverseConfiguration(children, queryStr, NAME);

        return URI.create(UnsafeUriCharactersEncoder.encode(baseUri + queryStr.toString()));
    }

}
