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