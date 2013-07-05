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
package org.switchyard.admin;

/**
 * Throttling
 * <p/>
 * Throttling details associated with a management object.
 */
public interface Throttling {

    /**
     * @return true if throttling is enabled.
     */
    boolean isEnabled();

    /**
     * enable throttling for the associated object.
     */
    void enable();

    /**
     * disable throttling for the associated object.
     */
    void disable();

    /**
     * @return the maximum number of requests per time period.
     */
    int getMaxRequests();

    /**
     * @param maxRequests the maximumn number of requests per time period.
     */
    void setMaxRequests(int maxRequests);

    /**
     * @return the time period, in milliseconds, over which requests are
     *         counted.
     */
    long getTimePeriod();
    
    /**
     * Update the throttling details.
     * 
     * @param enabled true to enable, false to disable, null for no change.
     * @param maxRequests maxRequests; null for no change.
     */
    void update(Boolean enabled, Integer maxRequests);
}
