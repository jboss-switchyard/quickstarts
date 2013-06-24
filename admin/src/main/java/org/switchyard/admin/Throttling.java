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
