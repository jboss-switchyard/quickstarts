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
package org.switchyard.console.client.model;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

/**
 * MessageMetrics
 * 
 * <p/>
 * Model element for SwitchYard MessageMetrics.
 * 
 * @author Rob Cernich
 */
public interface MessageMetrics {

    /**
     * Return the number of successful messages.
     * 
     * @return success count
     */
    int getSuccessCount();

    /**
     * @param value success count
     */
    void setSuccessCount(int value);

    /**
     * Return the number of failed messages.
     * 
     * @return fault count
     */
    int getFaultCount();

    /**
     * @param value fault count
     */
    void setFaultCount(int value);

    /**
     * Return the total number of messages processed. This is equivalent to
     * getSuccessCount() + getFaultCount().
     * 
     * @return total message count
     */
    int getTotalCount();

    /**
     * @param value total count
     */
    void setTotalCount(int value);

    /**
     * Total processing time for all messages in TimeUnit.MILLISECONDS.
     * 
     * @return total processing time
     */
    @PropertyName("totalTime")
    long getTotalProcessingTime();

    /**
     * @param value total processing time
     */
    @PropertyName("totalTime")
    void setTotalProcessingTime(long value);

    /**
     * Average processing time for all messages in TimeUnit.MILLISECONDS.
     * 
     * @return average processing time
     */
    @PropertyName("averageTime")
    Double getAverageProcessingTime();

    /**
     * @param value average processing time
     */
    @PropertyName("averageTime")
    void setAverageProcessingTime(Double value);

    /**
     * Minimum processing time for a message in TimeUnit.MILLISECONDS.
     * 
     * @return min processing time
     */
    @PropertyName("minTime")
    int getMinProcessingTime();

    /**
     * @param value min processing time
     */
    @PropertyName("minTime")
    void setMinProcessingTime(int value);

    /**
     * Maximum processing time for a message in TimeUnit.MILLISECONDS.
     * 
     * @return max processing time
     */
    @PropertyName("maxTime")
    int getMaxProcessingTime();

    /**
     * @param value max processing time
     */
    @PropertyName("maxTime")
    void setMaxProcessingTime(int value);

}
