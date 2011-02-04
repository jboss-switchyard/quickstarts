/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
        PortName portName = new PortName("{http://test.ws/}GoodbyeWebService:GoodbyeWebServicePort");
        Service service = WSDLUtil.getService("target/test-classes/MultiplePortService.wsdl", portName);
        Assert.assertNotNull(service);
        Assert.assertEquals(service.getQName(), new QName("http://test.ws/", "GoodbyeWebService"));
        Port port = WSDLUtil.getPort(service, portName);
        Assert.assertNotNull(port);
        Assert.assertEquals(port.getName(), "GoodbyeWebServicePort");
    }
    
    @Test
    public void halfQualifiedPortName() throws Exception {
        PortName portName = new PortName("HelloWebService:HelloWebServicePort");
        Service service = WSDLUtil.getService("target/test-classes/MultiplePortService.wsdl", portName);
        Assert.assertNotNull(service);
        Assert.assertEquals(service.getQName(), new QName("http://test.ws/", "HelloWebService"));
        Port port = WSDLUtil.getPort(service, portName);
        Assert.assertNotNull(port);
        Assert.assertEquals(port.getName(), "HelloWebServicePort");
    }
    
    @Test
    public void nonQualifiedPortName() throws Exception {
        PortName portName = new PortName("HelloWebServicePortFrench");
        Service service = WSDLUtil.getService("target/test-classes/MultiplePortService.wsdl", portName);
        Assert.assertNotNull(service);
        Assert.assertEquals(service.getQName(), new QName("http://test.ws/", "GoodbyeWebService"));
        service = WSDLUtil.getService("target/test-classes/MultiplePortService.wsdl", new PortName("HelloWebService:"));
        Assert.assertNotNull(service);
        Assert.assertEquals(service.getQName(), new QName("http://test.ws/", "HelloWebService"));
        Port port = WSDLUtil.getPort(service, portName);
        Assert.assertEquals(port.getName(), "HelloWebServicePortFrench");
    }
    
    @Test
    public void nullPortName() throws Exception {
        Service service = WSDLUtil.getService("target/test-classes/MultiplePortService.wsdl", new PortName(null));
        Assert.assertNotNull(service);
        Assert.assertEquals(service.getQName(), new QName("http://test.ws/", "GoodbyeWebService"));
        Port port = WSDLUtil.getPort(service, new PortName(null));
        Assert.assertNotNull(port);
        Assert.assertEquals(port.getName(), "GoodbyeWebServicePort");
        service = WSDLUtil.getService("target/test-classes/MultiplePortService.wsdl", new PortName("HelloWebService:"));
        Assert.assertNotNull(service);
        Assert.assertEquals(service.getQName(), new QName("http://test.ws/", "HelloWebService"));
        port = WSDLUtil.getPort(service, new PortName(null));
        Assert.assertEquals(port.getName(), "HelloWebServicePortFrench");
    }
    
    @Test(expected=WSDLException.class)
    public void nonExistentService() throws Exception {
        Service service = WSDLUtil.getService("target/test-classes/MultiplePortService.wsdl", new PortName("NonexistentWebService:"));
    }
    
    @Test(expected=WSDLException.class)
    public void nonExistentPortName() throws Exception {
        Service service = WSDLUtil.getService("target/test-classes/MultiplePortService.wsdl", new PortName("HelloWebService:"));
        Assert.assertNotNull(service);
        Assert.assertEquals(service.getQName(), new QName("http://test.ws/", "HelloWebService"));
        Port port = WSDLUtil.getPort(service, new PortName("HelloWebServiceSpanishPort"));
    }
}
