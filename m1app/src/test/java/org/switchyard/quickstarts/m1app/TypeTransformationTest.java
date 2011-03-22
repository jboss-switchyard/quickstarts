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

package org.switchyard.quickstarts.m1app;

import junit.framework.Assert;

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Test;
import org.switchyard.test.SwitchYardTestCase;
import org.w3c.dom.Element;

public class TypeTransformationTest extends SwitchYardTestCase {

    @Test
    public void testTransformXMLtoJavaOrder() throws Exception {
        Order order = new TransformOrder_XML_Java().transform(readResourceDocument("/xml/order.xml").getDocumentElement());

        Assert.assertEquals("PO-19838-XYZ", order.getOrderId());
        Assert.assertEquals("BUTTER", order.getItemId());
        Assert.assertEquals(200, order.getQuantity());
    }
    
    @Test
    public void testTransformJavaOrderAckToXML() throws Exception {
        OrderAck orderAck = new OrderAck()
                .setOrderId("PO-19838-XYZ")
                .setAccepted(true)
                .setStatus("Order Accepted");

        Element result = new TransformOrderAck_Java_XML().transform(orderAck);

        XMLUnit.setIgnoreWhitespace(true);
        XMLAssert.assertXMLEqual(readResourceDocument("/xml/orderAck.xml"), result.getOwnerDocument());
    }
}
