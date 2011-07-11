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

package org.switchyard.quickstarts.transform.jaxb;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.dom.DOMSource;

import org.junit.Assert;
import org.junit.Test;
import org.switchyard.test.SwitchYardTestCase;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.mixins.CDIMixIn;

@SwitchYardTestCaseConfig(mixins = CDIMixIn.class)
public class JaxbTransformationTest extends SwitchYardTestCase {
    
    public static final QName FROM_TYPE =
        new QName("urn:switchyard-quickstart:transform-jaxb:1.0", "order");
    public static final QName TO_TYPE =
        new QName("urn:switchyard-quickstart:transform-jaxb:1.0", "orderAck");
    
    // Paths to XML test files
    final String ORDER_XML = "/xml/order.xml";
    final String ORDER_ACK_XML = "/xml/orderAck.xml";
    
    @Test
    public void testJaxbOrderToXML() throws Exception {
        Order order = new Order();
        order.setItemId("BUTTER");
        order.setOrderId("PO-19838-XYZ");
        order.setQuantity(200);
        
        JAXBContext jaxbContext = JAXBContext.newInstance(new Class[] {Order.class});
        StringWriter resultWriter = new StringWriter();
        Marshaller marshaller = jaxbContext.createMarshaller();
        
        marshaller.marshal(order, resultWriter);
        compareXMLToResource(resultWriter.toString(), ORDER_XML);
    }
    
    @Test
    public void testJaxbOrderAckToXML() throws Exception {
        OrderAck orderAck = new OrderAck();
        orderAck.setAccepted(Boolean.TRUE);
        orderAck.setOrderId("PO-19838-XYZ");
        orderAck.setStatus("Order Accepted");
        
        JAXBContext jaxbContext = JAXBContext.newInstance(new Class[] {OrderAck.class});
        StringWriter resultWriter = new StringWriter();
        Marshaller marshaller = jaxbContext.createMarshaller();
        
        marshaller.marshal(orderAck, resultWriter);
        compareXMLToResource(resultWriter.toString(), ORDER_ACK_XML);
    }
    
    @Test
    public void testTransformXMLtoJava() throws Exception {
        OrderAck orderAck = newInvoker("OrderService")
            .operation("submitOrder")
            .inputType(FROM_TYPE)
            .sendInOut(new DOMSource(readResourceDocument(ORDER_XML)))
            .getContent(OrderAck.class);

        Assert.assertTrue(orderAck.isAccepted());
    }

    @Test
    public void testTransformJavaToXML() throws Exception {
        Order testOrder = new Order();
        testOrder.setOrderId("PO-19838-XYZ");
        testOrder.setItemId("BUTTER");
        testOrder.setQuantity(100);

        String result = newInvoker("OrderService")
            .operation("submitOrder")
            .expectedOutputType(TO_TYPE)
            .sendInOut(testOrder)
            .getContent(String.class);

        compareXMLToResource(result, ORDER_ACK_XML);
    }
}
