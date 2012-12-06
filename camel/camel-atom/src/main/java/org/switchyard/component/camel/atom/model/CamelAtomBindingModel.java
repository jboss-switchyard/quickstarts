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
package org.switchyard.component.camel.atom.model;

import java.net.URI;
import java.util.Date;

import org.switchyard.component.camel.common.model.CamelBindingModel;
import org.switchyard.component.camel.common.model.consumer.CamelScheduledPollConsumer;

/**
 * Represents the configuration settings for an Atom endpoint in Camel.  The 
 * Atom component only supports consumer processing, so this binding can only
 * be used on a service (not a reference).
 */
public interface CamelAtomBindingModel extends CamelBindingModel {

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
    CamelAtomBindingModel setFeedURI(URI uri);

    /**
     * Whether feed entries will be split on each poll.
     * @return split setting or null if no configuration has been specified
     */
    Boolean isSplitEntries();

    /**
     * Specify that feed entries will be split on each poll.
     * @param split true if entries should be split, false otherwise
     * @return a reference to this Atom binding model
     */
    CamelAtomBindingModel setSplitEntries(Boolean split);

    /**
     * Whether the component should only return new RSS entries.
     * @return filter setting or null if no configuration has been specified
     */
    Boolean isFilter();

    /**
     * Specify that the source feed should be filtered.
     * @param filter true to filter the feed
     * @return a reference to this Atom binding model
     */
    CamelAtomBindingModel setFilter(Boolean filter);

    /**
     * Set the start date used by the filter for pulling new feeds.
     * @param lastUpdate date after which entries should be pulled
     * @return a reference to this Atom binding model
     */
    CamelAtomBindingModel setLastUpdate(Date lastUpdate);

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
    CamelAtomBindingModel setThrottleEntries(Boolean throttled);

    /**
     * Whether throttling is enabled for this endpoint.
     * @return throttle setting or null if no configuration has been specified
     */
    Boolean isThrottleEntries();

    /**
     * Specify whether the underlying Abdera feed object is included as a header.
     * @param header true to include the header, false to not include
     * @return a reference to this Atom binding model
     */
    CamelAtomBindingModel setFeedHeader(Boolean header);

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
    CamelAtomBindingModel setSortEntries(Boolean sorted);

    /**
     * Whether split entries are sorted by date.
     * @return sort setting or null if no configuration has been specified
     */
    Boolean isSortEntries();

    /**
     * Sets consumer properties for atom.
     * 
     * @param consumer Configuration of consumer.
     * @return a reference to this Atom binding model
     */
    CamelAtomBindingModel setConsumer(CamelScheduledPollConsumer consumer);

    /**
     * @return Atom consumer configuration.
     */
    CamelScheduledPollConsumer getConsumer();

}
