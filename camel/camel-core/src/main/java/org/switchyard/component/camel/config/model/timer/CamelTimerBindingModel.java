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

package org.switchyard.component.camel.config.model.timer;


import java.util.Date;

import org.switchyard.component.camel.config.model.CamelBindingModel;


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
    public String getName();

    /**
     * Specify the timer name.
     * @param name the timer name
     * @return a reference to this Camel Timer binding model
     */
    public CamelTimerBindingModel setName(String name);

    /**
     * A java.util.Date the first event should be generated. 
     * If using the URI, the pattern expected is: yyyy-MM-dd HH:mm:ss 
     * or yyyy-MM-dd'T'HH:mm:ss. 
     * @return the first event date
     */
    public Date getTime();

    /**
     * Specify the java.util.Date the first event should be generated. 
     * If using the URI, the pattern expected is: yyyy-MM-dd HH:mm:ss 
     * or yyyy-MM-dd'T'HH:mm:ss
     * @param time the specified first event date
     * @return a reference to this Camel Timer binding model
     */
    public CamelTimerBindingModel setTime(Date time);

    /**
     * A custom Date pattern to use for setting the time option using URI syntax.
     * @return the custom Date pattern
     */
    public String getPattern();

    /**
     * Specify a custom Date pattern to use for setting the time option 
     * using URI syntax.
     * @param pattern the custom Date pattern
     * @return a reference to this Camel Timer binding model
     */
    public CamelTimerBindingModel setPattern(String pattern);
    
    /**
     * The time in milliseconds to generate periodic events. 
     * @return the period in milliseconds
     */
    public Long getPeriod();

    /**
     * If greater than 0, generate periodic events every period milliseconds.
     * @param period the period in milliseconds
     * @return a reference to this Camel Timer binding model
     */
    public CamelTimerBindingModel setPeriod(Long period);
    
    /**
     * The number of milliseconds to wait before the first event is generated. 
     * @return the number of milliseconds to wait before the first event
     */
    public Long getDelay();

    /**
     * Specify the number of milliseconds to wait before the first event is 
     * generated. 
     * Should not be used in conjunction with the time option.
     * @param delay The number of milliseconds to wait
     * @return a reference to this Camel Timer binding model
     */
    public CamelTimerBindingModel setDelay(Long delay);
    
    /**
     * Events take place at approximately regular intervals, separated by 
     * the specified period. 
     * @return true if events take place at regular intervals, false otherwihse
     */
    public Boolean isFixedRate();

    /**
     * Specify whether events take place at approximately regular intervals, separated by
     * the specified period.
     * @param fixedRate whether events take place at approximately regular intervals (true),
     * or not (false)
     * @return a reference to this Camel Timer binding model
     */
    public CamelTimerBindingModel setFixedRate(Boolean fixedRate);
    
    /**
     * Whether or not the thread associated with the timer endpoint 
     * runs as a daemon. 
     * @return true if the thread runs as a daemon, false otherwise
     */
    public Boolean isDaemon();

    /**
     * Specify whether or not the thread associated with the timer endpoint 
     * runs as a daemon.
     * @param daemon true if the thread runs as a daemon, false otherwise
     * @return a reference to this Camel Timer binding model
     */
    public CamelTimerBindingModel setDaemon(Boolean daemon);
    
}
