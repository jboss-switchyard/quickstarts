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

package org.switchyard.component.resteasy;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.Message;
import org.switchyard.ServiceDomain;
import org.switchyard.component.resteasy.config.model.RESTEasyBindingModel;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.config.model.composite.CompositeModel;
import org.switchyard.config.model.composite.CompositeReferenceModel;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.deploy.ServiceDomainManager;
import org.switchyard.metadata.BaseService;
import org.switchyard.metadata.InOutOperation;
import org.switchyard.metadata.ServiceOperation;
import org.switchyard.test.Invoker;
import org.switchyard.test.MockHandler;
import org.switchyard.component.test.mixins.http.HTTPMixIn;

/**
 * Contains tests for RESTEasy Gateway.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class RESTEasyGatewayTest {

    private static final QName STRING_QNAME = new QName("java:java.lang.String");
    private static final QName RESPONSE_QNAME = new QName("javax.ws.rs.core.Response");
    private static ModelPuller<CompositeModel> _puller;
    private ServiceDomain _domain;
    private HTTPMixIn _httpMixIn = new HTTPMixIn();

    private Invoker _consumerService;
    private Invoker _consumerService2;

    private RESTEasyBindingModel _config;
    private RESTEasyBindingModel _configRef;
    private InboundHandler _restInbound;
    private OutboundHandler _restOutbound;
    private OutboundHandler _restOutbound2;
    private final MockHandler mockService = new MockHandler().forwardInToOut();

    @Before
    public void setUp() throws Exception {
        _httpMixIn.initialize();
        _domain = new ServiceDomainManager().createDomain();
        _consumerService = new Invoker(_domain, QName.valueOf("{urn:resteasy:test:1.0}SampleRESTEasyConsumerService"));
        _consumerService2 = new Invoker(_domain, QName.valueOf("{urn:resteasy:test:1.0}TimeoutConsumerService"));

        _puller = new ModelPuller<CompositeModel>();
        CompositeModel composite = _puller.pull("/HelloSwitchYard.xml", getClass());
        composite.assertModelValid();

        CompositeServiceModel compositeService = composite.getServices().get(0);
        _config = (RESTEasyBindingModel)compositeService.getBindings().get(0);

        // Massive hack for Test Runner. Register both a service and a reference binding.

        _domain.registerService(_config.getServiceName(), new HelloRESTEasyInterface(), mockService);
        _domain.registerServiceReference(_config.getServiceName(), new HelloRESTEasyInterface());
        _restInbound = new InboundHandler(_config, _domain);

        CompositeReferenceModel compositeReference = composite.getReferences().get(0);
        _configRef = (RESTEasyBindingModel)compositeReference.getBindings().get(0);

        _restOutbound = new OutboundHandler(_configRef, null);
        // Massive hack for Test Runner. Register both a service and a reference binding.
        _domain.registerService(_configRef.getServiceName(), new HelloRESTEasyInterface(), _restOutbound);
        _domain.registerServiceReference(_configRef.getServiceName(), new HelloRESTEasyInterface());

        _configRef = (RESTEasyBindingModel)composite.getReferences().get(1).getBindings().get(0);
        _restOutbound2 = new OutboundHandler(_configRef, null);
        // Massive hack for Test Runner. Register both a service and a reference binding.
        _domain.registerService(_configRef.getServiceName(), new HelloRESTEasyInterface(), _restOutbound2);
        _domain.registerServiceReference(_configRef.getServiceName(), new HelloRESTEasyInterface());

        _restInbound.start();
        _restOutbound.start();
        _restOutbound2.start();
    }

    @After
    public void tearDown() throws Exception {
        _restInbound.stop();
        _restOutbound.stop();
        _restOutbound2.stop();
    }

    @Test
    public void restGatewayServiceTest() throws Exception {
        _httpMixIn.setContentType("text/plain");
        String response = _httpMixIn.sendString("http://localhost:8080/greeters/magesh", "", HTTPMixIn.HTTP_PUT);
        Assert.assertEquals(1, mockService.getMessages().size());
        Assert.assertEquals("magesh", response);
        response = _httpMixIn.sendString("http://localhost:8080/greeters/keith", "", HTTPMixIn.HTTP_GET);
        Assert.assertEquals(2, mockService.getMessages().size());
        Assert.assertEquals("keith", response);
        response = _httpMixIn.sendString("http://localhost:8080/greeters/magesh", "keith", HTTPMixIn.HTTP_POST);
        Assert.assertEquals(3, mockService.getMessages().size());
        Assert.assertEquals("keith", response);
        response = _httpMixIn.sendString("http://localhost:8080/greeters/response", "", HTTPMixIn.HTTP_GET);
        Assert.assertEquals(4, mockService.getMessages().size());
    }

    @Test
    public void restGatewayReferenceTest() throws Exception {
        Message responseMsg = _consumerService.operation("addGreeter").sendInOut("magesh");
        Assert.assertEquals("magesh", responseMsg.getContent(String.class));
        responseMsg = _consumerService.operation("greeterInfo").sendInOut("keith");
        Assert.assertEquals("keith", responseMsg.getContent(String.class));

        // This cannot be tested by design of SwitchYard, as it does not allow multiple parameters
        /*responseMsg = _consumerService.operation("sayHello").sendInOut("keith");
        Assert.assertEquals("keith", responseMsg.getContent(String.class));*/
    }

    @Test
    public void restGatewayReferenceTimeout() throws Exception {
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
            Message responseMsg = _consumerService2.operation("addGreeter").sendInOut("magesh");
        } catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            Assert.assertTrue(baos.toString().contains("SocketTimeoutException: Read timed out"));
        }
        httpServer.stop(0);
    }

    private static class HelloRESTEasyInterface extends BaseService {
        private static Set<ServiceOperation> _operations = new HashSet<ServiceOperation>(3);
        static {
            _operations.add(new InOutOperation("addGreeter", STRING_QNAME, STRING_QNAME));
            _operations.add(new InOutOperation("greeterInfo", STRING_QNAME, STRING_QNAME));
            _operations.add(new InOutOperation("sayHello", STRING_QNAME, STRING_QNAME));
            _operations.add(new InOutOperation("testResponse", STRING_QNAME, RESPONSE_QNAME));
        }
        public HelloRESTEasyInterface() {
            super(_operations);
        }
    }
}

