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

package org.switchyard.component.camel.config.model.seda.v1;

import java.net.URI;

import org.switchyard.component.camel.config.model.QueryString;
import org.switchyard.component.camel.config.model.seda.CamelSedaBindingModel;
import org.switchyard.component.camel.config.model.v1.NameValueModel;
import org.switchyard.component.camel.config.model.v1.V1BaseCamelBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;


/**
 * Represents the configuration settings for Camel Timer binding.
 *
 * @author Mario Antollini
 */
public class V1CamelSedaBindingModel extends V1BaseCamelBindingModel
implements CamelSedaBindingModel {

    /**
     * Camel endpoint type.
     */
    public static final String SEDA = "seda";

    /**
     * Camel Seda configuration values.
     */
    private static final String NAME = "name";
    private static final String SIZE = "size";
    private static final String CONCURRENT_CONSUMERS = "concurrentConsumers";
    private static final String WAIT_FOR_TASK_TO_COMPLETE = "waitForTaskToComplete";
    private static final String TIMEOUT = "timeout";
    private static final String MULTIPLE_CONSUMERS = "multipleConsumers";
    private static final String LIMIT_CONCURRENT_CONSUMERS = "limitConcurrentConsumers";

    /**
     * Create a new CamelSedaBindingModel.
     */
    public V1CamelSedaBindingModel() {
        super(SEDA);
        setModelChildrenOrder(
                NAME,
                SIZE,
                CONCURRENT_CONSUMERS,
                WAIT_FOR_TASK_TO_COMPLETE,
                TIMEOUT,
                MULTIPLE_CONSUMERS,
                LIMIT_CONCURRENT_CONSUMERS);
    }

    /**
     * Create a CamelSedaBindingModel from the specified configuration and descriptor.
     *
     * @param config The switchyard configuration instance.
     * @param desc The switchyard descriptor instance.
     */
    public V1CamelSedaBindingModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    @Override
    public URI getComponentURI() {
        // base URI without params
        String uriStr = SEDA + ":" + getConfig(NAME);
        // create query string from config values
        QueryString queryStr = new QueryString()
        .add(SIZE, getConfig(SIZE))
        .add(WAIT_FOR_TASK_TO_COMPLETE, getConfig(WAIT_FOR_TASK_TO_COMPLETE))
        .add(CONCURRENT_CONSUMERS, getConfig(CONCURRENT_CONSUMERS))
        .add(TIMEOUT, getConfig(TIMEOUT))
        .add(MULTIPLE_CONSUMERS, getConfig(MULTIPLE_CONSUMERS))
        .add(LIMIT_CONCURRENT_CONSUMERS, getConfig(LIMIT_CONCURRENT_CONSUMERS));

        return URI.create(uriStr.toString() + queryStr);
    }


    @Override
    public String getName() {
        return getConfig(NAME);
    }

    @Override
    public CamelSedaBindingModel setName(String name) {
        setConfig(NAME, name);
        return this;
    }

    @Override
    public Integer getSize() {
        return getIntegerConfig(SIZE);
    }

    @Override
    public CamelSedaBindingModel setSize(Integer size) {
        setConfig(SIZE, String.valueOf(size));
        return this;
    }

    @Override
    public Integer getConcurrentConsumers() {
        return getIntegerConfig(CONCURRENT_CONSUMERS);
    }

    @Override
    public CamelSedaBindingModel setConcurrentConsumers(
            Integer concurrentConsumers) {
        setConfig(CONCURRENT_CONSUMERS, String.valueOf(concurrentConsumers));
        return this;
    }

    @Override
    public String getWaitForTaskToComplete() {
        return getConfig(WAIT_FOR_TASK_TO_COMPLETE);
    }

    @Override
    public CamelSedaBindingModel setWaitForTaskToComplete(
            String waitForTaskToComplete) {
        setConfig(WAIT_FOR_TASK_TO_COMPLETE, waitForTaskToComplete);
        return this;
    }

    @Override
    public Long getTimeout() {
        return getLongConfig(TIMEOUT);
    }

    @Override
    public CamelSedaBindingModel setTimeout(Long timeout) {
        setConfig(TIMEOUT, String.valueOf(timeout));
        return this;
    }

    @Override
    public Boolean isMultipleConsumers() {
        return getBooleanConfig(MULTIPLE_CONSUMERS);
    }

    @Override
    public CamelSedaBindingModel setMultipleConsumers(Boolean multipleConsumers) {
        setConfig(MULTIPLE_CONSUMERS, String.valueOf(multipleConsumers));
        return this;
    }

    @Override
    public Boolean isLimitConcurrentConsumers() {
        return getBooleanConfig(LIMIT_CONCURRENT_CONSUMERS);
    }

    @Override
    public CamelSedaBindingModel setLimitConcurrentConsumers(
            Boolean limitConcurrentConsumers) {
        setConfig(LIMIT_CONCURRENT_CONSUMERS, String.valueOf(limitConcurrentConsumers));
        return this;
    }


    private Integer getIntegerConfig(String configName) {
        String value = getConfig(configName);
        return value != null ? Integer.parseInt(value) : null;
    }

    private Boolean getBooleanConfig(String configName) {
        String value = getConfig(configName);
        return value != null ? Boolean.valueOf(value) : null;
    }

    private Long getLongConfig(String configName) {
        String value = getConfig(configName);
        return value != null ? Long.parseLong(value) : null;
    }

    private String getConfig(String configName) {
        Configuration config = getModelConfiguration().getFirstChild(configName);
        if (config != null) {
            return config.getValue();
        } else {
            return null;
        }
    }

    private void setConfig(String name, String value) {
        Configuration config = getModelConfiguration().getFirstChild(name);
        if (config != null) {
            // set an existing config value
            config.setValue(value);
        } else {
            // create the config model and set the value
            NameValueModel model = new NameValueModel(name);
            model.setValue(value);
            setChildModel(model);
        }
    }

}


