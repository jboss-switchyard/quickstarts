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

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.littleshoot.proxy.DefaultHttpProxyServer;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.ProxyAuthorizationHandler;
import org.switchyard.Message;
import org.switchyard.ServiceDomain;
import org.switchyard.common.net.SocketAddr;
import org.switchyard.common.xml.XMLHelper;
import org.switchyard.component.soap.config.model.ProxyModel;
import org.switchyard.component.soap.config.model.SOAPBindingModel;
import org.switchyard.component.soap.config.model.SOAPNamespace;
import org.switchyard.component.soap.config.model.v1.V1ProxyModel;
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
import org.w3c.dom.Node;

/**
 * Contains tests for Http proxy support on SOAPGateway. Thanks to alessio.soldano@jboss.com!
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2013 Red Hat Inc.
 */
@RunWith(SwitchYardRunner.class)
public class HttpProxyTest {

    private static final int PROXYPORT = 9090;
    private static final String PROXY_USER = "foo";
    private static final String PROXY_PWD = "bar";
    private static final String METHOD_NAME = "sayHello";

    private static final String input = "<test:sayHello xmlns:test=\"urn:switchyard-component-soap:test-ws:1.0\">"
                     + "   <arg0>Magesh</arg0>"
                     + "</test:sayHello>";
    private static final String output = "<test:sayHelloResponse xmlns:test=\"urn:switchyard-component-soap:test-ws:1.0\">"
                     + "   <return>Hello Magesh! The soapAction received is </return>"
                     + "</test:sayHelloResponse>";

    private ServiceDomain _domain;

    @org.switchyard.test.ServiceOperation("unknown-host")
    private Invoker _proxyConsumerService1;
    @org.switchyard.test.ServiceOperation("proxy-auth-required")
    private Invoker _proxyConsumerService2;
    @org.switchyard.test.ServiceOperation("all-is-well")
    private Invoker _proxyConsumerService3;

    private SOAPBindingModel _config;
    private InboundHandler _soapInbound;
    
    private static ModelPuller<CompositeModel> _puller;
    private HttpProxyServer _proxyServer;


    @Before
    public void setUp() throws Exception {
        String host = System.getProperty("org.switchyard.test.soap.host", "localhost");
        String port = System.getProperty("org.switchyard.test.soap.port", "8080");
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
        config.setServiceName(_proxyConsumerService1.getServiceName());
        config.setName("proxy-test");
        config.setEndpointAddress("http://unreachablehost/HelloWebService");

        // Service consumer or Reference binding
        OutboundHandler soapProxyOutbound1 = new OutboundHandler(config);
        soapProxyOutbound1.start();
        _domain.registerService(_proxyConsumerService1.getServiceName(), new HelloWebServiceInterface(), soapProxyOutbound1);

        ProxyModel proxy = new V1ProxyModel(SOAPNamespace.DEFAULT.uri());
        proxy.setHost(host);
        proxy.setPort("" + PROXYPORT);
        config.setProxyConfig(proxy);
        config.setEndpointAddress(serviceURL.toExternalForm());
        config.setServiceName(_proxyConsumerService2.getServiceName());

        // Service consumer or Reference binding
        OutboundHandler soapProxyOutbound2 = new OutboundHandler(config);
        soapProxyOutbound2.start();
        _domain.registerService(_proxyConsumerService2.getServiceName(), new HelloWebServiceInterface(), soapProxyOutbound2);

        proxy.setUser(PROXY_USER);
        proxy.setPassword(PROXY_PWD);
        config.setProxyConfig(proxy);
        config.setServiceName(_proxyConsumerService3.getServiceName());

        // Service consumer or Reference binding
        OutboundHandler soapProxyOutbound3 = new OutboundHandler(config);
        soapProxyOutbound3.start();
        _domain.registerService(_proxyConsumerService3.getServiceName(), new HelloWebServiceInterface(), soapProxyOutbound3);

        XMLUnit.setIgnoreWhitespace(true);
    }
    
    @After
    public void tearDown() throws Exception {
        _soapInbound.stop();
        _proxyServer.stop();
    }

    @Ignore // Can be tested offline
    @Test
    public void unknownHost() throws Exception {
        try {
            Message responseMsg = _proxyConsumerService1.operation(METHOD_NAME).sendInOut(input);
        } catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            Assert.assertTrue(baos.toString().contains("UnknownHostException: unreachablehost"));
        }
    }

    @Test
    public void authenticationMissing() throws Exception {
        try {
            Message responseMsg = _proxyConsumerService2.operation(METHOD_NAME).sendInOut(input);
        } catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            Assert.assertTrue(baos.toString().contains("407: Proxy Authentication Required"));
        }
    }

    @Test
    public void allIsWell() throws Exception {
        Message responseMsg = _proxyConsumerService3.operation(METHOD_NAME).sendInOut(input);
        String response = XMLHelper.toString(responseMsg.getContent(Node.class));
        XMLAssert.assertXMLEqual(output, response);
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
