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
