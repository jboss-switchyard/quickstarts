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
package org.switchyard.extensions.wsdl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.switchyard.common.type.Classes;
import org.switchyard.common.xml.XMLHelper;
import org.switchyard.metadata.InOnlyOperation;
import org.switchyard.metadata.InOutOperation;
import org.switchyard.metadata.ServiceOperation;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * A utility class that creates ServiceOperation from a WSDL port definition.
 * 
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
public class WSDLReader {

    private static final Logger LOGGER = Logger.getLogger(WSDLReader.class);

    private static final String WSDLNS_URI = "http://schemas.xmlsoap.org/wsdl/";
    private static final String XMLNS_URI = "http://www.w3.org/2000/xmlns/";
    private static final String ATTR_ELEMENT = "element";
    private static final String ATTR_MESSAGE = "message";
    private static final String ATTR_NAME = "name";
    private static final String ATTR_TARGET_NS = "targetNamespace";
    private static final String ATTR_XMLNS = "xmlns";
    private static final QName PORT_TYPE = new QName(WSDLNS_URI, "portType");
    private static final QName OPERATION = new QName(WSDLNS_URI, "operation");
    private static final QName INPUT = new QName(WSDLNS_URI, "input");
    private static final QName OUTPUT = new QName(WSDLNS_URI, "output");
    private static final QName FAULT = new QName(WSDLNS_URI, "fault");
    private static final QName MESSAGE = new QName(WSDLNS_URI, "message");
    private static final QName PART = new QName(WSDLNS_URI, "part");

    /**
     * Read the WSDL document accessible via the specified
     * URI into a WSDL definition.
     *
     * @param wsdlURI a URI (can be a filename or URL) pointing to a WSDL XML definition.
     * @param portName the port name to match with
     * @return the ServiceOperations.
     * @throws WSDLReaderException if the wsdl cannot be read or is improper
     */
    public HashSet<ServiceOperation> readWSDL(final String wsdlURI, final String portName) throws WSDLReaderException {

        Element defEl = readWSDL(wsdlURI);
        Map<String, String> namespaces = parseNamespaces(defEl);
        Map<QName, QName> parts = getParts(defEl, namespaces);
        Element portType = getPortType(defEl, portName);
        if (portType == null) {
            throw new WSDLReaderException("Unable to find portType with name " + portName);
        }
        HashSet<ServiceOperation> ops = new HashSet<ServiceOperation>();
        List<Element> operations = getOperations(portType);
        int size = operations.size();
        for (int i = 0; i < size; i++) {
            ops.add(createServiceOperation(operations.get(i), parts, namespaces));
        }

        return ops;
    }

    /**
     * Obtains the WSDL document located at the URI.
     *
     * @param wsdlURI a URI (can be a filename or URL) pointing to a WSDL XML definition.
     * @return the WSDL document
     * @throws WSDLReaderException if the wsdl cannot be read or is improper
     */
    public Element readWSDL(final String wsdlURI) throws WSDLReaderException {
        try {
            LOGGER.trace("Retrieving document at '" + wsdlURI + "'");

            URL url = getURL(wsdlURI);
            InputStream inputStream = url.openStream();
            InputSource inputSource = new InputSource(inputStream);
            inputSource.setSystemId(url.toString());
            Document doc = XMLHelper.getDocument(inputSource);

            inputStream.close();

            return doc.getDocumentElement();

        } catch (IOException e) {
            throw new WSDLReaderException("Unable to resolve WSDL document at " + wsdlURI, e);
        } catch (ParserConfigurationException pce) {
            throw new WSDLReaderException(pce);
        } catch (SAXException se) {
            throw new WSDLReaderException(se);
        }
    }

    /**
     * Parse a WSDL definition for a given port name.
     *
     * @param defEl the definition element.
     * @param portName the porttype name.
     * @return the porttype element.
     */
    private Element getPortType(final Element defEl, final String portName) {
        Element tempEl = XMLHelper.getFirstChildElement(defEl);
        Element portType = null;

        while (tempEl != null) {
            QName qname = new QName(tempEl.getNamespaceURI(), tempEl.getLocalName());
            if (PORT_TYPE.equals(qname)) {
                if (portName != null
                    && tempEl.hasAttribute("name")
                    && !tempEl.getAttribute("name").equals(portName)) {
                    tempEl = XMLHelper.getNextSiblingElement(tempEl);
                    continue;
                }
                portType = tempEl;
                break;
            }
            tempEl = XMLHelper.getNextSiblingElement(tempEl);
        }
        return portType;
    }

    /**
     * Parse a port type element and return all operations defined in it.
     *
     * @param portEl the portType element.
     * @return a list of operation elements.
     */
    private List<Element> getOperations(final Element portEl) {
        Element tempEl = XMLHelper.getFirstChildElement(portEl);
        List<Element> operations = new ArrayList<Element>();

        while (tempEl != null) {
            QName qname = new QName(tempEl.getNamespaceURI(), tempEl.getLocalName());
            if (OPERATION.equals(qname)) {
                operations.add(tempEl);
            }
            tempEl = XMLHelper.getNextSiblingElement(tempEl);
        }
        return operations;
    }

