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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.BeforeClass;
import org.junit.Assert;
import org.junit.Test;
import org.switchyard.common.type.Classes;
import org.switchyard.component.test.mixins.http.HTTPMixIn;

public class SoapAddressingQuickstartTest extends AbstractQuickstartTest {
    private static String bundleName = "org.switchyard.quickstarts.switchyard.soap.addressing";
    private static String featureName = "switchyard-quickstart-soap-addressing";
    private static String SWITCHYARD_WEB_SERVICE = "http://localhost:8181/cxf/soap-addressing/order/OrderService";

    private HTTPMixIn _httpMixIn = new HTTPMixIn();

    @BeforeClass
    public static void before() throws Exception {
        startTestContainer(featureName, bundleName);
    }

    @Test
    public void addressingError() throws Exception {
        _httpMixIn.initialize();
        try {
            XMLUnit.setIgnoreWhitespace(true);
            String response = _httpMixIn.postString(SWITCHYARD_WEB_SERVICE, REQUEST);
            Assert.assertTrue(response.contains("MessageAddressingHeaderRequired"));
            Assert.assertTrue(response.contains("A required header representing a Message Addressing Property is not present"));
        } finally {
            _httpMixIn.uninitialize();
        }
    }

    @Test
    public void addressingReplyTo() throws Exception {
        _httpMixIn.initialize();
        try {
            File testFile = new File("/tmp/test.txt");
            if (testFile.exists()) {
                testFile.delete();
            }
            String response = _httpMixIn.postString(SWITCHYARD_WEB_SERVICE, REQUEST2);
            Assert.assertTrue(response, response.contains("urn:switchyard-quickstart:soap-addressing:1.0:OrderService:orderResponse"));
            Assert.assertTrue(response, response.contains("uuid:3d3fcbbb-fd43-4118-b40e-62577894f39a"));
            Assert.assertTrue(response, response.contains("Thank you for your order. You should hear back from our WarehouseService shortly!"));
            // Wait for the actual response from WarehouseService
            int timeout = 0;
            while (!testFile.exists()) {
                Thread.sleep(100);
                timeout += 100;
                if (timeout == 15000) {
                    break;
                }
            }
            System.out.println("...................." + testFile.exists());
            BufferedReader stream = new BufferedReader(new FileReader(testFile));
            String text = stream.readLine();
            stream.close();
            Assert.assertEquals("Order Boeing with quantity 10 accepted.", text);
        } finally {
            _httpMixIn.uninitialize();
        }
    }

    private static String REQUEST = "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns2=\"urn:switchyard-quickstart:soap-addressing:1.0\">"
                            + "    <S:Body>"
                            + "        <ns2:order>"
                            + "            <item>Boeing</item>"
                            + "            <quantity>10</quantity>"
                            + "        </ns2:order>"
                            + "    </S:Body>"
                            + "</S:Envelope>";

    private static String REQUEST2 = "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns2=\"urn:switchyard-quickstart:soap-addressing:1.0\">"
                            + "    <S:Header xmlns:wsa=\"http://www.w3.org/2005/08/addressing\">"
                            + "        <wsa:MessageID>uuid:3d3fcbbb-fd43-4118-b40e-62577894f39a</wsa:MessageID>"
                            + "        <wsa:Action>urn:switchyard-quickstart:soap-addressing:1.0:OrderService:orderRequest</wsa:Action>"
                            + "    </S:Header>"
                            + "    <S:Body>"
                            + "        <ns2:order>"
                            + "            <item>Boeing</item>"
                            + "            <quantity>10</quantity>"
                            + "        </ns2:order>"
                            + "    </S:Body>"
                            + "</S:Envelope>";
}
