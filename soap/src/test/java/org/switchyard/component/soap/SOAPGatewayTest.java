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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.xml.namespace.QName;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.ExchangeHandler;
import org.switchyard.Message;
import org.switchyard.ServiceDomain;
import org.switchyard.common.net.SocketAddr;
import org.switchyard.component.soap.config.model.SOAPBindingModel;
import org.switchyard.component.soap.util.SOAPUtil;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.config.model.composite.CompositeModel;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.metadata.BaseService;
import org.switchyard.metadata.InOnlyOperation;
import org.switchyard.metadata.InOutOperation;
import org.switchyard.metadata.ServiceOperation;
import org.switchyard.test.InvocationFaultException;
import org.switchyard.test.Invoker;
import org.switchyard.test.SwitchYardRunner;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Contains tests for SOAPGateway.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
@RunWith(SwitchYardRunner.class)
public class SOAPGatewayTest {
    private static final int DEFAULT_THREAD_COUNT = 10;
    private static final long DEFAULT_NO_OF_THREADS = 100;

    private ServiceDomain _domain;

    @org.switchyard.test.ServiceOperation("webservice-consumer")
    private Invoker consumerService;

    @org.switchyard.test.ServiceOperation("webservice-consumer-classpath-wsdl")
    private Invoker consumerCPWsdl;

    private SOAPBindingModel _config;
    private static URL _serviceURL;
    private SOAPGateway _soapInbound;
    private SOAPGateway _soapOutbound;
    private SOAPGateway _soapOutbound2;
    private long _noOfThreads = DEFAULT_NO_OF_THREADS;
    
    private static ModelPuller<CompositeModel> _puller;

    private class WebServiceInvoker implements Callable<String> {

        private long _threadNo;

        public WebServiceInvoker(long threadNo) {
            _threadNo = threadNo;
        }

        public String call() {
            String input = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body>"
                     + "   <test:sayHello xmlns:test=\"urn:switchyard-component-soap:test-ws:1.0\">"
                     + "      <arg0>Thread " + _threadNo + "</arg0>"
                     + "   </test:sayHello>"
                     + "</soap:Body></soap:Envelope>";
            String output = null;

            try {
                HttpURLConnection con = (HttpURLConnection) _serviceURL.openConnection();
                con.setDoInput(true);
                con.setDoOutput(true);
                con.setRequestProperty("Content-type", "text/xml; charset=utf-8");
                OutputStream outStream = con.getOutputStream();
                outStream.write(input.getBytes());
                InputStream inStream = con.getInputStream();
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                byte[] byteBuf = new byte[256];
                int len = inStream.read(byteBuf);
                while (len > -1) {
                    byteStream.write(byteBuf, 0, len);
                    len = inStream.read(byteBuf);
                }
                outStream.close();
                inStream.close();
                byteStream.close();
                output =  byteStream.toString();

            } catch (IOException ioe) {
                output = "<error>" + ioe + "</error>";
            }
            return output;
        }
    }

    @Before
    public void setUp() throws Exception {
        _puller = new ModelPuller<CompositeModel>();
        
        // Provide a switchyard service
        SOAPProvider provider = new SOAPProvider();

        CompositeModel composite = _puller.pull("/HelloSwitchYard.xml", getClass());
        composite.assertModelValid();

        CompositeServiceModel compositeService = composite.getServices().get(0);
        _config = (SOAPBindingModel)compositeService.getBindings().get(0);

        _domain.registerService(_config.getServiceName(), provider, new HelloWebServiceInterface());

        String host = System.getProperty("org.switchyard.test.soap.host", "localhost");
        String port = System.getProperty("org.switchyard.test.soap.port", "48080");

        // Service exposed as WS
        _soapInbound = new SOAPGateway();

        _config.setPublishAsWS(true);
        _config.setSocketAddr(new SocketAddr(host, Integer.parseInt(port)));
        _soapInbound.init(_config, _domain);

        _soapInbound.start();

        _serviceURL = new URL("http://" + host + ":" + port + "/HelloWebService");

        // A WS Consumer as Service
        _soapOutbound = new SOAPGateway();
        SOAPBindingModel config2 = new SOAPBindingModel();
        config2.setWsdl(_serviceURL.toExternalForm() + "?wsdl");
        config2.setServiceName(consumerService.getServiceName());
        _soapOutbound.init(config2, _domain);
        _soapOutbound.start();

        _soapOutbound2 = new SOAPGateway();
        SOAPBindingModel config3 = new SOAPBindingModel();
        config3.setWsdl(_config.getWsdl());
        config3.setServiceName(consumerCPWsdl.getServiceName());
        _soapOutbound2.init(config3, _domain);
        _soapOutbound2.start();

        XMLUnit.setIgnoreWhitespace(true);
    }
    
    @After
    public void tearDown() throws Exception {
        _soapOutbound.stop();
        _soapOutbound2.stop();
        // There is an issue when running under JUnit that at shutdown,
        // the port isn't completely released. When the next service
        // is published up on that same port, it goes into a strange state.
        // _soapInbound.stop();
        _soapInbound.destroy();
        _soapOutbound.destroy();
        _soapOutbound2.destroy();
    }

