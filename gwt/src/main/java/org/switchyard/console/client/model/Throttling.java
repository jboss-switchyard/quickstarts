/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
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

/**
 * Throttling
 * <p/>
 * Throttling details associated switchyard object.
 */
public interface Throttling {

    /**
     * @return true if throttling is enabled.
     */
    Boolean isEnabled();
    
    /**
     * @param enabled true to enable throttling.
     */
    void setEnabled(Boolean enabled);

    /**
     * @return the maximum number of requests per time period.
     */
    Integer getMaxRequests();

    /**
     * @param maxRequests the maximumn number of requests per time period.
     */
    void setMaxRequests(Integer maxRequests);

    /**
     * @return the time period, in milliseconds, over which requests are
     *         counted.
     */
    Long getTimePeriod();

    /**
     * @param timePeriod the time period, in milliseconds, over which requests
     *            are counted.
     */
    void setTimePeriod(Long timePeriod);
}
