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
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.BeforeClass;
import org.junit.Assert;
import org.junit.Test;
import org.switchyard.common.type.Classes;
import org.switchyard.component.test.mixins.http.HTTPMixIn;

public class SoapBindingRpcQuickstartTest extends AbstractQuickstartTest {
    private static String bundleName = "org.switchyard.quickstarts.switchyard.soap.binding.rpc";
    private static String featureName = "switchyard-quickstart-soap-binding-rpc";
    private static String SWITCHYARD_WEB_SERVICE = "http://localhost:8181/cxf/soap-binding-rpc/HelloWorldWSService";

    private HTTPMixIn _httpMixIn = new HTTPMixIn();

    static int size = 13000000;

    @BeforeClass
    public static void before() throws Exception {
        startTestContainer(featureName, bundleName);
    }

    @Test
    public void helloWorldService() throws Exception {
        HTTPMixIn httpMixIn = new HTTPMixIn();

        httpMixIn.initialize();
        try {
            XMLUnit.setIgnoreWhitespace(true);
            String replyMsg = httpMixIn.postString(SWITCHYARD_WEB_SERVICE, REQUEST);
            XMLAssert.assertXMLEqual(RESPONSE, replyMsg);
            int status = httpMixIn.sendStringAndGetStatus(SWITCHYARD_WEB_SERVICE, REQUEST2, HTTPMixIn.HTTP_POST);
            Assert.assertEquals(500, status);
        } finally {
            httpMixIn.uninitialize();
        }
    }

    private static String REQUEST = "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns2=\"urn:switchyard-quickstart:soap-binding-rpc:1.0\">"
                            + "    <S:Body>"
                            + "        <ns2:sayHello>"
                            + "            <toWhom>SwitchYard</toWhom>"
                            + "            <language>English</language>"
                            + "        </ns2:sayHello>"
                            + "    </S:Body>"
                            + "</S:Envelope>";

    private static String REQUEST2 = "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns2=\"urn:switchyard-quickstart:soap-binding-rpc:1.0\">"
                            + "    <S:Body>"
                            + "        <ns2:sayHello>"
                            + "            <toWhom>500</toWhom>"
                            + "            <language>English</language>"
                            + "        </ns2:sayHello>"
                            + "    </S:Body>"
                            + "</S:Envelope>";

    private static String RESPONSE = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                            + "<SOAP-ENV:Header/>"
                            + "    <SOAP-ENV:Body>"
                            + "        <ns2:sayHelloResponse xmlns:ns2=\"urn:switchyard-quickstart:soap-binding-rpc:1.0\">"
                            + "            <return>Hello World Greeting for 'SwitchYard' in English on a Sunday!</return>"
                            + "        </ns2:sayHelloResponse>"
                            + "    </SOAP-ENV:Body>"
                            + "</SOAP-ENV:Envelope>";
}
