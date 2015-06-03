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

import java.util.HashSet;
import java.util.Set;

import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;

import org.junit.Assert;
import org.junit.Test;
import org.switchyard.component.soap.util.WSDLUtil;
import org.switchyard.metadata.BaseService;
import org.switchyard.metadata.InOutOperation;
import org.switchyard.metadata.ServiceOperation;

/**
 * Tests for SOAPGateway's wsPort parameter.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
public class WSDLUtilTest {

    @Test
    public void fullyQualifiedPortName() throws Exception {
        PortName portName = new PortName("{urn:switchyard-component-soap:test-ws:1.0}GoodbyeWebService:GoodbyeWebServicePort");
        Service service = WSDLUtil.getService("MultiplePortService.wsdl", portName);
        Assert.assertNotNull(service);
        Assert.assertEquals(service.getQName(), new QName("urn:switchyard-component-soap:test-ws:1.0", "GoodbyeWebService"));
        Port port = WSDLUtil.getPort(service, portName);
        Assert.assertNotNull(port);
        Assert.assertEquals(port.getName(), "GoodbyeWebServicePort");
    }
    
    @Test
    public void halfQualifiedPortName() throws Exception {
        PortName portName = new PortName("HelloWebService:HelloWebServicePort");
        Service service = WSDLUtil.getService("MultiplePortService.wsdl", portName);
        Assert.assertNotNull(service);
        Assert.assertEquals(service.getQName(), new QName("urn:switchyard-component-soap:test-ws:1.0", "HelloWebService"));
        Port port = WSDLUtil.getPort(service, portName);
        Assert.assertNotNull(port);
        Assert.assertEquals(port.getName(), "HelloWebServicePort");
    }
    
    @Test
    public void nonQualifiedPortName() throws Exception {
        PortName portName = new PortName("HelloWebServicePortFrench");
        Service service = WSDLUtil.getService("MultiplePortService.wsdl", portName);
        Assert.assertNotNull(service);
        service = WSDLUtil.getService("MultiplePortService.wsdl", new PortName("HelloWebService:"));
        Assert.assertNotNull(service);
        Assert.assertEquals(service.getQName(), new QName("urn:switchyard-component-soap:test-ws:1.0", "HelloWebService"));
        Port port = WSDLUtil.getPort(service, portName);
        Assert.assertEquals(port.getName(), "HelloWebServicePortFrench");
    }
    
    @Test
    public void nullPortName() throws Exception {
        Service service = WSDLUtil.getService("MultiplePortService.wsdl", new PortName(null));
        Assert.assertNotNull(service);
        Port port = WSDLUtil.getPort(service, new PortName(null));
        Assert.assertNotNull(port);
        service = WSDLUtil.getService("MultiplePortService.wsdl", new PortName("HelloWebService:"));
        Assert.assertNotNull(service);
        Assert.assertEquals(service.getQName(), new QName("urn:switchyard-component-soap:test-ws:1.0", "HelloWebService"));
        port = WSDLUtil.getPort(service, new PortName(null));
        Assert.assertEquals(port.getName(), "HelloWebServicePortFrench");
    }
    
    @Test(expected=WSDLException.class)
    public void nonExistentService() throws Exception {
        WSDLUtil.getService("MultiplePortService.wsdl", new PortName("NonexistentWebService:"));
    }
    
    @Test(expected=WSDLException.class)
    public void nonExistentPortName() throws Exception {
        Service service = WSDLUtil.getService("MultiplePortService.wsdl", new PortName("HelloWebService:"));
        Assert.assertNotNull(service);
        Assert.assertEquals(service.getQName(), new QName("urn:switchyard-component-soap:test-ws:1.0", "HelloWebService"));
        WSDLUtil.getPort(service, new PortName("HelloWebServiceSpanishPort"));
    }

    @Test
    public void soapAction() throws Exception {
        Service service = WSDLUtil.getService("HelloWebService.wsdl", new PortName("HelloWebService:"));
        Assert.assertNotNull(service);
        Assert.assertEquals(service.getQName(), new QName("urn:switchyard-component-soap:test-ws:1.0", "HelloWebService"));
        Port port = WSDLUtil.getPort(service, new PortName("HelloWebServicePort"));
        Assert.assertNotNull(port);
        String action = WSDLUtil.getSoapAction(port, new QName("urn:switchyard-component-soap:test-ws:1.0", "sayHello"), true);
        Assert.assertEquals(action, "uri:something:that:needs#tobevalid");
        action = WSDLUtil.getSoapAction(port, new QName("urn:switchyard-component-soap:test-ws:1.0", "helloWS"), true);
        Assert.assertEquals(action, "");
        
    }

    @Test
    public void soap12Action() throws Exception {
        Service service = WSDLUtil.getService("HelloWebService1.2.wsdl", new PortName("HelloSOAP12Service:"));
        Assert.assertNotNull(service);
        Assert.assertEquals(service.getQName(), new QName("urn:switchyard-component-soap:test-ws:1.0", "HelloSOAP12Service"));
        Port port = WSDLUtil.getPort(service, new PortName("HelloSOAP12ServicePort"));
        Assert.assertNotNull(port);
        String action = WSDLUtil.getSoapAction(port, new QName("urn:switchyard-component-soap:test-ws:1.0", "sayHello"), true);
        Assert.assertEquals(action, "uri:soap12:that:needs#tobevalid");
        action = WSDLUtil.getSoapAction(port, new QName("urn:switchyard-component-soap:test-ws:1.0", "helloWS"), true);
        Assert.assertEquals(action, "");
    }

    @Test
    public void soapAddressing() throws Exception {
        Definition definition = WSDLUtil.readWSDL("HelloWebServiceAddressing.wsdl");
        String targetNamespace = definition.getTargetNamespace();
        Service service = WSDLUtil.getService(definition, new PortName("HelloAddressingService:HelloSOAPAddressingServicePort"));
        Assert.assertNotNull(service);
        Assert.assertEquals(service.getQName(), new QName(targetNamespace, "HelloAddressingService"));
        Port port = WSDLUtil.getPort(service, new PortName("HelloSOAPAddressingServicePort"));
        Assert.assertNotNull(port);
        Feature feature = WSDLUtil.getFeature(definition, port, true);
        Assert.assertEquals("[AddressingEnabled:true, AddressingRequired:false, MtomEnabled:false]", feature.toString());
        String action = WSDLUtil.getInputAction(port, new QName(targetNamespace, "sayHello"), true);
        Assert.assertEquals("urn:switchyard-component-soap:test-ws:1.0:HelloAddressingService:sayHelloRequest", action);
        action = WSDLUtil.getOutputAction(port, new QName(targetNamespace, "sayHello"), true);
        Assert.assertEquals("urn:switchyard-component-soap:test-ws:1.0:HelloAddressingService:sayHelloResponse", action);

        port = WSDLUtil.getPort(service, new PortName("HelloSOAPAddressingServicePort2"));
        Assert.assertNotNull(port);
        feature = WSDLUtil.getFeature(definition, port, true);
        Assert.assertEquals("[AddressingEnabled:true, AddressingRequired:true, MtomEnabled:false]", feature.toString());
        action = WSDLUtil.getInputAction(port, new QName(targetNamespace, "sayHello"), true);
        Assert.assertEquals("http://mysite/input", action);
        action = WSDLUtil.getOutputAction(port, new QName(targetNamespace, "sayHello"), true);
        Assert.assertEquals("http://mysite/output", action);

        port = WSDLUtil.getPort(service, new PortName("HelloSOAPAddressingServicePort3"));
        Assert.assertNotNull(port);
        feature = WSDLUtil.getFeature(definition, port, true);
        Assert.assertEquals("[AddressingEnabled:true, AddressingRequired:true, MtomEnabled:false]", feature.toString());
        action = WSDLUtil.getFaultAction(port, new QName(targetNamespace, "sayHello"), "UnknownMessage", true);
        Assert.assertEquals("urn:switchyard-component-soap:test-ws:1.0:HelloAddressingService3:sayHello:Fault:UnknownMessage", action);
    }

    @Test
    public void soapMtom() throws Exception {
        // Document policy ref
        Definition definition = WSDLUtil.readWSDL("ImageServiceMtom.wsdl");
        Service service = WSDLUtil.getService(definition, new PortName("ImageService:ImageServicePort"));
        Assert.assertNotNull(service);
        Assert.assertEquals(service.getQName(), new QName("urn:switchyard-component-soap:test-ws:1.0", "ImageService"));
        Port port = WSDLUtil.getPort(service, new PortName("ImageServicePort"));
        Assert.assertNotNull(port);
        Feature feature = WSDLUtil.getFeature(definition, port, true);
        Assert.assertEquals("[AddressingEnabled:false, AddressingRequired:false, MtomEnabled:true]", feature.toString());
        // Document policy ref another style
        port = WSDLUtil.getPort(service, new PortName("ImageServicePort2"));
        Assert.assertNotNull(port);
        feature = WSDLUtil.getFeature(definition, port, true);
        Assert.assertEquals("[AddressingEnabled:false, AddressingRequired:false, MtomEnabled:true]", feature.toString());

        // Document element def
        definition = WSDLUtil.readWSDL("ImageServiceMtom2.wsdl");
        service = WSDLUtil.getService(definition, new PortName("ImageService:ImageServicePort"));
        Assert.assertNotNull(service);
        Assert.assertEquals(service.getQName(), new QName("urn:switchyard-component-soap:test-ws:1.0", "ImageService"));
        port = WSDLUtil.getPort(service, new PortName("ImageServicePort"));
        Assert.assertNotNull(port);
        feature = WSDLUtil.getFeature(definition, port, true);
        Assert.assertEquals("[AddressingEnabled:false, AddressingRequired:false, MtomEnabled:true]", feature.toString());
    }

    @Test
    public void importedWSDL() throws Exception {
        Service service = WSDLUtil.getService("HelloWebServiceImport.wsdl", new PortName(null));
        Assert.assertNotNull(service);
        Port port = WSDLUtil.getPort(service, new PortName("HelloWebServicePort"));
        Assert.assertNotNull(port);
        service = WSDLUtil.getService("HelloWebServiceImport.wsdl", new PortName("HelloWebService:"));
        Assert.assertNotNull(service);
        PortName portName = new PortName("{urn:switchyard-metadata-interface}HelloWebService:");
        service = WSDLUtil.getService("HelloWebServiceImport.wsdl", portName);
        Assert.assertNotNull(service);
    }

    @Test
    public void soapMtomImportTypes() throws Exception {
        // Document policy ref
        Definition definition = WSDLUtil.readWSDL("ImageServiceMtomImportTypes.wsdl");
        Service service = WSDLUtil.getService(definition, new PortName("ImageService:ImageServicePort"));
        Assert.assertNotNull(service);
        Assert.assertEquals(service.getQName(), new QName("urn:switchyard-component-soap:test-ws:1.0", "ImageService"));
        Port port = WSDLUtil.getPort(service, new PortName("ImageServicePort"));
        Assert.assertNotNull(port);
        Feature feature = WSDLUtil.getFeature(definition, port, true);
        Assert.assertEquals("[AddressingEnabled:false, AddressingRequired:false, MtomEnabled:true]", feature.toString());
        // Document policy ref another style
        port = WSDLUtil.getPort(service, new PortName("ImageServicePort2"));
        Assert.assertNotNull(port);
        feature = WSDLUtil.getFeature(definition, port, true);
        Assert.assertEquals("[AddressingEnabled:false, AddressingRequired:false, MtomEnabled:true]", feature.toString());

        // Document element def
        definition = WSDLUtil.readWSDL("ImageServiceMtomImportTypes2.wsdl");
        service = WSDLUtil.getService(definition, new PortName("ImageService:ImageServicePort"));
        Assert.assertNotNull(service);
        Assert.assertEquals(service.getQName(), new QName("urn:switchyard-component-soap:test-ws:1.0", "ImageService"));
        port = WSDLUtil.getPort(service, new PortName("ImageServicePort"));
        Assert.assertNotNull(port);
        feature = WSDLUtil.getFeature(definition, port, true);
        Assert.assertEquals("[AddressingEnabled:false, AddressingRequired:false, MtomEnabled:true]", feature.toString());

        // RPC message definition
        definition = WSDLUtil.readWSDL("ImageServiceMtomImportTypes3.wsdl");
        service = WSDLUtil.getService(definition, new PortName("ImageService:ImageServicePort"));
        Assert.assertNotNull(service);
        Assert.assertEquals(service.getQName(), new QName("urn:switchyard-component-soap:test-ws:1.0", "ImageService"));
        port = WSDLUtil.getPort(service, new PortName("ImageServicePort"));
        Assert.assertNotNull(port);
        feature = WSDLUtil.getFeature(definition, port, true);
        Assert.assertEquals("[AddressingEnabled:false, AddressingRequired:false, MtomEnabled:true]", feature.toString());
    }

    /*
    @Test
    public void contracts() throws Exception {
        Service service = WSDLUtil.getService("DoclitOrderService.wsdl", new PortName(null));
        Assert.assertNotNull(service);
        Assert.assertEquals(service.getQName(), new QName("urn:switchyard-component-soap:test-ws:1.0", "OrderService"));
        Port port = WSDLUtil.getPort(service, new PortName("OrderService:"));
        Assert.assertNotNull(port);
        Assert.assertEquals(port.getName(), "OrderServicePort");
        Map<String, BaseExchangeContract> contracts = WSDLUtil.getContracts(port, new MockServiceReference(new OrderServiceInterface(), "OrderService"));
        Assert.assertNotNull(contracts);
        // One operation
        Assert.assertEquals(contracts.size(), 1);
        BaseExchangeContract contract = contracts.get("submitOrder");
        Assert.assertNotNull(contract);
        BaseInvocationContract soapMetaData = contract.getInvokerInvocationMetaData();
        Assert.assertNotNull(soapMetaData);
        // Input and output types
        Assert.assertNotNull(soapMetaData.getInputType());
        Assert.assertEquals(soapMetaData.getInputType(), new QName("urn:switchyard-component-soap:test-ws:1.0", "order"));
        Assert.assertNotNull(soapMetaData.getOutputType());
        Assert.assertEquals(soapMetaData.getOutputType(), new QName("urn:switchyard-component-soap:test-ws:1.0", "orderAck"));
    }
    */

    private static class OrderServiceInterface extends BaseService {
        private static Set<ServiceOperation> _operations = new HashSet<ServiceOperation>(2);
        static {
            _operations.add(new InOutOperation("submitOrder"));
        }
        public OrderServiceInterface() {
            super(_operations);
        }
    }
}
