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
package org.switchyard.test.quickstarts.demo;

import org.custommonkey.xmlunit.XMLUnit;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.test.ArquillianUtil;
import org.switchyard.test.WebServiceInvoker;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
@RunWith(Arquillian.class)
public class OrdersDemoQuickstartTest {

    @Deployment
    public static JavaArchive createDeployment() {
        return ArquillianUtil.createDemoDeployment("switchyard-quickstart-demo-orders");
    }

    @Test
    public void testOrders() throws IOException, SAXException {
        String response =
                WebServiceInvoker.target("http://localhost:18001/OrderService")
                .send(SOAP_REQUEST);

        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.compareXML(EXPECTED_SOAP_RESPONSE, response);
    }

    private static final String SOAP_REQUEST = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
            "    <soap:Body>\n" +
            "        <orders:submitOrder xmlns:orders=\"urn:switchyard-quickstart-demo:orders:1.0\">\n" +
            "            <order>\n" +
            "                <orderId>PO-19838-XYZ</orderId>\n" +
            "                <itemId>BUTTER</itemId>\n" +
            "                <quantity>200</quantity>\n" +
            "            </order>\n" +
            "        </orders:submitOrder>\n" +
            "    </soap:Body>\n" +
            "</soap:Envelope>";

    private static final String EXPECTED_SOAP_RESPONSE = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
            "    <SOAP-ENV:Body>\n" +
            "        <orders:submitOrderResponse xmlns:orders=\"urn:switchyard-quickstart-demo:orders:1.0\">\n" +
            "            <orderAck>\n" +
            "                <orderId>PO-19838-XYZ</orderId>\n" +
            "                <accepted>true</accepted>\n" +
            "                <status>Order Accepted</status>\n" +
            "            </orderAck>\n" +
            "        </orders:submitOrderResponse>\n" +
            "    </SOAP-ENV:Body>\n" +
            "</SOAP-ENV:Envelope>";
}
