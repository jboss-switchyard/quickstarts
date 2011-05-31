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

package org.switchyard.component.camel.config.model.atom;

import java.net.URI;
import java.util.Date;

import org.switchyard.component.camel.config.model.CamelBindingModel;

/**
 * Represents the configuration settings for an Atom endpoint in Camel.  The 
 * Atom component only supports consumer processing, so this binding can only
 * be used on a service (not a reference).
 */
public interface AtomBindingModel extends CamelBindingModel {
    
    /**
     * The Atom feed URI which will be polled.
     * @return the feed URI or null if it has not been specified
     */
    URI getFeedURI();
    /**
     * Set the Atom feed URI.
     * @param uri feed URI
     * @return a reference to this Atom binding model
     */
    AtomBindingModel setFeedURI(URI uri);
    /**
     * Whether feed entries will be split on each poll.
     * @return split setting or null if no configuration has been specified
     */
    Boolean isSplit();
    /**
     * Specify that feed entries will be split on each poll.
     * @param split true if entries should be split, false otherwise
     * @return a reference to this Atom binding model
     */
    AtomBindingModel setSplit(boolean split);
    /**
     * Whether the component should only return new RSS entries.
     * @return filter setting or null if no configuration has been specified
     */
    Boolean isFiltered();
    /**
     * Specify that the source feed should be filtered.
     * @param filter true to filter the feed
     * @return a reference to this Atom binding model
     */
    AtomBindingModel setFiltered(boolean filter);
    /**
     * Set the start date used by the filter for pulling new feeds.
     * @param lastUpdate date after which entries should be pulled
     * @return a reference to this Atom binding model
     */
    AtomBindingModel setLastUpdate(Date lastUpdate);
    /**
     * The start date used by the filter for pulling new feeds.
     * @return date after which entries should be pulled or null if no
     * configuration has been specified
     */
    Date getLastUpdate();
    /**
     * Enables throttled delivery of feeds.
     * @param throttled specify true to enable throttling
     * @return a reference to this Atom binding model
     */
    AtomBindingModel setThrottled(boolean throttled);
    /**
     * Whether throttling is enabled for this endpoint.
     * @return throttle setting or null if no configuration has been specified
     */
    Boolean isThrottled();
    /**
     * Specify whether the underlying Abdera feed object is included as a header.
     * @param header true to include the header, false to not include
     * @return a reference to this Atom binding model
     */
    AtomBindingModel setFeedHeader(boolean header);
    /**
     * Whether the Abdera feed object is included as a header.
     * @return feed header setting or null if no configuration has been specified
     */
    Boolean isFeedHeader();
    /**
     * Specifies whether split entries are sorted by date.
     * @param sorted set to true for split entries to be sorted
     * @return a reference to this Atom binding model
     */
    AtomBindingModel setSorted(boolean sorted);
    /**
     * Whether split entries are sorted by date.
     * @return sort setting or null if no configuration has been specified
     */
    Boolean isSorted();
    /**
     * Set the delay in milliseconds between each polling interval.
     * @param delay wait in milliseconds between each poll
     * @return a reference to this Atom binding model
     */
    AtomBindingModel setDelay(int delay);
    /**
     * The delay in milliseconds between each poll.
     * @return the delay setting or null if no configuration has been specified
     */
    Integer getDelay();
    /**
     * Set the initial delay before polling.
     * @param delay delay in milliseconds
     * @return a reference to this Atom binding model
     */
    AtomBindingModel setInitialDelay(int delay);
    /**
     * The initial delay in milliseconds before polling.
     * @return the delay setting or null if no configuration has been specified
     */
    Integer getInitialDelay();
    /**
     * Specifies whether there is a fixed delay between polling intervals.
     * @param fixedDelay true to set a fixed delay between intervals
     * @return a reference to this Atom binding model
     */
    AtomBindingModel setFixedDelay(boolean fixedDelay);
    /**
     * Whether a fixed delay is enabled between polls.
     * @return fixed delay setting or null if no configuration has been specified
     */
    Boolean isFixedDelay();
}
