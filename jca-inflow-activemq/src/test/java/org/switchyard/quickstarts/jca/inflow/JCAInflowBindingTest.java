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
package org.switchyard.quickstarts.jca.inflow;

import java.io.File;
import java.io.IOException;

import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import junit.framework.Assert;

import org.apache.activemq.ActiveMQConnectionMetaData;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.ResourceAdapterArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.Exchange;
import org.switchyard.component.test.mixins.activemq.ActiveMQMixIn;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.component.test.mixins.jca.JCAMixIn;
import org.switchyard.component.test.mixins.jca.ResourceAdapterConfig;
import org.switchyard.component.test.mixins.naming.NamingMixIn;
import org.switchyard.component.test.mixins.transaction.TransactionMixIn;
import org.switchyard.test.BeforeDeploy;
import org.switchyard.test.MockHandler;
import org.switchyard.test.ShrinkwrapUtil;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.SwitchYardTestKit;

/**
 * Functional test for a SwitchYard Service which has a service binding to a HornetQ
 * queue via JCA inflow.
 * 
 * @author Daniel Bevenius
 */
@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(
        config = SwitchYardTestCaseConfig.SWITCHYARD_XML,
        mixins = {NamingMixIn.class, TransactionMixIn.class, ActiveMQMixIn.class, JCAMixIn.class, CDIMixIn.class}
)
public class JCAInflowBindingTest {
    
    private SwitchYardTestKit _testKit;
    private ActiveMQMixIn _amqMixIn;
    private JCAMixIn _jcaMixIn;
    
    @BeforeDeploy
    public void before() throws IOException {
        ResourceAdapterConfig ra = new ResourceAdapterConfig(ResourceAdapterConfig.ResourceAdapterType.OTHER);
        ra.setName("activemq-ra.rar");
        ra.addConnectionDefinition("java:/JmsXA", "org.apache.activemq.ra.ActiveMQManagedConnectionFactory");

        String version = ActiveMQConnectionMetaData.PROVIDER_VERSION;

        // place activemq-ra.rar in test-classes directory to don't ship this dependency as binary
        File output = new File("target/test-classes", "activemq-ra.rar");
        ResourceAdapterArchive rar = ShrinkwrapUtil.getArchive("org.apache.activemq", "activemq-rar", version, ResourceAdapterArchive.class, "rar");
        rar.as(ZipExporter.class).exportTo(output, true);

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

        Session session = _amqMixIn.getSession();
        Queue queue = session.createQueue("JCAInflowGreetingServiceQueue");
        final MessageProducer producer = session.createProducer(queue);
        final TextMessage message = session.createTextMessage();
        message.setText(PAYLOAD);
        producer.send(message);

        greetingService.waitForOKMessage();

        final Exchange recievedExchange = greetingService.getMessages().iterator().next();
        Assert.assertEquals(PAYLOAD, recievedExchange.getMessage().getContent(String.class));
        session.close();
    }
    
    private static final String PAYLOAD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<qs:person xmlns:qs=\"urn:switchyard-quickstart:jca-inflow-activemq:0.1.0\">\n"
            + "    <qs:name>dummy</qs:name>\n"
            + "    <qs:language>english</qs:language>\n"
            + "</qs:person>\n";
    
}
