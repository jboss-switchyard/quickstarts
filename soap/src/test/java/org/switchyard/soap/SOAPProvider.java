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

package org.switchyard.soap;

import org.switchyard.BaseHandler;
import org.switchyard.Exchange;
import org.switchyard.ExchangePattern;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.MessageBuilder;
import org.switchyard.message.DefaultMessage;
import org.switchyard.soap.util.SOAPUtil;
import org.switchyard.soap.util.XMLHelper;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SOAPProvider extends BaseHandler {

    @Override
    public void handleMessage(Exchange exchange) throws HandlerException {
        if (exchange.getPattern().equals(ExchangePattern.IN_OUT)) {
            Message message;
            Element request = exchange.getMessage().getContent(Element.class);
            Element name = XMLHelper.getFirstChildElementByName(request, "arg0");
            String toWhom = "";
            if (name != null) {
                toWhom = name.getTextContent();
            }
            String response = null;
            if (toWhom.length() == 0) {
                message = MessageBuilder.newInstance(DefaultMessage.class).buildMessage();
                response = "<soap:fault xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                            + "   <faultcode>soap:Server.AppError</faultcode>"
                            + "   <faultstring>Invalid name</faultstring>"
                            + "   <detail>"
                            + "      <message>Looks like you did not specify a name!</message>"
                            + "      <errorcode>1000</errorcode>"
                            + "   </detail>"
                            + "</soap:fault>";
                setContent(message, response);
                exchange.sendFault(message);
            } else {
                message = MessageBuilder.newInstance().buildMessage();
                response = "<test:sayHelloResponse xmlns:test=\"http://test.ws/\">"
                             + "   <return>Hello " + toWhom + "</return>"
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