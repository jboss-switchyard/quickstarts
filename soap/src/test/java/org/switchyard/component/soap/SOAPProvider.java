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

import org.switchyard.BaseHandler;
import org.switchyard.Exchange;
import org.switchyard.ExchangePattern;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.Property;
import org.switchyard.Scope;
import org.switchyard.common.xml.XMLHelper;
import org.switchyard.component.soap.util.SOAPUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * A mock up WebService provider.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
public class SOAPProvider extends BaseHandler {

    @Override
    public void handleMessage(Exchange exchange) throws HandlerException {
        if (exchange.getContract().getProviderOperation().getExchangePattern().equals(ExchangePattern.IN_OUT)) {
            Message message;
            Node request = exchange.getMessage().getContent(Node.class);
            Element name = XMLHelper.getFirstChildElementByName(request, "arg0");
            Element fault = XMLHelper.getFirstChildElementByName(request, "CustomFaultMessage");
            
            String toWhom = "";
            if (name != null) {
                toWhom = name.getTextContent();
            }
            String response = null;
            if (toWhom.length() == 0) {
                message = exchange.createMessage();
                if (fault != null) {
                    // for testing sendFault() with the message which is not SOAP/Fault format
                    message.setContent(fault);
                } else {
                    response = "<SOAP-ENV:fault xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                                + "   <faultcode>SOAP-ENV:Server.AppError</faultcode>"
                                + "   <faultstring>Invalid name</faultstring>"
                                + "   <detail>"
                                + "      <message>Looks like you did not specify a name!</message>"
                                + "      <errorcode>1000</errorcode>"
                                + "   </detail>"
                                + "</SOAP-ENV:fault>";
                    setContent(message, response);
                }
                exchange.sendFault(message);
            } else {
                message = exchange.createMessage();
                Property soapActionProp = exchange.getContext().getProperty("Soapaction", Scope.IN);
                String soapAction = "";
                if (soapActionProp != null) {
                    soapAction = (String) soapActionProp.getValue();
                } else {
                    soapActionProp = exchange.getContext().getProperty("Content-Type", Scope.IN);
                    if (soapActionProp != null) {
                        soapAction = (String) soapActionProp.getValue();
                    }
                }
                response = "<test:sayHelloResponse xmlns:test=\"urn:switchyard-component-soap:test-ws:1.0\">"
                             + "   <return>Hello " + toWhom + "! The soapAction received is " + soapAction + "</return>"
                             + "</test:sayHelloResponse>";
                setContent(message, response);
                exchange.send(message);
            }
        }
    }

    private void setContent(Message message, String response) {
        try {
            Document responseDom = SOAPUtil.parseAsDom(response);
            message.setContent(responseDom.getDocumentElement());
        } catch (Exception e) {
            // Generate fault
        }
    }
}
