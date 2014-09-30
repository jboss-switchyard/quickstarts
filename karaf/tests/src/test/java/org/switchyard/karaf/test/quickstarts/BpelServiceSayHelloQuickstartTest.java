/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
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
package org.switchyard.karaf.test.quickstarts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.custommonkey.xmlunit.NamespaceContext;
import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.junit.BeforeClass;
import org.junit.Test;
import org.switchyard.component.test.mixins.http.HTTPMixIn;
import org.w3c.dom.NodeList;

public class BpelServiceSayHelloQuickstartTest extends AbstractQuickstartTest {
    private static String bundleName = "org.switchyard.quickstarts.switchyard.bpel.say.hello";
    private static String featureName = "switchyard-quickstart-bpel-say-hello";

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
            String response = httpMixIn.postString("http://localhost:" + getSoapClientPort() + "/SayHelloService/SayHelloService", SOAP_REQUEST);

            org.w3c.dom.Document d = XMLUnit.buildControlDocument(response);
            java.util.HashMap<String,String> m = new java.util.HashMap<String,String>();
            m.put("tns", "http://www.jboss.org/bpel/examples");
            NamespaceContext ctx = new SimpleNamespaceContext(m);
            XpathEngine engine = XMLUnit.newXpathEngine();
            engine.setNamespaceContext(ctx);

            NodeList l = engine.getMatchingNodes("//tns:result", d);
            assertEquals(1, l.getLength());
            assertEquals(org.w3c.dom.Node.ELEMENT_NODE, l.item(0).getNodeType());
            
            if (!l.item(0).getTextContent().equals("Hello Fred")) {
                fail("Expecting 'Hello Fred'");
            }
        } finally {
            httpMixIn.uninitialize();
        }
    }

    private static final String SOAP_REQUEST = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:exam=\"http://www.jboss.org/bpel/examples\">\n" +
        "   <soapenv:Header/>\n" +
        "   <soapenv:Body>\n" +
        "       <exam:sayHello>\n" +
        "           <exam:input>Fred</exam:input>\n" +
        "       </exam:sayHello>\n" +
        "   </soapenv:Body>\n" +
        "</soapenv:Envelope>";
}
