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
package org.switchyard.test.quickstarts.demo;

import java.io.IOException;

import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.test.ArquillianUtil;
import org.switchyard.component.test.mixins.hornetq.HornetQMixIn;
import org.switchyard.test.quickstarts.util.ResourceDeployer;

/**
 * The policy-transaction demo quickstart test.
 */
@RunWith(Arquillian.class)
public class PolicyTransactionDemoQuickstartTest {

    private static final String QUEUE_IN = "policyQSTransacted";
    private static final String QUEUE_IN_NOTX = "policyQSNonTransacted";
    private static final String QUEUE_OUT_A = "queueA";
    private static final String QUEUE_OUT_B = "queueB";
    private static final String QUEUE_OUT_C = "queueC";
    
    private HornetQMixIn _hqMixIn;
    
    @Deployment(testable = false)
    public static JavaArchive createDeployment() throws IOException {
        System.out.println(ResourceDeployer.addQueue(QUEUE_IN));
        System.out.println(ResourceDeployer.addQueue(QUEUE_IN_NOTX));
        System.out.println(ResourceDeployer.addQueue(QUEUE_OUT_A));
        System.out.println(ResourceDeployer.addQueue(QUEUE_OUT_B));
        System.out.println(ResourceDeployer.addQueue(QUEUE_OUT_C));
        System.out.println(ResourceDeployer.addPooledConnectionFactory("hornetq-ra-in", true, "java:/JmsXAIn"));
        System.out.println(ResourceDeployer.addPooledConnectionFactory("hornetq-ra-out-a", true, "java:/JmsXAOutA"));
        System.out.println(ResourceDeployer.addPooledConnectionFactory("hornetq-ra-out-b", true, "java:/JmsXAOutB"));
        System.out.println(ResourceDeployer.addPooledConnectionFactory("hornetq-ra-out-c", true, "java:/JmsXAOutC"));
        return ArquillianUtil.createJarDemoDeployment("switchyard-demo-policy-transaction");
    }

    @Before
    public void before() throws Exception {
        _hqMixIn = new HornetQMixIn(false)
                            .setUser(ResourceDeployer.USER)
                            .setPassword(ResourceDeployer.PASSWD);
        _hqMixIn.initialize();
        cleanupQueues(_hqMixIn);
    }
    
    @After
    public void after() throws Exception {
        cleanupQueues(_hqMixIn);
        _hqMixIn.uninitialize();
    }
    
