/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
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

import org.custommonkey.xmlunit.XMLAssert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.switchyard.component.test.mixins.http.HTTPMixIn;

public class BPMServiceQuickstartTest extends AbstractQuickstartTest {
    private static String bundleName = "org.switchyard.quickstarts.switchyard.bpm.service";
    private static String featureName = "switchyard-quickstart-bpm-service";

    @BeforeClass
    public static void before() throws Exception {
        startTestContainer(featureName, bundleName);
    }

    @Override
    @Test
    public void testDeployment() throws Exception {
        HTTPMixIn httpMixIn = new HTTPMixIn();
        httpMixIn.initialize();
        try {
            String port = getSoapClientPort();
            String response = httpMixIn.postString("http://localhost:" + port + "/swydws/ProcessOrder", request);
            XMLAssert.assertXpathEvaluatesTo(String.valueOf(true), "//accepted", response);
        } finally {
            httpMixIn.uninitialize();
        }
    }

    private static final String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:switchyard-quickstart:bpm-service:1.0\">"
            + "    <soapenv:Header/>"
            + "    <soapenv:Body>"
            + "        <urn:submitOrder>"
            + "            <orderId>test1</orderId>"
            + "            <itemId>cowbell</itemId>"
            + "            <quantity>30</quantity>"
            + "        </urn:submitOrder>"
            + "    </soapenv:Body>"
            + "</soapenv:Envelope>";

}
