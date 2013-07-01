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
package org.switchyard.component.http;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.littleshoot.proxy.DefaultHttpProxyServer;
import org.littleshoot.proxy.HttpFilter;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.ProxyAuthorizationHandler;
import org.switchyard.Exchange;
import org.switchyard.Message;
import org.switchyard.ServiceDomain;
import org.switchyard.component.http.config.model.ProxyModel;
import org.switchyard.component.http.config.model.HttpBindingModel;
import org.switchyard.component.http.config.model.v1.V1ProxyModel;
import org.switchyard.component.http.config.model.v1.V1HttpBindingModel;
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
 * Contains tests for Http proxy support on HTTP Gateway.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2013 Red Hat Inc.
 */
@RunWith(SwitchYardRunner.class)
public class HttpProxyTest {

    private static final int PROXYPORT = 9090;
    private static final String PROXY_USER = "foo";
    private static final String PROXY_PWD = "bar";
    private static final String METHOD_NAME = "sayHello";
    private static final String INPUT = "Beal Conjecture";

    private ServiceDomain _domain;

    @org.switchyard.test.ServiceOperation("unknown-host")
    private Invoker _proxyConsumerService1;
    @org.switchyard.test.ServiceOperation("proxy-auth-required")
    private Invoker _proxyConsumerService2;
    @org.switchyard.test.ServiceOperation("all-is-well")
    private Invoker _proxyConsumerService3;

    private HttpBindingModel _config;
    private InboundHandler _httpInbound;
    
    private static ModelPuller<CompositeModel> _puller;
    private HttpProxyServer _proxyServer;


    @Before
    public void setUp() throws Exception {
        String host = System.getProperty("org.switchyard.test.http.host", "localhost");
        String port = System.getProperty("org.switchyard.test.http.port", "8080");
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
        _config = (HttpBindingModel)compositeService.getBindings().get(0);
        _domain.registerService(_config.getServiceName(), new HelloWebServiceInterface(), new MockHandler().forwardInToOut());
        _domain.registerServiceReference(_config.getServiceName(), new HelloWebServiceInterface());

        // Service exposed as WS
        _httpInbound = new InboundHandler(_config, _domain);
        _httpInbound.start();

        HttpBindingModel config = new V1HttpBindingModel() {
            @Override
            public CompositeReferenceModel getReference() {
                return new V1CompositeReferenceModel();
            }
        };
        config.setServiceName(_proxyConsumerService1.getServiceName());
        config.setName("proxy-test");
        config.setMethod("PUT");
        config.setAddress("http://unreachablehost/http");

        // Service consumer or Reference binding
        OutboundHandler httpProxyOutbound1 = new OutboundHandler(config, null);
        httpProxyOutbound1.start();
        _domain.registerService(_proxyConsumerService1.getServiceName(), new HelloWebServiceInterface(), httpProxyOutbound1);

        ProxyModel proxy = new V1ProxyModel();
        proxy.setHost(host);
        proxy.setPort("" + PROXYPORT);
        config.setProxyConfig(proxy);
        config.setAddress("http://" + host + ":" + port + "/http");
        config.setServiceName(_proxyConsumerService2.getServiceName());

        // Service consumer or Reference binding
        OutboundHandler httpProxyOutbound2 = new OutboundHandler(config, null);
        httpProxyOutbound2.start();
        _domain.registerService(_proxyConsumerService2.getServiceName(), new HelloWebServiceInterface(), httpProxyOutbound2);

        proxy.setUser(PROXY_USER);
        proxy.setPassword(PROXY_PWD);
        config.setProxyConfig(proxy);
        config.setServiceName(_proxyConsumerService3.getServiceName());

        // Service consumer or Reference binding
        OutboundHandler httpProxyOutbound3 = new OutboundHandler(config, null);
        httpProxyOutbound3.start();
        _domain.registerService(_proxyConsumerService3.getServiceName(), new HelloWebServiceInterface(), httpProxyOutbound3);

    }

    @After
    public void tearDown() throws Exception {
        _httpInbound.stop();
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
        MockHandler handler = new MockHandler();
        Exchange ex = _proxyConsumerService2.operation(METHOD_NAME).createExchange(handler);
        Message requestMsg = ex.createMessage().setContent(INPUT);
        ex.send(requestMsg);
        handler.waitForFaultMessage();
        String response = ex.getMessage().getContent(String.class);
        Assert.assertTrue(response.contains("407 Proxy Authentication Required"));
    }

    @Test
    public void allIsWell() throws Exception {
        Message responseMsg = _proxyConsumerService3.operation(METHOD_NAME).sendInOut(INPUT);
        Assert.assertEquals(INPUT, responseMsg.getContent(String.class));
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
