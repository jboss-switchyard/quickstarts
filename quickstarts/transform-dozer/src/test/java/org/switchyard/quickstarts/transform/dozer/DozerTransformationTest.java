/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
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
package org.switchyard.quickstarts.transform.dozer;

import javax.xml.namespace.QName;

import org.junit.Assert;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.test.Invoker;
import org.switchyard.test.ServiceOperation;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.SwitchYardTestKit;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;

/**
 * Test JSON transformations around a service.
 */
@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(config = SwitchYardTestCaseConfig.SWITCHYARD_XML, mixins = CDIMixIn.class)
public class DozerTransformationTest {

    @ServiceOperation("OrderService.submitOrder")
    private Invoker submitOrder;

    private SwitchYardTestKit _testKit;

    // Message types being transformed
    public static final QName FROM_TYPE =
        new QName("urn:switchyard-quickstart:transform-dozer:1.0", "order");
    public static final QName TO_TYPE =
        new QName("urn:switchyard-quickstart:transform-dozer:1.0", "orderResponse");

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
    public void testTransformJavaToJson() throws Exception {
        Order testOrder = new Order()
            .setItem("BUTTER")
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
