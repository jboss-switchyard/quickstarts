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
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.dmr.ModelNode;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.test.ArquillianUtil;
import org.switchyard.test.SwitchYardTestKit;
import org.switchyard.component.test.mixins.http.HTTPMixIn;

@RunWith(Arquillian.class)
public class RestBindingQuickstartTest {

    @Deployment(testable = false)
    public static JavaArchive createDeployment() {
        return ArquillianUtil.createJarQSDeployment("switchyard-quickstart-rest-binding");
    }

    @Test
    public void testOrders() throws Exception {
        HTTPMixIn httpMixIn = new HTTPMixIn();

        httpMixIn.initialize();
        try {
            // Create our inventory
            String response = httpMixIn.sendString(BASE_URL + "/inventory/create", "", HTTPMixIn.HTTP_OPTIONS);
            Assert.assertEquals(SUCCESS, response);

            // Create an order
            response = httpMixIn.sendString(BASE_URL + "/order", "", HTTPMixIn.HTTP_POST);
            SwitchYardTestKit.compareXMLToString(response, ORDER);

            // Add a new item or update order
            response = httpMixIn.sendString(BASE_URL + "/order/item", ORDER1, HTTPMixIn.HTTP_PUT);
            Assert.assertEquals(SUCCESS, response);

            // Add some more items or update order
            response = httpMixIn.sendString(BASE_URL + "/order/item", ORDER2, HTTPMixIn.HTTP_PUT);
            Assert.assertEquals(SUCCESS, response);

            // Look at our order
            response = httpMixIn.sendString(BASE_URL + "/order/1", "", HTTPMixIn.HTTP_GET);
            SwitchYardTestKit.compareXMLToString(response, ORDER3);

            // Delete the first item
            response = httpMixIn.sendString(BASE_URL + "/order/1:1", "", HTTPMixIn.HTTP_DELETE);
            Assert.assertEquals(SUCCESS, response);

            // Look at our order
            response = httpMixIn.sendString(BASE_URL + "/order/1", "", HTTPMixIn.HTTP_GET);
            SwitchYardTestKit.compareXMLToString(response, ORDER4);

            // Update item descriptions in our inventory
            response = httpMixIn.sendString(BASE_URL + "/inventory/update", "", HTTPMixIn.HTTP_OPTIONS);
            Assert.assertEquals(SUCCESS, response);

            // Look at our order
            response = httpMixIn.sendString(BASE_URL + "/order/1", "", HTTPMixIn.HTTP_GET);
            SwitchYardTestKit.compareXMLToString(response, ORDER5);

            // Get item
            int status = httpMixIn.sendStringAndGetStatus(BASE_URL + "/warehouse/26", "", HTTPMixIn.HTTP_GET);
            Assert.assertEquals(404, status);

            // Destroy our inventory
            response = httpMixIn.sendString(BASE_URL + "/inventory/remove", "", HTTPMixIn.HTTP_OPTIONS);
            Assert.assertEquals(SUCCESS, response);

        } finally {
            httpMixIn.uninitialize();
        }
    }

    @Test
    public void testRestartGateway(@ArquillianResource ManagementClient client) throws Exception {
        HTTPMixIn httpMixIn = new HTTPMixIn();

        httpMixIn.initialize();
        try {
            // Create our inventory
            String response = httpMixIn.sendString(BASE_URL + "/inventory/create", "", HTTPMixIn.HTTP_OPTIONS);
            Assert.assertEquals(SUCCESS, response);

            // Look at our order
            response = httpMixIn.sendString(BASE_URL + "/order/1", "", HTTPMixIn.HTTP_GET);
            SwitchYardTestKit.compareXMLToString(response, ORDER4);

            final String namespace = "urn:switchyard-quickstart:resteasy-binding:1.0";
            final ModelNode operation = new ModelNode();
            operation.get(ModelDescriptionConstants.OP_ADDR).add("subsystem", "switchyard");
            operation.get(ModelDescriptionConstants.NAME).set("_OrderService_rest_1");
            operation.get("service-name").set(new QName(namespace, "OrderService").toString());
            operation.get("application-name").set(new QName(namespace, "OrderService").toString());

            // stop the gateway
            operation.get(ModelDescriptionConstants.OP).set("stop-gateway");
            ModelNode result = client.getControllerClient().execute(operation);
            Assert.assertEquals("Failed to stop gateway: " + result.toString(), ModelDescriptionConstants.SUCCESS,
                    result.get(ModelDescriptionConstants.OUTCOME).asString());
            // Update item descriptions in our inventory, should fail
            Assert.assertEquals(404,
                    httpMixIn.sendStringAndGetStatus(BASE_URL + "/inventory/update", "", HTTPMixIn.HTTP_OPTIONS));

            // restart the gateway
            operation.get(ModelDescriptionConstants.OP).set("start-gateway");
            result = client.getControllerClient().execute(operation);
            Assert.assertEquals("Failed to restart gateway: " + result.toString(), ModelDescriptionConstants.SUCCESS,
                    result.get(ModelDescriptionConstants.OUTCOME).asString());

            // Look at our order. if this fails, the item descriptions were
            // updated or the gateway didn't restart.
            response = httpMixIn.sendString(BASE_URL + "/order/1", "", HTTPMixIn.HTTP_GET);
            SwitchYardTestKit.compareXMLToString(response, ORDER4);

            // Destroy our inventory
            response = httpMixIn.sendString(BASE_URL + "/inventory/remove", "", HTTPMixIn.HTTP_OPTIONS);
            Assert.assertEquals(SUCCESS, response);
        } finally {
            httpMixIn.uninitialize();
        }
    }

