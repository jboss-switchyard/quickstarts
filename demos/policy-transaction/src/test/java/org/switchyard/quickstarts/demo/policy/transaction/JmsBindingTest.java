/*
 * JBoss, Home of Professional Open Source Copyright 2009, Red Hat Middleware
 * LLC, and individual contributors by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
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
import org.switchyard.component.bean.config.model.BeanSwitchYardScanner;
import org.switchyard.test.BeforeDeploy;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.component.test.mixins.hornetq.HornetQMixIn;
import org.switchyard.component.test.mixins.jca.JCAMixIn;
import org.switchyard.component.test.mixins.jca.ResourceAdapterConfig;

/**
 * Functional test for a policy-transaction demo quickstart.
 */
@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(
        config = SwitchYardTestCaseConfig.SWITCHYARD_XML,
        mixins = {CDIMixIn.class, HornetQMixIn.class, JCAMixIn.class},
        scanners = BeanSwitchYardScanner.class)
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
