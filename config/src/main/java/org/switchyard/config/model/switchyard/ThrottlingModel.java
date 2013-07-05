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
package org.switchyard.config.model.switchyard;

import org.switchyard.config.model.Model;

/**
 * The "throttling" configuration model.
 */
public interface ThrottlingModel extends Model {

    /** The "throttling" name. */
    public static final String THROTTLING = "throttling";

    /** The "timePeriod" attribute. */
    public static final String TIME_PERIOD = "timePeriod";

    /** The "maxRequests" attribute. */
    public static final String MAX_REQUESTS = "maxRequests";

    /**
     * Gets the timePeriod attribute.
     * @return the timePeriod attribute
     */
    public Long getTimePeriod();

    /**
     * Sets the timePeriod attribute.
     * @param timePeriod the timePeriod attribute
     * @return this ThrottlingModel (useful for chaining)
     */
    public ThrottlingModel setTimePeriod(Long timePeriod);

    /**
     * Gets the maxRequests attribute.
     * @return the maxRequests attribute
     */
    public int  getMaxRequests();

    /**
     * Sets the maxRequests attribute.
     * @param maxRequests the timePeriod attribute
     * @return this ThrottlingModel (useful for chaining)
     */
    public ThrottlingModel setMaxRequests(int maxRequests);

}
