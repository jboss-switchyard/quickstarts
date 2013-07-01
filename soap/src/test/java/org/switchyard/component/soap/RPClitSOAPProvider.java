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
import org.switchyard.common.xml.XMLHelper;
import org.switchyard.component.soap.util.SOAPUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * A mock up WebService provider.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class RPClitSOAPProvider extends BaseHandler {

    @Override
    public void handleMessage(Exchange exchange) throws HandlerException {
        if (exchange.getContract().getProviderOperation().getExchangePattern().equals(ExchangePattern.IN_OUT)) {
            String response = null;
            Message message = exchange.createMessage();
            Node request = exchange.getMessage().getContent(Node.class);
            Element orderId = XMLHelper.getFirstChildElementByName(request, "orderId");

            if (orderId != null && orderId.getFirstChild().getNodeValue().equals("PO-121212-XYZ")) {
                response = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                           "<SOAP-ENV:Body>" +
                           "<ns2:submitOrderResponse xmlns:ns2=\"urn:switchyard-component-soap:test-ws:1.0\">" +
                           "<orderId>PO-121212-XYZ</orderId>" +
                           "<accepted>true</accepted>" +
                           "<status>Order Accepted</status>" +
                           "</ns2:submitOrderResponse>" +
                           "</SOAP-ENV:Body>" +
                           "</SOAP-ENV:Envelope>";
            } else {
                response = "<soap:fault xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                  + "   <faultcode>soap:Server.AppError</faultcode>"
                  + "   <faultstring>Invalid soap request</faultstring>"
                  + "   <detail>"
                  + "      <message>The soap request does not look like what we have expected</message>"
                  + "      <errorcode>9999</errorcode>"
                  + "   </detail>"
                  + "</soap:fault>";
            }

            setContent(message, response);
            exchange.send(message);
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
