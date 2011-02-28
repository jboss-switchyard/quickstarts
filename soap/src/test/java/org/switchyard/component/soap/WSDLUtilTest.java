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

import javax.xml.namespace.QName;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;

import org.junit.Assert;
import org.junit.Test;

import org.switchyard.component.soap.util.WSDLUtil;

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
}
