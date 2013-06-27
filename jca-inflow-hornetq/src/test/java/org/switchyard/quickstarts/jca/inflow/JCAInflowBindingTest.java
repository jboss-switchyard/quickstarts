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
package org.switchyard.quickstarts.jca.inflow;

import javax.jms.MessageProducer;
import javax.jms.TextMessage;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.Exchange;
import org.switchyard.test.BeforeDeploy;
import org.switchyard.test.MockHandler;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.SwitchYardTestKit;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.component.test.mixins.hornetq.HornetQMixIn;
import org.switchyard.component.test.mixins.jca.JCAMixIn;
import org.switchyard.component.test.mixins.jca.ResourceAdapterConfig;

/**
 * Functional test for a SwitchYard Service which has a service binding to a HornetQ
 * queue via JCA inflow.
 * 
 * @author Daniel Bevenius
 */
@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(
        config = SwitchYardTestCaseConfig.SWITCHYARD_XML,
        mixins = {HornetQMixIn.class, JCAMixIn.class, CDIMixIn.class}
)
public class JCAInflowBindingTest {
    
    private SwitchYardTestKit _testKit;
    private HornetQMixIn _hqMixIn;
    private JCAMixIn _jcaMixIn;
    
    @BeforeDeploy
    public void before() {
        ResourceAdapterConfig ra = new ResourceAdapterConfig(ResourceAdapterConfig.ResourceAdapterType.HORNETQ);
        _jcaMixIn.deployResourceAdapters(ra);
    }
    /**
     * Triggers the 'GreetingService' by sending a HornetQ Message to the 'GreetingServiceQueue'
     */
    @Test
    public void triggerGreetingService() throws Exception {
        // replace existing implementation for testing purposes
        _testKit.removeService("GreetingService");
        final MockHandler greetingService = _testKit.registerInOnlyService("GreetingService");
        
        final MessageProducer producer = _hqMixIn.getJMSSession().createProducer(HornetQMixIn.getJMSQueue("JCAInflowGreetingServiceQueue"));
        final TextMessage message = _hqMixIn.getJMSSession().createTextMessage();
        message.setText(PAYLOAD);
        producer.send(message);

        greetingService.waitForOKMessage();

        final Exchange recievedExchange = greetingService.getMessages().iterator().next();
        Assert.assertEquals(PAYLOAD, recievedExchange.getMessage().getContent(String.class));
    }
    
    private static final String PAYLOAD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<qs:person xmlnl:qs=\"urn:switchyard-quickstart:jca-inflow-hornetq:0.1.0\">\n"
            + "    <qs:name>dummy</qs:name>\n"
            + "    <qs:language>english</qs:language>\n"
            + "</qs:person>\n";
    
}
