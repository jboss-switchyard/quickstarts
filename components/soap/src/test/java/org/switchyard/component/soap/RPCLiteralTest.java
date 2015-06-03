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
package org.switchyard.component.soap;

import java.io.StringWriter;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.custommonkey.xmlunit.XMLUnit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.Message;
import org.switchyard.ServiceDomain;
import org.switchyard.common.net.SocketAddr;
import org.switchyard.component.soap.config.model.SOAPBindingModel;
import org.switchyard.component.soap.config.model.SOAPNamespace;
import org.switchyard.component.soap.config.model.v1.V1SOAPBindingModel;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.config.model.composite.CompositeModel;
import org.switchyard.config.model.composite.CompositeReferenceModel;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.config.model.composite.v1.V1CompositeReferenceModel;
import org.switchyard.deploy.ServiceDomainManager;
import org.switchyard.metadata.BaseService;
import org.switchyard.metadata.InOutOperation;
import org.switchyard.metadata.ServiceOperation;
import org.switchyard.test.Invoker;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestKit;
import org.w3c.dom.Node;

/**
 * Tests for rpc literal.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
@RunWith(SwitchYardRunner.class)
public class RPCLiteralTest {

    private SwitchYardTestKit _testKit;
    
    private ServiceDomain _domain = new ServiceDomainManager().createDomain();
    private SOAPBindingModel _config;
    private static URL _serviceURL;
    private InboundHandler _soapInbound;
    private OutboundHandler _soapOutbound;
    
    @org.switchyard.test.ServiceOperation("webservice-consumer")
    private Invoker consumerService;

    private static ModelPuller<CompositeModel> _puller;
    
    @Before
    public void setUp() throws Exception {
        
        // Provide a switchyard service
        RPClitSOAPProvider provider = new RPClitSOAPProvider();

        String host = System.getProperty("org.switchyard.test.soap.host", "localhost");
        String port = System.getProperty("org.switchyard.test.soap.port", "48080");

        _puller = new ModelPuller<CompositeModel>();
        CompositeModel composite = _puller.pull("/RPClitSwitchyard.xml", getClass());
        
        CompositeServiceModel compositeService = composite.getServices().get(0);
        _config = (SOAPBindingModel)compositeService.getBindings().get(0);

        _domain.registerService(_config.getServiceName(), new OrderServiceInterface(), provider);
        _domain.registerServiceReference(_config.getServiceName(), new OrderServiceInterface());
        
        _config.setSocketAddr(new SocketAddr(host, Integer.parseInt(port)));
        
        _soapInbound = new InboundHandler(_config, _domain);

        _soapInbound.start();

        _serviceURL = new URL("http://" + host + ":" + port + "/OrderServiceRPC");

        // A WS Consumer as Service
        SOAPBindingModel config2 = new V1SOAPBindingModel(SOAPNamespace.DEFAULT.uri()) {
            @Override
            public CompositeReferenceModel getReference() {
                return new V1CompositeReferenceModel();
            }
        };
        config2.setWsdl(_serviceURL.toExternalForm() + "?wsdl");
        config2.setServiceName(consumerService.getServiceName());
        config2.setName("testGateway");
        _soapOutbound = new OutboundHandler(config2);
        _soapOutbound.start();
        _domain.registerService(consumerService.getServiceName(), new OrderServiceInterface(), _soapOutbound);
        
        XMLUnit.setIgnoreWhitespace(true);
    }

    @After
    public void tearDown() throws Exception {
        // NOOP
    }

    @Test
    public void sendMessage() throws Exception {
        
        DOMSource domSource = new DOMSource(_testKit.readResourceDocument("/rpclit_request.xml"));
        Message responseMsg = consumerService.operation("submitPhantomOrder").sendInOut(toString(domSource.getNode()));
        String response = toString(responseMsg.getContent(Node.class));
        _testKit.compareXMLToResource(response, "/rpclit_response.xml");
        
    }
    
    private static class OrderServiceInterface extends BaseService {
        private static Set<ServiceOperation> _operations = new HashSet<ServiceOperation>(2);
        static {
            _operations.add(new InOutOperation("submitPhantomOrder"));
        }
        public OrderServiceInterface() {
            super(_operations);
        }
    }
    
    private String toString(Node node) throws Exception
    {
        TransformerFactory transFactory = TransformerFactory.newInstance();
        Transformer transformer = transFactory.newTransformer();
        StringWriter sw = new StringWriter();
        DOMSource source = new DOMSource(node);
        StreamResult result = new StreamResult(sw);
        transformer.transform(source, result);
        return sw.toString();
    }
    
}
