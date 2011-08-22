/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010-2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License, v.2.1 along with this distribution; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.switchyard.quickstarts.bean.service;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.Assert;

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.test.Invoker;
import org.switchyard.test.ServiceOperation;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.mixins.CDIMixIn;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(mixins = CDIMixIn.class)
public class TypeTransformationTest {

    @ServiceOperation("OrderService.submitOrder")
    private Invoker submitOrder;

    final String ORDER_XML = "xml/order.xml";
    final String ORDER_ACK_XML = "xml/orderAck.xml";

    @Test
    public void testTransformXMLtoJava() throws Exception {

        OrderAck orderAck = submitOrder
            .inputType(QName.valueOf("{urn:switchyard-quickstart:bean-service:1.0}submitOrder"))
            .sendInOut(loadXML(ORDER_XML).getDocumentElement())
            .getContent(OrderAck.class);

        Assert.assertTrue(orderAck.isAccepted());

    }

    @Test
    public void testTransformJavaToXML() throws Exception {
        Order testOrder = new Order()
            .setOrderId("PO-19838-XYZ")
            .setItemId("BUTTER")
            .setQuantity(100);

        Element result = submitOrder
            .expectedOutputType(QName.valueOf("{urn:switchyard-quickstart:bean-service:1.0}submitOrderResponse"))
            .sendInOut(testOrder)
            .getContent(Element.class);
        XMLUnit.setIgnoreWhitespace(true);
        XMLAssert.assertXMLEqual(loadXML(ORDER_ACK_XML), result.getOwnerDocument());
    }

    private Document loadXML(String path) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.parse(getClass().getClassLoader().getResourceAsStream(path));
    }
}
