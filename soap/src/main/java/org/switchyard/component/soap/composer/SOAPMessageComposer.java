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

package org.switchyard.component.soap.composer;

import java.util.Iterator;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.dom.DOMSource;

import org.switchyard.Exchange;
import org.switchyard.ExchangeState;
import org.switchyard.Message;
import org.switchyard.component.common.composer.BaseMessageComposer;
import org.switchyard.component.soap.util.SOAPUtil;

/**
 * The SOAP implementation of MessageComposer simply copies the SOAP body into
 * the Message and SOAP headers into the Message's context, and vice-versa.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
public class SOAPMessageComposer extends BaseMessageComposer<SOAPMessage> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Message compose(SOAPMessage source, Exchange exchange, boolean create) throws Exception {
        final Message message = create ? exchange.createMessage() : exchange.getMessage();

        getContextMapper().mapFrom(source, exchange.getContext());

        final SOAPBody soapBody = source.getSOAPBody();
        if (soapBody == null) {
            throw new SOAPException("Missing SOAP body from request");
        }
        final Iterator<?> children = soapBody.getChildElements();
        boolean found = false;

        try {
            while (children.hasNext()) {
                final javax.xml.soap.Node node = (javax.xml.soap.Node) children.next();
                if (node instanceof SOAPElement) {
                    if (found) {
                        throw new SOAPException("Found multiple SOAPElements in SOAPBody");
                    }
                    node.detachNode();
                    message.setContent(new DOMSource(node));
                    found = true;
                }
            }
        } catch (Exception ex) {
            if (ex instanceof SOAPException) {
                throw (SOAPException) ex;
            }
            throw new SOAPException(ex);
        }

        if (!found) {
            throw new SOAPException("Could not find SOAPElement in SOAPBody");
        }

        return message;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SOAPMessage decompose(Exchange exchange, SOAPMessage target) throws Exception {
        final Message message = exchange.getMessage();

        if (message != null) {
            // check to see if the payload is null or it's a full SOAP Message
            if (message.getContent() == null) {
                throw new SOAPException("Null response from service");
            }
            if (message.getContent() instanceof SOAPMessage) {
                return (SOAPMessage) message.getContent();
            }
            
            // convert the message content to a form we can work with
            org.w3c.dom.Node input = message.getContent(org.w3c.dom.Node.class);
            
            try {
                org.w3c.dom.Node messageNodeImport = target.getSOAPBody().getOwnerDocument().importNode(input, true);
                if (exchange.getState() != ExchangeState.FAULT || isSOAPFaultPayload(input)) {
                    target.getSOAPBody().appendChild(messageNodeImport);
                } else {
                    // convert to SOAP Fault since ExchangeState is FAULT but the message is not SOAP Fault
                    SOAPUtil.addFault(target).addDetail().appendChild(messageNodeImport);
                }
            } catch (Exception e) {
                throw new SOAPException("Unable to parse SOAP Message", e);
            }
        }

        getContextMapper().mapTo(exchange.getContext(), target);

        return target;
    }

    private boolean isSOAPFaultPayload(org.w3c.dom.Node messageNode) {
        String rootName = messageNode.getLocalName().toLowerCase();

        if (rootName.equals("fault")) {
            String nsURI = messageNode.getNamespaceURI();

            if (nsURI.equals(SOAPUtil.SOAP12_URI) || nsURI.equals(SOAPUtil.SOAP11_URI)) {
                return true;
            }
        }

        return false;
    }

}
