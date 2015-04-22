/*
 * Copyright Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
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
import java.io.PrintStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.Message;
import org.switchyard.ServiceDomain;
import org.switchyard.common.net.SocketAddr;
import org.switchyard.component.soap.config.model.NtlmAuthModel;
import org.switchyard.component.soap.config.model.SOAPBindingModel;
import org.switchyard.component.soap.config.model.SOAPNamespace;
import org.switchyard.component.soap.config.model.v1.V1NtlmAuthModel;
import org.switchyard.component.soap.config.model.v1.V1SOAPBindingModel;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.config.model.composite.CompositeModel;
import org.switchyard.config.model.composite.CompositeReferenceModel;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.config.model.composite.v1.V1CompositeReferenceModel;
import org.switchyard.metadata.BaseService;
import org.switchyard.metadata.InOutOperation;
import org.switchyard.metadata.ServiceOperation;
import org.switchyard.test.Invoker;
import org.switchyard.test.SwitchYardRunner;

/**
 * Contains tests for Http authentication support on SOAPGateway.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2013 Red Hat Inc.
 */
@RunWith(SwitchYardRunner.class)
public class AuthenticationTest {

    private static final String METHOD_NAME = "sayHello";

    private static final String input = "<test:sayHello xmlns:test=\"urn:switchyard-component-soap:test-ws:1.0\">"
                     + "   <arg0>Magesh</arg0>"
                     + "</test:sayHello>";
    private static final String output = "<test:sayHelloResponse xmlns:test=\"urn:switchyard-component-soap:test-ws:1.0\">"
                     + "   <return>Hello Magesh! The soapAction received is </return>"
                     + "</test:sayHelloResponse>";

    private ServiceDomain _domain;

    @org.switchyard.test.ServiceOperation("auth-required")
    private Invoker _authConsumerService1;
    @org.switchyard.test.ServiceOperation("all-is-well")
    private Invoker _authConsumerService2;

    private SOAPBindingModel _config;
    private InboundHandler _soapInbound;
    
    private static ModelPuller<CompositeModel> _puller;


    @Before
    public void setUp() throws Exception {
        String host = System.getProperty("org.switchyard.test.soap.host", "localhost");
        String port = System.getProperty("org.switchyard.test.soap.port", "8080");

        _puller = new ModelPuller<CompositeModel>();

        // Provide a switchyard service
        CompositeModel composite = _puller.pull("/HelloSwitchYard.xml", getClass());
        CompositeServiceModel compositeService = composite.getServices().get(0);
        _config = (SOAPBindingModel)compositeService.getBindings().get(0);
        _config.setSocketAddr(new SocketAddr(host, Integer.parseInt(port)));
        _domain.registerService(_config.getServiceName(), new HelloWebServiceInterface(), new SOAPProvider());
        _domain.registerServiceReference(_config.getServiceName(), new HelloWebServiceInterface());

        // Service exposed as WS
        _soapInbound = new InboundHandler(_config, _domain);
        _soapInbound.start();

        URL serviceURL = new URL("http://" + host + ":" + port + "/HelloWebService");

        SOAPBindingModel config = new V1SOAPBindingModel(SOAPNamespace.DEFAULT.uri()) {
            @Override
            public CompositeReferenceModel getReference() {
                return new V1CompositeReferenceModel();
            }
        };
        config.setWsdl(serviceURL.toExternalForm() + "?wsdl");
        config.setServiceName(_authConsumerService1.getServiceName());
        config.setName("auth-test");
        config.setEndpointAddress("http://192.168.169.4/index.htm");

        // Service consumer or Reference binding
        OutboundHandler soapProxyOutbound1 = new OutboundHandler(config);
        soapProxyOutbound1.start();
        _domain.registerService(_authConsumerService1.getServiceName(), new HelloWebServiceInterface(), soapProxyOutbound1);

        NtlmAuthModel auth = new V1NtlmAuthModel(SOAPNamespace.DEFAULT.uri());
        auth.setUser("SwitchYard");
        auth.setPassword("JBoss123!");
        auth.setDomain("JBOSS");
        config.setNtlmAuthConfig(auth);
        config.setServiceName(_authConsumerService2.getServiceName());

        // Service consumer or Reference binding
        OutboundHandler soapProxyOutbound2 = new OutboundHandler(config);
        soapProxyOutbound2.start();
        _domain.registerService(_authConsumerService2.getServiceName(), new HelloWebServiceInterface(), soapProxyOutbound2);
    }

    @After
    public void tearDown() throws Exception {
        _soapInbound.stop();
    }

    @Ignore // This can be tested only offline.
    @Test
    public void authenticationMissing() throws Exception {
        try {
            Message responseMsg = _authConsumerService1.operation(METHOD_NAME).sendInOut(input);
        } catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            Assert.assertTrue(baos.toString().contains("401: Unauthorized"));
        }
    }

    @Ignore // This can be tested only offline.
    @Test
    public void allIsWell() throws Exception {
        try {
            Message responseMsg = _authConsumerService2.operation(METHOD_NAME).sendInOut(input);
        } catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            // Because the local server is not setup with a SOAP Endpoint or to allow POST method.
            // If we get to this point then we are good.
            Assert.assertTrue(baos.toString().contains("405: Method Not Allowed"));
        }
    }

    private static class HelloWebServiceInterface extends BaseService {
        private static Set<ServiceOperation> _operations = new HashSet<ServiceOperation>(1);
        static {
            _operations.add(new InOutOperation(METHOD_NAME));
        }
        public HelloWebServiceInterface() {
            super(_operations);
        }
    }
}
