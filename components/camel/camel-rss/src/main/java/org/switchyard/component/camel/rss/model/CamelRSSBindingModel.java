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
package org.switchyard.component.camel.rss.model;

import java.net.URI;
import java.util.Date;

import org.switchyard.component.camel.common.model.CamelBindingModel;
import org.switchyard.component.camel.common.model.consumer.CamelScheduledPollConsumer;

/**
 * Represents the configuration settings for an RSS endpoint in Camel.  The 
 * RSS component only supports consumer processing, so this binding can only
 * be used on a service (not a reference).
 */
public interface CamelRSSBindingModel extends CamelBindingModel {

    /**
     * The RSS feed URI which will be polled.
     * @return the feed URI or null if it has not been specified
     */
    URI getFeedURI();

    /**
     * Set the RSS feed URI.
     * @param uri feed URI
     * @return a reference to this RSS binding model
     */
    CamelRSSBindingModel setFeedURI(URI uri);

    /**
     * Whether feed entries will be split on each poll.
     * @return split setting or null if no configuration has been specified
     */
    Boolean isSplitEntries();

    /**
     * Specify that feed entries will be split on each poll.
     * @param split true if entries should be split, false otherwise
     * @return a reference to this RSS binding model
     */
    CamelRSSBindingModel setSplitEntries(Boolean split);

    /**
     * Whether the component should only return new RSS entries.
     * @return filter setting or null if no configuration has been specified
     */
    Boolean isFilter();

    /**
     * Specify that the source feed should be filtered.
     * @param filter true to filter the feed
     * @return a reference to this RSS binding model
     */
    CamelRSSBindingModel setFilter(Boolean filter);

    /**
     * Set the start date used by the filter for pulling new feeds.
     * @param lastUpdate date after which entries should be pulled
     * @return a reference to this RSS binding model
     */
    CamelRSSBindingModel setLastUpdate(Date lastUpdate);

    /**
     * The start date used by the filter for pulling new feeds.
     * @return date after which entries should be pulled or null if no
     * configuration has been specified
     */
    Date getLastUpdate();

    /**
     * Enables throttled delivery of feeds.
     * @param throttled specify true to enable throttling
     * @return a reference to this RSS binding model
     */
    CamelRSSBindingModel setThrottleEntries(Boolean throttled);

    /**
     * Whether throttling is enabled for this endpoint.
     * @return throttle setting or null if no configuration has been specified
     */
    Boolean isThrottleEntries();

    /**
     * Specify whether the underlying Abdera feed object is included as a header.
     * @param header true to include the header, false to not include
     * @return a reference to this RSS binding model
     */
    CamelRSSBindingModel setFeedHeader(Boolean header);

    /**
     * Whether the Abdera feed object is included as a header.
     * @return feed header setting or null if no configuration has been specified
     */
    Boolean isFeedHeader();

    /**
     * Specifies whether split entries are sorted by date.
     * @param sorted set to true for split entries to be sorted
     * @return a reference to this RSS binding model
     */
    CamelRSSBindingModel setSortEntries(Boolean sorted);

    /**
     * Whether split entries are sorted by date.
     * @return sort setting or null if no configuration has been specified
     */
    Boolean isSortEntries();

    /**
     * Sets consumer properties for RSS.
     * 
     * @param consumer Configuration of consumer.
     * @return a reference to this RSS binding model
     */
    CamelRSSBindingModel setConsumer(CamelScheduledPollConsumer consumer);

    /**
     * @return RSS consumer configuration.
     */
    CamelScheduledPollConsumer getConsumer();

}
