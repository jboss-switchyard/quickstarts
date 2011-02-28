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

import java.util.Iterator;

import javax.xml.soap.Node;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.switchyard.Exchange;
import org.switchyard.Message;

/**
 * The default implementation of MessageComposer simply copies the SOAP body into
 * the Message and SOAP headers into the Message's context.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
public class DefaultMessageComposer implements MessageComposer {

    /**
     * Create a Message from the given SOAP message.
     *
     *
     * @param soapMessage the SOAP message to be converted
     * @param exchange The message Exchange.
     * @return a Message
     * @throws SOAPException If the SOAP message is not correct.
     */
    public Message compose(final SOAPMessage soapMessage, final Exchange exchange)
    throws SOAPException {
        Message message = exchange.createMessage();

        final SOAPBody soapBody = soapMessage.getSOAPBody();
        if (soapBody == null) {
            throw new SOAPException("Missing SOAP body from request");
        }
        final Iterator children = soapBody.getChildElements();
        boolean found = false;

        try {
            while (children.hasNext()) {
                final Node node = (Node) children.next();
                if (node instanceof SOAPElement) {
                    if (found) {
                        throw new SOAPException("Found multiple SOAPElements in SOAPBody");
                    }
                    node.detachNode();
                    message.setContent(node);
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
}
