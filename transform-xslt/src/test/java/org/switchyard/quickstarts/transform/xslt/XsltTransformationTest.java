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

package org.switchyard.quickstarts.transform.xslt;

import java.io.StringWriter;

import javax.xml.namespace.QName;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.test.Invoker;
import org.switchyard.test.ServiceOperation;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.SwitchYardTestKit;
import org.switchyard.test.mixins.CDIMixIn;
import org.w3c.dom.Element;

@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(config = SwitchYardTestCaseConfig.SWITCHYARD_XML, mixins = CDIMixIn.class)
public class XsltTransformationTest {

    @ServiceOperation("OrderService.submitOrder")
    private Invoker submitOrder;

    private SwitchYardTestKit _testKit;
    
    public static final QName FROM_TYPE =
        new QName("urn:switchyard-quickstart:transform-xslt:1.0", "order");
    public static final QName TO_TYPE =
        new QName("urn:switchyard-quickstart:transform-xslt:1.0", "orderAck");
    
    // Paths to XML test files
    final String ORDER_XML = "/xml/order.xml";
    final String ORDER_ACK_XML = "/xml/orderAck.xml";
    
    @Test
    public void testTransformXSLT() throws Exception {
        Element orderAck = submitOrder
            .inputType(FROM_TYPE)
            .expectedOutputType(TO_TYPE)
            .sendInOut(_testKit.readResourceDocument(ORDER_XML).getDocumentElement())
            .getContent(Element.class);

        StreamResult res = new StreamResult(new StringWriter());
        TransformerFactory.newInstance().newTransformer().transform(new DOMSource(orderAck), res);
        _testKit.compareXMLToResource(res.getWriter().toString(), ORDER_ACK_XML);
    }
}
