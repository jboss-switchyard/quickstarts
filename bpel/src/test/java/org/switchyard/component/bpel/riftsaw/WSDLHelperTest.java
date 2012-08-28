/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009-11, Red Hat Middleware LLC, and others contributors as indicated
 * by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
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
package org.switchyard.component.bpel.riftsaw;

import static org.junit.Assert.*;

import javax.xml.namespace.QName;

import org.apache.ode.utils.DOMUtils;
import org.junit.Test;

public class WSDLHelperTest {

	@Test
	public void testUnwrapMessagePart() {
		String content="content";
		String xml="<message><partName><"+content+"/></partName></message>";
		
		try {
			org.w3c.dom.Element elem=DOMUtils.stringToDOM(xml);
			
			org.w3c.dom.Element unwrapped=WSDLHelper.unwrapMessagePart(elem);
			
			if (unwrapped == null) {
				fail("Result is null");
			}
			
			if (unwrapped.getNodeName().equals(content) == false) {
				fail("Unexpected unwrapped content: "+unwrapped.getNodeName());
			}
		} catch(Exception e) {
			fail("Failed: "+e);
		}
	}

    @Test
    public void testUnwrapMessagePartWhitespace() {
        String content="content";
        String xml="<message><partName>\r\n   <"+content+"/>\r\n</partName></message>";
        
        try {
            org.w3c.dom.Element elem=DOMUtils.stringToDOM(xml);
            
            org.w3c.dom.Element unwrapped=WSDLHelper.unwrapMessagePart(elem);
            
            if (unwrapped == null) {
                fail("Result is null");
            }
         
            if (unwrapped.getNodeName().equals(content) == false) {
                fail("Unexpected unwrapped content: "+unwrapped.getNodeName());
            }
        } catch(Exception e) {
            fail("Failed: "+e);
        }
    }

	@Test
	public void testWrapRequestMessagePart() {
		try {
			java.net.URL url=WSDLHelperTest.class.getResource("/tests/wsdl/LoanService.wsdl");
			
			javax.wsdl.Definition wsdl=javax.wsdl.factory.WSDLFactory.newInstance().newWSDLReader().readWSDL(url.getFile());
			
			javax.wsdl.PortType portType=
						wsdl.getPortType(QName.valueOf("{http://example.com/loan-approval/loanService/}loanServicePT"));
			if (portType == null) {
				fail("Unable to get port type");
			}
			
			javax.wsdl.Operation op=portType.getOperation("request", null, null);
			
			if (op == null) {
				fail("Unable to get operation 'request'");
			}
			
			String content="content";
			String xml="<"+content+"/>";
			
			org.w3c.dom.Element elem=DOMUtils.stringToDOM(xml);
				
			org.w3c.dom.Element wrapped=WSDLHelper.wrapRequestMessagePart(elem, op);
			
			if (wrapped == null) {
				fail("Result is null");
			}
			
			if (wrapped.getFirstChild().getNodeName().equals("reqpart") == false) {
				fail("Part name was not 'reqpart': "+wrapped.getFirstChild().getNodeName());
			}
			
			if (wrapped.getFirstChild().getFirstChild().getNodeName().equals(content) == false) {
				fail("Content was not '"+content+"': "+wrapped.getFirstChild().getFirstChild().getNodeName());
			}
		} catch(Exception e) {
			fail("Failed: "+e);
		}
	}

