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
package org.switchyard.component.camel.atom.model.v1;

import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.switchyard.component.camel.atom.model.CamelAtomBindingModel;
import org.switchyard.component.camel.common.QueryString;
import org.switchyard.component.camel.common.model.consumer.CamelScheduledPollConsumer;
import org.switchyard.component.camel.common.model.v1.V1BaseCamelBindingModel;
import org.switchyard.component.camel.common.model.v1.V1CamelScheduledPollConsumer;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

import static org.switchyard.component.camel.atom.model.Constants.ATOM_NAMESPACE_V1;

/**
 * Implementation of AtomBindingModel.
 */
public class V1CamelAtomBindingModel extends V1BaseCamelBindingModel
    implements CamelAtomBindingModel {

    /**
     * Camel endpoint type.
     */
    public static final String ATOM = "atom";

    /**
     * Camel endpoint configuration values.
     */
    private static final String FEED_URI = "feedURI";
    private static final String SPLIT_ENTRIES = "splitEntries";
    private static final String FILTER = "filter";
    private static final String LAST_UPDATE = "lastUpdate";
    private static final String THROTTLE_ENTRIES = "throttleEntries";
    private static final String FEED_HEADER = "feedHeader";
    private static final String SORT_ENTRIES = "sortEntries";

    private CamelScheduledPollConsumer _consume;

    // Used for dateTime fields
    private static DateFormat _dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * Create a new AtomBindingModel.
     */
    public V1CamelAtomBindingModel() {
        super(ATOM, ATOM_NAMESPACE_V1);

        setModelChildrenOrder(FEED_URI, SPLIT_ENTRIES, FILTER, LAST_UPDATE,
            THROTTLE_ENTRIES, FEED_HEADER, SORT_ENTRIES, CONSUME);
    }

    /**
     * Create a AtomBindingModel from the specified configuration and descriptor.
     * 
     * @param config The switchyard configuration instance.
     * @param desc The switchyard descriptor instance.
     */
    public V1CamelAtomBindingModel(Configuration config, Descriptor desc) {
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
    public V1CamelAtomBindingModel setFeedURI(URI uri) {
        return setConfig(FEED_URI, uri.toString());
    }

    @Override
    public Date getLastUpdate() {
        return getDateConfig(LAST_UPDATE, _dateFormat);
    }

    @Override
    public V1CamelAtomBindingModel setLastUpdate(Date date) {
        setConfig(LAST_UPDATE, _dateFormat.format(date));
        return this;
    }
    
    @Override
    public Boolean isFeedHeader() {
        return getBooleanConfig(FEED_HEADER);
    }

    @Override
    public V1CamelAtomBindingModel setFeedHeader(Boolean feedHeader) {
        return setConfig(FEED_HEADER, feedHeader);
    }

    @Override
    public Boolean isFilter() {
        return getBooleanConfig(FILTER);
    }

    @Override
    public V1CamelAtomBindingModel setFilter(Boolean filtered) {
        return setConfig(FILTER, filtered);
    }

    @Override
    public Boolean isSortEntries() {
        return getBooleanConfig(SORT_ENTRIES);
    }

    @Override
    public V1CamelAtomBindingModel setSortEntries(Boolean sorted) {
        return setConfig(SORT_ENTRIES, sorted);
    }

    @Override
    public Boolean isSplitEntries() {
        return getBooleanConfig(SPLIT_ENTRIES);
    }

    @Override
    public V1CamelAtomBindingModel setSplitEntries(Boolean split) {
        return setConfig(SPLIT_ENTRIES, String.valueOf(split));
    }

    @Override
    public Boolean isThrottleEntries() {
        return getBooleanConfig(THROTTLE_ENTRIES);
    }

    @Override
    public V1CamelAtomBindingModel setThrottleEntries(Boolean throttled) {
        return setConfig(THROTTLE_ENTRIES, String.valueOf(throttled));
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
    public V1CamelAtomBindingModel setConsumer(CamelScheduledPollConsumer consumer) {
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
