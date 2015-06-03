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

package org.switchyard.component.soap;

import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.metadata.InOutService;
import org.switchyard.test.MockHandler;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.SwitchYardTestKit;
import org.switchyard.component.test.mixins.http.HTTPMixIn;

/**
 * Tests SOAP-specific message composer.
 */
@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(
        config = "unwrapped-switchyard.xml",
        mixins = {HTTPMixIn.class})
public class UnwrappedMessageTest {

    private static final String UNWRAPPED_PAYLOAD = "unwrappedRequest.xml";
    private static final String WRAPPED_REPLY = "wrappedReply.xml";
    
    private SwitchYardTestKit testKit;
    private HTTPMixIn httpMixIn;

    @Test
    public void testWrapUnwrap() throws Exception {
        MockHandler provider = new MockHandler().forwardInToOut();
        testKit.registerInOutService("Payments", provider, new InOutService("submit"));
        String reply = httpMixIn.postResource("http://localhost:18001/Payments", "wrappedRequest.xml");
        String receivedPayload = provider.getMessages().poll(300, TimeUnit.MILLISECONDS)
                .getMessage().getContent(String.class);
        
        // verify request is unwrapped
        XMLUnit.setIgnoreWhitespace(true);
        Diff requestDiff = XMLUnit.compareXML(testKit.readResourceString(UNWRAPPED_PAYLOAD), receivedPayload);
        Assert.assertTrue(requestDiff.toString(), requestDiff.similar());
        
        // verify reply is wrapped
        Diff replyDiff = XMLUnit.compareXML(testKit.readResourceString(WRAPPED_REPLY), reply);
        Assert.assertTrue(replyDiff.toString(), replyDiff.similar());
    }
}