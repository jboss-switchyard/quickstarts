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
package org.switchyard.component.camel.core.model.timer;

import java.util.Date;

import org.switchyard.component.camel.common.model.CamelBindingModel;

/**
 * Represents the configuration settings for Camel Timer binding.
 * 
 * @author Mario Antollini
 */
public interface CamelTimerBindingModel extends CamelBindingModel {

    /**
     * The timer name.
     * @return the timer name
     */
    String getTimerName();

    /**
     * Specify the timer name.
     * @param name the timer name
     * @return a reference to this Camel Timer binding model
     */
    CamelTimerBindingModel setTimerName(String name);

    /**
     * A java.util.Date the first event should be generated. 
     * If using the URI, the pattern expected is: yyyy-MM-dd HH:mm:ss 
     * or yyyy-MM-dd'T'HH:mm:ss. 
     * @return the first event date
     */
    Date getTime();

    /**
     * Specify the java.util.Date the first event should be generated. 
     * If using the URI, the pattern expected is: yyyy-MM-dd HH:mm:ss 
     * or yyyy-MM-dd'T'HH:mm:ss
     * @param time the specified first event date
     * @return a reference to this Camel Timer binding model
     */
    CamelTimerBindingModel setTime(Date time);

    /**
     * A custom Date pattern to use for setting the time option using URI syntax.
     * @return the custom Date pattern
     */
    String getPattern();

    /**
     * Specify a custom Date pattern to use for setting the time option 
     * using URI syntax.
     * @param pattern the custom Date pattern
     * @return a reference to this Camel Timer binding model
     */
    CamelTimerBindingModel setPattern(String pattern);

    /**
     * The time in milliseconds to generate periodic events. 
     * @return the period in milliseconds
     */
    Long getPeriod();

    /**
     * If greater than 0, generate periodic events every period milliseconds.
     * @param period the period in milliseconds
     * @return a reference to this Camel Timer binding model
     */
    CamelTimerBindingModel setPeriod(Long period);

    /**
     * The number of milliseconds to wait before the first event is generated. 
     * @return the number of milliseconds to wait before the first event
     */
    Long getDelay();

    /**
     * Specify the number of milliseconds to wait before the first event is 
     * generated. 
     * Should not be used in conjunction with the time option.
     * @param delay The number of milliseconds to wait
     * @return a reference to this Camel Timer binding model
     */
    CamelTimerBindingModel setDelay(Long delay);

    /**
     * Events take place at approximately regular intervals, separated by 
     * the specified period. 
     * @return true if events take place at regular intervals, false otherwihse
     */
    Boolean isFixedRate();

    /**
     * Specify whether events take place at approximately regular intervals, separated by
     * the specified period.
     * @param fixedRate whether events take place at approximately regular intervals (true),
     * or not (false)
     * @return a reference to this Camel Timer binding model
     */
    CamelTimerBindingModel setFixedRate(Boolean fixedRate);

    /**
     * Whether or not the thread associated with the timer endpoint 
     * runs as a daemon. 
     * @return true if the thread runs as a daemon, false otherwise
     */
    Boolean isDaemon();

    /**
     * Specify whether or not the thread associated with the timer endpoint 
     * runs as a daemon.
     * @param daemon true if the thread runs as a daemon, false otherwise
     * @return a reference to this Camel Timer binding model
     */
    CamelTimerBindingModel setDaemon(Boolean daemon);

}
