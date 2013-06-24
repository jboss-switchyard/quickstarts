/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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
