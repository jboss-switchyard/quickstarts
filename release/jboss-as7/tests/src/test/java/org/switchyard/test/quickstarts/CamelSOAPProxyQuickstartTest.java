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

import java.io.IOException;

import org.custommonkey.xmlunit.XMLAssert;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.component.test.mixins.http.HTTPMixIn;
import org.switchyard.test.ArquillianUtil;

@RunWith(Arquillian.class)
public class CamelSOAPProxyQuickstartTest {

    @Deployment(testable = false, order=1, name="ReverseService")
    public static WebArchive createProxyDeployment() throws IOException {
        return ArquillianUtil.createWarQSDeployment("switchyard-camel-soap-proxy-reverse-service");
    }

    @Deployment(testable = false, order=2)
    public static JavaArchive createDeployment() throws IOException {
        return ArquillianUtil.createJarQSDeployment("switchyard-camel-soap-proxy");
    }

    @Test
    public void testDeployment() throws Exception {
        HTTPMixIn soapMixIn = new HTTPMixIn();
        soapMixIn.initialize();

        try {
            String port = System.getProperty("org.switchyard.component.soap.client.port", "8080");
            // For karaf, the directory will generally be cxf/proxy/ReverseService
            String dir = System.getProperty("org.switchyard.component.soap.client.dir", "proxy/ReverseService");
            String url = "http://localhost:" + port + "/" + dir;
            String response = soapMixIn.postString(url, XML);
            XMLAssert.assertXpathEvaluatesTo("raboof", "//text", response);
        } finally {
            soapMixIn.uninitialize();
        }
    }

    private static final String XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">"
            + "    <SOAP-ENV:Header/>"
            + "    <SOAP-ENV:Body>"
            + "        <ns2:reverse xmlns:ns2=\"urn:switchyard-quickstart:camel-soap-proxy:1.0\">"
            + "            <text>foobar</text>"
            + "        </ns2:reverse>"
            + "    </SOAP-ENV:Body>"
            + "</SOAP-ENV:Envelope>";
    
}