    /**
     * Parse a definition element and return all message parts defined in it.
     *
     * @param defEl the definition element.
     * @param namespaces a map of namespaceURIs
     * @return a map of message parts.
     * @throws WSDLReaderException if the wsdl operation is improper
     */
    private Map<QName, QName> getParts(final Element defEl, final Map<String, String> namespaces) throws WSDLReaderException {
        NodeList messages = defEl.getElementsByTagNameNS(MESSAGE.getNamespaceURI(), MESSAGE.getLocalPart());
        int msgSize = messages.getLength();
        Map<QName, QName> parts = new HashMap<QName, QName>();
        for (int i = 0; i < msgSize; i++) {
            Element msgEl = (Element) messages.item(i);
            NodeList partEls = msgEl.getElementsByTagNameNS(PART.getNamespaceURI(), PART.getLocalPart());
            if (partEls.getLength() != 1) {
                throw new WSDLReaderException("Service operations on a WSDL interface must have exactly one parameter.");
            }
            Element partEl = (Element) partEls.item(0);
            parts.put(getQName(msgEl.getAttribute(ATTR_NAME), namespaces), getQName(partEl.getAttribute(ATTR_ELEMENT), namespaces));
        }
        return parts;
    }

    /**
     * Parse and create a Service Operation.
     *
     * @param opEl the operation element.
     * @param parts a map of message parts.
     * @param namespaces a map of namespaceURIs
     * @return a ServiceOperation.
     */
    private ServiceOperation createServiceOperation(final Element opEl, final Map<QName, QName> parts, final Map<String, String> namespaces) {
        Element tempEl = XMLHelper.getFirstChildElement(opEl);
        QName inputType = null;
        QName outputType = null;
        QName faultType = null;

        while (tempEl != null) {
            QName qname = new QName(tempEl.getNamespaceURI(), tempEl.getLocalName());
            if (INPUT.equals(qname)) {
                // input
                inputType = parts.get(getQName(tempEl.getAttribute(ATTR_MESSAGE), namespaces));
            } else if (OUTPUT.equals(qname)) {
                // output
                outputType = parts.get(getQName(tempEl.getAttribute(ATTR_MESSAGE), namespaces));
            } else if (FAULT.equals(qname)) {
                // fault
                faultType = parts.get(getQName(tempEl.getAttribute(ATTR_MESSAGE), namespaces));
            }
            tempEl = XMLHelper.getNextSiblingElement(tempEl);
        }
        if (outputType == null) {
            return new InOnlyOperation(opEl.getAttribute(ATTR_NAME), inputType);
        } else {
            return new InOutOperation(opEl.getAttribute(ATTR_NAME), inputType, outputType, faultType);
        }
    }

    /**
     * Parse and create a list of namespace declarations.
     *
     * @param defEl the definitions element.
     * @return the list of namespaces.
     */
    private Map<String, String> parseNamespaces(final Element defEl) {
        Map<String, String> namespaces = new HashMap<String, String>();
        String targetNamespace = defEl.getAttribute(ATTR_TARGET_NS);
        namespaces.put(null, targetNamespace);
        NamedNodeMap attrs = defEl.getAttributes();
        int size = attrs.getLength();

        for (int i = 0; i < size; i++) {
            Attr attr = (Attr) attrs.item(i);
            String namespaceURI = attr.getNamespaceURI();
            String localPart = attr.getLocalName();
            String value = attr.getValue();

            if (namespaceURI != null && namespaceURI.equals(XMLNS_URI)) {
                if (localPart != null && !localPart.equals(ATTR_XMLNS)) {
                    namespaces.put(localPart, value);
                }
            }
        }
        return namespaces;
    }

    /**
     * Creates a QName with attribute value.
     *
     * @param value the attribute value.
     * @param namespaces a map of namespaceURIs
     * @return the fully qualified name.
     */
    private QName getQName(final String value, final Map<String, String> namespaces) {
        String name = null;
        String namespace = null;
        if (value != null) {
            int idx = value.lastIndexOf(":");
            name = value.substring(idx + 1, value.length());
            if (idx > 0) {
                namespace = value.substring(0, idx);
            }
        }
        return new QName(namespaces.get(namespace), name);
    }


    /**
     * Convert a path/uri to a URL.
     *
     * @param path a path or URI.
     * @return the URL.
     * @throws MalformedURLException If the url path is not valid
     */
    private URL getURL(final String path) throws MalformedURLException {
        if (path.startsWith("http://") || path.startsWith("https://") || path.startsWith("file://")) {
            return new URL(null, path);
        } else {
            URL url;
            try {
                url = Classes.getResource(path, getClass());
            } catch (IOException ioe) {
                url = null;
            }
            if (url == null) {
                File localFile = new File(path);
                url = localFile.toURI().toURL();
            }
            return url;
        }
    }
}
