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
package org.switchyard.component.camel.quartz.model;

import java.util.Date;

import org.switchyard.component.camel.common.model.CamelBindingModel;

/**
 * Binding model for quartz endpoint.
 * 
 * @author Lukasz Dywicki
 */
public interface CamelQuartzBindingModel extends CamelBindingModel {

    /**
     * Binding / camel endpoint prefix.
     */
    String QUARTZ = "quartz";

    /**
     * Returns the name of the timer.
     * @return timer name
     */
    String getName();
    
    /**
     * Sets the name of the timer.
     * @param name timer name
     * @return a reference to this config model
     */
    CamelQuartzBindingModel setName(String name);

    /**
     * Returns the cron expression for this quartz endpoint.
     * @return cron expression
     */
    String getCron();
    
    /**
     * Sets the cron expression for this quartz endpoint.
     * @param cron expression
     * @return a reference to this config model
     */
    CamelQuartzBindingModel setCron(String cron);
    
    /**
     * Whether the quartz schedule is stateful.
     * @return true if the endpoint is stateful, false otherwise
     */
    Boolean isStateful();
    
    /**
     * Specifies whether the schedule should be stateful.
     * @param stateful true for stateful, false for stateless
     * @return a reference to this config model
     */
    CamelQuartzBindingModel setStateful(Boolean stateful);

    /**
     * Returns the schedule start time.
     * @return Date corresponding to the schedule start time.
     */
    Date getStartTime();
    
    /**
     * Sets the schedule start time.
     * @param startTime Date corresponding to the schedule start time.
     * @return a reference to this config model
     */
    CamelQuartzBindingModel setStartTime(Date startTime);

    /**
     * Returns the schedule end time.
     * @return Date corresponding to the schedule end time.
     */
    Date getEndTime();
    
    /**
     * Sets the schedule end time.
     * @param endTime Date corresponding to the schedule end time.
     * @return a reference to this config model
     */
    CamelQuartzBindingModel setEndTime(Date endTime);

}
