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
package org.switchyard.component.camel.core.model.seda.v1;

import java.net.URI;

import org.switchyard.component.camel.common.QueryString;
import org.switchyard.component.camel.common.model.v1.V1BaseCamelBindingModel;
import org.switchyard.component.camel.core.model.seda.CamelSedaBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

import static org.switchyard.component.camel.core.model.Constants.CORE_NAMESPACE_V1;

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
        super(SEDA, CORE_NAMESPACE_V1);

        setModelChildrenOrder(NAME, SIZE, CONCURRENT_CONSUMERS, WAIT_FOR_TASK_TO_COMPLETE,
            TIMEOUT, MULTIPLE_CONSUMERS, LIMIT_CONCURRENT_CONSUMERS);
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
    public String getEndpointName() {
        return getConfig(NAME);
    }

    @Override
    public V1CamelSedaBindingModel setEndpointName(String name) {
        return setConfig(NAME, name);
    }

    @Override
    public Integer getSize() {
        return getIntegerConfig(SIZE);
    }

    @Override
    public V1CamelSedaBindingModel setSize(Integer size) {
        return setConfig(SIZE, String.valueOf(size));
    }

    @Override
    public Integer getConcurrentConsumers() {
        return getIntegerConfig(CONCURRENT_CONSUMERS);
    }

    @Override
    public V1CamelSedaBindingModel setConcurrentConsumers(Integer concurrentConsumers) {
        return setConfig(CONCURRENT_CONSUMERS, concurrentConsumers);
    }

    @Override
    public String getWaitForTaskToComplete() {
        return getConfig(WAIT_FOR_TASK_TO_COMPLETE);
    }

    @Override
    public V1CamelSedaBindingModel setWaitForTaskToComplete(String waitForTaskToComplete) {
        return setConfig(WAIT_FOR_TASK_TO_COMPLETE, waitForTaskToComplete);
    }

    @Override
    public Long getTimeout() {
        return getLongConfig(TIMEOUT);
    }

    @Override
    public V1CamelSedaBindingModel setTimeout(Long timeout) {
        return setConfig(TIMEOUT, timeout);
    }

    @Override
    public Boolean isMultipleConsumers() {
        return getBooleanConfig(MULTIPLE_CONSUMERS);
    }

    @Override
    public V1CamelSedaBindingModel setMultipleConsumers(Boolean multipleConsumers) {
        return setConfig(MULTIPLE_CONSUMERS, multipleConsumers);
    }

    @Override
    public Boolean isLimitConcurrentConsumers() {
        return getBooleanConfig(LIMIT_CONCURRENT_CONSUMERS);
    }

    @Override
    public V1CamelSedaBindingModel setLimitConcurrentConsumers(Boolean limitConcurrentConsumers) {
        return setConfig(LIMIT_CONCURRENT_CONSUMERS, limitConcurrentConsumers);
    }

    @Override
    public URI getComponentURI() {
        String uriStr = SEDA + "://" + getConfig(NAME);
        // create query string from config values
        QueryString queryString = new QueryString();
        traverseConfiguration(getModelConfiguration().getChildren(), queryString, NAME);

        return URI.create(uriStr.toString() + queryString);
    }

}
