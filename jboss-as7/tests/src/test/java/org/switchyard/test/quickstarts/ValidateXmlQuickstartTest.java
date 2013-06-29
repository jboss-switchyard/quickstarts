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

import org.custommonkey.xmlunit.XMLAssert;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.test.ArquillianUtil;
import org.switchyard.component.test.mixins.http.HTTPMixIn;

/**
 *
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
@RunWith(Arquillian.class)
public class ValidateXmlQuickstartTest {

    @Deployment(testable = false)
    public static JavaArchive createDeployment() {
        return ArquillianUtil.createJarQSDeployment("switchyard-quickstart-validate-xml");
    }

    @Test
    public void testOrders() throws Exception {
        HTTPMixIn httpMixIn = new HTTPMixIn();

        httpMixIn.initialize();
        try {
            String response = httpMixIn.postString("http://localhost:8080/quickstart-validate-xml/OrderService", SOAP_REQUEST);System.out.println(response);
            Assert.assertTrue(response.contains("Invalid content was found starting with element 'invalid-element'. No child element is expected at this point."));
        } finally {
            httpMixIn.uninitialize();
        }
    }

    private static final String SOAP_REQUEST = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
            "    <soap:Body>\n" +
            "        <orders:order xmlns:orders=\"urn:switchyard-quickstart:validate-xml:0.1.0\">\n" +
            "            <orderId>PO-19838-XYZ</orderId>\n" +
            "            <itemId>BUTTER</itemId>\n" +
            "            <quantity>200</quantity>\n" +
            "            <invalid-element>This element is not allowed by XML Schema.</invalid-element>\n" +
            "        </orders:order>\n" +
            "    </soap:Body>\n" +
            "</soap:Envelope>";

}
