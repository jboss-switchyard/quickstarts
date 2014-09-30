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

import java.util.Collections;

import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.BeforeClass;
import org.junit.Test;
import org.switchyard.component.test.mixins.http.HTTPMixIn;

public class BpelSimpleCorrelationQuickstartTest extends AbstractQuickstartTest {
    private static String bundleName = "org.switchyard.quickstarts.switchyard.bpel.simple.correlation";
    private static String featureName = "switchyard-quickstart-bpel-simple-correlation";

    @BeforeClass
    public static void before() throws Exception {
        startTestContainer(featureName, bundleName);
    }

    @Override
    @Test
    public void testDeployment() throws Exception {
        HTTPMixIn soapMixIn = new HTTPMixIn();
        soapMixIn.initialize();

        try {
            String url = "http://localhost:" + getSoapClientPort() + "/HelloGoodbyeService/HelloGoodbyeService";
            String response = soapMixIn.postString(url, HELLO_REQUEST);
            XMLUnit.setXpathNamespaceContext(new SimpleNamespaceContext(Collections.singletonMap("ns", "http://www.jboss.org/bpel/examples/simple_correlation/")));
            XMLAssert.assertXpathEvaluatesTo("1", "//ns:sessionId/ns:id", response);
            XMLAssert.assertXpathEvaluatesTo("BPEL, Hello World!", "//ns:parameter", response);

            response = soapMixIn.postString(url, GOODBYE_REQUEST);
            XMLAssert.assertXpathEvaluatesTo("1", "//ns:sessionId/ns:id", response);
            XMLAssert.assertXpathEvaluatesTo("BPEL, Goodbye World!", "//ns:parameter", response);
        } finally {
            soapMixIn.uninitialize();
        }
    }

    private static final String HELLO_REQUEST = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sim=\"http://www.jboss.org/bpel/examples/simple_correlation/\">" +
            "   <soapenv:Header/>" +
            "   <soapenv:Body>" +
            "      <sim:helloMessage>" +
            "         <sim:sessionId>" +
            "            <sim:id>1</sim:id>" +
            "         </sim:sessionId>" +
            "         <sim:parameter>BPEL</sim:parameter>" +
            "      </sim:helloMessage>" +
            "   </soapenv:Body>" +
            "</soapenv:Envelope>";
    
    private static final String GOODBYE_REQUEST = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sim=\"http://www.jboss.org/bpel/examples/simple_correlation/\">" +
            "   <soapenv:Header/>" +
            "   <soapenv:Body>" +
            "      <sim:goodbyeMessage>" +
            "         <sim:sessionId>" +
            "            <sim:id>1</sim:id>" +
            "         </sim:sessionId>" +
            "         <sim:parameter>BPEL</sim:parameter>" +
            "      </sim:goodbyeMessage>" +
            "   </soapenv:Body>" +
            "</soapenv:Envelope>";
}
