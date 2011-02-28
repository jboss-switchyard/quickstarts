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

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.switchyard.Message;

/**
 * Message decomposer holds the logic for converting SwitchYard messages to SOAP/XML messages.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
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
