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
package org.switchyard.test.quickstarts;

import java.util.HashMap;
import java.util.Map;

import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.component.test.mixins.http.HTTPMixIn;
import org.switchyard.test.ArquillianUtil;

/**
 * Test deployment of bus extension done by custom cdi beans.
 */
@RunWith(Arquillian.class)
public class CamelCdiBusQuickstartTest {

    @Deployment(testable = false)
    public static JavaArchive createDeployment() {
        return ArquillianUtil.createJarQSDeployment("switchyard-camel-bus-cdi");
    }

    @Test
    public void testDeployment() throws Exception {
        HTTPMixIn httpMixIn = new HTTPMixIn();

        httpMixIn.initialize();
        try {
            XMLUnit.setIgnoreWhitespace(true);
            String response = httpMixIn.postString("http://localhost:8080/quickstart-cdi-bus/OrderService", SOAP_REQUEST);

            Map<String, String> namespaces = new HashMap<String, String>();
            namespaces.put("ns", "urn:switchyard-quickstart:cdi-bus:1.0");
            XMLUnit.setXpathNamespaceContext(new SimpleNamespaceContext(namespaces));

            XMLAssert.assertXpathEvaluatesTo("PO-19838-XYZ", "//ns:orderAck/orderId", response);
            XMLAssert.assertXpathEvaluatesTo("true", "//ns:orderAck/accepted", response);
            XMLAssert.assertXpathEvaluatesTo("Order Accepted", "//ns:orderAck/status", response);
        } finally {
            httpMixIn.uninitialize();
        }
    }

    private static final String SOAP_REQUEST = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
            "    <soap:Header/>" +
            "    <soap:Body>\n" +
            "        <orders:order xmlns:orders=\"urn:switchyard-quickstart:cdi-bus:1.0\">\n" +
            "            <orderId>PO-19838-XYZ</orderId>\n" +
            "            <itemId>BUTTER</itemId>\n" +
            "            <quantity>200</quantity>\n" +
            "        </orders:order>\n" +
            "    </soap:Body>\n" +
            "</soap:Envelope>";

}
