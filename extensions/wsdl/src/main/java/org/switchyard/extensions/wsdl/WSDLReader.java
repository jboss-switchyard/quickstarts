/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

import org.jboss.logging.Logger;
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

    private static final String SOAP11_URI = "http://schemas.xmlsoap.org/wsdl/soap/";
    private static final String SOAP12_URI = "http://schemas.xmlsoap.org/wsdl/soap12/";
    private static final String WSDLNS_URI = "http://schemas.xmlsoap.org/wsdl/";
    private static final String XMLNS_URI = "http://www.w3.org/2000/xmlns/";
    private static final String ATTR_ELEMENT = "element";
    private static final String ATTR_LOCATION = "location";
    private static final String ATTR_MESSAGE = "message";
    private static final String ATTR_NAME = "name";
    private static final String ATTR_TARGET_NS = "targetNamespace";
    private static final String ATTR_XMLNS = "xmlns";
    private static final String ATTR_STYLE = "style";
    private static final String ATTR_TYPE = "type";
    private static final QName IMPORT = new QName(WSDLNS_URI, "import");
    private static final QName PORT_TYPE = new QName(WSDLNS_URI, "portType");
    private static final QName OPERATION = new QName(WSDLNS_URI, "operation");
    private static final QName INPUT = new QName(WSDLNS_URI, "input");
    private static final QName OUTPUT = new QName(WSDLNS_URI, "output");
    private static final QName FAULT = new QName(WSDLNS_URI, "fault");
    private static final QName MESSAGE = new QName(WSDLNS_URI, "message");
    private static final QName PART = new QName(WSDLNS_URI, "part");
    private static final QName BINDING = new QName(WSDLNS_URI, "binding");
    private static final QName SOAP11_BINDING = new QName(SOAP11_URI, "binding");
    private static final QName SOAP12_BINDING = new QName(SOAP12_URI, "binding");
    private static final String DOCUMENT = "document";
    private static final String RESPONSE = "Response";

    private Boolean _documentStyle;

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
        Element portType = getPortType(defEl, portName, namespaces);
        if (portType == null) {
            throw WSDLExtensionsMessages.MESSAGES.unableFindPort(portName);
        }
        String style = getStyle(defEl, portType, namespaces);
        _documentStyle = style.equals(DOCUMENT) ? true : false;
        Map<QName, QName> parts = getParts(defEl, portType, namespaces);
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
            throw WSDLExtensionsMessages.MESSAGES.unableResolveWSDL(wsdlURI, e);
        } catch (ParserConfigurationException pce) {
            throw new WSDLReaderException(pce);
        } catch (SAXException se) {
            throw new WSDLReaderException(se);
        }
    }

    /**
     * Parse a WSDL definition for imports.
     *
     * @param defEl the definition element.
     * @return the imported WSDL Locations.
     */
    private List<String> getImports(final Element defEl) {
        Element tempEl = XMLHelper.getFirstChildElement(defEl);
        List<String> imports = new ArrayList<String>();

        while (tempEl != null) {
            QName qname = new QName(tempEl.getNamespaceURI(), tempEl.getLocalName());
            if (IMPORT.equals(qname)) {
                imports.add(tempEl.getAttribute(ATTR_LOCATION));
            }
            tempEl = XMLHelper.getNextSiblingElement(tempEl);
        }
        return imports;
    }

    /**
     * Parse a WSDL definition for a given port name.
     *
     * @param defEl the definition element.
     * @param portName the porttype name.
     * @param namespaces the namespaces map.
     * @return the porttype element.
     * @throws WSDLReaderException if the wsdl cannot be read or is improper
     */
    private Element getPortType(Element defEl, final String portName, Map<String, String> namespaces) throws WSDLReaderException {
        Element tempEl = XMLHelper.getFirstChildElement(defEl);
        Element portType = null;

        while (tempEl != null) {
            QName qname = new QName(tempEl.getNamespaceURI(), tempEl.getLocalName());
            if (PORT_TYPE.equals(qname)) {
                if (portName != null
                    && tempEl.hasAttribute(ATTR_NAME)
                    && !tempEl.getAttribute(ATTR_NAME).equals(portName)) {
                    tempEl = XMLHelper.getNextSiblingElement(tempEl);
                    continue;
                }
                portType = tempEl;
                break;
            }
            tempEl = XMLHelper.getNextSiblingElement(tempEl);
        }
        if (portType == null) {
            List<String> wsdlImports = getImports(defEl);
            int size = wsdlImports.size();
            for (int i = 0; i < size; i++) {
                Element importedDefEl = readWSDL(wsdlImports.get(i));
                namespaces.putAll(parseNamespaces(importedDefEl));
                portType = getPortType(importedDefEl, portName, namespaces);
                if (portType != null) {
                    break;
                }
            }
        }
        return portType;
    }

    /**
     * Find the style of a WSDL port.
     *
     * @param defEl the definition element.
     * @param portType the porttype element.
     * @param namespaces a map of namespaceURIs
     * @return the style, can be 'document' or 'rpc'.
     * @throws WSDLReaderException if the wsdl cannot be read or is improper
     */
    private String getStyle(final Element defEl, final Element portType, Map<String, String> namespaces) throws WSDLReaderException {
        Element tempEl = XMLHelper.getFirstChildElement(defEl);
        QName portTypeName = getQName(portType.getAttributeNode(ATTR_NAME).getValue(), namespaces);
        String style = DOCUMENT;

        while (tempEl != null) {
            QName qname = new QName(tempEl.getNamespaceURI(), tempEl.getLocalName());
            if (BINDING.equals(qname)) {
                QName bindingType = getQName(tempEl.getAttributeNode(ATTR_TYPE).getValue(), namespaces);
                if (bindingType.equals(portTypeName)) {
                    Element tempEl2 = XMLHelper.getFirstChildElement(tempEl);
                    while (tempEl2 != null) {
                        QName qname2 = new QName(tempEl2.getNamespaceURI(), tempEl2.getLocalName());
                        if ((SOAP11_BINDING.equals(qname2) || SOAP12_BINDING.equals(qname2)) && tempEl2.hasAttribute(ATTR_STYLE)) {
                            style = tempEl2.getAttributeNode(ATTR_STYLE).getValue();
                            break;
                        }
                        tempEl2 = XMLHelper.getNextSiblingElement(tempEl2);
                    }
                    break;
                }
            }
            tempEl = XMLHelper.getNextSiblingElement(tempEl);
        }
        if (style == null) {
            List<String> wsdlImports = getImports(defEl);
            int size = wsdlImports.size();
            for (int i = 0; i < size; i++) {
                Element importedDefEl = readWSDL(wsdlImports.get(i));
                namespaces.putAll(parseNamespaces(importedDefEl));
                style = getStyle(importedDefEl, portType, namespaces);
                if (style != null) {
                    break;
                }
            }
        }
        return style;
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
     * Parse a port element and return the operation element matching the message element.
     *
     * @param portEl the portType element.
     * @param msgEl the message element.
     * @param namespaces a map of namespaceURIs
     * @return the operation element.
     */
    private Element getOperationByInput(final Element portEl, final Element msgEl, final Map<String, String> namespaces) {
        Element tempEl = XMLHelper.getFirstChildElement(portEl);
        Element operation = null;
        QName msgQName = getQName(msgEl.getAttribute(ATTR_NAME), namespaces);

outer:  while (tempEl != null) {
            QName qname = new QName(tempEl.getNamespaceURI(), tempEl.getLocalName());
            if (OPERATION.equals(qname)) {
                Element inputEl = XMLHelper.getFirstChildElement(tempEl);
                while (inputEl != null) {
                    QName inputElQName = new QName(inputEl.getNamespaceURI(), inputEl.getLocalName());
                    if (INPUT.equals(inputElQName)) {
                        QName inputMessageQName = getQName(inputEl.getAttribute(ATTR_MESSAGE), namespaces);
                        if (inputMessageQName.equals(msgQName)) {
                            operation = tempEl;
                            break outer;
                        }
                    }
                    inputEl = XMLHelper.getNextSiblingElement(inputEl);
                }
            }
            tempEl = XMLHelper.getNextSiblingElement(tempEl);
        }
        return operation;
    }

    /**
     * Parse a port element and return the operation element matching the message element.
     *
     * @param portEl the portType element.
     * @param msgEl the message element.
     * @param namespaces a map of namespaceURIs
     * @return the operation element.
     */
    private Element getOperationByOutput(final Element portEl, final Element msgEl, final Map<String, String> namespaces) {
        Element tempEl = XMLHelper.getFirstChildElement(portEl);
        Element operation = null;
        QName msgQName = getQName(msgEl.getAttribute(ATTR_NAME), namespaces);

outer:  while (tempEl != null) {
            QName qname = new QName(tempEl.getNamespaceURI(), tempEl.getLocalName());
            if (OPERATION.equals(qname)) {
                Element inputEl = XMLHelper.getFirstChildElement(tempEl);
                while (inputEl != null) {
                    QName inputElQName = new QName(inputEl.getNamespaceURI(), inputEl.getLocalName());
                    if (OUTPUT.equals(inputElQName)) {
                        QName inputMessageQName = getQName(inputEl.getAttribute(ATTR_MESSAGE), namespaces);
                        if (inputMessageQName.equals(msgQName)) {
                            operation = tempEl;
                            break outer;
                        }
                    }
                    inputEl = XMLHelper.getNextSiblingElement(inputEl);
                }
            }
            tempEl = XMLHelper.getNextSiblingElement(tempEl);
        }
        return operation;
    }

    /**
     * Parse a definition element and return all message parts defined in it.
     *
     * @param defEl the definition element.
     * @param portType the portType element.
     * @param namespaces a map of namespaceURIs
     * @return a map of message parts.
     * @throws WSDLReaderException if the wsdl operation is improper
     */
    private Map<QName, QName> getParts(final Element defEl, final Element portType, Map<String, String> namespaces) throws WSDLReaderException {
        NodeList messages = defEl.getElementsByTagNameNS(MESSAGE.getNamespaceURI(), MESSAGE.getLocalPart());
        int msgSize = messages.getLength();
        Map<QName, QName> parts = new HashMap<QName, QName>();
        for (int i = 0; i < msgSize; i++) {
            Element msgEl = (Element) messages.item(i);
            NodeList partEls = msgEl.getElementsByTagNameNS(PART.getNamespaceURI(), PART.getLocalPart());
            if (_documentStyle && (partEls.getLength() != 1)) {
                throw WSDLExtensionsMessages.MESSAGES.wsdlInterfaceNeedsOneParameter();
            }
            if (_documentStyle) {
                Element partEl = (Element) partEls.item(0);
                parts.put(getQName(msgEl.getAttribute(ATTR_NAME), namespaces), getQName(partEl.getAttribute(ATTR_ELEMENT), namespaces));
            } else {
                if (!msgEl.hasAttribute(ATTR_NAME)) {
                    throw WSDLExtensionsMessages.MESSAGES.messageNameMissing();
                }
                Element operationEl = getOperationByInput(portType, msgEl, namespaces);
                if (operationEl != null) {
                    // request
                    // Create a fictional wrapper with the operation name
                    parts.put(getQName(msgEl.getAttribute(ATTR_NAME), namespaces), getQName(operationEl.getAttribute(ATTR_NAME), namespaces));
                } else {
                    // response
                    operationEl = getOperationByOutput(portType, msgEl, namespaces);
                    if (operationEl != null) {
                        // Create a fictional wrapper with the operation name + Response suffix
                        parts.put(getQName(msgEl.getAttribute(ATTR_NAME), namespaces), getQName(operationEl.getAttribute(ATTR_NAME) + RESPONSE, namespaces));
                    } else {
                        WSDLExtensionsMessages.MESSAGES.missingOperationForMessage(msgEl.getLocalName());
                    }
                }
            }
        }
        if (parts.isEmpty()) {
            List<String> wsdlImports = getImports(defEl);
            int size = wsdlImports.size();
            for (int i = 0; i < size; i++) {
                Element importedDefEl = readWSDL(wsdlImports.get(i));
                namespaces.putAll(parseNamespaces(importedDefEl));
                parts = getParts(importedDefEl, portType, namespaces);
                if (!parts.isEmpty()) {
                    break;
                }
            }
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
