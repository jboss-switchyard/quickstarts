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
package org.switchyard.karaf.test.quickstarts;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.switchyard.component.test.mixins.http.HTTPMixIn;

public class ValidateXMLQuickstartTest extends AbstractQuickstartTest {
    private static String bundleName = "org.switchyard.quickstarts.switchyard-validate-xml";
    private static String featureName = "switchyard-quickstart-validate-xml";

    @BeforeClass
    public static void before() throws Exception {
        startTestContainer(featureName, bundleName);
    }

    @Test
    public void testOrders() throws Exception {
        HTTPMixIn httpMixIn = new HTTPMixIn();

        httpMixIn.initialize();
        try {
            String response = httpMixIn.postString("http://localhost:" + getSoapClientPort() + "/quickstart-validate-xml/OrderService", SOAP_REQUEST);
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
