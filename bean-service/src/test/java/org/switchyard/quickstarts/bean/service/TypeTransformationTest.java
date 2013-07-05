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
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
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
