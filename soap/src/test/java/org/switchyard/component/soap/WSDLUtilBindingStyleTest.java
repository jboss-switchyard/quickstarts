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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.xml.namespace.QName;

import org.junit.Test;
import org.switchyard.component.soap.util.WSDLUtil;
import org.switchyard.SwitchYardException;

/**
 * Test of binding style discovery.
 */
public class WSDLUtilBindingStyleTest {

    private static final String TEST_WSDL = "BindingStyleTest.wsdl";

    protected final void assertStyle(String portName, String bindingName, String style) throws Exception {
        Service service = WSDLUtil.getService(TEST_WSDL, new PortName(portName + ":"));

        assertNotNull(service);
        assertEquals(service.getQName(), new QName("urn:switchyard-component-soap:test-ws:1.0", portName));
        Port port = WSDLUtil.getPort(service, new PortName(bindingName));
        assertNotNull(port);
        assertEquals(style, WSDLUtil.getStyle(port));
    }

    @Test
    public void testSoapDocumentBinding() throws Exception {
        assertStyle("DocumentBindingService", "DocumentBinding", WSDLUtil.DOCUMENT);
    }

    @Test
    public void testSoapRpcBinding() throws Exception {
        assertStyle("RpcBindingService", "RpcBinding", "rpc");
    }

    @Test
    public void testOperationRpcBinding() throws Exception {
        assertStyle("OperationRpcBindingService", "OperationRpcBinding", "rpc");
    }

    @Test(expected = SwitchYardException.class)
    public void testDocumentRpcMixedOperationsBinding() throws Exception {
        assertStyle("DocumentRpcMixedOperationsService", "DocumentRpcMixedOperationsBinding", null);
    }

    @Test(expected = SwitchYardException.class)
    public void testRpcDocumentMixedOperationsBinding() throws Exception {
        assertStyle("RpcDocumentMixedOperationsService", "RpcDocumentMixedOperationsBinding", null);
    }

    @Test(expected = SwitchYardException.class)
    public void testRpcDocument2MixedOperationsBinding() throws Exception {
        assertStyle("RpcDocument2MixedOperationsService", "RpcDocument2MixedOperationsBinding", null);
    }

}
