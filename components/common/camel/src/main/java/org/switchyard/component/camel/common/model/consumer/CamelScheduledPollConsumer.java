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
package org.switchyard.component.camel.common.model.consumer;

import java.util.concurrent.TimeUnit;

/**
 * Scheduled poll consumer interface. Defines common properties for schedule
 * based consumers.
 * 
 * @author Lukasz Dywicki
 */
public interface CamelScheduledPollConsumer {

    /**
     * Delay before the next poll.
     * 
     * @return the delay setting or null if it has not been specified.
     */
    Integer getDelay();

    /**
     * Specify the time before next poll.
     * 
     * @param delay the time in configured time unit.
     * @return a reference to this binding model.
     */
    CamelScheduledPollConsumer setDelay(Integer delay);

    /**
     * Delay before polling starts in given time unit.
     * 
     * @return the initial delay setting or null if it has not been specified.
     */
    Integer getInitialDelay();

    /**
     * Specify the time before polling starts.
     * 
     * @param initialDelay the time in configured time unit.
     * @return a reference to this binding model.
     */
    CamelScheduledPollConsumer setInitialDelay(Integer initialDelay);

    /**
     * Time unit used to count delay and initial delay. By default it is millisecond.
     * 
     * @return Time unit of initial delay/delay.
     */
    TimeUnit getTimeUnit();

    /**
     * Specify the time unit of initial delay/delay parameters.
     * 
     * @param timeUnit Time unit to use.
     * @return a reference to this binding model
     */
    CamelScheduledPollConsumer setTimeUnit(String timeUnit);

    /**
     * Controls if fixed delay or fixed rate is used. 
     * 
     * @return True if fixed rate is used.
     */
    Boolean isUseFixedDelay();

    /**
     * Specify whether to use fixed delay between pools, otherwise fixed rate is
     * used.
     * 
     * @param useFixedDelay true: fixed delay between pools. False: fixed rate is used
     * @return a reference to this binding model.
     */
    CamelScheduledPollConsumer setUseFixedDelay(Boolean useFixedDelay);

    /**
     * If the polling consumer did not poll any message, you can enable this option
     * to send an empty message (no body) instead.
     * 
     * @return Should empty message be sent when there is nothing to poll.
     */
    Boolean isSendEmptyMessageWhenIdle();

    /**
     * Specify whether to use empty messages if there is nothing to poll.
     * 
     * @param sendEmptyMessageWhenIdle True - empty message will be sent.
     * @return a reference to this binding model.
     */
    CamelScheduledPollConsumer setSendEmptyMessageWhenIdle(Boolean sendEmptyMessageWhenIdle);

}
