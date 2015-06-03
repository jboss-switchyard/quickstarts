/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.switchyard.component.soap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.Exchange;
import org.switchyard.Message;
import org.switchyard.Property;
import org.switchyard.ServiceDomain;
import org.switchyard.common.net.SocketAddr;
import org.switchyard.component.soap.composer.SOAPComposition;
import org.switchyard.component.soap.config.model.SOAPBindingModel;
import org.switchyard.component.soap.config.model.SOAPNamespace;
import org.switchyard.component.soap.config.model.v1.V1SOAPBindingModel;
import org.switchyard.component.soap.util.SOAPUtil;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.config.model.composite.CompositeModel;
import org.switchyard.config.model.composite.CompositeReferenceModel;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.config.model.composite.v1.V1CompositeReferenceModel;
import org.switchyard.metadata.BaseService;
import org.switchyard.metadata.InOnlyOperation;
import org.switchyard.metadata.InOutOperation;
import org.switchyard.metadata.ServiceOperation;
import org.switchyard.test.InvocationFaultException;
import org.switchyard.test.Invoker;
import org.switchyard.test.MockHandler;
import org.switchyard.test.SwitchYardRunner;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

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

    @org.switchyard.test.ServiceOperation("webservice-consumer11")
    private Invoker _consumerService11;

    @org.switchyard.test.ServiceOperation("webservice-consumer11-classpath-wsdl")
    private Invoker _consumerCPWsdl;

    @org.switchyard.test.ServiceOperation("webservice-consumer12")
    private Invoker _consumerService12;

    @org.switchyard.test.ServiceOperation("webservice-consumer3")
    private Invoker _consumerService3;

    private SOAPBindingModel _config;
    private static URL _serviceURL;
    private InboundHandler _soapInbound11;
    private InboundHandler _soapInbound12;
    private OutboundHandler _soapOutbound11_1;
    private OutboundHandler _soapOutbound11_2;
    private OutboundHandler _soapOutbound12_1;
    private OutboundHandler _soapOutbound3;
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

        // Massive hack for Test Runner. Register both a service and a reference binding.
        _domain.registerService(_config.getServiceName(), new HelloWebServiceInterface(), provider);
        _domain.registerServiceReference(_config.getServiceName(), new HelloWebServiceInterface());

        String host = System.getProperty("org.switchyard.test.soap.host", "localhost");
        String port = System.getProperty("org.switchyard.test.soap.port", "8080");

        // Service exposed as WS
        _soapInbound11 = new InboundHandler(_config, _domain);

        _config.setSocketAddr(new SocketAddr(host, Integer.parseInt(port)));

        _soapInbound11.start();

        _serviceURL = new URL("http://" + host + ":" + port + "/HelloWebService");

        // A WS Consumer as Service
        SOAPBindingModel config2 = new V1SOAPBindingModel(SOAPNamespace.DEFAULT.uri()) {
            @Override
            public CompositeReferenceModel getReference() {
                return new V1CompositeReferenceModel();
            }
        };
        config2.setWsdl(_serviceURL.toExternalForm() + "?wsdl");
        config2.setServiceName(_consumerService11.getServiceName());
        config2.setName("testGateway");
        _soapOutbound11_1 = new OutboundHandler(config2);
        _soapOutbound11_1.start();
        // Hack for Test Runner. Register a service to test outbound.
        _domain.registerService(_consumerService11.getServiceName(), new HelloWebServiceInterface(), _soapOutbound11_1);

        SOAPBindingModel config3 = new V1SOAPBindingModel(SOAPNamespace.DEFAULT.uri()) {
            @Override
            public CompositeReferenceModel getReference() {
                return new V1CompositeReferenceModel();
            }
        };
        config3.setWsdl(_config.getWsdl());
        config3.setServiceName(_consumerCPWsdl.getServiceName());
        config3.setName("testGateway");
        _soapOutbound11_2 = new OutboundHandler(config3);
        _soapOutbound11_2.start();
        // Hack for Test Runner. Register a service to test outbound.
        _domain.registerService(_consumerCPWsdl.getServiceName(), new HelloWebServiceInterface(), _soapOutbound11_2);

        composite = _puller.pull("/HelloSwitchYard1.2.xml", getClass());
        composite.assertModelValid();

        compositeService = composite.getServices().get(0);
        SOAPBindingModel _config12 = (SOAPBindingModel)compositeService.getBindings().get(0);
        _config12.setSocketAddr(new SocketAddr(host, Integer.parseInt(port)));

        // Massive hack for Test Runner. Register both a service and a reference binding.
        _domain.registerService(_config12.getServiceName(), new HelloWebServiceInterface(), provider);
        _domain.registerServiceReference(_config12.getServiceName(), new HelloWebServiceInterface());

        // Service exposed as WS
        _soapInbound12 = new InboundHandler(_config12, _domain);
        _soapInbound12.start();

        // We cannot use HelloWebServiceXXX, because the context path suffix XXX is ignored by JAXWS
        URL serviceURL = new URL("http://" + host + ":" + port + "/HelloSOAP12Service");

        SOAPBindingModel config4 = new V1SOAPBindingModel(SOAPNamespace.DEFAULT.uri()) {
            @Override
            public CompositeReferenceModel getReference() {
                return new V1CompositeReferenceModel();
            }
        };
        config4.setWsdl(serviceURL.toExternalForm() + "?wsdl");
        config4.setServiceName(_consumerService12.getServiceName());
        config4.setName("testGateway");
        _soapOutbound12_1 = new OutboundHandler(config4);
        _soapOutbound12_1.start();
        // Hack for Test Runner. Register a service to test outbound.
        _domain.registerService(_consumerService12.getServiceName(), new HelloWebServiceInterface(), _soapOutbound12_1);

        config4.setServiceName(_consumerService3.getServiceName());
        config4.setEndpointAddress("http://localhost:8090/forever");
        config4.setTimeout(1300);
        _soapOutbound3 = new OutboundHandler(config4);
        _soapOutbound3.start();
        _domain.registerService(_consumerService3.getServiceName(), new HelloWebServiceInterface(), _soapOutbound3);

        XMLUnit.setIgnoreWhitespace(true);
    }

    @After
    public void tearDown() throws Exception {
        _soapInbound11.stop();
        _soapInbound12.stop();
    }

    @Test
    public void invokeWithClassPathResource() throws Exception {
        Element input = SOAPUtil.parseAsDom("<test:sayHello xmlns:test=\"urn:switchyard-component-soap:test-ws:1.0\">"
                     + "   <arg0>Hello</arg0>"
                     + "</test:sayHello>").getDocumentElement();
        String rootCause = null;
        try {
            _consumerCPWsdl.operation("sayHello").sendInOut(input);
        } catch (InvocationFaultException ife) {
            rootCause = getRootCause(ife);
        }

        // CXF replaces the url properly
        if (rootCause != null) {
            // A real URL here would depend on the test environment's host and port hence,
            // it is sufficient to test that we actually loaded the WSDL from classpath
            Assert.assertEquals("javax.xml.ws.WebServiceException: Unsupported endpoint address: REPLACE_WITH_ACTUAL_URL", rootCause);
        }
    }

    @Test
    public void invokeOneWay() throws Exception {
        Element input = SOAPUtil.parseAsDom("<!--Comment --><test:helloWS xmlns:test=\"urn:switchyard-component-soap:test-ws:1.0\">"
                     + "   <arg0>Hello</arg0>"
                     + "</test:helloWS>").getDocumentElement();

        _consumerService11.operation("helloWS").sendInOnly(input);
    }

    @Test
    public void soapGatewayReferenceTimeout() throws Exception {
        Element input = SOAPUtil.parseAsDom("<test:sayHello xmlns:test=\"urn:switchyard-component-soap:test-ws:1.0\">"
                     + "   <arg0>Hello</arg0>"
                     + "</test:sayHello>").getDocumentElement();
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(8090), 10);
        httpServer.setExecutor(null); // creates a default executor
        httpServer.start();
        HttpContext httpContext = httpServer.createContext("/forever", new HttpHandler() {
            public void handle(HttpExchange exchange) {
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ie) {
                        //Ignore
            }}});
        try {
            Message responseMsg = _consumerService3.operation("sayHello").sendInOut(input);
        } catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            Assert.assertTrue(baos.toString().contains("SocketTimeoutException: Read timed out"));
        }
        httpServer.stop(0);
    }

    @Ignore // mime headers are not parsed into the SOAPMessage with CXF
    @Test
    public void invokeRequestResponse() throws Exception {
        String input = "<test:sayHello xmlns:test=\"urn:switchyard-component-soap:test-ws:1.0\">"
                     + "   <arg0>Magesh</arg0>"
                     + "</test:sayHello>";

        String output = "<test:sayHelloResponse xmlns:test=\"urn:switchyard-component-soap:test-ws:1.0\">"
                     + "   <return>Hello Magesh! The soapAction received is \"uri:something:that:needs#tobevalid\"</return>"
                     + "</test:sayHelloResponse>";


        Message responseMsg = _consumerService11.operation("sayHello").sendInOut(input);

        String response = toString(responseMsg.getContent(Node.class));
        XMLAssert.assertXMLEqual(output, response);
    }

    @Ignore // mime headers are not parsed into the SOAPMessage with CXF
    @Test
    public void invokeRequestResponseSOAP12() throws Exception {
        String input = "<test:sayHello xmlns:test=\"urn:switchyard-component-soap:test-ws:1.0\">"
                     + "   <arg0>Magesh</arg0>"
                     + "</test:sayHello>";

        String output = "<test:sayHelloResponse xmlns:test=\"urn:switchyard-component-soap:test-ws:1.0\">"
                     + "   <return>Hello Magesh! The soapAction received is application/soap+xml; charset=utf-8; action=\"uri:soap12:that:needs#tobevalid\"</return>"
                     + "</test:sayHelloResponse>";


        Message responseMsg = _consumerService12.operation("sayHello").sendInOut(input);

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

    @Ignore
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

        Message responseMsg = _consumerService11.operation("sayHello").sendInOut(input);
        String response = toString(responseMsg.getContent(Node.class));
        XMLAssert.assertXMLEqual(output, response);
    }

    @Ignore
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
                        +    "<faultcode xmlns:ns0=\"http://www.w3.org/2003/05/soap-envelope\">ns0:Fault</faultcode>"
                        +    "<faultstring>Send failed</faultstring>"
                        +    "<detail>"
                        +    faultString
                        +    "</detail>"
                        + "</SOAP-ENV:Fault>";

        Message responseMsg = _consumerService11.operation("sayHello").sendInOut(input);
        String response = toString(responseMsg.getContent(Node.class));
        XMLAssert.assertXMLEqual(output, response);
    }

    @Test
    public void invokeCustomFaultWithDetails() throws Exception {
        String faultString =  "<CustomFaultMessage>"
                              + "errorcode=1000;"
                              + "Invalid name: Looks like you did not specify a name!"
                              + "</CustomFaultMessage>";
        
        String input = "<test:sayHello xmlns:test=\"urn:switchyard-component-soap:test-ws:1.0\">"
                       + "   <arg0></arg0>"
                       + "</test:sayHello>";

        String faultStr = "SOAPFaultInfo [_actor=null, _codeAsQName={http://schemas.xmlsoap.org/soap/envelope/}Server.AppError, "
                            + "_reasonTexts={}, _role=null, _string=Invalid name, _stringLocale=null, _subcodes=[], _detail=[detail: null]]";

        MockHandler handler = new MockHandler();
        Exchange ex = _consumerService11.operation("sayHello").createExchange(handler);

        Message requestMsg = ex.createMessage().setContent(input);
        ex.send(requestMsg);
        handler.waitForFaultMessage();
        Exchange exchange = handler.getFaults().iterator().next();
        Property faultInfoProperty = exchange.getContext().getProperty(SOAPComposition.SOAP_FAULT_INFO);
        Assert.assertNotNull(faultInfoProperty);
        Assert.assertEquals(faultStr, faultInfoProperty.getValue().toString());
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
            output =  "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                     + "<SOAP-ENV:Header/>"
                     + "<SOAP-ENV:Body>"
                     + "   <test:sayHelloResponse xmlns:test=\"urn:switchyard-component-soap:test-ws:1.0\">"
                     + "      <return>Hello Thread " + i + "! The soapAction received is </return>"
                     + "   </test:sayHelloResponse>"
                     + "</SOAP-ENV:Body></SOAP-ENV:Envelope>";
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
