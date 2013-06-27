/*
 * 2012 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.switchyard.quickstarts.demo.policy.transaction;

import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.component.test.mixins.hornetq.HornetQMixIn;
import org.switchyard.component.test.mixins.jca.JCAMixIn;
import org.switchyard.component.test.mixins.jca.ResourceAdapterConfig;
import org.switchyard.test.BeforeDeploy;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;

/**
 * Functional test for a policy-transaction demo quickstart.
 */
@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(
        config = SwitchYardTestCaseConfig.SWITCHYARD_XML,
        mixins = {HornetQMixIn.class, JCAMixIn.class, CDIMixIn.class}
)
public class JmsBindingTest {
    private static final String QUEUE_IN = "policyQSTransacted";
    private static final String QUEUE_IN_NOTX = "policyQSNonTransacted";
    private static final String QUEUE_OUT_A = "queueA";
    private static final String QUEUE_OUT_B = "queueB";
    private static final String QUEUE_OUT_C = "queueC";
    
    private HornetQMixIn _hqMixIn;
    private JCAMixIn _jcaMixIn;
    
    @BeforeDeploy
    public void before() {
        ResourceAdapterConfig ra = new ResourceAdapterConfig(ResourceAdapterConfig.ResourceAdapterType.HORNETQ);
        _jcaMixIn.deployResourceAdapters(ra);
    }
    
    @After
    public void after() throws Exception {
        Session session = _hqMixIn.createJMSSession();
        MessageConsumer consumer = session.createConsumer(HornetQMixIn.getJMSQueue(QUEUE_OUT_A));
        while (consumer.receive(1000) != null);
        consumer = session.createConsumer(HornetQMixIn.getJMSQueue(QUEUE_OUT_B));
        while (consumer.receive(1000) != null);
        consumer = session.createConsumer(HornetQMixIn.getJMSQueue(QUEUE_OUT_C));
        while (consumer.receive(1000) != null);
        session.close();
    }
    
    /**
     * Triggers the 'WorkService' by sending a HornetQ Message to the 'policyQSTransacted' queue.
     */
    @Test
    public void testRollbackA() throws Exception {
        String command = "rollback.A";
        
        Session session = _hqMixIn.getJMSSession();
        final MessageProducer producer = session.createProducer(HornetQMixIn.getJMSQueue(QUEUE_IN));
        final TextMessage message = _hqMixIn.getJMSSession().createTextMessage();
        message.setText(command);
        producer.send(message);
        session.close();
        
        session = _hqMixIn.createJMSSession();
        MessageConsumer consumer = session.createConsumer(HornetQMixIn.getJMSQueue(QUEUE_OUT_A));
        ObjectMessage msg = ObjectMessage.class.cast(consumer.receive(30000));
        Assert.assertEquals(command, msg.getObject());
        Assert.assertNull(consumer.receive(1000));

        consumer = session.createConsumer(HornetQMixIn.getJMSQueue(QUEUE_OUT_B));
        msg = ObjectMessage.class.cast(consumer.receive(1000));
        Assert.assertEquals(command, msg.getObject());
        msg = ObjectMessage.class.cast(consumer.receive(1000));
        Assert.assertEquals(command, msg.getObject());
        msg = ObjectMessage.class.cast(consumer.receive(1000));
        Assert.assertEquals(command, msg.getObject());
        msg = ObjectMessage.class.cast(consumer.receive(1000));
        Assert.assertEquals(command, msg.getObject());
        Assert.assertNull(consumer.receive(1000));
        
        consumer = session.createConsumer(HornetQMixIn.getJMSQueue(QUEUE_OUT_C));
        msg = ObjectMessage.class.cast(consumer.receive(1000));
        Assert.assertEquals(command, msg.getObject());
        msg = ObjectMessage.class.cast(consumer.receive(1000));
        Assert.assertEquals(command, msg.getObject());
        msg = ObjectMessage.class.cast(consumer.receive(1000));
        Assert.assertEquals(command, msg.getObject());
        msg = ObjectMessage.class.cast(consumer.receive(1000));
        Assert.assertEquals(command, msg.getObject());
        Assert.assertNull(consumer.receive(1000));
        session.close();
    }
    
    @Test
    public void testRollbackB() throws Exception {
        String command = "rollback.B";
        
        Session session = _hqMixIn.getJMSSession();
        final MessageProducer producer = session.createProducer(HornetQMixIn.getJMSQueue(QUEUE_IN));
        final TextMessage message = _hqMixIn.getJMSSession().createTextMessage();
        message.setText(command);
        producer.send(message);
        session.close();
        
        session = _hqMixIn.createJMSSession();
        MessageConsumer consumer = session.createConsumer(HornetQMixIn.getJMSQueue(QUEUE_OUT_A));
        ObjectMessage msg = ObjectMessage.class.cast(consumer.receive(1000));
        Assert.assertEquals(command, msg.getObject());
        Assert.assertNull(consumer.receive(1000));

        consumer = session.createConsumer(HornetQMixIn.getJMSQueue(QUEUE_OUT_B));
        msg = ObjectMessage.class.cast(consumer.receive(1000));
        Assert.assertEquals(command, msg.getObject());
        Assert.assertNull(consumer.receive(1000));
        
        consumer = session.createConsumer(HornetQMixIn.getJMSQueue(QUEUE_OUT_C));
        msg = ObjectMessage.class.cast(consumer.receive(1000));
        Assert.assertEquals(command, msg.getObject());
        Assert.assertNull(consumer.receive(1000));
        session.close();
    }
    
    @Test
    public void testNonTransacted() throws Exception {
        String command = "rollback.A";
        
        Session session = _hqMixIn.getJMSSession();
        final MessageProducer producer = session.createProducer(HornetQMixIn.getJMSQueue(QUEUE_IN_NOTX));
        final TextMessage message = _hqMixIn.getJMSSession().createTextMessage();
        message.setText(command);
        producer.send(message);
        session.close();
        
        session = _hqMixIn.createJMSSession();
        MessageConsumer consumer = session.createConsumer(HornetQMixIn.getJMSQueue(QUEUE_OUT_A));
        Assert.assertNull(consumer.receive(1000));
        consumer = session.createConsumer(HornetQMixIn.getJMSQueue(QUEUE_OUT_B));
        Assert.assertNull(consumer.receive(1000));
        consumer = session.createConsumer(HornetQMixIn.getJMSQueue(QUEUE_OUT_C));
        Assert.assertNull(consumer.receive(1000));
    }
}
