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
