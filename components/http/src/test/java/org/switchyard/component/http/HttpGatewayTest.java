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

package org.switchyard.component.http;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jboss.com.sun.net.httpserver.HttpContext;
import org.jboss.com.sun.net.httpserver.HttpExchange;
import org.jboss.com.sun.net.httpserver.HttpHandler;
import org.jboss.com.sun.net.httpserver.HttpServer;
import org.switchyard.Exchange;
import org.switchyard.Message;
import org.switchyard.ServiceDomain;
import org.switchyard.component.http.composer.HttpRequestBindingData;
import org.switchyard.component.http.composer.HttpComposition;
import org.switchyard.component.http.composer.HttpContextMapper;
import org.switchyard.component.http.config.model.HttpBindingModel;
import org.switchyard.component.test.mixins.http.HTTPMixIn;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.config.model.composite.CompositeModel;
import org.switchyard.config.model.composite.CompositeReferenceModel;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.metadata.BaseService;
import org.switchyard.metadata.InOnlyOperation;
import org.switchyard.metadata.InOnlyService;
import org.switchyard.metadata.InOutOperation;
import org.switchyard.metadata.InOutService;
import org.switchyard.metadata.ServiceOperation;
import org.switchyard.test.Invoker;
import org.switchyard.test.MockHandler;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;

