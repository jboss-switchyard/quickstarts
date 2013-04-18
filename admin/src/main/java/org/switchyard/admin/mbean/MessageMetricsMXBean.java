/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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

package org.switchyard.admin.mbean;

/**
 * Reports messaging metrics for entities in a SwitchYard domain : service, 
 * reference, runtime, etc.
 */
public interface MessageMetricsMXBean {

    /**
     * Return the number of successful messages.
     * @return success count
     */
    int getSuccessCount();
    /**
     * Return the number of failed messages.
     * @return fault count
     */
    int getFaultCount();
    /**
     * Return the total number of messages processed.  This is equivalent to
     * getSuccessCount() + getFaultCount().
     * @return total message count
     */
    int getTotalCount();
    /**
     * Total processing time for all messages in TimeUnit.MILLISECONDS.
     * @return total processing time
     */
    long getTotalProcessingTime();
    /**
     * Average processing time for all messages in TimeUnit.MILLISECONDS.
     * @return average processing time
     */
    double getAverageProcessingTime();
    /**
     * Minimum processing time for a message in TimeUnit.MILLISECONDS.
     * @return min processing time
     */
    int getMinProcessingTime();
    /**
     * Maximum processing time for a message in TimeUnit.MILLISECONDS.
     * @return max processing time
     */
    int getMaxProcessingTime();

    /**
     * Reset all collected metrics.
     */
    void reset();


}
