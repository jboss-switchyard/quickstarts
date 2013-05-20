/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.custommonkey.xmlunit.XMLUnit;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.component.test.mixins.http.HTTPMixIn;
import org.switchyard.test.ArquillianUtil;

/*
 *  WS-A test.
 */
@RunWith(Arquillian.class)
public class SoapAddressingQuickstartTest {

    private static final String SWITCHYARD_WEB_SERVICE = "http://localhost:8080/soap-addressing/order/OrderService";

    private HTTPMixIn _httpMixIn = new HTTPMixIn();

    @Deployment(testable = false)
    public static JavaArchive createDeployment() {
        return ArquillianUtil.createJarQSDeployment("switchyard-quickstart-soap-addressing");
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
            Assert.assertTrue(response.contains("urn:switchyard-quickstart:soap-addressing:1.0:OrderService:orderResponse"));
            Assert.assertTrue(response.contains("uuid:3d3fcbbb-fd43-4118-b40e-62577894f39a"));
            Assert.assertTrue(response.contains("Thank you for your order. You should hear back from our WarehouseService shortly!"));
            // Wait for the actual response from WarehouseService
            int timeout = 0;
            while (!testFile.exists()) {
                Thread.sleep(100);
                timeout += 100;
                if (timeout == 5000) {
                    break;
                }
            }
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
