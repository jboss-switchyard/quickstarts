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
package org.switchyard.quickstarts.transform.smooks;

import javax.xml.namespace.QName;

import org.junit.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.test.BeforeDeploy;
import org.switchyard.test.Invoker;
import org.switchyard.test.ServiceOperation;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.SwitchYardTestKit;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;

/**
 * Test Smooks transformations around a service.
 */
@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(config = SwitchYardTestCaseConfig.SWITCHYARD_XML, mixins = CDIMixIn.class)
public class ServiceTransformationTest {

    @ServiceOperation("OrderService.submitOrder")
    private Invoker submitOrder;

    private SwitchYardTestKit _testKit;

    // Message types being transformed
    public static final QName FROM_TYPE =
        new QName("urn:switchyard-quickstart:transform-smooks:1.0", "order");
    public static final QName TO_TYPE =
        new QName("urn:switchyard-quickstart:transform-smooks:1.0", "orderAck");

    // Paths to XML test files
    final String ORDER_XML = "/xml/order.xml";
    final String ORDER_ACK_XML = "/xml/orderAck.xml";

    @BeforeDeploy
    public void setProperties() {
        System.setProperty("org.switchyard.component.soap.standalone.port", "18001");
    }

    @Test
    public void testTransformXMLtoJava() throws Exception {
        OrderAck orderAck = submitOrder
            .inputType(FROM_TYPE)
            .sendInOut(_testKit.readResourceString(ORDER_XML))
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

        _testKit.compareXMLToResource(result, ORDER_ACK_XML);
    }
}
