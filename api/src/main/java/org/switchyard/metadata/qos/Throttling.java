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

package org.switchyard.metadata.qos;

/**
 * Represents configuration for throttling of service references.
 */
public class Throttling {
    
    /**
     * Message header used to specify the max requests used by the throttler in a given
     * time period.
     */
    public static final String MAX_REQUESTS = "org.switchyard.qos.throttling.maxRequests";
    
    /**
     * Default time period is one second.
     */
    public static final long DEFAULT_TIME_PERIOD = 1000;
    
    
    private int _maxRequests;
    private long _timePeriod = DEFAULT_TIME_PERIOD;

    /**
     * Returns the maximum number of requests allowed in a given time period.
     * @return max requests
     */
    public int getMaxRequests() {
        return _maxRequests;
    }
    
    /**
     * Returns the time period in milliseconds used to calculate the max request window.
     * @return time period in milliseconds
     */
    public long getTimePeriod() {
        return _timePeriod;
    }
    
    /**
     * Specifies the maximum number of requests allowed in a given time period.
     * @param max maximum number of requests
     * @return a reference to this Throttling instance
     */
    public Throttling setMaxRequests(int max) {
        _maxRequests = max;
        return this;
    }
    
    /**
     * Specifies the time period in milliseconds used to calculate the max request window.
     * @param timeMS time period in milliseconds
     * @return a reference to this Throttling instance
     */
    public Throttling setTimePeriod(long timeMS) {
        _timePeriod = timeMS;
        return this;
    }
}
