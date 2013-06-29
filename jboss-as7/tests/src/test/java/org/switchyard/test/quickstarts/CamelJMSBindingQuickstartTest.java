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
package org.switchyard.test.quickstarts;

import java.io.IOException;

import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
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
import org.switchyard.component.test.mixins.hornetq.HornetQMixIn;
import org.switchyard.test.ArquillianUtil;
import org.switchyard.test.quickstarts.util.ResourceDeployer;

@RunWith(Arquillian.class)
public class CamelJMSBindingQuickstartTest {

    private static final String QUEUE = "GreetingServiceQueue";

    @Deployment(testable = false)
    public static JavaArchive createDeployment() throws IOException {
        ResourceDeployer.addQueue(QUEUE);
        ResourceDeployer.addPropertiesUser();
        return ArquillianUtil.createJarQSDeployment("switchyard-quickstart-camel-jms-binding");
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
            Message message = hqMixIn.createJMSMessage("Tomo");
            producer.send(message);
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
            Message message = hqMixIn.createJMSMessage("Restarter");
            producer.send(message);
            Thread.sleep(1000);
            Assert.assertFalse(browser.getEnumeration().hasMoreElements());

            final String namespace = "urn:switchyard-quickstart:camel-jms-binding:0.1.0";
            final String application = "camel-jms-binding";
            final String service = "GreetingService";
            final String bindingType = "jms";
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
            message = hqMixIn.createJMSMessage("Stopped");
            producer.send(message);
            Thread.sleep(1000);
            Assert.assertEquals(message.getJMSMessageID(),
                    ((Message) browser.getEnumeration().nextElement()).getJMSMessageID());

            // restart the gateway
            operation.get(ModelDescriptionConstants.OP).set("start-gateway");
            result = client.getControllerClient().execute(operation);
            Assert.assertEquals("Failed to restart gateway" + result.toString(), ModelDescriptionConstants.SUCCESS,
                    result.get(ModelDescriptionConstants.OUTCOME).asString());
            message = hqMixIn.createJMSMessage("Restarted");
            producer.send(message);
            Thread.sleep(1000);
            Assert.assertFalse(browser.getEnumeration().hasMoreElements());
        } finally {
            hqMixIn.uninitialize();
            ResourceDeployer.removeQueue(QUEUE);
        }
    }

}
