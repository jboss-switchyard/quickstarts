/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.switchyard.component.resteasy.context;

import javax.ws.rs.core.MediaType;
import javax.xml.namespace.QName;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.HandlerException;
import org.switchyard.ServiceDomain;
import org.switchyard.component.resteasy.InboundHandler;
import org.switchyard.component.resteasy.OutboundHandler;
import org.switchyard.component.resteasy.config.model.RESTEasyBindingModel;
import org.switchyard.component.resteasy.resource.ResourcePublisherFactory;
import org.switchyard.component.test.mixins.http.HTTPMixIn;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.config.model.composite.CompositeModel;
import org.switchyard.config.model.composite.CompositeReferenceModel;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.deploy.ServiceDomainManager;
import org.switchyard.metadata.BaseService;
import org.switchyard.metadata.InOutOperation;
import org.switchyard.metadata.ServiceOperation;
import org.switchyard.test.InvocationFaultException;
import org.switchyard.test.Invoker;
import org.switchyard.test.MockHandler;

/**
 * Contains tests for RESTEasy context parameters.
 */
public class RESTEasyContextParamsTest {

    private static final QName STRING_QNAME = new QName("java:java.lang.String");
    private static final QName RESPONSE_QNAME = new QName("java:javax.ws.rs.core.Response");
    private static final String PAYLOAD = "payload";
    private static final String APP_ERROR = "App Error";
    private static final String OP_CREATE = "create";
    private static final String OP_CREATE_WITH_OWN_MEDIA_TYPE = "createWithOwnMediaType";

    private static ModelPuller<CompositeModel> _puller;

    private ServiceDomain _domain;
    private HTTPMixIn _httpMixIn = new HTTPMixIn();

    private Invoker _noBuiltinsConsumerService;
    private Invoker _consumerService;

    private RESTEasyBindingModel _config;
    private RESTEasyBindingModel _configRef;

    private InboundHandler _restInbound;
    private OutboundHandler _noBuiltinsRestOutbound;
    private OutboundHandler _restOutbound;

    private final MockHandler _mockService = new MockHandler().replyWithFault(APP_ERROR);

    private final static int PORT = 6080;

    @Before
    public void setUp() throws Exception {
        System.setProperty("org.switchyard.component.resteasy.standalone.port", PORT + "");

        _httpMixIn.initialize();
        _domain = new ServiceDomainManager().createDomain();
        _noBuiltinsConsumerService = new Invoker(_domain, QName.valueOf("{urn:resteasy:test:1.1}ContextParamNoBuiltinsTestReference"));
        _consumerService = new Invoker(_domain, QName.valueOf("{urn:resteasy:test:1.1}ContextParamTestReference"));

        _puller = new ModelPuller<CompositeModel>();
        CompositeModel composite = _puller.pull("/ContextParamsSwitchYard.xml", getClass());
        composite.assertModelValid();

        CompositeServiceModel compositeService = composite.getServices().get(0);
        _config = (RESTEasyBindingModel) compositeService.getBindings().get(0);

        // Massive hack for Test Runner. Register both a service and a reference binding.
        _domain.registerService(_config.getServiceName(), new RESTEasyContextParamsInterface(), _mockService);
        _domain.registerServiceReference(_config.getServiceName(), new RESTEasyContextParamsInterface());
        _restInbound = new InboundHandler(_config, _domain);

        CompositeReferenceModel compositeReference = composite.getReferences().get(0);
        _configRef = (RESTEasyBindingModel) compositeReference.getBindings().get(0);

        _noBuiltinsRestOutbound = new OutboundHandler(_configRef, null);
        // Massive hack for Test Runner. Register both a service and a reference binding.
        _domain.registerService(_configRef.getServiceName(), new RESTEasyContextParamsInterface(), _noBuiltinsRestOutbound);
        _domain.registerServiceReference(_configRef.getServiceName(), new RESTEasyContextParamsInterface());

        compositeReference = composite.getReferences().get(1);
        _configRef = (RESTEasyBindingModel) compositeReference.getBindings().get(0);
        _restOutbound = new OutboundHandler(_configRef, null);
        // Massive hack for Test Runner. Register both a service and a reference binding.
        _domain.registerService(_configRef.getServiceName(), new RESTEasyContextParamsInterface(), _restOutbound);
        _domain.registerServiceReference(_configRef.getServiceName(), new RESTEasyContextParamsInterface());

        _restInbound.start();
        _noBuiltinsRestOutbound.start();
        _restOutbound.start();
    }

    @After
    public void tearDown() throws Exception {
        _restInbound.stop();
        _noBuiltinsRestOutbound.stop();
        _restOutbound.stop();
    }

    @Test
    public void exceptionMapperProviderServiceTest() throws Exception {
        _httpMixIn.setContentType(MediaType.TEXT_PLAIN);
        String contextPath = "/cp/api";
        if (ResourcePublisherFactory.ignoreContext()) {
            contextPath = "";
        }
        String response = _httpMixIn.sendString("http://localhost:" + PORT + contextPath + "/context-params", PAYLOAD, HTTPMixIn.HTTP_POST);
        Assert.assertEquals(WebApplicationExceptionMapper.EM_PREFIX + APP_ERROR, response);
    }

    @Test
    public void restGatewayReferenceNoBuiltinsTest() throws Exception {
        String expectedException = "";
        try {
            _noBuiltinsConsumerService.operation(OP_CREATE).sendInOut(PAYLOAD);
        } catch (InvocationFaultException e) {
            expectedException = ((HandlerException) e.getFaultMessage().getContent()).getCause().getCause().getMessage();
        }
        Assert.assertEquals("could not find writer for content-type text/plain type: java.lang.String", expectedException);
    }

    @Test
    public void messageBodyWriterProviderReferenceTest() throws Exception {
        _consumerService.operation(OP_CREATE_WITH_OWN_MEDIA_TYPE).sendInOut(PAYLOAD);
        Assert.assertTrue(SyTxtMessageBodyRW.isInvoked());
    }

    private static class RESTEasyContextParamsInterface extends BaseService {

        private static Set<ServiceOperation> _operations = new HashSet<ServiceOperation>(1);

        static {
            _operations.add(new InOutOperation(OP_CREATE, STRING_QNAME, RESPONSE_QNAME));
            _operations.add(new InOutOperation(OP_CREATE_WITH_OWN_MEDIA_TYPE, STRING_QNAME, RESPONSE_QNAME));
        }

        public RESTEasyContextParamsInterface() {
            super(_operations);
        }
    }
}
