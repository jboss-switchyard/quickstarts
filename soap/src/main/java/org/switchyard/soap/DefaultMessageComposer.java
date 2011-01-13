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

import java.util.Iterator;

import javax.xml.soap.Node;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.switchyard.Message;
import org.switchyard.MessageBuilder;

/**
 * The default implementation of MessageComposer simply copies the SOAP body into
 * the Message and SOAP headers into the Message's context.
 */
public class DefaultMessageComposer implements MessageComposer {

    /**
     * Create a Message from the given SOAP message.
     * @param soapMessage the SOAP message to be converted
     * @return a Message
     * @throws SOAPException If the SOAP message is not correct.
     */
    public Message compose(final SOAPMessage soapMessage) throws SOAPException {
        Message message = MessageBuilder.newInstance().buildMessage();

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
            throw new SOAPException(ex);
        }

        if (!found) {
            throw new SOAPException("Could not find SOAPElement in SOAPBody");
        }

        return message;
    }
}
