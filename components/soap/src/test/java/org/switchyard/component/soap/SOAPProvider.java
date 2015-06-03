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

import org.switchyard.BaseHandler;
import org.switchyard.Exchange;
import org.switchyard.ExchangePattern;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.Property;
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
        Message message = exchange.createMessage();
        if (exchange.getContract().getProviderOperation().getExchangePattern().equals(ExchangePattern.IN_OUT)) {
            Message receivedMessage = exchange.getMessage();
            Node request = receivedMessage.getContent(Node.class);
            Element name = XMLHelper.getFirstChildElementByName(request, "arg0");
            Element fault = XMLHelper.getFirstChildElementByName(request, "CustomFaultMessage");
            
            String toWhom = "";
            if (name != null) {
                toWhom = name.getTextContent();
            }
            String response = null;
            if (toWhom.length() == 0) {
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
                Property soapActionProp = exchange.getContext().getProperty("Soapaction");
                if (soapActionProp == null) {
                    soapActionProp = exchange.getContext().getProperty("SOAPAction");
                }
                String soapAction = "";
                if (soapActionProp != null) {
                    soapAction = (String) soapActionProp.getValue();
                } else {
                    soapActionProp = exchange.getContext().getProperty("Content-Type");
                    if (soapActionProp == null) {
                        soapActionProp = exchange.getContext().getProperty("Content-type");
                    }
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
