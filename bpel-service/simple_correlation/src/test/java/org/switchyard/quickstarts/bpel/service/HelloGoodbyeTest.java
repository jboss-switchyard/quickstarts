/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2010-2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more 
 * details. You should have received a copy of the GNU Lesser General Public 
 * License, v.2.1 along with this distribution; if not, write to the Free 
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  
 * 02110-1301, USA.
 */

package org.switchyard.quickstarts.bpel.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.test.Invoker;
import org.switchyard.test.ServiceOperation;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.SwitchYardTestKit;

@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(
        config = SwitchYardTestCaseConfig.SWITCHYARD_XML,
        mixins = {CDIMixIn.class})
public class HelloGoodbyeTest {
    
    @ServiceOperation("simpleCorrelationService.hello")
    private Invoker correlationHello;
    
    @ServiceOperation("simpleCorrelationService.goodbye")
    private Invoker correlationGoodbye;

    private SwitchYardTestKit testKit;
    
    @Test
    public void testHelloGoodbye() throws Exception {
        String requestTxt = testKit.readResourceString("xml/xml-hello_request1.xml");
        String replyTxt = testKit.readResourceString("xml/xml-hello_response1.xml");
        
        String replyMsg = correlationHello.sendInOut(requestTxt).getContent(String.class);
        Assert.assertEquals(replyTxt, replyMsg);

        requestTxt = testKit.readResourceString("xml/xml-goodbye_request1.xml");
        replyTxt = testKit.readResourceString("xml/xml-goodbye_response1.xml");
        
        replyMsg = correlationGoodbye.sendInOut(requestTxt).getContent(String.class);
        Assert.assertEquals(replyTxt, replyMsg);
    }
    
}
