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

package org.switchyard.quickstarts.transform.json;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.test.Invoker;
import org.switchyard.test.ServiceOperation;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.SwitchYardTestKit;
import org.switchyard.test.mixins.CDIMixIn;
import org.switchyard.test.mixins.HornetQMixIn;

/**
 * Test JSON transformations around a service.
 */
@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(config = SwitchYardTestCaseConfig.SWITCHYARD_XML, mixins = {CDIMixIn.class, HornetQMixIn.class})
public class JsonTransformationTest {

    @ServiceOperation("OrderService.submitOrder")
    private Invoker submitOrder;

    private SwitchYardTestKit _testKit;

    // Message types being transformed
    public static final QName FROM_TYPE =
        new QName("urn:switchyard-quickstart:transform-json:1.0", "order");
    public static final QName TO_TYPE =
        new QName("urn:switchyard-quickstart:transform-json:1.0", "orderResponse");

    // Paths to JSON test files
    final String ORDER_JSON = "/json/order.json";
    final String ORDER_ACK_JSON = "/json/orderAck.json";

    @Test
    public void testTransformJSONtoJava() throws Exception {
        OrderAck orderAck = submitOrder
            .inputType(FROM_TYPE)
            .sendInOut(_testKit.readResourceString(ORDER_JSON))
            .getContent(OrderAck.class);

        Assert.assertTrue(orderAck.isAccepted());
    }

    @Test
    public void testTransformJavaToXML() throws Exception {
        Order testOrder = new Order()
            .setOrderId("PO-19838-XYZ")
            .setItemId("BUTTER")
            .setQuantity(100);

        String result = submitOrder
            .expectedOutputType(TO_TYPE)
            .sendInOut(testOrder)
            .getContent(String.class);

        // parse the reference and returned JSON strings and compare for equality
        ObjectMapper mapper = new ObjectMapper();
        OrderAck expected = mapper.readValue(
                _testKit.readResourceString(ORDER_ACK_JSON), OrderAck.class);
        OrderAck returned = mapper.readValue(result, OrderAck.class);
        
        Assert.assertEquals(expected, returned);
    }
}
