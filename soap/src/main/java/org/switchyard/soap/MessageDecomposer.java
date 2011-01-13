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

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.switchyard.Message;

/**
 * Message decomposer holds the logic for converting SwitchYard messages to SOAP/XML messages.
 */
public interface MessageDecomposer {

    /**
     * Converts the Message to SOAPMessage.
     * @param message a Message to be converted
     * @return the SOAP message as SOAPMessage
     * @throws SOAPException If the SOAP message could not be created/formatted.
     */
    SOAPMessage decompose(Message message) throws SOAPException;
}
