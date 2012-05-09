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
package org.switchyard.component.camel.config.model.atom.v1;

import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.switchyard.component.camel.config.model.QueryString;
import org.switchyard.component.camel.config.model.CamelScheduledPollConsumer;
import org.switchyard.component.camel.config.model.atom.AtomBindingModel;
import org.switchyard.component.camel.config.model.v1.V1BaseCamelBindingModel;
import org.switchyard.component.camel.config.model.v1.V1CamelScheduledPollConsumer;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * Implementation of AtomBindingModel.
 */
public class V1AtomBindingModel extends V1BaseCamelBindingModel implements AtomBindingModel {

    /**
     * Camel endpoint type.
     */
    public static final String ATOM = "atom";

    /**
     * Camel endpoint configuration values.
     */
    private static final String FEED_URI            = "feedURI";
    private static final String SPLIT_ENTRIES       = "splitEntries";
    private static final String FILTER              = "filter";
    private static final String LAST_UPDATE         = "lastUpdate";
    private static final String THROTTLE_ENTRIES    = "throttleEntries";
    private static final String FEED_HEADER         = "feedHeader";
    private static final String SORT_ENTRIES        = "sortEntries";

    /**
     * Consumer element.
     */
    public static final String CONSUME        = "consume";
    private CamelScheduledPollConsumer _consume;

    // Used for dateTime fields
    private static DateFormat _dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * Create a new AtomBindingModel.
     */
    public V1AtomBindingModel() {
        super(ATOM);
        setModelChildrenOrder(
            FEED_URI,
            SPLIT_ENTRIES, 
            FILTER, 
            LAST_UPDATE, 
            THROTTLE_ENTRIES, 
            FEED_HEADER, 
            SORT_ENTRIES);
    }
    
    /**
     * Create a AtomBindingModel from the specified configuration and descriptor.
     * 
     * @param config The switchyard configuration instance.
     * @param desc The switchyard descriptor instance.
     */
    public V1AtomBindingModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    @Override
    public URI getFeedURI() {
        String uriStr = getConfig(FEED_URI);
        if (uriStr != null) {
            return URI.create(uriStr);
        } else {
            return null;
        }
    }

    @Override
    public V1AtomBindingModel setFeedURI(URI uri) {
        return setConfig(FEED_URI, uri.toString());
    }

    @Override
    public Date getLastUpdate() {
        return getDateConfig(LAST_UPDATE, _dateFormat);
    }

    @Override
    public V1AtomBindingModel setLastUpdate(Date date) {
        setConfig(LAST_UPDATE, _dateFormat.format(date));
        return this;
    }
    
    @Override
    public Boolean isFeedHeader() {
        return getBooleanConfig(FEED_HEADER);
    }

    @Override
    public V1AtomBindingModel setFeedHeader(boolean feedHeader) {
        setConfig(FEED_HEADER, String.valueOf(feedHeader));
        return this;
    }

    @Override
    public Boolean isFiltered() {
        return getBooleanConfig(FILTER);
    }

    @Override
    public V1AtomBindingModel setFiltered(boolean filtered) {
        setConfig(FILTER, String.valueOf(filtered));
        return this;
    }

    @Override
    public Boolean isSorted() {
        return getBooleanConfig(SORT_ENTRIES);
    }

    @Override
    public V1AtomBindingModel setSorted(boolean sorted) {
        setConfig(SORT_ENTRIES, String.valueOf(sorted));
        return this;
    }

    @Override
    public Boolean isSplit() {
        return getBooleanConfig(SPLIT_ENTRIES);
    }

    @Override
    public V1AtomBindingModel setSplit(boolean split) {
        setConfig(SPLIT_ENTRIES, String.valueOf(split));
        return this;
    }

    @Override
    public Boolean isThrottled() {
        return getBooleanConfig(THROTTLE_ENTRIES);
    }

    @Override
    public V1AtomBindingModel setThrottled(boolean throttled) {
        setConfig(THROTTLE_ENTRIES, String.valueOf(throttled));
        return this;
    }


    @Override
    public CamelScheduledPollConsumer getConsumer() {
        if (_consume == null) {
            Configuration config = getModelConfiguration().getFirstChild(CONSUME);
            _consume = new V1CamelScheduledPollConsumer(config,
                getModelDescriptor());
        }
        return _consume;
    }

    @Override
    public V1AtomBindingModel setConsumer(CamelScheduledPollConsumer consumer) {
        Configuration config = getModelConfiguration().getFirstChild(CONSUME);
        if (config != null) {
            // set an existing config value
            getModelConfiguration().removeChildren(CONSUME);
            getModelConfiguration().addChild(((V1CamelScheduledPollConsumer) consumer)
                .getModelConfiguration());
        } else {
            setChildModel((V1CamelScheduledPollConsumer) consumer);
        }
        _consume = consumer;
        return this;
    }

    @Override
    public URI getComponentURI() {
        Configuration modelConfiguration = getModelConfiguration();
        List<Configuration> children = modelConfiguration.getChildren();

        String baseUri = ATOM + "://" + getFeedURI();

        QueryString queryStr = new QueryString();
        traverseConfiguration(children, queryStr, FEED_URI);

        return URI.create(baseUri + queryStr.toString());
    }
}
