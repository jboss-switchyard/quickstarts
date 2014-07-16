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

public class BpelServiceLoanApprovalQuickstartTest extends AbstractQuickstartTest {
    private static String bundleName = "org.switchyard.quickstarts.switchyard-bpel-loan-approval";
    private static String featureName = "switchyard-quickstart-bpel-loan-approval";

    @BeforeClass
    public static void before() throws Exception {
        startTestContainer(featureName, bundleName);
    }

    @Test
    public void testLoanRequestAccepted() throws Exception {
        HTTPMixIn httpMixIn = new HTTPMixIn();
        httpMixIn.initialize();
        try {
            String port = System.getProperty("org.switchyard.component.soap.standalone.port", "8181/cxf");
            String response = httpMixIn.postString("http://localhost:" + port + "/loanService/loanService", SOAP_REQUEST_1);

            org.w3c.dom.Document d = XMLUnit.buildControlDocument(response);
            java.util.HashMap<String,String> m = new java.util.HashMap<String,String>();
            m.put("tns", "http://example.com/loan-approval/loanService/");
            NamespaceContext ctx = new SimpleNamespaceContext(m);
            XpathEngine engine = XMLUnit.newXpathEngine();
            engine.setNamespaceContext(ctx);

            NodeList l = engine.getMatchingNodes("//tns:accept", d);
            assertEquals(1, l.getLength());
            assertEquals(org.w3c.dom.Node.ELEMENT_NODE, l.item(0).getNodeType());
            
            if (!l.item(0).getTextContent().equals("yes")) {
                fail("Expecting 'yes'");
            }
        } finally {
            httpMixIn.uninitialize();
        }
    }

    @Test
    public void testLoanRequestUnableToHandle() throws Exception {
        HTTPMixIn httpMixIn = new HTTPMixIn();
        httpMixIn.initialize();
        try {
            String response = httpMixIn.postString("http://localhost:" + getSoapClientPort() + "/loanService/loanService", SOAP_REQUEST_2);

            org.w3c.dom.Document d = XMLUnit.buildControlDocument(response);
            java.util.HashMap<String,String> m = new java.util.HashMap<String,String>();
            //m.put("tns", "http://example.com/loan-approval/loanService/");
            NamespaceContext ctx = new SimpleNamespaceContext(m);
            XpathEngine engine = XMLUnit.newXpathEngine();
            engine.setNamespaceContext(ctx);

            NodeList l = engine.getMatchingNodes("//faultcode", d);
            assertEquals(1, l.getLength());
            assertEquals(org.w3c.dom.Node.ELEMENT_NODE, l.item(0).getNodeType());
            
            if (!l.item(0).getTextContent().endsWith(":unableToHandleRequest")) {
                fail("Expecting 'unableToHandleRequest' fault code");
            }
        } finally {
            httpMixIn.uninitialize();
        }
    }

    private static final String SOAP_REQUEST_1 = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:exam=\"http://www.jboss.org/bpel/examples\">\n" +
        "   <soapenv:Header/>\n" +
        "   <soapenv:Body>\n" +
        "   <ns1:request xmlns:ns1=\"http://example.com/loan-approval/loanService/\">\n" +
            "   <ns1:firstName>Fred</ns1:firstName>\n" +
            "   <ns1:name>Bloggs</ns1:name>\n" +
            "   <ns1:amount>100</ns1:amount>\n" +
            "   </ns1:request>\n" +
        "   </soapenv:Body>\n" +
        "</soapenv:Envelope>";

    private static final String SOAP_REQUEST_2 = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:exam=\"http://www.jboss.org/bpel/examples\">\n" +
        "   <soapenv:Header/>\n" +
        "   <soapenv:Body>\n" +
        "   <ns1:request xmlns:ns1=\"http://example.com/loan-approval/loanService/\">\n" +
            "   <ns1:firstName>Fred</ns1:firstName>\n" +
            "   <ns1:name>Bloggs</ns1:name>\n" +
            "   <ns1:amount>11000</ns1:amount>\n" +
            "   </ns1:request>\n" +
        "   </soapenv:Body>\n" +
        "</soapenv:Envelope>";
}