    @Test
    public void testMappedVariableNames() throws Exception {
        Set<QName> expected = new LinkedHashSet<QName>();
        expected.add(QName.valueOf("one"));
        expected.add(QName.valueOf("two"));
        expected.add(QName.valueOf("three"));
        Set<QName> actual = _config.getComposerMappedVariableNames();
        Assert.assertEquals(expected, actual);
        expected.clear();
        expected.add(QName.valueOf("{urn:foo-bar}four"));
        expected.add(QName.valueOf("{urn:foo-bar}five"));
        expected.add(QName.valueOf("{urn:foo-bar}six"));
        actual = _config.getDecomposerMappedVariableNames();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void invokeWithClassPathResource() throws Exception {
        Element input = SOAPUtil.parseAsDom("<test:sayHello xmlns:test=\"urn:switchyard-component-soap:test-ws:1.0\">"
                     + "   <arg0>Hello</arg0>"
                     + "</test:sayHello>").getDocumentElement();
        String rootCause = null;
        try {
            consumerCPWsdl.sendInOut(input);
        } catch (InvocationFaultException ife) {
            rootCause = getRootCause(ife);
        }

        // A real URL here would depend on the test environment's host and port hence,
        // it is sufficient to test that we actually loaded the WSDL from classpath
        Assert.assertEquals("javax.xml.ws.WebServiceException: Unsupported endpoint address: REPLACE_WITH_ACTUAL_URL", rootCause);
    }

    @Test
    public void invokeOneWay() throws Exception {
        Element input = SOAPUtil.parseAsDom("<!--Comment --><test:helloWS xmlns:test=\"urn:switchyard-component-soap:test-ws:1.0\">"
                     + "   <arg0>Hello</arg0>"
                     + "</test:helloWS>").getDocumentElement();

        consumerService.sendInOnly(input);
    }

    @Test
    public void invokeRequestResponse() throws Exception {
        String input = "<test:sayHello xmlns:test=\"urn:switchyard-component-soap:test-ws:1.0\">"
                     + "   <arg0>Jimbo</arg0>"
                     + "</test:sayHello>";

        String output = "<test:sayHelloResponse xmlns:test=\"urn:switchyard-component-soap:test-ws:1.0\">"
                     + "   <return>Hello Jimbo</return>"
                     + "</test:sayHelloResponse>";


        Message responseMsg = consumerService.sendInOut(input);

        String response = toString(responseMsg.getContent(Node.class));
        XMLAssert.assertXMLEqual(output, response);
    }

    private String toString(Node node) throws Exception
    {
        TransformerFactory transFactory = TransformerFactory.newInstance();
        Transformer transformer = transFactory.newTransformer();
        StringWriter sw = new StringWriter();
        DOMSource source = new DOMSource(node);
        StreamResult result = new StreamResult(sw);
        transformer.transform(source, result);
        return sw.toString();
    }

    @Test
    public void invokeRequestResponseFault() throws Exception {
        String input = "<test:sayHello xmlns:test=\"urn:switchyard-component-soap:test-ws:1.0\">"
                     + "   <arg0></arg0>"
                     + "</test:sayHello>";

        String output = "<SOAP-ENV:Fault xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                        + "   <faultcode>SOAP-ENV:Server.AppError</faultcode>"
                        + "   <faultstring>Invalid name</faultstring>"
                        + "   <detail>"
                        + "      <message>Looks like you did not specify a name!</message>"
                        + "      <errorcode>1000</errorcode>"
                        + "   </detail>"
                        + "</SOAP-ENV:Fault>";

        Message responseMsg = consumerService.sendInOut(input);
        String response = toString(responseMsg.getContent(Node.class));
        XMLAssert.assertXMLEqual(output, response);
    }

    @Test
    public void invokeRequestResponseCustomFault() throws Exception {
        String faultString =  "<CustomFaultMessage>"
        		                + "errorcode=1000;"
                              + "Invalid name: Looks like you did not specify a name!"
           	                + "</CustomFaultMessage>";
        
        String input = "<test:sayHello xmlns:test=\"urn:switchyard-component-soap:test-ws:1.0\">"
                       + "   <arg0></arg0>"
                       +    faultString
                       + "</test:sayHello>";

        String output = "<SOAP-ENV:Fault xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                        +    "<faultcode>SOAP-ENV:Server</faultcode>"
                        +    "<faultstring>Send failed</faultstring>"
                        +    "<detail>"
                        +    faultString
                        +    "</detail>"
                        + "</SOAP-ENV:Fault>";

        Message responseMsg = consumerService.sendInOut(input);
        String response = toString(responseMsg.getContent(Node.class));
        XMLAssert.assertXMLEqual(output, response);
    }

    @Test
    public void invokeMultiThreaded() throws Exception {
        String output = null;
        String response = null;
        Collection<Callable<String>> callables = new ArrayList<Callable<String>>();
        for (int i = 0; i < _noOfThreads; i++) {
            callables.add(new WebServiceInvoker(i));
        }

        ExecutorService executorService = Executors.newFixedThreadPool(DEFAULT_THREAD_COUNT);
        Collection<Future<String>> futures = executorService.invokeAll(callables);
        Assert.assertEquals(futures.size(), _noOfThreads);
        int i = 0;

        for (Future<String> future : futures) {
            response = future.get();
            output =  "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body>"
                     + "   <test:sayHelloResponse xmlns:test=\"urn:switchyard-component-soap:test-ws:1.0\">"
                     + "      <return>Hello Thread " + i + "</return>"
                     + "   </test:sayHelloResponse>"
                     + "</soap:Body></soap:Envelope>";
            XMLAssert.assertXMLEqual(output, response);
            i++;
        }
    }

    private String getRootCause(Throwable t) {
        if(t.getCause() != null){
            return getRootCause(t.getCause());
        } else {
            return t.toString();
        }
    }

    private static class HelloWebServiceInterface extends BaseService {
        private static Set<ServiceOperation> _operations = new HashSet<ServiceOperation>(2);
        static {
            _operations.add(new InOutOperation("sayHello"));
            _operations.add(new InOnlyOperation("helloWS"));
        }
        public HelloWebServiceInterface() {
            super(_operations);
        }
    }
}

