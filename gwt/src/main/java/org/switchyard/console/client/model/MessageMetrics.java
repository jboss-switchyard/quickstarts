/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
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