	@Test
	public void testWrapResponseMessagePart() {
		try {
			java.net.URL url=WSDLHelperTest.class.getResource("/tests/wsdl/LoanService.wsdl");
			
			javax.wsdl.Definition wsdl=javax.wsdl.factory.WSDLFactory.newInstance().newWSDLReader().readWSDL(url.getFile());
			
			javax.wsdl.PortType portType=
						wsdl.getPortType(QName.valueOf("{http://example.com/loan-approval/loanService/}loanServicePT"));
			if (portType == null) {
				fail("Unable to get port type");
			}
			
			javax.wsdl.Operation op=portType.getOperation("request", null, null);
			
			if (op == null) {
				fail("Unable to get operation 'request'");
			}
			
			String content="content";
			String xml="<"+content+"/>";
			
			org.w3c.dom.Element elem=DOMUtils.stringToDOM(xml);
				
			org.w3c.dom.Element wrapped=WSDLHelper.wrapResponseMessagePart(elem, op);
			
			if (wrapped == null) {
				fail("Result is null");
			}
			
			if (wrapped.getFirstChild().getNodeName().equals("resppart") == false) {
				fail("Part name was not 'resppart': "+wrapped.getFirstChild().getNodeName());
			}
			
			if (wrapped.getFirstChild().getFirstChild().getNodeName().equals(content) == false) {
				fail("Content was not '"+content+"': "+wrapped.getFirstChild().getFirstChild().getNodeName());
			}
		} catch(Exception e) {
			fail("Failed: "+e);
		}
	}

	@Test
	public void testWrapFaultMessagePartWithName() {
		try {
			java.net.URL url=WSDLHelperTest.class.getResource("/tests/wsdl/LoanService.wsdl");
			
			javax.wsdl.Definition wsdl=javax.wsdl.factory.WSDLFactory.newInstance().newWSDLReader().readWSDL(url.getFile());
			
			javax.wsdl.PortType portType=
						wsdl.getPortType(QName.valueOf("{http://example.com/loan-approval/loanService/}loanServicePT"));
			if (portType == null) {
				fail("Unable to get port type");
			}
			
			javax.wsdl.Operation op=portType.getOperation("request", null, null);
			
			if (op == null) {
				fail("Unable to get operation 'request'");
			}
			
			String content="content";
			String xml="<"+content+"/>";
			
			org.w3c.dom.Element elem=DOMUtils.stringToDOM(xml);
			
			org.w3c.dom.Element wrapped=WSDLHelper.wrapFaultMessagePart(elem, op, "unableToHandleRequest");
			
			if (wrapped == null) {
				fail("Result is null");
			}
			
			if (wrapped.getFirstChild().getNodeName().equals("errorCode") == false) {
				fail("Part name was not 'errorCode': "+wrapped.getFirstChild().getNodeName());
			}
			
			if (wrapped.getFirstChild().getFirstChild().getNodeName().equals(content) == false) {
				fail("Content was not '"+content+"': "+wrapped.getFirstChild().getFirstChild().getNodeName());
			}
		} catch(Exception e) {
			fail("Failed: "+e);
		}
	}

	@Test
	public void testWrapFaultMessagePartWithoutName() {
		try {
			java.net.URL url=WSDLHelperTest.class.getResource("/tests/wsdl/LoanService.wsdl");
			
			javax.wsdl.Definition wsdl=javax.wsdl.factory.WSDLFactory.newInstance().newWSDLReader().readWSDL(url.getFile());
			
			javax.wsdl.PortType portType=
						wsdl.getPortType(QName.valueOf("{http://example.com/loan-approval/loanService/}loanServicePT"));
			if (portType == null) {
				fail("Unable to get port type");
			}
			
			javax.wsdl.Operation op=portType.getOperation("request", null, null);
			
			if (op == null) {
				fail("Unable to get operation 'request'");
			}
			
			String xml="<integer xmlns=\"http://example.com/loan-approval/xsd/error-messages/\" />";
			
			org.w3c.dom.Element elem=DOMUtils.stringToDOM(xml);
			
			org.w3c.dom.Element wrapped=WSDLHelper.wrapFaultMessagePart(elem, op, null);
			
			if (wrapped == null) {
				fail("Result is null");
			}
			
			if (wrapped.getFirstChild().getNodeName().equals("errorCode") == false) {
				fail("Part name was not 'errorCode': "+wrapped.getFirstChild().getNodeName());
			}
			
			if (wrapped.getFirstChild().getFirstChild().getNodeName().equals(elem.getNodeName()) == false) {
				fail("Content was not '"+elem.getNodeName()+"': "+wrapped.getFirstChild().getFirstChild().getNodeName());
			}
		} catch(Exception e) {
			fail("Failed: "+e);
		}
	}
}
