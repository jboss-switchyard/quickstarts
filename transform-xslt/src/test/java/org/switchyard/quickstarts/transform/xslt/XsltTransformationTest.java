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
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
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
