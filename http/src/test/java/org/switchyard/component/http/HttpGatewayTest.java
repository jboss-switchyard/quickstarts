/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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

package org.switchyard.component.http;

import java.util.HashSet;
import java.util.Set;
import javax.xml.namespace.QName;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.Message;
import org.switchyard.ServiceDomain;
import org.switchyard.component.http.config.model.HttpBindingModel;
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
import org.switchyard.test.InvocationFaultException;
import org.switchyard.test.Invoker;
import org.switchyard.test.MockHandler;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.mixins.HTTPMixIn;

/**
 * Contains tests for HTTP Gateway.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(mixins = HTTPMixIn.class)
public class HttpGatewayTest {

    private static final QName STRING_QNAME = new QName("java:java.lang.String");
    private static ModelPuller<CompositeModel> _puller;
    private ServiceDomain _domain;
    private HTTPMixIn httpMixIn;

    @org.switchyard.test.ServiceOperation("{urn:resteasy:test:1.0}SampleHttpConsumerService")
    private Invoker _consumerService;

    private HttpBindingModel _config;
    private HttpBindingModel _configRef;
    private InboundHandler _httpInbound;
    private OutboundHandler _httpOutbound;
    private final MockHandler mockService = new MockHandler().forwardInToOut();

    @Before
    public void setUp() throws Exception {
        _puller = new ModelPuller<CompositeModel>();
        CompositeModel composite = _puller.pull("/HelloSwitchYard.xml", getClass());
        composite.assertModelValid();

        CompositeServiceModel compositeService = composite.getServices().get(0);
        _config = (HttpBindingModel)compositeService.getBindings().get(0);

        // Massive hack for Test Runner. Register both a service and a reference binding.
        _domain.registerService(_config.getServiceName(), new InOutService(), mockService);
        _domain.registerServiceReference(_config.getServiceName(), new InOutService());
        _httpInbound = new InboundHandler(_config, _domain);
        _httpInbound.start();

        CompositeReferenceModel compositeReference = composite.getReferences().get(0);
        _configRef = (HttpBindingModel)compositeReference.getBindings().get(0);

        _httpOutbound = new OutboundHandler(_configRef);
        // Massive hack for Test Runner. Register both a service and a reference binding.
        _domain.registerService(_configRef.getServiceName(), new HelloInterface(), _httpOutbound);
        _domain.registerServiceReference(_configRef.getServiceName(), new HelloInterface());
        _httpOutbound.start();
    }

    @After
    public void tearDown() throws Exception {
        _httpInbound.stop();
        _httpOutbound.stop();
    }

    @Test
    public void httpGatewayServiceTest() throws Exception {
        String response = httpMixIn.sendString("http://localhost:8080/http", "magesh", HTTPMixIn.HTTP_POST);
        Assert.assertEquals(1, mockService.getMessages().size());
        Assert.assertEquals("magesh", response);
    }

    @Test
    public void httpGatewayReferenceTest() throws Exception {
        Message responseMsg = _consumerService.operation("sayHello").sendInOut("magesh");
        Assert.assertEquals("magesh", responseMsg.getContent(String.class));
    }

    private static class HelloInterface extends BaseService {
        private static Set<ServiceOperation> _operations = new HashSet<ServiceOperation>(1);
        static {
            _operations.add(new InOutOperation("sayHello", STRING_QNAME, STRING_QNAME));
        }
        public HelloInterface() {
            super(_operations);
        }
    }
}

