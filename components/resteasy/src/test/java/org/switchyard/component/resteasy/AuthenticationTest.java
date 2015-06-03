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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
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
import org.switchyard.component.resteasy.config.model.NtlmAuthModel;
import org.switchyard.component.resteasy.config.model.RESTEasyBindingModel;
import org.switchyard.component.resteasy.config.model.RESTEasyNamespace;
import org.switchyard.component.resteasy.config.model.v1.V1NtlmAuthModel;
import org.switchyard.component.resteasy.config.model.v1.V1RESTEasyBindingModel;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.config.model.composite.CompositeModel;
import org.switchyard.config.model.composite.CompositeReferenceModel;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.config.model.composite.v1.V1CompositeReferenceModel;
import org.switchyard.metadata.BaseService;
import org.switchyard.metadata.InOutOperation;
import org.switchyard.metadata.ServiceOperation;
import org.switchyard.test.Invoker;
import org.switchyard.test.MockHandler;
import org.switchyard.test.SwitchYardRunner;

/**
 * Contains tests for Http authentication support on RESTEasyGateway.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2013 Red Hat Inc.
 */
@RunWith(SwitchYardRunner.class)
public class AuthenticationTest {

    private static final String METHOD_NAME = "addGreeter";

    private static final String input = "Magesh";
    private static final String output = "";

    private ServiceDomain _domain;

    @org.switchyard.test.ServiceOperation("auth-required")
    private Invoker _authConsumerService1;
    @org.switchyard.test.ServiceOperation("all-is-well")
    private Invoker _authConsumerService2;

    private RESTEasyBindingModel _config;
    private InboundHandler _resteasyInbound;
    
    private static ModelPuller<CompositeModel> _puller;


    @Before
    public void setUp() throws Exception {
        String host = System.getProperty("org.switchyard.test.resteasy.host", "localhost");
        String port = System.getProperty("org.switchyard.test.resteasy.port", "8080");

        _puller = new ModelPuller<CompositeModel>();

        // Provide a switchyard service
        CompositeModel composite = _puller.pull("/HelloSwitchYard.xml", getClass());
        CompositeServiceModel compositeService = composite.getServices().get(0);
        _config = (RESTEasyBindingModel)compositeService.getBindings().get(0);
        _domain.registerService(_config.getServiceName(), new HelloWebServiceInterface(), new MockHandler().forwardInToOut());
        _domain.registerServiceReference(_config.getServiceName(), new HelloWebServiceInterface());

        // Service exposed as WS
        _resteasyInbound = new InboundHandler(_config, _domain);
        _resteasyInbound.start();

        RESTEasyBindingModel config = new V1RESTEasyBindingModel(RESTEasyNamespace.DEFAULT.uri()) {
            @Override
            public CompositeReferenceModel getReference() {
                return new V1CompositeReferenceModel();
            }
        };
        config.setServiceName(_authConsumerService1.getServiceName());
        config.setName("auth-test");
        config.setInterfaces("org.switchyard.component.resteasy.HelloResource");
        config.setAddress("http://192.168.169.4/");

        // Service consumer or Reference binding
        OutboundHandler resteasyProxyOutbound1 = new OutboundHandler(config, null);
        resteasyProxyOutbound1.start();
        _domain.registerService(_authConsumerService1.getServiceName(), new HelloWebServiceInterface(), resteasyProxyOutbound1);

        NtlmAuthModel auth = new V1NtlmAuthModel(RESTEasyNamespace.DEFAULT.uri());
        auth.setUser("SwitchYard");
        auth.setPassword("JBoss123!");
        auth.setDomain("JBOSS");
        config.setNtlmAuthConfig(auth);
        config.setServiceName(_authConsumerService2.getServiceName());

        // Service consumer or Reference binding
        OutboundHandler resteasyProxyOutbound2 = new OutboundHandler(config, null);
        resteasyProxyOutbound2.start();
        _domain.registerService(_authConsumerService2.getServiceName(), new HelloWebServiceInterface(), resteasyProxyOutbound2);
    }

    @After
    public void tearDown() throws Exception {
        _resteasyInbound.stop();
    }

    @Ignore // This can be tested only offline.
    @Test
    public void authenticationMissing() throws Exception {
        try {
            Message responseMsg = _authConsumerService1.operation(METHOD_NAME).sendInOut(input);
        } catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            Assert.assertTrue(baos.toString().contains("401 Unauthorized"));
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
            Assert.assertTrue(baos.toString().contains("Error status 501"));
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
