/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
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
package org.switchyard.quickstarts.camel.rss;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.Exchange;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.test.MockHandler;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.SwitchYardTestKit;
import org.switchyard.transform.config.model.TransformSwitchYardScanner;

@SwitchYardTestCaseConfig(
    config = SwitchYardTestCaseConfig.SWITCHYARD_XML,
    mixins = { CDIMixIn.class },
    scanners = TransformSwitchYardScanner.class)
@RunWith(SwitchYardRunner.class)
public class CamelRSSPollTest {

    private SwitchYardTestKit _testKit;
    
    private static final String SONG_TITLE = "I'd Do Anything For Love (But I won't Do that)";
    private static final String SONG_URL = "http://en.wikipedia.org/wiki/I%27d_Do_Anything_for_Love_(But_I_Won%27t_Do_That)";
    private static final String SONG_DESCRIPTION = "I'd Do Anything For Love (But I won't Do That) by Meatloaf";

    @Test
    public void shouldRetrieveGreetings() throws Exception {

        _testKit.removeService("PrintService");
        final MockHandler printService = _testKit.registerInOnlyService("PrintService");
        Thread.sleep(10001);
        
        final LinkedBlockingQueue<Exchange> receivedMessages = printService.getMessages();
        for (Exchange e : receivedMessages) {
            SyndFeed feed = (SyndFeed) e.getMessage().getContent();
            @SuppressWarnings("unchecked")
            List<SyndEntry> entries = feed.getEntries();
            Assert.assertEquals(feed.getEntries().size(), 1);
            Iterator<SyndEntry> itEntries = entries.iterator();
     
            while (itEntries.hasNext()) {
                SyndEntry entry = (SyndEntry) itEntries.next();
                Assert.assertTrue(entry.getTitle().equals(SONG_TITLE));
                Assert.assertTrue(entry.getLink().equals(SONG_URL));
                Assert.assertTrue(entry.getDescription().getValue().equals(SONG_DESCRIPTION));
            }
        }
    }
}
