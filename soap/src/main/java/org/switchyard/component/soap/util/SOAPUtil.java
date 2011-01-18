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
 
package org.switchyard.component.soap.util;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;

import javax.wsdl.Operation;
import javax.wsdl.OperationType;
import javax.wsdl.Port;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.Detail;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Contains utility methods to examine/manipulate SOAP Messages.
 */
public final class SOAPUtil {
    private static final Logger LOGGER = Logger.getLogger(SOAPUtil.class);
    private static final QName SERVER_FAULT_QN = new QName("http://schemas.xmlsoap.org/soap/envelope/", "Server");
    private static final boolean RETURN_STACK_TRACES = false;

    /** SOAP Message Factory holder. */
    public static final MessageFactory SOAP_MESSAGE_FACTORY;

    private SOAPUtil() {
    }

    /**
     * Check if we are invoking a @Oneway annotated method.
     *
     * @param port The WSDL service port.
     * @param operationName The name of the operation obtained from SOAP message.
     * @return True if there is no response to be expected.
     */
    public static boolean isMessageOneWay(final Port port, final String operationName) {
        boolean isOneWay = false;
        // Overloaded methods not supported
        // Encrypted messages will be treated as request-response as it cannot be decrypted
        Operation operation = port.getBinding().getPortType().getOperation(operationName, null, null);
        if (operation != null) {
            isOneWay = operation.getStyle().equals(OperationType.ONE_WAY);
        }
        return isOneWay;
    }

    /**
     * Get the methods Input message's name.
     *
     * @param port The WSDL service port.
     * @param operationName The name of the operation obtained from SOAP message.
     * @return The local name of the input message.
     */
    public static String getMessageName(final Port port, final String operationName) {
        String messageName = null;
        // Overloaded methods not supported
        // Encrypted messages will be treated as request-response as it cannot be decrypted
        Operation operation = port.getBinding().getPortType().getOperation(operationName, null, null);
        if (operation != null) {
            messageName = operation.getInput().getMessage().getQName().getLocalPart();
        }
        return messageName;
    }

    /**
     * Retrives the operation name from the SOAP Envelope.
     *
     * @param soapMessage The SOAP message.
     * @return The operation name.
     * @throws SOAPException If the SOAP message is invalid
     */
    public static String getOperationName(final SOAPMessage soapMessage) throws SOAPException {
        String operationName = null;
        SOAPBody body = soapMessage.getSOAPPart().getEnvelope().getBody();
        if (body != null) {
            Iterator<Node> nodes = body.getChildElements();
            Node node = null;
            while (nodes.hasNext()) {
                node = nodes.next();
                if (node instanceof Element) {
                    operationName = node.getLocalName();
                }
            }
        }
        return operationName;
    }

    /**
     * Generates a SOAP Fault Message based on the Exception passed.
     * @param th The Exception.
     * @return The SOAP Message containing the Fault.
     * @throws SOAPException If the message could not be generated.
     */
    public static SOAPMessage generateFault(final Throwable th) throws SOAPException {
        final SOAPMessage faultMsg = SOAP_MESSAGE_FACTORY.createMessage();
        if (th instanceof SOAPFaultException) {
            // Copy the Fault from the exception
            SOAPFault exFault = ((SOAPFaultException) th).getFault();
            SOAPFault fault = faultMsg.getSOAPBody().addFault(exFault.getFaultCodeAsQName(), exFault.getFaultString());
            fault.addNamespaceDeclaration(fault.getElementQName().getPrefix(), SERVER_FAULT_QN.getNamespaceURI());
            fault.setFaultActor(exFault.getFaultActor());
            if (exFault.hasDetail()) {
                Detail exDetail = exFault.getDetail();
                Detail detail = fault.addDetail();
                for (Iterator<DetailEntry> entries = exDetail.getDetailEntries(); entries.hasNext();) {
                    DetailEntry exEntry = entries.next();
                    DetailEntry entry = detail.addDetailEntry(exEntry.getElementName());
                    entry.setValue(exEntry.getValue());
                }
            }
        } else {
            if (RETURN_STACK_TRACES) {
                final StringWriter sw = new StringWriter();
                final PrintWriter pw = new PrintWriter(sw);
                th.printStackTrace(pw);
                pw.flush();
                pw.close();
                faultMsg.getSOAPBody().addFault(SERVER_FAULT_QN, sw.toString());
            } else {
                faultMsg.getSOAPBody().addFault(SERVER_FAULT_QN, th.getMessage());
            }
        }
        return faultMsg;
    }

    /**
     * Create a new document based on a SOAP Message.
     * @param soapRes the SOAP Message
     * @return the new document
     * @throws ParserConfigurationException for errors during creation
     * @throws XMLStreamException If the SOAP message could not be read
     */
    public static Document parseAsDom(final String soapRes) throws ParserConfigurationException, XMLStreamException {
        final XMLEventReader reader = XMLHelper.getXMLEventReader(new ByteArrayInputStream(soapRes.getBytes()));
        return XMLHelper.createDocument(reader);
    }

    static {
        MessageFactory soapMessageFactory = null;
        try {
            soapMessageFactory = MessageFactory.newInstance();
        } catch (final SOAPException soape) {
            LOGGER.error("Could not instantiate SOAP Message Factory", soape);
        }
        SOAP_MESSAGE_FACTORY = soapMessageFactory;
    }
}
