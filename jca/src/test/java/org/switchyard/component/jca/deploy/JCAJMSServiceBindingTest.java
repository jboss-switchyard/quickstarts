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
package org.switchyard.component.jca.deploy;

import javax.jms.MessageProducer;
import javax.jms.TextMessage;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.Context;
import org.switchyard.Exchange;
import org.switchyard.HandlerException;
import org.switchyard.component.common.label.PropertyLabel;
import org.switchyard.component.jca.composer.JMSContextMapper;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.component.test.mixins.hornetq.HornetQMixIn;
import org.switchyard.component.test.mixins.jca.JCAMixIn;
import org.switchyard.component.test.mixins.jca.ResourceAdapterConfig;
import org.switchyard.Property;
import org.switchyard.test.BeforeDeploy;
import org.switchyard.test.MockHandler;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.SwitchYardTestKit;

/**
 * Functional test for {@link JCAActivator}.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(config = "switchyard-inbound-jms-test.xml", mixins = {JCAMixIn.class, HornetQMixIn.class, CDIMixIn.class})
public class JCAJMSServiceBindingTest  {
    
    private static final String INPUT_QUEUE = "TestQueue";
    private SwitchYardTestKit _testKit;
    private HornetQMixIn _hqMixIn;
    private JCAMixIn _jcaMixIn;
    
    @BeforeDeploy
    public void before() {
        ResourceAdapterConfig ra = new ResourceAdapterConfig(ResourceAdapterConfig.ResourceAdapterType.HORNETQ);
        _jcaMixIn.deployResourceAdapters(ra);
    }
    
    @Test
    public void testInflowJMS() throws Exception {
        _testKit.removeService("JCAJMSService");
        final MockHandler mockHandler = _testKit.registerInOnlyService("JCAJMSService");
        
        final MessageProducer producer = _hqMixIn.getJMSSession().createProducer(HornetQMixIn.getJMSQueue(INPUT_QUEUE));
        TextMessage msg = _hqMixIn.getJMSSession().createTextMessage();
        msg.setText("payload");
        producer.send(msg);
        
        // wait for message to be picked up the HornetQ queue.
        mockHandler.waitForOKMessage();
        
        Assert.assertEquals(1, mockHandler.getMessages().size());
        Exchange exchange = mockHandler.getMessages().poll();
        final Object content = exchange.getMessage().getContent();
        Assert.assertTrue(content instanceof String);
        final String string = (String) content;
        Assert.assertEquals(string, "payloadtest");
        final Context context = exchange.getContext();
        final String val = context.getProperty("testProp").getValue().toString();
        Assert.assertEquals(val, "testVal");

        Property jmsMessageId = context.getProperty(JMSContextMapper.HEADER_JMS_MESSAGE_ID);
        Assert.assertTrue(jmsMessageId.hasLabel(PropertyLabel.HEADER.label()));
        Assert.assertNotNull(jmsMessageId.getValue().toString());
    }
    
    @Test
    public void testInflowJMS_fault_rollback() throws Exception {
        _testKit.removeService("JCAJMSService");
        final MockHandler mockHandler = new MockHandler() {
            private int count = 0;
            public void handleMessage(final Exchange exchange) throws HandlerException {
                if (count < 4) {
                    count++;
                    handleFault(exchange);
                    throw new HandlerException("faultmessage");
                } else {
                    super.handleMessage(exchange);
                }
            }
        };
        _testKit.registerInOnlyService("JCAJMSService", mockHandler);
        
        final MessageProducer producer = _hqMixIn.getJMSSession().createProducer(HornetQMixIn.getJMSQueue(INPUT_QUEUE));
        TextMessage msg = _hqMixIn.getJMSSession().createTextMessage();
        msg.setText("payload");
        producer.send(msg);
        
        // wait for message to be picked up the HornetQ queue.
        mockHandler.waitForOKMessage();
        
        Assert.assertEquals(4, mockHandler.getFaults().size());
        Exchange faultExchange = mockHandler.getFaults().poll();
        final Object faultContent = faultExchange.getMessage().getContent();
        Assert.assertTrue(faultContent instanceof HandlerException);
        Assert.assertEquals("faultmessage", HandlerException.class.cast(faultContent).getMessage());
        Assert.assertEquals(1, mockHandler.getMessages().size());
        Exchange exchange = mockHandler.getMessages().poll();
        final Object content = exchange.getMessage().getContent();
        Assert.assertTrue(content instanceof String);
        Assert.assertEquals("payloadtest", content);
    }

    @Test
    public void testInflowJMS_fault_commit() throws Exception {
        _testKit.removeService("JCAJMSService");
        final MockHandler mockHandler = new MockHandler() {
            private int count = 0;
            public void handleMessage(final Exchange exchange) throws HandlerException {
                if (count < 4) {
                    count++;
                    handleFault(exchange);
                    exchange.sendFault(exchange.createMessage().setContent(new JCAJMSFault("faultmessage")));
                } else {
                    super.handleMessage(exchange);
                }
            }
        };
        _testKit.registerInOnlyService("JCAJMSService", mockHandler);
        
        final MessageProducer producer = _hqMixIn.getJMSSession().createProducer(HornetQMixIn.getJMSQueue(INPUT_QUEUE));
        TextMessage msg = _hqMixIn.getJMSSession().createTextMessage();
        msg.setText("payload");
        producer.send(msg);
        
        // wait for message to be picked up the HornetQ queue.
        mockHandler.waitForFaultMessage();
        
        Assert.assertEquals(1, mockHandler.getFaults().size());
        Exchange exchange = mockHandler.getFaults().poll();
        final Object content = exchange.getMessage().getContent();
        Assert.assertTrue(content instanceof JCAJMSFault);
        Assert.assertEquals("faultmessage", JCAJMSFault.class.cast(content).getMessage());
    }
}

