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
package org.switchyard.quickstarts.jca.outbound;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionMetaData;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.ResourceAdapterArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.component.test.mixins.activemq.ActiveMQMixIn;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.component.test.mixins.jca.JCAMixIn;
import org.switchyard.component.test.mixins.jca.ResourceAdapterConfig;
import org.switchyard.test.BeforeDeploy;
import org.switchyard.test.ShrinkwrapUtil;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;

/**
 * Functional test for a SwitchYard Service which has reference bindings to a HornetQ
 * queue via JCA outbound.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(
        config = SwitchYardTestCaseConfig.SWITCHYARD_XML,
        mixins = {ActiveMQMixIn.class, JCAMixIn.class, CDIMixIn.class}
)
public class JCAOutboundBindingTest {
    private static final String ORDER_QUEUE = "OrderQueue";
    private static final String SHIPPING_QUEUE = "ShippingQueue";
    private static final String FILLING_STOCK_QUEUE = "FillingStockQueue";
    
    private ActiveMQMixIn mixIn;
    private JCAMixIn _jcaMixIn;
    
    @BeforeDeploy
    public void before() {
        ResourceAdapterConfig ra = new ResourceAdapterConfig(ResourceAdapterConfig.ResourceAdapterType.OTHER);
        ra.setName("activemq-ra.rar");

        String version = ActiveMQConnectionMetaData.PROVIDER_VERSION;

        // place activemq-ra.rar in test-classes directory to don't ship this dependency as binary
        File output = new File("target/test-classes", "activemq-ra.rar");
        ResourceAdapterArchive rar = ShrinkwrapUtil.getArchive("org.apache.activemq", "activemq-rar", version, ResourceAdapterArchive.class, "rar");
        // copy ironjacamar configuration to resource adapter
        rar.addAsManifestResource("ironjacamar.xml");
        rar.as(ZipExporter.class).exportTo(output, true);

        _jcaMixIn.deployResourceAdapters(ra);
    }
    /**
     * Triggers the 'OrderService' by sending a HornetQ Message to the 'OrderQueue'
     */
    @Test
    public void testOrderService() throws Exception {
        String[] orders = {"BREAD", "PIZZA", "JAM", "POTATO", "MILK", "JAM"};
        
        Session session = mixIn.getSession();
        final MessageProducer producer = session.createProducer(session.createQueue(ORDER_QUEUE));
        for (String order : orders ) {
            final TextMessage message = session.createTextMessage();
            message.setText(order);
            producer.send(message);
        }
        session.close();
        
        Thread.sleep(1000);
        
        session = mixIn.getSession();
        MessageConsumer consumer = session.createConsumer(session.createQueue(SHIPPING_QUEUE));
        List<String> expectedShippingOrders = new ArrayList<String>(Arrays.asList("BREAD", "JAM", "MILK", "JAM"));
        Message msg = null;
        while ((msg = consumer.receive(1000)) != null) {
            if (msg instanceof TextMessage) {
                Assert.assertTrue(expectedShippingOrders.remove(((TextMessage) msg).getText()));
            }
        }
        Assert.assertEquals(0, expectedShippingOrders.size());
        
        consumer = session.createConsumer(session.createQueue(FILLING_STOCK_QUEUE));
        List<String> expectedFillingStockOrders = new ArrayList<String>(Arrays.asList("PIZZA", "POTATO"));
        while ((msg = consumer.receive(1000)) != null) {
            if (msg instanceof TextMessage) {
                Assert.assertTrue(expectedFillingStockOrders.remove(((TextMessage) msg).getText()));
            }
        }
        Assert.assertEquals(0, expectedFillingStockOrders.size());
    }
}
