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
