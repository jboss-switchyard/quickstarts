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

import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;

import org.switchyard.Context;
import org.switchyard.Exchange;
import org.switchyard.ExchangeState;
import org.switchyard.Message;
import org.switchyard.Property;
import org.switchyard.Scope;
import org.switchyard.component.soap.util.SOAPUtil;
import org.w3c.dom.Node;

/**
 * The default implementation of MessageDecomposer simply copies the Message body onto SOAP
 * and adds SOAP headers from the Message's context.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
public class DefaultMessageDecomposer implements MessageDecomposer {

    /**
     * {@inheritDoc}
     */
    @Override
    public SOAPMessage decompose(final Exchange exchange, final Set<QName> mappedVariableNames) throws SOAPException {
        final Message message = exchange.getMessage();

        if (SOAPUtil.SOAP_MESSAGE_FACTORY == null) {
            throw new SOAPException("Failed to instantiate SOAP Message Factory");
        }
        SOAPMessage response;
        if (message != null) {
            // check to see if the payload is null or it's a full SOAP Message
            if (message.getContent() == null) {
                throw new SOAPException("Null response from service");
            }
            if (message.getContent() instanceof SOAPMessage) {
                return (SOAPMessage) message.getContent();
            }
            
            // convert the message content to a form we can work with
            Node input = message.getContent(Node.class);
            
            try {
                response = SOAPUtil.SOAP_MESSAGE_FACTORY.createMessage();

                Node messageNodeImport = response.getSOAPBody().getOwnerDocument().importNode(input, true);
                if (exchange.getState() != ExchangeState.FAULT || isSOAPFaultPayload(input)) {
                    response.getSOAPBody().appendChild(messageNodeImport);
                } else {
                    // convert to SOAP Fault since ExchangeState is FAULT but the message is not SOAP Fault
                    response.getSOAPBody().addFault(SOAPUtil.SERVER_FAULT_QN, "Send failed")
                                          .addDetail()
                                          .appendChild(messageNodeImport);
                }
            } catch (Exception e) {
                throw new SOAPException("Unable to parse SOAP Message", e);
            }
        } else {
            response = SOAPUtil.SOAP_MESSAGE_FACTORY.createMessage();
        }

        final Context context = exchange.getContext();
        SOAPHeader soapHeader = response.getSOAPHeader();
        for (QName name : mappedVariableNames) {
            Property property = context.getProperty(name.toString(), Scope.OUT);
            if (property != null) {
                Object value = property.getValue();
                if (value != null) {
                    soapHeader.addChildElement(name).setValue(String.valueOf(value));
                }
            }
        }

        return response;
    }

    private boolean isSOAPFaultPayload(Node messageNode) {
        String rootName = messageNode.getLocalName().toLowerCase();

        if (rootName.equals("fault")) {
            String nsURI = messageNode.getNamespaceURI();

            if ("http://schemas.xmlsoap.org/soap/envelope/".equals(nsURI)) {
                return true;
            }
        }

        return false;
    }
}
