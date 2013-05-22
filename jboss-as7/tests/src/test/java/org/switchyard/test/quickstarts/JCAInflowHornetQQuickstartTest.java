/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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
package org.switchyard.test.quickstarts;

import java.io.IOException;

import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.dmr.ModelNode;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.test.ArquillianUtil;
import org.switchyard.component.test.mixins.hornetq.HornetQMixIn;
import org.switchyard.test.quickstarts.util.ResourceDeployer;

@RunWith(Arquillian.class)
public class JCAInflowHornetQQuickstartTest {

    private static final String QUEUE = "JCAInflowGreetingServiceQueue";
    
    @Deployment(testable = false)
    public static JavaArchive createDeployment() throws IOException {
        ResourceDeployer.addQueue(QUEUE);
        ResourceDeployer.addPropertiesUser();
        return ArquillianUtil.createJarQSDeployment("switchyard-quickstart-jca-inflow-hornetq");
    }

    @Test
    public void testDeployment() throws Exception {
        HornetQMixIn hqMixIn = new HornetQMixIn(false)
                                    .setUser(ResourceDeployer.USER)
                                    .setPassword(ResourceDeployer.PASSWD);
        hqMixIn.initialize();
        
        try {
            Session session = hqMixIn.getJMSSession();
            MessageProducer producer = session.createProducer(HornetQMixIn.getJMSQueue(QUEUE));
            TextMessage message = session.createTextMessage();
            message.setText(PAYLOAD);
            producer.send(message);
            Thread.sleep(1000);
        } finally {
            hqMixIn.uninitialize();
            ResourceDeployer.removeQueue(QUEUE);
        }
    }

    @Test
    public void testGatewayRestart(@ArquillianResource ManagementClient client) throws Exception {
        HornetQMixIn hqMixIn = new HornetQMixIn(false)
                                    .setUser(ResourceDeployer.USER)
                                    .setPassword(ResourceDeployer.PASSWD);
        hqMixIn.initialize();
        
        try {
            Session session = hqMixIn.getJMSSession();
            Queue queue = HornetQMixIn.getJMSQueue(QUEUE);
            MessageProducer producer = session.createProducer(queue);
            QueueBrowser browser = session.createBrowser(queue);
            TextMessage message = session.createTextMessage();
            message.setText(PAYLOAD);
            producer.send(message);
            Thread.sleep(1000);
            Assert.assertFalse(browser.getEnumeration().hasMoreElements());

            final String namespace = "urn:switchyard-quickstart:jca-inflow-hornetq:0.1.0";
            final String application = "jca-inflow-hornetq";
            final String service = "GreetingService";
            final String bindingType = "jca";
            final ModelNode operation = new ModelNode();
            operation.get(ModelDescriptionConstants.OP_ADDR).add("subsystem", "switchyard");
            operation.get(ModelDescriptionConstants.NAME).set("_" + service + "_" + bindingType + "_1");
            operation.get("service-name").set(new QName(namespace, service).toString());
            operation.get("application-name").set(new QName(namespace, application).toString());

            // stop the gateway
            operation.get(ModelDescriptionConstants.OP).set("stop-gateway");
            ModelNode result = client.getControllerClient().execute(operation);
            Assert.assertEquals("Failed to stop gateway: " + result.toString(), ModelDescriptionConstants.SUCCESS,
                    result.get(ModelDescriptionConstants.OUTCOME).asString());
            message = session.createTextMessage();
            message.setText(PAYLOAD);
            producer.send(message);
            Thread.sleep(1000);
            Assert.assertEquals(message.getJMSMessageID(),
                    ((Message) browser.getEnumeration().nextElement()).getJMSMessageID());

            // restart the gateway
            operation.get(ModelDescriptionConstants.OP).set("start-gateway");
            result = client.getControllerClient().execute(operation);
            Assert.assertEquals("Failed to restart gateway" + result.toString(), ModelDescriptionConstants.SUCCESS,
                    result.get(ModelDescriptionConstants.OUTCOME).asString());
            message = session.createTextMessage();
            message.setText(PAYLOAD);
            producer.send(message);
            Thread.sleep(1000);
            Assert.assertFalse(browser.getEnumeration().hasMoreElements());
        } finally {
            hqMixIn.uninitialize();
            ResourceDeployer.removeQueue(QUEUE);
        }
    }

    private static final String PAYLOAD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<qs:person xmlns:qs=\"urn:switchyard-quickstart:jca-inflow-hornetq:0.1.0\">\n"
            + "    <name>Fernando</name>\n"
            + "    <language>spanish</language>\n"
            + "</qs:person>\n";
    
}
