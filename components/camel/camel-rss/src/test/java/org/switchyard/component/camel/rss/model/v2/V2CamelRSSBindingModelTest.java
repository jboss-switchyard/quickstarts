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
package org.switchyard.component.camel.rss.model.v2;

import static junit.framework.Assert.assertEquals;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.camel.component.feed.FeedEndpoint;
import org.switchyard.component.camel.common.model.consumer.CamelScheduledPollConsumer;
import org.switchyard.component.camel.common.model.v1.V1CamelScheduledPollConsumer;
import org.switchyard.component.camel.config.test.v1.V1BaseCamelServiceBindingModelTest;
import org.switchyard.component.camel.rss.model.CamelRSSNamespace;
import org.switchyard.component.camel.rss.model.v2.V2CamelRSSBindingModel;

/**
 * Test of rss binding model.
 */
public class V2CamelRSSBindingModelTest extends V1BaseCamelServiceBindingModelTest<V2CamelRSSBindingModel, FeedEndpoint> {

    private static final String CAMEL_XML = "/v2/switchyard-rss-binding.xml";

    private static final String CAMEL_URI = 
        "rss://file:///dev/null?feedHeader=true&filter=true&lastUpdate=2011-01-01T12:00:00"
        + "&sortEntries=true&splitEntries=true&throttleEntries=true"
        + "&delay=15000&initialDelay=20000&useFixedDelay=true";

    private Date referenceDate;
    private static final URI FEED_URI = URI.create("file:///dev/null");
    private static final Boolean FEED_HEADER = true;
    private static final Boolean FILTERED = true;
    private static final Boolean SORTED = true;
    private static final Boolean SPLIT = true;
    private static final Boolean THROTTLED = true;

    public V2CamelRSSBindingModelTest() throws ParseException {
        super(FeedEndpoint.class, CAMEL_XML);

        referenceDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .parse("2011-01-01T12:00:00");
    }


    @Override
    protected void createModelAssertions(V2CamelRSSBindingModel model) {
        assertEquals(FEED_URI, model.getFeedURI());
        assertEquals(referenceDate.toString(), model.getLastUpdate().toString());
        assertEquals(FEED_HEADER, model.isFeedHeader());
        assertEquals(FILTERED, model.isFilter());
        assertEquals(SORTED, model.isSortEntries());
        assertEquals(SPLIT, model.isSplitEntries());
        assertEquals(THROTTLED, model.isThrottleEntries());
    }

    @Override
    protected V2CamelRSSBindingModel createTestModel() {
        V2CamelRSSBindingModel abm = new V2CamelRSSBindingModel(CamelRSSNamespace.V_2_0.uri())
            .setFeedURI(FEED_URI)
            .setSplitEntries(SPLIT)
            .setFilter(FILTERED)
            .setLastUpdate(referenceDate)
            .setThrottleEntries(THROTTLED)
            .setFeedHeader(FEED_HEADER)
            .setSortEntries(SORTED);

        CamelScheduledPollConsumer consumer = new V1CamelScheduledPollConsumer(CamelRSSNamespace.V_2_0.uri(), V2CamelRSSBindingModel.CONSUME)
            .setInitialDelay(20000)
            .setDelay(15000)
            .setUseFixedDelay(true);
        return abm.setConsumer(consumer);
    }

    @Override
    protected String createEndpointUri() {
        return CAMEL_URI;
    }

}
