/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
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

package org.switchyard.quickstarts.camel.rest.binding;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.component.bean.config.model.BeanSwitchYardScanner;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.SwitchYardTestKit;
import org.switchyard.test.mixins.CDIMixIn;
import org.switchyard.test.mixins.HTTPMixIn;

/**
 * Tests for Camel CXFRS binding.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
@SwitchYardTestCaseConfig(
        config = SwitchYardTestCaseConfig.SWITCHYARD_XML, 
        mixins = {CDIMixIn.class, HTTPMixIn.class},
        scanners = BeanSwitchYardScanner.class)
@RunWith(SwitchYardRunner.class)
public class CamelCxfRsBindingTest {

    private HTTPMixIn http;

    @Test
    public void orderServiceRESTEndpoint() throws Exception {
        // Create our inventory
        String response = http.sendString(BASE_URL + "/order/inventory/create", "", HTTPMixIn.HTTP_OPTIONS);
        Assert.assertEquals(SUCCESS, response);

        // Create an order
        response = http.sendString(BASE_URL + "/order", "", HTTPMixIn.HTTP_POST);
        SwitchYardTestKit.compareXMLToString(response, ORDER);

        // Add a new item or update order
        response = http.sendString(BASE_URL + "/order/item", ORDER1, HTTPMixIn.HTTP_PUT);
        Assert.assertEquals(SUCCESS, response);

        // Add some more items or update order
        response = http.sendString(BASE_URL + "/order/item", ORDER2, HTTPMixIn.HTTP_PUT);
        Assert.assertEquals(SUCCESS, response);

        // Look at our order
        response = http.sendString(BASE_URL + "/order/1", "", HTTPMixIn.HTTP_GET);
        SwitchYardTestKit.compareXMLToString(response, ORDER3);

        // Delete the first item
        response = http.sendString(BASE_URL + "/order/1:1", "", HTTPMixIn.HTTP_DELETE);
        Assert.assertEquals(SUCCESS, response);

        // Look at our order
        response = http.sendString(BASE_URL + "/order/1", "", HTTPMixIn.HTTP_GET);
        SwitchYardTestKit.compareXMLToString(response, ORDER4);

        // Update item descriptions in our inventory
        response = http.sendString(BASE_URL + "/order/inventory/update", "", HTTPMixIn.HTTP_OPTIONS);
        Assert.assertEquals(SUCCESS, response);

        // Look at our order
        response = http.sendString(BASE_URL + "/order/1", "", HTTPMixIn.HTTP_GET);
        SwitchYardTestKit.compareXMLToString(response, ORDER5);

        // Destroy our inventory
        response = http.sendString(BASE_URL + "/order/inventory/remove", "", HTTPMixIn.HTTP_OPTIONS);
        Assert.assertEquals(SUCCESS, response);
    }

    private static final String BASE_URL = "http://localhost:18001";
    private static final String SUCCESS = "Order service is DUMB!";
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
