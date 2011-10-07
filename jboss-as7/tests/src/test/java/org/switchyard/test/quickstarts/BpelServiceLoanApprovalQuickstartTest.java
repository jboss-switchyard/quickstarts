/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package org.switchyard.test.quickstarts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.custommonkey.xmlunit.NamespaceContext;
import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.test.ArquillianUtil;
import org.switchyard.test.mixins.HTTPMixIn;
import org.w3c.dom.NodeList;

/**
 *
 */
@RunWith(Arquillian.class)
public class BpelServiceLoanApprovalQuickstartTest {

    @Deployment(testable = false)
    public static JavaArchive createDeployment() {
        return ArquillianUtil.createJarQSDeployment("switchyard-quickstart-bpel-service-loan-approval");
    }

    @Test
    public void testLoanRequestAccepted() throws Exception {
        HTTPMixIn httpMixIn = new HTTPMixIn();
        httpMixIn.initialize();
        try {
            String response = httpMixIn.postString("http://localhost:18001/loanService", SOAP_REQUEST_1);

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
            String response = httpMixIn.postString("http://localhost:18001/loanService", SOAP_REQUEST_2);

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
            "   <firstName>Fred</firstName>\n" +
            "   <name>Bloggs</name>\n" +
            "   <amount>100</amount>\n" +
            "   </ns1:request>\n" +
        "   </soapenv:Body>\n" +
        "</soapenv:Envelope>";

    private static final String SOAP_REQUEST_2 = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:exam=\"http://www.jboss.org/bpel/examples\">\n" +
        "   <soapenv:Header/>\n" +
        "   <soapenv:Body>\n" +
        "   <ns1:request xmlns:ns1=\"http://example.com/loan-approval/loanService/\">\n" +
            "   <firstName>Fred</firstName>\n" +
            "   <name>Bloggs</name>\n" +
            "   <amount>11000</amount>\n" +
            "   </ns1:request>\n" +
        "   </soapenv:Body>\n" +
        "</soapenv:Envelope>";
}
