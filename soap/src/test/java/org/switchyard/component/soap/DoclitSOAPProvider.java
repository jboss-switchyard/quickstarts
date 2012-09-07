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
public class DoclitSOAPProvider extends BaseHandler {

    @Override
    public void handleMessage(Exchange exchange) throws HandlerException {
        if (exchange.getContract().getProviderOperation().getExchangePattern().equals(ExchangePattern.IN_OUT)) {
            String response = null;
            Message message = exchange.createMessage();
            
            Node request = exchange.getMessage().getContent(Node.class);
            Element orderId = XMLHelper.getFirstChildElementByName(request, "orderId");
            
            if(orderId != null && orderId.getFirstChild().getNodeValue().equals("PO-19838-XYZ")){
                
                response = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                           "<SOAP-ENV:Body>" +
                           "<ns2:orderAck xmlns:ns2=\"urn:switchyard-component-soap:test-ws:1.0\">" +
                           "<orderId>PO-19838-XYZ</orderId>" +
                           "<accepted>true</accepted>" +
                           "<status>Order Accepted</status>" +
                           "</ns2:orderAck>" +
                           "</SOAP-ENV:Body>" +
                           "</SOAP-ENV:Envelope>";    
            
            }else{
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
