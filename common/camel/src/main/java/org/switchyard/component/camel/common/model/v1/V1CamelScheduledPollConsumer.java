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
package org.switchyard.component.camel.common.model.v1;

import java.util.concurrent.TimeUnit;

import org.switchyard.component.camel.common.model.consumer.CamelScheduledPollConsumer;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * Base class for scheduled consumers.
 * 
 * @author Lukasz Dywicki
 */
public class V1CamelScheduledPollConsumer extends V1BaseCamelModel
    implements CamelScheduledPollConsumer {

    /**
     * The name of the 'initialDelay' element.
     */
    public static final String INITIAL_DELAY = "initialDelay";
    /**
     * The name of the 'delay' element.
     */
    public static final String DELAY = "delay";
    /**
     * The name of the 'useFixedDelay' element.
     */
    public static final String USE_FIXED_DELAY = "useFixedDelay";

    /**
     * The name of the 'timeUnit' element.
     */
    private static final String TIME_UNIT = "timeUnit";

    /**
     * The name of the 'sendEmptyMessageWhenIdle' element.
     */
    private static final String SEND_EMPTY_MESSAGE_WHEN_IDLE = "sendEmptyMessageWhenIdle";

    /**
     * Creates model bound to given namespace.
     * 
     * @param name Name of element.
     * @param namespace Namespace to bound.
     */
    public V1CamelScheduledPollConsumer(String name, String namespace) {
        super(name, namespace);

        setModelChildrenOrder(INITIAL_DELAY, DELAY, USE_FIXED_DELAY,
            TIME_UNIT, SEND_EMPTY_MESSAGE_WHEN_IDLE
        );
    }

    /**
     * Constructor.
     * 
     * @param config The switchyard configuration instance.
     * @param desc The switchyard descriptor instance.
     */
    public V1CamelScheduledPollConsumer(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    @Override
    public Integer getInitialDelay() {
        return getIntegerConfig(INITIAL_DELAY);
    }

    @Override
    public V1CamelScheduledPollConsumer setInitialDelay(Integer initialDelay) {
        return setConfig(INITIAL_DELAY, initialDelay);
    }

    @Override
    public Integer getDelay() {
        return getIntegerConfig(DELAY);
    }

    @Override
    public V1CamelScheduledPollConsumer setDelay(Integer delay) {
        return setConfig(DELAY, delay);
    }

    @Override
    public Boolean isUseFixedDelay() {
        return getBooleanConfig(USE_FIXED_DELAY);
    }

    @Override
    public V1CamelScheduledPollConsumer setUseFixedDelay(Boolean useFixedDelay) {
        setConfig(USE_FIXED_DELAY, String.valueOf(useFixedDelay));
        return this;
    }

    @Override
    public TimeUnit getTimeUnit() {
        return getEnumerationConfig(TIME_UNIT, TimeUnit.class);
    }

    @Override
    public V1CamelScheduledPollConsumer setTimeUnit(String timeUnit) {
        return setConfig(TIME_UNIT, timeUnit);
    }

    @Override
    public Boolean isSendEmptyMessageWhenIdle() {
        return getBooleanConfig(SEND_EMPTY_MESSAGE_WHEN_IDLE);
    }

    @Override
    public V1CamelScheduledPollConsumer setSendEmptyMessageWhenIdle(Boolean sendEmptyMessageWhenIdle) {
        return setConfig(SEND_EMPTY_MESSAGE_WHEN_IDLE, sendEmptyMessageWhenIdle);
    }

}