    public void cleanupQueues(HornetQMixIn mixin) {
        Session session = mixin.createJMSSession();
        try {
            MessageConsumer consumer = session.createConsumer(HornetQMixIn.getJMSQueue(QUEUE_IN));
            while(consumer.receive(1000) != null) {}
            consumer = session.createConsumer(HornetQMixIn.getJMSQueue(QUEUE_OUT_A));
            while(consumer.receive(1000) != null) {}
            consumer = session.createConsumer(HornetQMixIn.getJMSQueue(QUEUE_OUT_B));
            while(consumer.receive(1000) != null) {}
            consumer = session.createConsumer(HornetQMixIn.getJMSQueue(QUEUE_OUT_C));
            while(consumer.receive(1000) != null) {}
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                session.close();
            } catch (Exception e) { /* ignore */ }
        }
    }

    @AfterClass
    public static void afterClass() throws Exception {
        System.out.println(ResourceDeployer.removeQueue(QUEUE_IN));
        System.out.println(ResourceDeployer.removeQueue(QUEUE_IN_NOTX));
        System.out.println(ResourceDeployer.removeQueue(QUEUE_OUT_A));
        System.out.println(ResourceDeployer.removeQueue(QUEUE_OUT_B));
        System.out.println(ResourceDeployer.removeQueue(QUEUE_OUT_C));
        System.out.println(ResourceDeployer.removePooledConnectionFactory("hornetq-ra-in"));
        System.out.println(ResourceDeployer.removePooledConnectionFactory("hornetq-ra-out-a"));
        System.out.println(ResourceDeployer.removePooledConnectionFactory("hornetq-ra-out-b"));
        System.out.println(ResourceDeployer.removePooledConnectionFactory("hornetq-ra-out-c"));
    }
    
    @Test
    public void testRollbackA() throws Exception {
        String command = "rollback.A";
        
        Session session = _hqMixIn.createJMSSession();
        MessageProducer producer = session.createProducer(HornetQMixIn.getJMSQueue(QUEUE_IN));
        Message message = _hqMixIn.createJMSMessage(command);
        producer.send(message);
        session.close();

        session = _hqMixIn.createJMSSession();
        MessageConsumer consumer = session.createConsumer(HornetQMixIn.getJMSQueue(QUEUE_OUT_A));
        TextMessage msg = TextMessage.class.cast(consumer.receive(30000));
        Assert.assertEquals(command, msg.getText());
        Assert.assertNull(consumer.receive(1000));
        
        consumer = session.createConsumer(HornetQMixIn.getJMSQueue(QUEUE_OUT_B));
        msg = TextMessage.class.cast(consumer.receive(10000));
        Assert.assertEquals(command, msg.getText());
        msg = TextMessage.class.cast(consumer.receive(10000));
        Assert.assertEquals(command, msg.getText());
        msg = TextMessage.class.cast(consumer.receive(10000));
        Assert.assertEquals(command, msg.getText());
        msg = TextMessage.class.cast(consumer.receive(10000));
        Assert.assertEquals(command, msg.getText());
        Assert.assertNull(consumer.receive(1000));
        
        consumer = session.createConsumer(HornetQMixIn.getJMSQueue(QUEUE_OUT_C));
        msg = TextMessage.class.cast(consumer.receive(10000));
        Assert.assertEquals(command, msg.getText());
        msg = TextMessage.class.cast(consumer.receive(10000));
        Assert.assertEquals(command, msg.getText());
        msg = TextMessage.class.cast(consumer.receive(10000));
        Assert.assertEquals(command, msg.getText());
        msg = TextMessage.class.cast(consumer.receive(10000));
        Assert.assertEquals(command, msg.getText());
        Assert.assertNull(consumer.receive(1000));
        session.close();
    }

    @Test
    public void testRollbackB() throws Exception {
        String command = "rollback.B";
        
        Session session = _hqMixIn.createJMSSession();
        MessageProducer producer = session.createProducer(HornetQMixIn.getJMSQueue(QUEUE_IN));
        Message message = _hqMixIn.createJMSMessage(command);
        producer.send(message);
        session.close();

        session = _hqMixIn.createJMSSession();
        MessageConsumer consumer = session.createConsumer(HornetQMixIn.getJMSQueue(QUEUE_OUT_A));
        TextMessage msg = TextMessage.class.cast(consumer.receive(30000));
        Assert.assertEquals(command, msg.getText());
        Assert.assertNull(consumer.receive(1000));
        
        consumer = session.createConsumer(HornetQMixIn.getJMSQueue(QUEUE_OUT_B));
        msg = TextMessage.class.cast(consumer.receive(10000));
        Assert.assertEquals(command, msg.getText());
        Assert.assertNull(consumer.receive(1000));
        
        consumer = session.createConsumer(HornetQMixIn.getJMSQueue(QUEUE_OUT_C));
        msg = TextMessage.class.cast(consumer.receive(10000));
        Assert.assertEquals(command, msg.getText());
        Assert.assertNull(consumer.receive(1000));
        session.close();
    }
    
    @Test
    public void testNonTransacted() throws Exception {
        String command = "rollback.A";
        
        Session session = _hqMixIn.createJMSSession();
        MessageProducer producer = session.createProducer(HornetQMixIn.getJMSQueue(QUEUE_IN_NOTX));
        Message message = _hqMixIn.createJMSMessage(command);
        producer.send(message);
        session.close();

        session = _hqMixIn.createJMSSession();
        MessageConsumer consumer = session.createConsumer(HornetQMixIn.getJMSQueue(QUEUE_OUT_A));
        Assert.assertNull(consumer.receive(1000));
        consumer = session.createConsumer(HornetQMixIn.getJMSQueue(QUEUE_OUT_B));
        Assert.assertNull(consumer.receive(1000));
        consumer = session.createConsumer(HornetQMixIn.getJMSQueue(QUEUE_OUT_C));
        Assert.assertNull(consumer.receive(1000));
        session.close();
    }
}
