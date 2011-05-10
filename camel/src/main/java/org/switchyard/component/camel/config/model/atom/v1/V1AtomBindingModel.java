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

import org.switchyard.component.camel.config.model.QueryString;
import org.switchyard.component.camel.config.model.atom.AtomBindingModel;
import org.switchyard.component.camel.config.model.v1.NameValueModel;
import org.switchyard.component.camel.config.model.v1.V1BaseCamelBindingModel;
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
    private static final String DELAY               = "consumer.delay";
    private static final String INITIAL_DELAY       = "consumer.initialDelay";
    private static final String FIXED_DELAY         = "consumer.userFixedDelay";
    
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
                SORT_ENTRIES, 
                DELAY, 
                INITIAL_DELAY, 
                FIXED_DELAY);
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
    public Integer getDelay() {
        return getIntegerConfig(DELAY);
    }
    
    @Override
    public V1AtomBindingModel setDelay(int delay) {
        setConfig(DELAY, String.valueOf(delay));
        return this;
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
        setConfig(FEED_URI, uri.toString());
        return this;
    }

    @Override
    public Boolean isFixedDelay() {
        return getBooleanConfig(FIXED_DELAY);
    }

    @Override
    public V1AtomBindingModel setFixedDelay(boolean delay) {
        setConfig(FIXED_DELAY, String.valueOf(delay));
        return this;
    }

    @Override
    public Integer getInitialDelay() {
        return getIntegerConfig(INITIAL_DELAY);
    }

    @Override
    public V1AtomBindingModel setInitialDelay(int delay) {
        setConfig(INITIAL_DELAY, String.valueOf(delay));
        return this;
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
    public URI getComponentURI() {
        // base URI without params
        String uriStr = ATOM + "://" + getConfig(FEED_URI);
        // create query string from config values
        QueryString queryStr = new QueryString()
            .add(DELAY, getConfig(DELAY))
            .add(FEED_HEADER, getConfig(FEED_HEADER))
            .add(FILTER, getConfig(FILTER))
            .add(FIXED_DELAY, getConfig(FIXED_DELAY))
            .add(INITIAL_DELAY, getConfig(INITIAL_DELAY))
            .add(LAST_UPDATE, getConfig(LAST_UPDATE))
            .add(SORT_ENTRIES, getConfig(SORT_ENTRIES))
            .add(SPLIT_ENTRIES, getConfig(SPLIT_ENTRIES))
            .add(THROTTLE_ENTRIES, getConfig(THROTTLE_ENTRIES));
        
        return URI.create(uriStr.toString() + queryStr);
    }
    
    private Date getDateConfig(String configName, DateFormat format) {
        String value = getConfig(configName);
        if (value == null) {
            return null;
        } else {
            try {
                return format.parse(value);
            } catch (java.text.ParseException parseEx) {
                throw new IllegalArgumentException("Failed to parse "
                        + configName + " as a date.", parseEx);
            }
        }
    }
    
    private Integer getIntegerConfig(String configName) {
        String value = getConfig(configName);
        return value != null ? Integer.parseInt(value) : null;
    }
    
    private Boolean getBooleanConfig(String configName) {
        String value = getConfig(configName);
        return value != null ? Boolean.valueOf(value) : null;
    }
    
    private String getConfig(String configName) {
        Configuration config = getModelConfiguration().getFirstChild(configName);
        if (config != null) {
            return config.getValue();
        } else {
            return null;
        }
    }
    
    private void setConfig(String name, String value) {
        Configuration config = getModelConfiguration().getFirstChild(name);
        if (config != null) {
            // set an existing config value
            config.setValue(value);
        } else {
            // create the config model and set the value
            NameValueModel model = new NameValueModel(name);
            model.setValue(value);
            setChildModel(model);
        }
    }
    
}
