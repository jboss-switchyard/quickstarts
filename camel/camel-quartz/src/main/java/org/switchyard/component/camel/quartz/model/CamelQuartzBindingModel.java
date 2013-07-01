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
    String getTimerName();
    
    /**
     * Sets the name of the timer.
     * @param name timer name
     * @return a reference to this config model
     */
    CamelQuartzBindingModel setTimerName(String name);

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
