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

import org.custommonkey.xmlunit.XMLAssert;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.test.ArquillianUtil;
import org.switchyard.component.test.mixins.http.HTTPMixIn;

/**
 *
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
@RunWith(Arquillian.class)
public class ValidateXmlQuickstartTest {

    @Deployment(testable = false)
    public static JavaArchive createDeployment() {
        return ArquillianUtil.createJarQSDeployment("switchyard-quickstart-validate-xml");
    }

    @Test
    public void testOrders() throws Exception {
        HTTPMixIn httpMixIn = new HTTPMixIn();

        httpMixIn.initialize();
        try {
            String response = httpMixIn.postString("http://localhost:8080/quickstart-validate-xml/OrderService", SOAP_REQUEST);System.out.println(response);
            XMLAssert.assertXpathEvaluatesTo("Error during validation with '/xsd/orders.xsd' as 'XML_SCHEMA'.", "//faultstring", response);
        } finally {
            httpMixIn.uninitialize();
        }
    }

    private static final String SOAP_REQUEST = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
            "    <soap:Body>\n" +
            "        <orders:order xmlns:orders=\"urn:switchyard-quickstart:validate-xml:0.1.0\">\n" +
            "            <orderId>PO-19838-XYZ</orderId>\n" +
            "            <itemId>BUTTER</itemId>\n" +
            "            <quantity>200</quantity>\n" +
            "            <invalid-element>This element is not allowed by XML Schema.</invalid-element>\n" +
            "        </orders:order>\n" +
            "    </soap:Body>\n" +
            "</soap:Envelope>";

}
