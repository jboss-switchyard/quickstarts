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

package org.switchyard.extensions.wsdl;

import javax.xml.namespace.QName;

import org.junit.Assert;
import org.junit.Test;
import org.switchyard.ExchangePattern;
import org.switchyard.extensions.wsdl.WSDLReaderException;
import org.switchyard.extensions.wsdl.WSDLService;
import org.switchyard.metadata.ServiceOperation;

/**
 * Tests for WSDLService
 * 
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
public class WSDLServiceTest {
    
    static final QName METHOD1_INPUT = new QName("urn:switchyard-metadata-wsdl", "helloWS");
    static final QName METHOD2_INPUT = new QName("urn:switchyard-metadata-wsdl", "sayHello");
    static final QName METHOD2_OUTPUT = new QName("urn:switchyard-metadata-wsdl", "sayHelloResponse");

    @Test
    public void interfaceWSDLService() throws Exception {
        WSDLService wsdlService = WSDLService.fromWSDL("HelloWebService.wsdl", "HelloWebService");
        
        Assert.assertEquals("{urn:switchyard-metadata-wsdl}HelloWebService", wsdlService.getPortType().toString());
        
        // There should be two operations
        Assert.assertEquals(2, wsdlService.getOperations().size());
        
        // method1 is InOnly
        ServiceOperation method1 = wsdlService.getOperation("helloWS");
        Assert.assertNotNull(method1);
        Assert.assertEquals(method1.getInputType(), METHOD1_INPUT);
        Assert.assertEquals(method1.getExchangePattern(), ExchangePattern.IN_ONLY);
        
        //method2 is InOut
        ServiceOperation method2 = wsdlService.getOperation("sayHello");
        Assert.assertNotNull(method2);
        Assert.assertEquals(method2.getInputType(), METHOD2_INPUT);
        Assert.assertEquals(method2.getOutputType(), METHOD2_OUTPUT);
        Assert.assertEquals(method2.getExchangePattern(), ExchangePattern.IN_OUT);
    }

    @Test
    public void interfaceWSDLServiceNoPortName() throws Exception {
        WSDLService wsdlService = WSDLService.fromWSDL("HelloWebService.wsdl#wsdl.porttype(HelloWebService)");

        Assert.assertEquals("{urn:switchyard-metadata-wsdl}HelloWebService", wsdlService.getPortType().toString());
        
        // There should be two operations
        Assert.assertEquals(2, wsdlService.getOperations().size());
    }

    @Test
    public void invalidInterface() throws Exception {
        try {
            WSDLService.fromWSDL("wsdl.porttype(HelloWebService)");
        } catch (WSDLReaderException e) {
            Assert.assertEquals("Invalid WSDL interface wsdl.porttype(HelloWebService)", e.getMessage());
        }
    }

    @Test
    public void invalidInterface2() throws Exception {
        try {
            WSDLService.fromWSDL("HelloWebService.wsdl#(HelloWebService)");
        } catch (WSDLReaderException e) {
            Assert.assertEquals("Invalid WSDL interface part HelloWebService.wsdl#(HelloWebService)", e.getMessage());
        }
    }

    @Test
    public void nonExistingPort() throws Exception {
        try {
            WSDLService.fromWSDL("HelloWebService.wsdl", "hello");
        } catch (WSDLReaderException e) {
            Assert.assertEquals("Unable to find portType with name hello", e.getMessage());
        }
    }

    @Test
    public void nonExistingFile() throws Exception {
        try {
            WSDLService.fromWSDL("unknown.wsdl", "hello");
        } catch (WSDLReaderException e) {
            Assert.assertEquals("Unable to resolve WSDL document at unknown.wsdl", e.getMessage());
        }
    }

    @Test
    public void improperWSDL() throws Exception {
        try {
            WSDLService.fromWSDL("HelloWebService2.wsdl", "HelloWebService");
        } catch (WSDLReaderException e) {
            Assert.assertEquals("Service operations on a WSDL interface must have exactly one parameter.", e.getMessage());
        }
    }
}
