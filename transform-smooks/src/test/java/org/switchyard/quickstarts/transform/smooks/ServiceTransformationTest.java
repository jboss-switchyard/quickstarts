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

package org.switchyard.quickstarts.transform.smooks;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.junit.Test;
import org.switchyard.test.SwitchYardDeploymentConfig;
import org.switchyard.test.SwitchYardTestCase;
import org.switchyard.test.TestMixIns;
import org.switchyard.test.mixins.CDIMixIn;

/**
 * Test Smooks transformations around a service.
 */
@TestMixIns(CDIMixIn.class)
@SwitchYardDeploymentConfig(SwitchYardDeploymentConfig.SWITCHYARD_XML)
public class ServiceTransformationTest extends SwitchYardTestCase {

    // Message types being transformed
    public static final QName FROM_TYPE =
        new QName("urn:switchyard-quickstarts:transform-smooks:1.0", "submitOrder");
    public static final QName TO_TYPE =
        new QName("urn:switchyard-quickstarts:transform-smooks:1.0", "submitOrderResponse");

    // Paths to XML test files
    final String ORDER_XML = "/xml/order.xml";
    final String ORDER_ACK_XML = "/xml/orderAck.xml";

    @Test
    public void testTransformXMLtoJava() throws Exception {
        OrderAck orderAck = newInvoker("OrderService")
            .operation("submitOrder")
            .inputType(FROM_TYPE)
            .sendInOut(readResourceString(ORDER_XML))
            .getContent(OrderAck.class);

        Assert.assertTrue(orderAck.isAccepted());
    }

    @Test
    public void testTransformJavaToXML() throws Exception {
        Order testOrder = new Order()
            .setOrderId("PO-19838-XYZ")
            .setItemId("BUTTER")
            .setQuantity(100);

        String result = newInvoker("OrderService")
            .operation("submitOrder")
            .expectedOutputType(TO_TYPE)
            .sendInOut(testOrder)
            .getContent(String.class);

        compareXMLToResource(result, ORDER_ACK_XML);
    }
}