    private static final String BASE_URL = "http://localhost:8080/rest-binding";
    private static final String SUCCESS = "SUCCESS";
    private static final String ORDER = "<order>"
                                       + "    <orderId>1</orderId>"
                                       + "</order>";
    private static final String ORDER1 = "<order>"
                                       + "    <orderId>1</orderId>"
                                       + "    <orderItem>"
                                       + "        <item>"
                                       + "            <itemId>1</itemId>"
                                       + "         </item>"
                                       + "         <quantity>10</quantity>"
                                       + "     </orderItem>"
                                       + "</order>";
    private static final String ORDER2 = "<order>"
                                       + "    <orderId>1</orderId>"
                                       + "    <orderItem>"
                                       + "        <item>"
                                       + "            <itemId>3</itemId>"
                                       + "        </item>"
                                       + "        <quantity>5</quantity>"
                                       + "    </orderItem>"
                                       + "    <orderItem>"
                                       + "        <item>"
                                       + "            <itemId>4</itemId>"
                                       + "        </item>"
                                       + "        <quantity>3</quantity>"
                                       + "    </orderItem>"
                                       + "</order>";
    private static final String ORDER3 = "<order>"
                                       + "    <orderId>1</orderId>"
                                       + "    <orderItem>"
                                       + "        <item>"
                                       + "            <itemId>1</itemId>"
                                       + "            <name>Hydrogen Atom - No, we are not kidding!</name>"
                                       + "        </item>"
                                       + "        <quantity>10</quantity>"
                                       + "    </orderItem>"
                                       + "    <orderItem>"
                                       + "        <item>"
                                       + "            <itemId>3</itemId>"
                                       + "            <name>Einstein's Bust - Talks about your future :)</name>"
                                       + "        </item>"
                                       + "        <quantity>5</quantity>"
                                       + "    </orderItem>"
                                       + "    <orderItem>"
                                       + "        <item>"
                                       + "            <itemId>4</itemId>"
                                       + "            <name>Time Machine</name>"
                                       + "        </item>"
                                       + "        <quantity>3</quantity>"
                                       + "    </orderItem>"
                                       + "</order>";
    private static final String ORDER4 = "<order>"
                                       + "    <orderId>1</orderId>"
                                       + "    <orderItem>"
                                       + "        <item>"
                                       + "            <itemId>3</itemId>"
                                       + "            <name>Einstein's Bust - Talks about your future :)</name>"
                                       + "        </item>"
                                       + "        <quantity>5</quantity>"
                                       + "    </orderItem>"
                                       + "    <orderItem>"
                                       + "        <item>"
                                       + "            <itemId>4</itemId>"
                                       + "            <name>Time Machine</name>"
                                       + "        </item>"
                                       + "        <quantity>3</quantity>"
                                       + "    </orderItem>"
                                       + "</order>";
    private static final String ORDER5 = "<order>"
                                       + "    <orderId>1</orderId>"
                                       + "    <orderItem>"
                                       + "        <item>"
                                       + "            <itemId>3</itemId>"
                                       + "            <name>Theory of relativity</name>"
                                       + "        </item>"
                                       + "        <quantity>5</quantity>"
                                       + "    </orderItem>"
                                       + "    <orderItem>"
                                       + "        <item>"
                                       + "            <itemId>4</itemId>"
                                       + "            <name>Coffee Maker</name>"
                                       + "        </item>"
                                       + "        <quantity>3</quantity>"
                                       + "    </orderItem>"
                                       + "</order>";
}
