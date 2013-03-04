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

package org.switchyard.component.resteasy;

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
    private static ModelPuller<CompositeModel> _puller;
    private ServiceDomain _domain;
    private HTTPMixIn _httpMixIn = new HTTPMixIn();
    
    private Invoker _consumerService;

    private RESTEasyBindingModel _config;
    private RESTEasyBindingModel _configRef;
    private InboundHandler _restInbound;
    private OutboundHandler _restOutbound;
    private final MockHandler mockService = new MockHandler().forwardInToOut();

    @Before
    public void setUp() throws Exception {
        _httpMixIn.initialize();
        _domain = new ServiceDomainManager().createDomain();
        _consumerService = new Invoker(_domain, QName.valueOf("{urn:resteasy:test:1.0}SampleRESTEasyConsumerService"));
        
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

        _restOutbound = new OutboundHandler(_configRef);
        // Massive hack for Test Runner. Register both a service and a reference binding.
        _domain.registerService(_configRef.getServiceName(), new HelloRESTEasyInterface(), _restOutbound);
        _domain.registerServiceReference(_configRef.getServiceName(), new HelloRESTEasyInterface());

        _restInbound.start();
        _restOutbound.start();
    }

    @After
    public void tearDown() throws Exception {
        _restInbound.stop();
        _restOutbound.stop();
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

    private static class HelloRESTEasyInterface extends BaseService {
        private static Set<ServiceOperation> _operations = new HashSet<ServiceOperation>(3);
        static {
            _operations.add(new InOutOperation("addGreeter", STRING_QNAME, STRING_QNAME));
            _operations.add(new InOutOperation("greeterInfo", STRING_QNAME, STRING_QNAME));
            _operations.add(new InOutOperation("sayHello", STRING_QNAME, STRING_QNAME));
        }
        public HelloRESTEasyInterface() {
            super(_operations);
        }
    }
}

