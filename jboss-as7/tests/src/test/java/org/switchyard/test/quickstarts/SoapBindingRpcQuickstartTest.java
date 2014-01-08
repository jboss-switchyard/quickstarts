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
import org.custommonkey.xmlunit.XMLUnit;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.component.test.mixins.http.HTTPMixIn;
import org.switchyard.test.ArquillianUtil;

@RunWith(Arquillian.class)
public class SoapBindingRpcQuickstartTest {

    private static final String SWITCHYARD_WEB_SERVICE = "http://localhost:8080/soap-binding-rpc/HelloWorldWSService";

    @Deployment(testable = false)
    public static JavaArchive createDeployment() {
        return ArquillianUtil.createJarQSDeployment("switchyard-soap-binding-rpc");
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