/**
 * Contains tests for HTTP Gateway.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(mixins = HTTPMixIn.class)
public class HttpGatewayTest {

    private static final String METHOD_NAME = "sayHello";
    private static final String INPUT = "magesh";
    private static final String OUTPUT = "response to " + INPUT;
    private static final QName STRING_QNAME = new QName("java:java.lang.String");
    private static ModelPuller<CompositeModel> _puller;
    private ServiceDomain _domain;
    private HTTPMixIn httpMixIn;

    @org.switchyard.test.ServiceOperation("{urn:http:test:1.1}SampleHttpConsumerService")
    private Invoker _consumerService;

    @org.switchyard.test.ServiceOperation("{urn:http:test:1.1}InvalidHttpConsumerService")
    private Invoker _consumerService2;

    @org.switchyard.test.ServiceOperation("{urn:http:test:1.1}AuthHttpConsumerService")
    private Invoker _consumerService3;

    @org.switchyard.test.ServiceOperation("{urn:http:test:1.1}NtlmHttpConsumerService")
    private Invoker _consumerService4;

    @org.switchyard.test.ServiceOperation("{urn:http:test:1.1}TimeoutHttpConsumerService")
    private Invoker _consumerService5;

    @org.switchyard.test.ServiceOperation("{urn:http:test:1.1}OneWayHttpConsumerService")
    private Invoker _inOnlyConsumerService;

    private InboundHandler _httpInbound;
    private InboundHandler _httpInbound2;
    private OutboundHandler _httpOutbound;
    private OutboundHandler _httpOutbound2;
    private OutboundHandler _httpOutbound3;
    private OutboundHandler _httpOutbound4;
    private OutboundHandler _httpOutbound5;
    private final MockHandler mockService = new MockHandler().forwardInToOut();

    @Before
    public void setUp() throws Exception {
        _puller = new ModelPuller<CompositeModel>();
        CompositeModel composite = _puller.pull("/HelloSwitchYard.xml", getClass());
        composite.assertModelValid();

        CompositeServiceModel compositeService = composite.getServices().get(0);
        HttpBindingModel config = (HttpBindingModel)compositeService.getBindings().get(0);

        // Massive hack for Test Runner. Register both a service and a reference binding.
        _domain.registerService(config.getServiceName(), new InOutService(), mockService);
        _domain.registerServiceReference(config.getServiceName(), new HelloInterface());
        _httpInbound = new InboundHandler(config, _domain);
        _httpInbound.start();

        compositeService = composite.getServices().get(1);
        HttpBindingModel config2 = (HttpBindingModel)compositeService.getBindings().get(0);

        _domain.registerService(config2.getServiceName(), new InOnlyService(), new MockHandler());
        _domain.registerServiceReference(config2.getServiceName(), new HelloInterface());
        _httpInbound2 = new InboundHandler(config2, _domain);
        _httpInbound2.start();

        CompositeReferenceModel compositeReference = composite.getReferences().get(0);
        HttpBindingModel configRef = (HttpBindingModel)compositeReference.getBindings().get(0);

        _httpOutbound = new OutboundHandler(configRef, null);
        _domain.registerService(configRef.getServiceName(), new HelloInterface(), _httpOutbound);
        _httpOutbound.start();

        compositeReference = composite.getReferences().get(1);
        HttpBindingModel configRef2 = (HttpBindingModel)compositeReference.getBindings().get(0);
        _httpOutbound2 = new OutboundHandler(configRef2, null);
        _domain.registerService(configRef2.getServiceName(), new HelloInterface(), _httpOutbound2);
        _httpOutbound2.start();

        compositeReference = composite.getReferences().get(2);
        HttpBindingModel configRef3 = (HttpBindingModel)compositeReference.getBindings().get(0);
        _httpOutbound3 = new OutboundHandler(configRef3, null);
        _domain.registerService(configRef3.getServiceName(), new HelloInterface(), _httpOutbound3);
        _httpOutbound3.start();

        compositeReference = composite.getReferences().get(3);
        HttpBindingModel configRef4 = (HttpBindingModel)compositeReference.getBindings().get(0);
        _httpOutbound4 = new OutboundHandler(configRef4, null);
        _domain.registerService(configRef4.getServiceName(), new HelloInterface(), _httpOutbound4);
        _httpOutbound4.start();

        compositeReference = composite.getReferences().get(4);
        HttpBindingModel configRef5 = (HttpBindingModel)compositeReference.getBindings().get(0);
        _httpOutbound5 = new OutboundHandler(configRef5, null);
        _domain.registerService(configRef5.getServiceName(), new HelloInterface(), _httpOutbound5);
        _httpOutbound5.start();
    }

    @After
    public void tearDown() throws Exception {
        _httpInbound.stop();
        _httpInbound2.stop();
        _httpOutbound.stop();
        _httpOutbound2.stop();
        _httpOutbound3.stop();
        _httpOutbound4.stop();
        _httpOutbound5.stop();
    }

    @Test
    public void httpGatewayServiceTest() throws Exception {
        String response = httpMixIn.sendString("http://localhost:8080/http", INPUT, HTTPMixIn.HTTP_POST);
        //Test CSR - Composing a Service Request
        Assert.assertEquals(String.class, mockService.getMessages().peek().getMessage().getContent().getClass());
        Assert.assertEquals(1, mockService.getMessages().size());
        Assert.assertEquals(INPUT, response);
    }

    @Test
    public void httpOneWayStatusTest() throws Exception {
        int status = httpMixIn.sendStringAndGetStatus("http://localhost:8080/oneway", INPUT, HTTPMixIn.HTTP_POST);
        Assert.assertEquals(202, status);
    }

    @Test
    public void httpGatewayReferenceTest() throws Exception {
        Message responseMsg = _consumerService.operation(METHOD_NAME).sendInOut(INPUT);
        //Test CRR - Composing an Reference Response
        Assert.assertEquals(String.class, responseMsg.getContent().getClass());
        Assert.assertEquals(INPUT, responseMsg.getContent());
    }

    @Test
    public void httpStatus() throws Exception {
        MockHandler handler = new MockHandler();
        Exchange ex = _consumerService.operation(METHOD_NAME).createExchange(handler);
        Message requestMsg = ex.createMessage().setContent(INPUT);
        requestMsg.getContext().setProperty("SomeRequestHeader", "BAR");
        ex.send(requestMsg);
        handler.waitForOKMessage();
        Assert.assertEquals(200, ex.getContext().getProperty(HttpContextMapper.HTTP_RESPONSE_STATUS).getValue());
    }

    @Test
    public void httpFault() throws Exception {
        MockHandler handler = new MockHandler();
        Exchange ex = _consumerService2.operation(METHOD_NAME).createExchange(handler);
        Message requestMsg = ex.createMessage().setContent(INPUT);
        ex.send(requestMsg);
        handler.waitForFaultMessage();
        Assert.assertEquals(404, ex.getContext().getProperty(HttpContextMapper.HTTP_RESPONSE_STATUS).getValue());
    }

    @Test
    public void httpTimeout() throws Exception {
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
            Message responseMsg = _consumerService5.operation(METHOD_NAME).sendInOut(INPUT);
        } catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            Assert.assertTrue(baos.toString().contains("SocketTimeoutException: Read timed out"));
        }
        httpServer.stop(0);
    }

    @Test
    public void authentication() throws Exception {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(8100), 10);
        httpServer.setExecutor(null); // creates a default executor
        httpServer.start();
        HttpContext context = httpServer.createContext("/basic-secured-endpoint", new StandaloneHandler());
        context.setAuthenticator(new HttpBasicAuthenticator());
        Message responseMsg = _consumerService3.operation(METHOD_NAME).sendInOut(INPUT);
        Assert.assertEquals(OUTPUT, responseMsg.getContent(String.class));
        httpServer.stop(0);
    }

    @Ignore // Exclusively for Magesh ;)
    @Test
    public void ntlmAuthentication() throws Exception {
        Message responseMsg = _consumerService4.operation(METHOD_NAME).sendInOut(INPUT);
        Assert.assertEquals(OUTPUT, responseMsg.getContent(String.class));
    }

    private static class HelloInterface extends BaseService {
        private static Set<ServiceOperation> _operations = new HashSet<ServiceOperation>(2);
        static {
            _operations.add(new InOutOperation(METHOD_NAME, STRING_QNAME, STRING_QNAME));
            _operations.add(new InOnlyOperation("oneWay", STRING_QNAME));
        }
        public HelloInterface() {
            super(_operations);
        }
    }

    private static class StandaloneHandler implements HttpHandler {

        public void handle(HttpExchange exchange) {
            try {
                HttpRequestBindingData httpRequest = new HttpRequestBindingData();
                httpRequest.setBodyFromStream(exchange.getRequestBody());
                Assert.assertEquals(INPUT, httpRequest.getBodyAsString());
                httpRequest.setBody(OUTPUT);
                exchange.sendResponseHeaders(200, httpRequest.getBodyBytes().available());
                httpRequest.writeBodyToStream(exchange.getResponseBody());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

