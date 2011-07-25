/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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

package org.switchyard.component.soap;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;

import org.junit.Assert;
import org.junit.Test;

import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.ServiceReference;
import org.switchyard.component.soap.util.WSDLUtil;
import org.switchyard.metadata.BaseExchangeContract;
import org.switchyard.metadata.BaseInvocationContract;
import org.switchyard.metadata.BaseService;
import org.switchyard.metadata.ExchangeContract;
import org.switchyard.metadata.InOutOperation;
import org.switchyard.metadata.ServiceInterface;
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
        Assert.assertEquals(service.getQName(), new QName("urn:switchyard-component-soap:test-ws:1.0", "GoodbyeWebService"));
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
        Assert.assertEquals(service.getQName(), new QName("urn:switchyard-component-soap:test-ws:1.0", "GoodbyeWebService"));
        Port port = WSDLUtil.getPort(service, new PortName(null));
        Assert.assertNotNull(port);
        Assert.assertEquals(port.getName(), "GoodbyeWebServicePort");
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

    private static class OrderServiceInterface extends BaseService {
        private static Set<ServiceOperation> _operations = new HashSet<ServiceOperation>(2);
        static {
            _operations.add(new InOutOperation("submitOrder"));
        }
        public OrderServiceInterface() {
            super(_operations);
        }
    }

    private static class MockServiceReference implements ServiceReference {
        private ServiceInterface _interface;
        private QName _name;

        public MockServiceReference(ServiceInterface iface, String name) {
            _interface = iface;
            _name = new QName("urn:switchyard-component-soap:test-ws:1.0", name);
        }

        @Override
        public ServiceInterface getInterface() {
            return _interface;
        }

        @Override
        public Exchange createExchange(ExchangeContract contract) {
            return null;
        }

        @Override
        public Exchange createExchange(ExchangeContract contract, ExchangeHandler handler) {
            return null;
        }

        @Override
        public QName getName() {
            return _name;
        }
    }
}
