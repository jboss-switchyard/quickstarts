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

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
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
