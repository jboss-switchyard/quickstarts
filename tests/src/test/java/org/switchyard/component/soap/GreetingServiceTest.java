/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
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

package org.switchyard.component.soap;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.ServiceReference;
import org.switchyard.component.soap.config.model.SOAPBindingModel;
import org.switchyard.component.soap.util.StreamUtil;
import org.switchyard.test.SwitchYardCDITestCase;

public class GreetingServiceTest extends SwitchYardCDITestCase {

    private static final QName GREETING_SERVICE_NAME = new QName("GreetingService");

    private SOAPBindingModel config;

    @Before
    public void setUp() throws MalformedURLException {
        String host = System.getProperty("org.switchyard.test.soap.host", "localhost");
        String port = System.getProperty("org.switchyard.test.soap.port", "48080");
        
        config = new SOAPBindingModel();
        config.setPublishAsWS(true);
        config.setWsdl("src/main/resources/GreetingServiceImplService.wsdl");
        config.setServiceName(GREETING_SERVICE_NAME);
        config.setServerHost(host);
        config.setServerPort(Integer.parseInt(port));

        // Need to explicitly add the transformer for exceptions...
        addTransformer(new HandlerExceptionTransformer());
    }

    @Test
    public void invokeRequestResponse() throws Exception {
        String soapRequest = "<gre:greet xmlns:gre=\"urn:switchyard-component-soap:test-greeting:1.0\">\n" +
                " <arg0>\n" +
                "    <person>\n" +
                "       <firstname>Mal</firstname>\n" +
                "       <lastname>Beck</lastname>\n" +
                "    </person>\n" +
                "    <time>2011-01-22T21:32:52</time>\n" +
                " </arg0>\n" +
                "</gre:greet>";
        String expectedResponse = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP-ENV:Header/><SOAP-ENV:Body><gre:greetResponse xmlns:gre=\"urn:switchyard-component-soap:test-greeting:1.0\">\n" +
                "    <return>\n" +
                "        <greetingid>987789</greetingid>\n" +
                "        <person>\n" +
                "            <firstname>Mal</firstname>\n" +
                "            <lastname>Beck</lastname>\n" +
                "        </person>\n" +
                "    </return>\n" +
                "</gre:greetResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>";

        test(soapRequest, expectedResponse, false);
    }

    @Test
    public void invokeRequestResponse_App_Exception() throws Exception {
        String soapRequest = "<gre:greet xmlns:gre=\"urn:switchyard-component-soap:test-greeting:1.0\">\n" +
                " <arg0>\n" +
                "    <person>\n" +
                "       <firstname>throwme</firstname>\n" + // This tells the service to throw an exception
                "    </person>\n" +
                " </arg0>\n" +
                "</gre:greet>";
        String expectedResponse = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP-ENV:Header/><SOAP-ENV:Body><SOAP-ENV:Fault><faultcode>SOAP-ENV:Server</faultcode><faultstring>Application Exception from GreetingService !!</faultstring></SOAP-ENV:Fault></SOAP-ENV:Body></SOAP-ENV:Envelope>";

        test(soapRequest, expectedResponse, false);
    }

    @Test
    public void invokeRequestResponse_bad_soap_01() throws Exception {
        String soapRequest = "<gre:greet xmlns:gre=\"http://broken/unknown/namespace\" />";
        String expectedResponse = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP-ENV:Header/><SOAP-ENV:Body><SOAP-ENV:Fault><faultcode>SOAP-ENV:Server</faultcode><faultstring>Invalid input SOAP payload namespace for service operation 'greet' (service 'GreetingService').  Port defines operation namespace as 'urn:switchyard-component-soap:test-greeting:1.0'.  Actual namespace on input SOAP message 'http://broken/unknown/namespace'.</faultstring></SOAP-ENV:Fault></SOAP-ENV:Body></SOAP-ENV:Envelope>";

        test(soapRequest, expectedResponse, false);
    }

    @Test
    public void invokeRequestResponse_bad_soap_02() throws Exception {
        String soapRequest = "<gre:xxxxx xmlns:gre=\"urn:switchyard-component-soap:test-greeting:1.0\" />";
        String expectedResponse = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP-ENV:Header/><SOAP-ENV:Body><SOAP-ENV:Fault><faultcode>SOAP-ENV:Server</faultcode><faultstring>Operation 'xxxxx' not available on target Service 'GreetingService'.</faultstring></SOAP-ENV:Fault></SOAP-ENV:Body></SOAP-ENV:Envelope>";

        test(soapRequest, expectedResponse, false);
    }

    public void test(String request, String expectedResponse, boolean dumpResponse) throws Exception {

        // Launch the SOAP Handler...
        ServiceReference service = getServiceDomain().getService(new QName("GreetingService"));
        InboundHandler inboundHandler = new InboundHandler(config);
        inboundHandler.start(service);

        try {
            SOAPMessage soapRequest = StreamUtil.readSOAP(request);
            SOAPMessage soapResponse = inboundHandler.invoke(soapRequest);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            soapResponse.writeTo(baos);
            String actualResponse = new String(baos.toByteArray());

            if(dumpResponse) {
                System.out.println(actualResponse);
            }

//            Assert.assertEquals(expectedResponse, actualResponse);

            XMLUnit.setIgnoreWhitespace(true);
            XMLAssert.assertXMLEqual(expectedResponse, actualResponse);
        } finally {
            inboundHandler.stop();
        }
    }
}
