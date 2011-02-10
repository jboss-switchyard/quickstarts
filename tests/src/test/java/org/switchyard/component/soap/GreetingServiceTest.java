/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.switchyard.component.soap;

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.component.soap.config.model.SOAPBindingModel;
import org.switchyard.component.soap.util.StreamUtil;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

public class GreetingServiceTest {

    private static final QName GREETING_SERVICE_NAME = new QName("GreetingService");

    private WeldContainer _weld;
    private SOAPBindingModel config;

    public GreetingServiceTest() throws MalformedURLException {
        String host = System.getProperty("org.switchyard.test.soap.host", "localhost");
        String port = System.getProperty("org.switchyard.test.soap.port", "48080");
        
        config = new SOAPBindingModel();
        config.setPublishAsWS(true);
        config.setWsdl("src/main/resources/GreetingServiceImplService.wsdl");
        config.setServiceName(GREETING_SERVICE_NAME);
        config.setServerHost(host);
        config.setServerPort(Integer.parseInt(port));
    }

    @Before
    public void setUp() throws Exception {
        // Init the CDI container...
        _weld = new Weld().initialize();
        _weld.event().select(ContainerInitialized.class).fire(new ContainerInitialized());
    }

    @Test
    public void invokeRequestResponse() throws Exception {
        String soapRequest = "<gre:greet xmlns:gre=\"http://greeting.soap.component.switchyard.org/\">\n" +
                " <arg0>\n" +
                "    <person>\n" +
                "       <firstname>Mal</firstname>\n" +
                "       <lastname>Beck</lastname>\n" +
                "    </person>\n" +
                "    <time>2011-01-22T21:32:52</time>\n" +
                " </arg0>\n" +
                "</gre:greet>";
        String expectedResponse = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP-ENV:Header/><SOAP-ENV:Body><gre:greetResponse xmlns:gre=\"http://greeting.soap.component.switchyard.org/\">\n" +
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
        String soapRequest = "<gre:greet xmlns:gre=\"http://greeting.soap.component.switchyard.org/\">\n" +
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
        String expectedResponse = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP-ENV:Header/><SOAP-ENV:Body><SOAP-ENV:Fault><faultcode>SOAP-ENV:Server</faultcode><faultstring>Invalid input SOAP payload namespace for service operation 'greet' (service 'GreetingService').  Port defines operation namespace as 'http://greeting.soap.component.switchyard.org/'.  Actual namespace on input SOAP message 'http://broken/unknown/namespace'.</faultstring></SOAP-ENV:Fault></SOAP-ENV:Body></SOAP-ENV:Envelope>";

        test(soapRequest, expectedResponse, false);
    }

    @Test
    public void invokeRequestResponse_bad_soap_02() throws Exception {
        String soapRequest = "<gre:xxxxx xmlns:gre=\"http://greeting.soap.component.switchyard.org/\" />";
        String expectedResponse = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP-ENV:Header/><SOAP-ENV:Body><SOAP-ENV:Fault><faultcode>SOAP-ENV:Server</faultcode><faultstring>Operation 'xxxxx' not available on target Service 'GreetingService'.</faultstring></SOAP-ENV:Fault></SOAP-ENV:Body></SOAP-ENV:Envelope>";

        test(soapRequest, expectedResponse, false);
    }

    public void test(String request, String expectedResponse, boolean dumpResponse) throws Exception {

        // Launch the SOAP Handler...
        InboundHandler inboundHandler = new InboundHandler(config);
        inboundHandler.start();

        try {
            SOAPMessage soapRequest = StreamUtil.readSOAP(request);
            SOAPMessage soapResponse = inboundHandler.invoke(soapRequest);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            soapResponse.writeTo(baos);
            String actualResponse = new String(baos.toByteArray());

            if(dumpResponse) {
                System.out.println(actualResponse);
            }

            XMLUnit.setIgnoreWhitespace(true);
            XMLAssert.assertXMLEqual(expectedResponse, actualResponse);
        } finally {
            inboundHandler.stop();
        }
    }
}
