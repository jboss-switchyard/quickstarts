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
import org.junit.Ignore;
import org.junit.Test;
import org.switchyard.component.test.mixins.http.HTTPMixIn;

/**
 * Smooks integration needs to be patched up.
 */
@Ignore
public class TransformSmooksQuickstartTest extends AbstractQuickstartTest {
    private static String bundleName = "org.switchyard.quickstarts.switchyard.transform.smooks";
    private static String featureName = "switchyard-quickstart-transform-smooks";

    @BeforeClass
    public static void before() throws Exception {
        startTestContainer(featureName, bundleName);
    }

    @Test
    public void testGreeting() throws Exception {
        HTTPMixIn soapMixIn = new HTTPMixIn();
        soapMixIn.initialize();

        try {
            String url = "http://localhost:" + getSoapClientPort() + "/quickstart-transform-smooks/OrderService";
            soapMixIn.postStringAndTestXML(url, SOAP_REQUEST, SOAP_RESPONSE);
        } finally {
            soapMixIn.uninitialize();
        }
    }

    private static final String SOAP_REQUEST = 
"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
"   <soapenv:Header/>" +
"   <soapenv:Body>" +
"      <orders:order xmlns:orders=\"urn:switchyard-quickstart:transform-smooks:1.0\">" +
"         <orderId>PO-19838-XYZ</orderId>" +
"         <itemId>BUTTER</itemId>" +
"         <quantity>200</quantity>" +
"      </orders:order>" +
"   </soapenv:Body>" +
"</soapenv:Envelope>";

    private static final String SOAP_RESPONSE =
"<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
"   <SOAP-ENV:Header/>" +
"   <SOAP-ENV:Body>" +
"      <orders:orderAck xmlns:orders=\"urn:switchyard-quickstart:transform-smooks:1.0\">" +
"         <orderId>PO-19838-XYZ</orderId>" +
"         <accepted>true</accepted>" +
"         <status>Order Accepted</status>" +
"      </orders:orderAck>" +
"   </SOAP-ENV:Body>" +
"</SOAP-ENV:Envelope>";
}
