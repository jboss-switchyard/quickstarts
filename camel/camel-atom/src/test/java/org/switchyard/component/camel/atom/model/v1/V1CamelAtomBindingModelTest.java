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

import static junit.framework.Assert.assertEquals;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.camel.component.feed.FeedEndpoint;
import org.switchyard.component.camel.atom.model.Constants;
import org.switchyard.component.camel.common.model.consumer.CamelScheduledPollConsumer;
import org.switchyard.component.camel.common.model.v1.V1CamelScheduledPollConsumer;
import org.switchyard.component.camel.config.test.v1.V1BaseCamelServiceBindingModelTest;

/**
 * Test of atom binding model.
 */
public class V1CamelAtomBindingModelTest extends V1BaseCamelServiceBindingModelTest<V1CamelAtomBindingModel, FeedEndpoint> {

    private static final String CAMEL_XML = "/v1/switchyard-atom-binding.xml";

    private static final String CAMEL_URI = 
        "atom://file:///dev/null?feedHeader=true&filter=true&lastUpdate=2011-01-01T12:00:00"
        + "&sortEntries=true&splitEntries=true&throttleEntries=true"
        + "&delay=15000&initialDelay=20000&useFixedDelay=true";

    private Date referenceDate;
    private static final URI FEED_URI = URI.create("file:///dev/null");
    private static final Boolean FEED_HEADER = true;
    private static final Boolean FILTERED = true;
    private static final Boolean SORTED = true;
    private static final Boolean SPLIT = true;
    private static final Boolean THROTTLED = true;

    public V1CamelAtomBindingModelTest() throws ParseException {
        super(FeedEndpoint.class, CAMEL_XML);

        referenceDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .parse("2011-01-01T12:00:00");
    }


    @Override
    protected void createModelAssertions(V1CamelAtomBindingModel model) {
        assertEquals(FEED_URI, model.getFeedURI());
        assertEquals(referenceDate.toString(), model.getLastUpdate().toString());
        assertEquals(FEED_HEADER, model.isFeedHeader());
        assertEquals(FILTERED, model.isFilter());
        assertEquals(SORTED, model.isSortEntries());
        assertEquals(SPLIT, model.isSplitEntries());
        assertEquals(THROTTLED, model.isThrottleEntries());
    }

    @Override
    protected V1CamelAtomBindingModel createTestModel() {
        V1CamelAtomBindingModel abm = new V1CamelAtomBindingModel()
            .setFeedURI(FEED_URI)
            .setSplitEntries(SPLIT)
            .setFilter(FILTERED)
            .setLastUpdate(referenceDate)
            .setThrottleEntries(THROTTLED)
            .setFeedHeader(FEED_HEADER)
            .setSortEntries(SORTED);

        CamelScheduledPollConsumer consumer = new V1CamelScheduledPollConsumer(V1CamelAtomBindingModel.CONSUME, Constants.ATOM_NAMESPACE_V1)
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
