/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.littleshoot.proxy.DefaultHttpProxyServer;
import org.littleshoot.proxy.HttpFilter;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.ProxyAuthorizationHandler;
import org.switchyard.Exchange;
import org.switchyard.Message;
import org.switchyard.ServiceDomain;
import org.switchyard.component.resteasy.config.model.ProxyModel;
import org.switchyard.component.resteasy.config.model.RESTEasyBindingModel;
import org.switchyard.component.resteasy.config.model.v1.V1ProxyModel;
import org.switchyard.component.resteasy.config.model.v1.V1RESTEasyBindingModel;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.config.model.composite.CompositeModel;
import org.switchyard.config.model.composite.CompositeReferenceModel;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.config.model.composite.v1.V1CompositeReferenceModel;
import org.switchyard.metadata.BaseService;
import org.switchyard.metadata.InOnlyOperation;
import org.switchyard.metadata.InOutOperation;
import org.switchyard.metadata.ServiceOperation;
import org.switchyard.test.Invoker;
import org.switchyard.test.MockHandler;
import org.switchyard.test.SwitchYardRunner;

/**
 * Contains tests for Http proxy support on RESTEasy Gateway.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2013 Red Hat Inc.
 */
@RunWith(SwitchYardRunner.class)
public class HttpProxyTest {

    private static final QName STRING_QNAME = new QName("java:java.lang.String");
    private static final int PROXYPORT = 9090;
    private static final String PROXY_USER = "foo";
    private static final String PROXY_PWD = "bar";
    private static final String METHOD_NAME = "addGreeter";
    private static final String INPUT = "Beal";

    private ServiceDomain _domain;

    @org.switchyard.test.ServiceOperation("unknown-host")
    private Invoker _proxyConsumerService1;
    @org.switchyard.test.ServiceOperation("proxy-auth-required")
    private Invoker _proxyConsumerService2;
    @org.switchyard.test.ServiceOperation("all-is-well")
    private Invoker _proxyConsumerService3;

    private RESTEasyBindingModel _config;
    private InboundHandler _resteasyInbound;
    private OutboundHandler _resteasyProxyOutbound1;
    private OutboundHandler _resteasyProxyOutbound2;
    private OutboundHandler _resteasyProxyOutbound3;

    private static ModelPuller<CompositeModel> _puller;
    private HttpProxyServer _proxyServer;


    @Before
    public void setUp() throws Exception {
        String host = System.getProperty("org.switchyard.test.resteasy.host", "localhost");
        String port = System.getProperty("org.switchyard.test.resteasy.port", "8080");
        _proxyServer = new DefaultHttpProxyServer(PROXYPORT);
        ProxyAuthorizationHandler authorizationHandler = new ProxyAuthorizationHandler() {
            @Override
            public boolean authenticate(String user, String pwd) {
                return (PROXY_USER.equals(user) && PROXY_PWD.equals(pwd));
            }
        };
        _proxyServer.addProxyAuthenticationHandler(authorizationHandler);
        _proxyServer.start();

        _puller = new ModelPuller<CompositeModel>();

        // Provide a switchyard service
        CompositeModel composite = _puller.pull("/HelloSwitchYard.xml", getClass());
        CompositeServiceModel compositeService = composite.getServices().get(0);
        _config = (RESTEasyBindingModel)compositeService.getBindings().get(0);
        _domain.registerService(_config.getServiceName(), new HelloRESTEasyInterface(), new MockHandler().forwardInToOut());
        _domain.registerServiceReference(_config.getServiceName(), new HelloRESTEasyInterface());

        // Service exposed as WS
        _resteasyInbound = new InboundHandler(_config, _domain);
        _resteasyInbound.start();

        RESTEasyBindingModel config = new V1RESTEasyBindingModel() {
            @Override
            public CompositeReferenceModel getReference() {
                return new V1CompositeReferenceModel();
            }
        };
        config.setServiceName(_proxyConsumerService1.getServiceName());
        config.setName("proxy-test");
        config.setInterfaces("org.switchyard.component.resteasy.HelloResource");
        config.setAddress("http://unreachablehost");

        // Service consumer or Reference binding
        _resteasyProxyOutbound1 = new OutboundHandler(config, null);
        _resteasyProxyOutbound1.start();
        _domain.registerService(_proxyConsumerService1.getServiceName(), new HelloRESTEasyInterface(), _resteasyProxyOutbound1);

        ProxyModel proxy = new V1ProxyModel();
        proxy.setHost(host);
        proxy.setPort("" + PROXYPORT);
        config.setAddress("http://" + host + ":" + port);
        config.setProxyConfig(proxy);
        config.setServiceName(_proxyConsumerService2.getServiceName());

        // Service consumer or Reference binding
        _resteasyProxyOutbound2 = new OutboundHandler(config, null);
        _resteasyProxyOutbound2.start();
        _domain.registerService(_proxyConsumerService2.getServiceName(), new HelloRESTEasyInterface(), _resteasyProxyOutbound2);

        proxy.setUser(PROXY_USER);
        proxy.setPassword(PROXY_PWD);
        config.setProxyConfig(proxy);
        config.setServiceName(_proxyConsumerService3.getServiceName());

        // Service consumer or Reference binding
        _resteasyProxyOutbound3 = new OutboundHandler(config, null);
        _resteasyProxyOutbound3.start();
        _domain.registerService(_proxyConsumerService3.getServiceName(), new HelloRESTEasyInterface(), _resteasyProxyOutbound3);

    }
    
    @After
    public void tearDown() throws Exception {
        _resteasyInbound.stop();
        _resteasyProxyOutbound1.stop();
        _resteasyProxyOutbound2.stop();
        _resteasyProxyOutbound3.stop();
        _proxyServer.stop();
    }

    @Test
    public void unknownHost() throws Exception {
        try {
            Message responseMsg = _proxyConsumerService1.operation(METHOD_NAME).sendInOut(INPUT);
        } catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            Assert.assertTrue(baos.toString().contains("UnknownHostException: unreachablehost"));
        }
    }

    @Test
    public void authenticationMissing() throws Exception {
        try {
            Message responseMsg = _proxyConsumerService2.operation(METHOD_NAME).sendInOut(INPUT);
        } catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            Assert.assertTrue(baos.toString().contains("Error status 407 null returned"));
        }
    }

    @Ignore // Can be tested offline.
    @Test
    public void allIsWell() throws Exception {
        Message responseMsg = _proxyConsumerService3.operation(METHOD_NAME).sendInOut(INPUT);
        String response = responseMsg.getContent(String.class);
    }

    private static class HelloRESTEasyInterface extends BaseService {
        private static Set<ServiceOperation> _operations = new HashSet<ServiceOperation>(1);
        static {
            _operations.add(new InOutOperation(METHOD_NAME, STRING_QNAME, STRING_QNAME));
        }
        public HelloRESTEasyInterface() {
            super(_operations);
        }
    }
}
