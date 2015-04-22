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

import javax.xml.namespace.QName;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.quickstarts.transform.json.Order;
import org.switchyard.quickstarts.transform.json.OrderAck;
import org.switchyard.remote.RemoteInvoker;
import org.switchyard.remote.RemoteMessage;
import org.switchyard.remote.http.HttpInvoker;
import org.switchyard.test.ArquillianUtil;

/**
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
@RunWith(Arquillian.class)
public class TransformJsonQuickstartTest {

    private static final QName SERVICE = new QName("urn:switchyard-quickstart:transform-json:0.1.0", "OrderService");

    @Deployment(testable = false)
    public static JavaArchive createDeployment() {
        return ArquillianUtil.createJarQSDeployment("switchyard-transform-json");
    }

    @Test
    public void testDeployment() throws Exception {
        String port = System.getProperty("org.switchyard.component.sca.client.port", "8080");
        RemoteInvoker invoker = new HttpInvoker("http://localhost:" + port + "/switchyard-remote");

        // Create request payload
        Order order = new Order();
        order.setItemId("Turkey");
        order.setOrderId("Xmas Dinner");
        order.setQuantity(1);

        // Create the request message
        RemoteMessage message = new RemoteMessage();
        message.setService(SERVICE).setOperation("submitOrder").setContent(order);

        // Invoke the service
        RemoteMessage reply = invoker.invoke(message);
        Assert.assertFalse(reply.isFault());
        OrderAck orderAck = (OrderAck) reply.getContent();
        Assert.assertTrue(orderAck.isAccepted());
    }

}
