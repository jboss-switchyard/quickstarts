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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.Assert;

import org.junit.Test;
import org.switchyard.test.SwitchYardCDITestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TypeTransformationTest extends SwitchYardCDITestCase {

    final String ITEM_ID = "BUTTER";
    final String ORDER_XML = "xml/order.xml";
    
    @Test
    public void testTransformXMLtoJava() throws Exception {
        
        OrderAck orderAck = newInvoker("OrderService")
            .operation("submitOrder")
            .inputType(OrderTransform_XML_Java.FROM_TYPE)
            .sendInOut(loadXML(ORDER_XML))
            .getContent(OrderAck.class);
        
        Assert.assertTrue(orderAck.isAccepted());

    }
    
    private Element loadXML(String path) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(getClass().getClassLoader().getResourceAsStream(path));
        return doc.getDocumentElement();
    }
}
