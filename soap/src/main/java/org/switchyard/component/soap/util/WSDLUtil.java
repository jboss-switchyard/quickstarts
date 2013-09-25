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
 
package org.switchyard.component.soap.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Import;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.OperationType;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.Types;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.UnknownExtensibilityElement;
import javax.wsdl.extensions.schema.Schema;
import javax.wsdl.extensions.schema.SchemaImport;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.wsdl.extensions.soap12.SOAP12Address;
import javax.wsdl.extensions.soap12.SOAP12Binding;
import javax.wsdl.extensions.soap12.SOAP12Operation;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.jboss.logging.Logger;
import org.switchyard.ExchangePattern;
import org.switchyard.common.type.Classes;
import org.switchyard.common.xml.XMLHelper;
import org.switchyard.component.soap.SOAPMessages;
import org.switchyard.component.soap.Feature;
import org.switchyard.component.soap.PortName;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Contains utility methods to examine/manipulate WSDLs.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
public final class WSDLUtil {

    private static final Logger LOGGER = Logger.getLogger(WSDLUtil.class);

    /**
     * WSDL SOAP 1.1 QName namespace.
     */
    public static final String WSDL_SOAP11_URI = "http://schemas.xmlsoap.org/wsdl/soap/";

    /**
     * WSDL SOAP 1.2 QName namespace.
     */
    public static final String WSDL_SOAP12_URI = "http://schemas.xmlsoap.org/wsdl/soap12/";

    /**
     * Document style.
     */
    public static final String DOCUMENT = "document";

    private static final String SCHEME_URN = "urn:";
    private static final String STR_ACTION = "Action";
    private static final String STR_COLON = ":";
    private static final String STR_SLASH = "/";
    private static final String STR_FAULT = "Fault";
    private static final String REQUEST_SUFFIX = "Request";
    private static final String RESPONSE_SUFFIX = "Response";
    private static final String ATTR_ID = "Id";
    private static final String ATTR_REQUIRED = "required";
    private static final String ATTR_URI = "URI";
    private static final String ATTR_EXP_CT = "expectedContentTypes";
    private static final String ATTR_LOCATION = "location";
    private static final String ELE_ADDRESSING = "Addressing";
    private static final String ELE_ALL = "All";
    private static final String ELE_EXACTLYONE = "ExactlyOne";
    private static final String ELE_MTOM = "MTOM";
    private static final String WSA_METADATA_URI = "http://www.w3.org/2007/05/addressing/metadata";
    private static final String WSA_WSDL_URI = "http://www.w3.org/2006/05/addressing/wsdl";
    private static final String POLICY_URI = "http://www.w3.org/ns/ws-policy";
    private static final String WS_MTOM_URI = "http://www.w3.org/2007/08/soap12-mtom-policy";
    private static final String WSDL_XMIME_URI = "http://www.w3.org/2005/05/xmlmime";
    private static final String SECURITY_UTILITY_URI = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd";
    // No support for WSDL2.0 yet, as CXF doesn't have it.
    // http://cxf.apache.org/project-status.html - GSoC project just started
    // private static final String WSDL20_URI = "http://www.w3.org/2006/01/wsdl";
    private static final String WSDL11_URI = "http://schemas.xmlsoap.org/wsdl/";
    private static final QName USING_WSA_QNAME = new QName(WSA_WSDL_URI, "UsingAddressing");
    private static final QName POLICY_QNAME = new QName(POLICY_URI, "Policy");
    private static final QName POLICY_REFERENCE_QNAME = new QName(POLICY_URI, "PolicyReference");
    private static final QName POLICY_EXACTLYONE_QNAME = new QName(POLICY_URI, ELE_EXACTLYONE);
    private static final QName POLICY_ALL_QNAME = new QName(POLICY_URI, ELE_ALL);
    private static final QName WSA_QNAME = new QName(WSA_METADATA_URI, ELE_ADDRESSING);
    private static final QName WSDL_ACTION_QNAME = new QName(WSA_WSDL_URI, STR_ACTION);
    private static final QName WSDL_MTOM_QNAME = new QName(WS_MTOM_URI, ELE_MTOM);
    private static final QName MTOM_EXPT_QNAME = new QName(WSDL_XMIME_URI, ATTR_EXP_CT);

    private WSDLUtil() {
    }

    /**
     * Read the WSDL document and create a WSDL Definition.
     *
     * @param wsdlLocation location pointing to a WSDL XML definition.
     * @return the Definition.
     * @throws WSDLException If unable to read the WSDL
     */
    public static Definition readWSDL(final String wsdlLocation) throws WSDLException {
        InputStream inputStream = null;
        try {
            URL url = getURL(wsdlLocation);
            inputStream = url.openStream();
            InputSource source = new InputSource(inputStream);
            source.setSystemId(url.toString());
            Document wsdlDoc = XMLHelper.getDocument(source);
            WSDLFactory wsdlFactory = WSDLFactory.newInstance();
            WSDLReader reader = wsdlFactory.newWSDLReader();
            reader.setFeature("javax.wsdl.verbose", false);
            return reader.readWSDL(url.toString(), wsdlDoc);
        } catch (Exception e) {
            throw new WSDLException(WSDLException.OTHER_ERROR,
                    SOAPMessages.MESSAGES.unableToReadWSDL(wsdlLocation), e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ioe) {
                    LOGGER.error(ioe);
                }
            }
        }
    }

    /**
     * Read the WSDL document accessible via the specified
     * URI into a StreamSource.
     *
     * @param wsdlURI a URI (can be a filename or URL) pointing to a
     * WSDL XML definition.
     * @return the StreamSource.
     * @throws WSDLException If unable to read the WSDL
     */
    public static StreamSource getStream(final String wsdlURI) throws WSDLException {
        try {
            URL url = getURL(wsdlURI);
            InputStream inputStream = url.openStream();
            StreamSource inputSource = new StreamSource(inputStream);
            inputSource.setSystemId(url.toString());
            return inputSource;
        } catch (Exception e) {
            throw new WSDLException(WSDLException.OTHER_ERROR, 
                    SOAPMessages.MESSAGES.unableToResolveWSDL(wsdlURI.toString()), e);
            
        }
    }

    /**
     * Convert a path/uri to a URL.
     *
     * @param path a path or URI.
     * @return the URL.
     * @throws MalformedURLException If the url path is not valid
     */
    public static URL getURL(final String path) throws MalformedURLException {
        if (path.startsWith("http://") || path.startsWith("https://") || path.startsWith("file://")) {
            return new URL(null, path);
        } else {
            URL url;
            try {
                url = Classes.getResource(path, WSDLUtil.class);
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

    /**
     * Get the Service from the WSDL given a PortName.
     * If the PortName.getServiceQName() is empty (QName("")) then this method returns the first found Service.
     *
     * @param wsdlLocation location pointing to a WSDL XML definition.
     * @param portName the PortName.
     * @return the Service.
     * @throws WSDLException If the Service could not be retrieved.
     */
    public static Service getService(final String wsdlLocation, final PortName portName) throws WSDLException {
        Definition definition = readWSDL(wsdlLocation);
        return getService(definition, portName);
    }

    /**
     * Get the Service from the WSDL Definition given a PortName.
     * If the PortName.getServiceQName() is empty (QName("")) then this method returns the first found Service.
     *
     * @param definition the WSDL XML definition.
     * @param portName the PortName.
     * @return the Service.
     * @throws WSDLException If the Service could not be retrieved.
     */
    public static Service getService(final Definition definition, final PortName portName) throws WSDLException {
        Service service = null;
        if (portName.getServiceQName().equals(new QName(""))) {
            service = (Service) definition.getAllServices().values().iterator().next();
            portName.setServiceQName(service.getQName());
        } else {
            String namespace = portName.getNamespaceURI();
            Boolean namespaceSet = false;
            if (namespace.equals(XMLConstants.NULL_NS_URI)) {
                namespace = definition.getTargetNamespace();
            } else {
                namespaceSet = true;
            }
            QName serviceQName = new QName(namespace, portName.getServiceName());
            Iterator<Service> services = definition.getAllServices().values().iterator();
outer:      while (services.hasNext()) {
                Service wsdlService = services.next();
                if (wsdlService.getQName().equals(serviceQName)) {
                    service =  wsdlService;
                    break;
                } else if (!namespaceSet) {
                    Iterator<String> importedNamespaces = definition.getImports().keySet().iterator();
                    while (importedNamespaces.hasNext()) {
                        QName qName = new QName(importedNamespaces.next(), portName.getServiceName());
                        if (wsdlService.getQName().equals(qName)) {
                            service =  wsdlService;
                            break outer;
                        }
                    }
                }
            }
        }
        if (service == null) {
            throw new WSDLException(SOAPMessages.MESSAGES.couldNotFindServiceInTheWSDL(portName.toString(), definition.getDocumentBaseURI()), null);
        }
        return service;
    }

    /**
     * Get the Port from the Service given a port name string. If the PortName.getName() is null then this method returns the first found Port.
     *
     * @param wsdlService the Service to be queried for.
     * @param portName the PortName.
     * @return the Webservice Port.
     * @throws WSDLException If the Port could not be found.
     */
    public static Port getPort(final Service wsdlService, final PortName portName) throws WSDLException {
        String name = portName.getName();
        Port port = null;
        if ((name == null) || (name.length() == 0)) {
            try {
                port = (Port) wsdlService.getPorts().values().iterator().next();
            } catch (NoSuchElementException nsee) {
                throw new WSDLException(SOAPMessages.MESSAGES.couldNotFindAPortDefinitionWithinService(wsdlService.getQName().toString()), null);
            }
        } else {
            Iterator<Port> ports = wsdlService.getPorts().values().iterator();
            while (ports.hasNext()) {
                Port wsdlPort = ports.next();
                if (wsdlPort.getName().equals(name)) {
                    port = wsdlPort;
                    break;
                }
            }
        }
        if (port == null) {
            throw new WSDLException(SOAPMessages.MESSAGES.couldNotFindPortInTheService(portName.toString(), wsdlService.getQName().toString()), null);
        }
        return port;
    }

    /**
     * Get the style for the port binding.
     * @param port The WSDL port.
     * @return The style, can be 'document' or 'rpc'.
     */
    @SuppressWarnings("unchecked")
    public static String getStyle(Port port) {
        String portStyle = null;
        String bindingStyle = null;
        for (ExtensibilityElement element : (List<ExtensibilityElement>) port.getBinding().getExtensibilityElements()) {
            if ((element instanceof SOAPBinding) && (((SOAPBinding)element).getStyle() != null)) {
                 bindingStyle = ((SOAPBinding) element).getStyle();
            } else if ((element instanceof SOAP12Binding) && (((SOAP12Binding)element).getStyle() != null)) {
                 bindingStyle = ((SOAP12Binding) element).getStyle();
            }
            if (bindingStyle != null) {
                portStyle = bindingStyle.toLowerCase();
            }
        }

        String operationStyle = null;
        String currentOperationStyle = null;
        for (BindingOperation operation : (List<BindingOperation>) port.getBinding().getBindingOperations()) {
            for (ExtensibilityElement element : (List<ExtensibilityElement>) operation.getExtensibilityElements()) {
                if (element instanceof SOAPOperation) {
                    currentOperationStyle = ((SOAPOperation) element).getStyle();
                } else if (element instanceof SOAP12Operation) {
                    currentOperationStyle = ((SOAP12Operation) element).getStyle();
                }
                if (currentOperationStyle != null) {
                    if (operationStyle != null && !currentOperationStyle.equals(operationStyle)) {
                        throw SOAPMessages.MESSAGES.incompatibleStyleOfSoapOperationLevelBindingsDetected();
                    }
                    operationStyle = currentOperationStyle;
                }
            }
        }

        if (operationStyle != null && portStyle != null) {
            if (!portStyle.equals(operationStyle)) {
                throw SOAPMessages.MESSAGES.detectedMixingDifferentSoapBindingStyleOnPortTypeAndOperationLevel();
            }
            return portStyle;
        } else if (portStyle != null) {
            return portStyle;
        } else if (operationStyle != null) {
            return operationStyle;
        }
        return DOCUMENT; //default
    }

    /**
     * Get the SOAP {@link Operation} instance for the specified message element.
     *
     * @param port The WSDL port.
     * @param elementName The SOAP Body element QName.
     * @param documentStyle true if it is 'document', false if 'rpc'.
     * @return The Operation instance, or null if the operation was not found on the port.
     */
    public static Operation getOperationByElement(Port port, QName elementName, Boolean documentStyle) {
        List<Operation> operations = port.getBinding().getPortType().getOperations();

        for (Operation operation : operations) {
            if (!documentStyle && (elementName.getLocalPart().equals(operation.getName()))) {
                return operation;
            } else {
                // Note: WS-I Profile allows only one child under SOAPBody.
                Part part = (Part)operation.getInput().getMessage().getParts().values().iterator().next();
                if ((part.getElementName() != null) && elementName.equals(part.getElementName())
                    || (part.getTypeName() != null) && elementName.equals(part.getTypeName())) {
                    return operation;
                } else if (elementName.getLocalPart().equals(operation.getName())) {
                    return operation;
                }
            }
        }
        return null;
    }
    
    /**
     * Get the {@link Operation} instance for the specified SOAP operation name.
     * @param port The WSDL port.
     * @param operationName The SOAP Body element name.
     * @return The Operation instance, or null if the operation was not found on the port.
     */
    public static Operation getOperationByName(Port port, String operationName) {
        Operation operation = null;
        List<Operation> operationList = port.getBinding().getPortType().getOperations();
        
        for (Operation op : operationList) {
            if (op.getName().equals(operationName)) {
                operation = op;
                break;
            }
        }
        return operation;
    }

    /**
     * Get the SOAP Binding Id for the specified {@link Port}.
     *
     * @param port The WSDL port.
     * @param mtomEnabled MTOM feature boolean
     * @return The SOAPBinding Id found on the port.
     */
    public static String getBindingId(Port port, Boolean mtomEnabled) {
        String bindingId = null; 
        List<ExtensibilityElement> extElements = port.getExtensibilityElements();
        for (ExtensibilityElement extElement : extElements) {
            if (extElement instanceof SOAP12Address) {
                if (mtomEnabled) {
                    bindingId = javax.xml.ws.soap.SOAPBinding.SOAP12HTTP_MTOM_BINDING;
                } else {
                    bindingId = javax.xml.ws.soap.SOAPBinding.SOAP12HTTP_BINDING;
                }
                break;
            } else {
                if (mtomEnabled) {
                    bindingId = javax.xml.ws.soap.SOAPBinding.SOAP11HTTP_MTOM_BINDING;
                } else {
                    bindingId = javax.xml.ws.soap.SOAPBinding.SOAP11HTTP_BINDING;
                }
                break;
            }
        }
        return bindingId;
    }

    /**
     * Get the SOAP Binding Id for the specified {@link Port}.
     *
     * @param port The WSDL port.
     * @return The endpoint address.
     */
    public static String getEndpointAddress(Port port) {
        String address = null; 
        List<ExtensibilityElement> extElements = port.getExtensibilityElements();
        for (ExtensibilityElement extElement : extElements) {
            if (extElement instanceof SOAPAddress) {
                address = ((SOAPAddress)extElement).getLocationURI();
                break;
            } else if (extElement instanceof SOAP12Address) {
                address = ((SOAP12Address)extElement).getLocationURI();
                break;
            }
        }
        return address;
    }

    /**
     * Check if we are invoking a @Oneway annotated method.
     *
     * @param port The WSDL service port.
     * @param elementName The SOAP Body element QName.
     * @param documentStyle true if it is 'document', false if 'rpc'.
     * @return True if there is no response to be expected.
     */
    public static boolean isOneWay(final Port port, final QName elementName, Boolean documentStyle) {
        // Overloaded methods not supported
        // Encrypted messages will be treated as request-response as it cannot be decrypted
        Operation operation = getOperationByElement(port, elementName, documentStyle);
        return isOneWay(operation);
    }
    
    /**
     * Check if we are invoking a @Oneway annotated method.
    *
    * @param operation The WSDL Operation.
    * @return True if there is no response to be expected.
    */
   public static boolean isOneWay(final Operation operation) {
       boolean isOneWay = false;
       if (operation != null) {
           isOneWay = operation.getStyle().equals(OperationType.ONE_WAY);
       }
       return isOneWay;
   }

   /**
     * Get the SOAP {@link BindingOperation} instance for the specified SOAP operation name.
     * @param port The WSDL port.
     * @param elementName The SOAP Body element QName.
     * @param documentStyle true if it is 'document', false if 'rpc'.
     * @return The BindingOperation instance, or null if the operation was not found on the port.
     */
    public static BindingOperation getBindingOperation(Port port, QName elementName, Boolean documentStyle) {
        Operation operation = getOperationByElement(port, elementName, documentStyle);
        if (operation != null) {
            List<BindingOperation> bindingOperations = port.getBinding().getBindingOperations();
            for (BindingOperation bindingOperation : bindingOperations) {
                if (bindingOperation.getName().equals(operation.getName())) {
                    return bindingOperation;
                }
            }
        }
        return null;
    }

    /**
     * Get the soapAction value for a given operation.
     *
     * @param port The WSDL service port.
     * @param elementName The SOAP Body element QName.
     * @param documentStyle true if it is 'document', false if 'rpc'.
     * @return the soapAction value if it exists.
     */
    public static String getSoapAction(final Port port, final QName elementName, Boolean documentStyle) {
        // Overloaded methods not supported
        BindingOperation operation = getBindingOperation(port, elementName, documentStyle);
        return getSoapAction(operation);
    }

    /**
     * Get the soapAction value for a given operation.
     *
     * @param operation The WSDL BindingOperation.
     * @return the soapAction value if it exists.
     */
    public static String getSoapAction(final BindingOperation operation) {
        String soapActionUri = "";
        if (operation != null) {
            List<ExtensibilityElement> extElements = operation.getExtensibilityElements();
            for (ExtensibilityElement extElement : extElements) {
                if (extElement instanceof SOAPOperation) {
                    soapActionUri = ((SOAPOperation) extElement).getSoapActionURI();
                    break;
                } else if (extElement instanceof SOAP12Operation) {
                    SOAP12Operation soapOperation = ((SOAP12Operation) extElement);
                    Boolean soapActionRequired = soapOperation.getSoapActionRequired();
                    if ((soapActionRequired == null) || soapActionRequired) {
                        soapActionUri = soapOperation.getSoapActionURI();
                    }
                    break;
                }
            }
        }
        return soapActionUri;
    }

    /**
     * Get the input action value for a given operation.
     *
     * @param port The WSDL service port.
     * @param operationName The SOAP Operation element QName.
     * @param targetNamespace The target namespace.
     * @param documentStyle true if it is 'document', false if 'rpc'.
     * @return the input action value.
     */
    public static String getInputAction(final Port port, final QName operationName, String targetNamespace, Boolean documentStyle) {
        // Overloaded methods not supported
        Operation operation = getOperationByElement(port, operationName, documentStyle);
        String action = null;
        for (QName attribute : (Set<QName>)operation.getInput().getExtensionAttributes().keySet()) {
            if (attribute.equals(WSDL_ACTION_QNAME)) {
                Object value = operation.getInput().getExtensionAttribute(WSDL_ACTION_QNAME);
                if (value != null) {
                    action = ((QName)value).getLocalPart();
                    break;
                }
            }
        }

        if (action == null) {
            // http://www.w3.org/TR/2006/WD-ws-addr-wsdl-20060216/#defactionwsdl11
            // [target namespace][delimiter][port type name][delimiter][input name]
            String delimiter = targetNamespace.startsWith(SCHEME_URN) ? STR_COLON : STR_SLASH;
            String namespace = targetNamespace.endsWith(delimiter) ? targetNamespace.substring(0, targetNamespace.length()-2) : targetNamespace;
            if (operation.getInput().getName() != null) {
                action = namespace + delimiter + port.getBinding().getPortType().getQName().getLocalPart() + delimiter + operation.getInput().getName();
            } else {
                action = namespace + delimiter + port.getBinding().getPortType().getQName().getLocalPart() + delimiter + operation.getName() + REQUEST_SUFFIX;
            }
        }

        return action;
    }

    /**
     * Get the output action value for a given operation.
     *
     * @param port The WSDL service port.
     * @param operationName The SOAP Operation element QName.
     * @param targetNamespace The target namespace.
     * @param documentStyle true if it is 'document', false if 'rpc'.
     * @return the output action value.
     */
    public static String getOutputAction(final Port port, final QName operationName, String targetNamespace, Boolean documentStyle) {
        // Overloaded methods not supported
        Operation operation = getOperationByElement(port, operationName, documentStyle);
        String action = null;

        for (QName attribute : (Set<QName>)operation.getOutput().getExtensionAttributes().keySet()) {
            if (attribute.equals(WSDL_ACTION_QNAME)) {
                Object value = operation.getOutput().getExtensionAttribute(WSDL_ACTION_QNAME);
                if (value != null) {
                    action = ((QName)value).getLocalPart();
                    break;
                }
            }
        }

        if (action == null) {
            // http://www.w3.org/TR/2006/WD-ws-addr-wsdl-20060216/#defactionwsdl11
            // [target namespace][delimiter][port type name][delimiter][output name]
            String delimiter = targetNamespace.startsWith(SCHEME_URN) ? STR_COLON : STR_SLASH;
            String namespace = targetNamespace.endsWith(delimiter) ? targetNamespace.substring(0, targetNamespace.length()-2) : targetNamespace;
            if (operation.getOutput().getName() != null) {
                action = namespace + delimiter + port.getBinding().getPortType().getQName().getLocalPart() + delimiter + operation.getOutput().getName();
            } else {
                action = namespace + delimiter + port.getBinding().getPortType().getQName().getLocalPart() + delimiter + operation.getName() + RESPONSE_SUFFIX;
            }
        }

        return action;
    }

    /**
     * Get the fault action value for a given operation.
     *
     * @param port The WSDL service port.
     * @param operationName The SOAP Operation element QName.
     * @param targetNamespace The target namespace.
     * @param faultName The fault name.
     * @param documentStyle true if it is 'document', false if 'rpc'.
     * @return the fault action value.
     */
    public static String getFaultAction(final Port port, final QName operationName, String targetNamespace, String faultName, Boolean documentStyle) {
        // Overloaded methods not supported
        Operation operation = getOperationByElement(port, operationName, documentStyle);
        String action = null;

        if (operation.getFault(faultName) == null) {
            throw SOAPMessages.MESSAGES.faultNameNotFoundOnOperation(faultName, operationName.getLocalPart());
        }

        for (QName attribute : (Set<QName>)operation.getFault(faultName).getExtensionAttributes().keySet()) {
            if (attribute.equals(WSDL_ACTION_QNAME)) {
                Object value = operation.getFault(faultName).getExtensionAttribute(WSDL_ACTION_QNAME);
                if (value != null) {
                    action = ((QName)value).getLocalPart();
                    break;
                }
            }
        }

        if (action == null) {
            // http://www.w3.org/TR/2006/WD-ws-addr-wsdl-20060216/#defactionwsdl11
            // [target namespace][delimiter][port type name][delimiter][operation name][delimiter]Fault[delimiter][fault name]
            String delimiter = targetNamespace.startsWith(SCHEME_URN) ? STR_COLON : STR_SLASH;
            String namespace = targetNamespace.endsWith(delimiter) ? targetNamespace.substring(0, targetNamespace.length()-2) : targetNamespace;
            action = namespace + delimiter + port.getBinding().getPortType().getQName().getLocalPart() + delimiter
                            + operationName.getLocalPart() + delimiter + STR_FAULT + delimiter + faultName;
        }

        return action;
    }

    /**
     * Get enabled features.
     *
     * @param definition The WSDL definition.
     * @param port The WSDL service port.
     * @param documentStyle true if it is 'document', false if 'rpc'.
     * @return the feature booleans.
     */
    public static Feature getFeature(final Definition definition, final Port port, Boolean documentStyle) {
        Feature feature = new Feature();
        Boolean addressing = false;
        Boolean mtom = false;
        List<ExtensibilityElement> extensibilityElements = getExtensibilityElements(definition);
        if (documentStyle) {
            // Check if any element uses xmime:expectedContentTypes
            for (ExtensibilityElement element : extensibilityElements) {
                if (element instanceof Schema) {
                    mtom = setMtomEnabled((Schema)element);
                    if (mtom) {
                        feature.setMtomEnabled(true);
                        break;
                    }
                }
            }
        } else {
            // Check if any part uses xmime:expectedContentTypes
            for (Message message : ((Map<String, Message>)definition.getMessages()).values()) {
                for (Part part : (List<Part>)message.getOrderedParts(null)) {
                    if (part.getExtensionAttribute(MTOM_EXPT_QNAME) != null) {
                        feature.setMtomEnabled(true);
                    }
                }
            }
        }

outer:  for (ExtensibilityElement element : (List<ExtensibilityElement>)port.getBinding().getExtensibilityElements()) {
            if (element instanceof UnknownExtensibilityElement) {
                String attrValue = null;
                Element domElement = ((UnknownExtensibilityElement)element).getElement();
                if (element.getElementType().equals(USING_WSA_QNAME)) {
                    feature.setAddressingEnabled(true);
                    setAddressingRequired(feature, domElement);
                    break;
                } else if (element.getElementType().equals(POLICY_REFERENCE_QNAME)) {
                    String uri = XMLHelper.getAttribute(domElement, WSDL11_URI, ATTR_URI);
                    /*if (uri == null) {
                        uri = XMLHelper.getAttribute(domElement, WSDL20_URI, ATTR_URI);
                    }*/
                    if (uri == null) {
                        throw SOAPMessages.MESSAGES.policyReferenceURIMissingFor(port.getBinding().getQName().getLocalPart());
                    }
                    uri = uri.substring(1);
                    for (ExtensibilityElement defElement : extensibilityElements) {
                        if (defElement.getElementType().equals(POLICY_QNAME)) {
                            Element defDomElement = ((UnknownExtensibilityElement)defElement).getElement();
                            attrValue = XMLHelper.getAttribute(defDomElement, SECURITY_UTILITY_URI, ATTR_ID);
                            if ((attrValue != null)
                                && attrValue.equals(uri)) {
                                // Addressing
                                if (!addressing) {
                                    addressing = setAddressingEnabled(feature, defDomElement);
                                }
                                // MTOM
                                if (!mtom) {
                                    mtom = setMtomEnabled(feature, defDomElement);
                                }
                                if (addressing && mtom) {
                                    break outer;
                                }
                            }
                        }
                    }
                }
            }
        }
        return feature;
    }

    private static List<ExtensibilityElement> getExtensibilityElements(Definition definition) {
        List<ExtensibilityElement> elements = new ArrayList<ExtensibilityElement>();
        Types types = definition.getTypes();
        if (types != null) {
            elements.addAll(definition.getExtensibilityElements());
            elements.addAll(types.getExtensibilityElements());
        }
        Iterator<List<Import>> wsdlImports = definition.getImports().values().iterator();
        while (wsdlImports.hasNext()) {
            List<Import> imports = wsdlImports.next();
            int size = imports.size();
            for (int i = 0; i < size; i++) {
                elements.addAll(getExtensibilityElements(imports.get(i).getDefinition()));
            }
        }
        return elements;
    }

    private static Boolean setAddressingEnabled(Feature feature, Element defDomElement) {
        Element child = XMLHelper.getFirstChildElementByName(defDomElement, ELE_ADDRESSING);
        if (child != null) {
            feature.setAddressingEnabled(true);
            setAddressingRequired(feature, child);
            return true;
        } else {
            child = XMLHelper.getFirstChildElementByName(defDomElement, ELE_EXACTLYONE);
            if (child != null) {
                child = XMLHelper.getFirstChildElementByName(child, ELE_ALL);
                while (child != null) {
                    Element addressingEle = XMLHelper.getFirstChildElementByName(child, ELE_ADDRESSING);
                    if (addressingEle != null) {
                        feature.setAddressingEnabled(true);
                        setAddressingRequired(feature, addressingEle);
                        return true;
                    }
                    child = XMLHelper.getNextSiblingElementByName(child, ELE_ALL);
                }
            }
        }
        return false;
    }

    private static void setAddressingRequired(Feature feature, Element element) {
        String attrValue = XMLHelper.getAttribute(element, WSDL11_URI, ATTR_REQUIRED);
        /*if (attrValue == null) {
            attrValue = XMLHelper.getAttribute(element, WSDL20_URI, ATTR_REQUIRED);
        }*/
        if (attrValue != null) {
            feature.setAddressingRequired(Boolean.valueOf(attrValue));
        }
    }

    private static Boolean setMtomEnabled(Feature feature, Element defDomElement) {
        Element child = XMLHelper.getFirstChildElementByName(defDomElement, ELE_MTOM);
        if (child != null) {
            feature.setMtomEnabled(true);
            return true;
        } else {
            child = XMLHelper.getFirstChildElementByName(defDomElement, ELE_EXACTLYONE);
            if (child != null) {
                child = XMLHelper.getFirstChildElementByName(child, ELE_ALL);
                while (child != null) {
                    Element addressingEle = XMLHelper.getFirstChildElementByName(child, ELE_MTOM);
                    if (addressingEle != null) {
                        feature.setMtomEnabled(true);
                        return true;
                    }
                    child = XMLHelper.getNextSiblingElementByName(child, ELE_ALL);
                }
            }
        }
        return false;
    }

    // Caution: recursive
    private static Boolean setMtomEnabled(Schema schema) {
        if (schema != null) {
            if (hasExpectedContentTypes(schema.getElement())) {
                return true;
            }
            for (List<SchemaImport> c : ((Map<String, List<SchemaImport>>)schema.getImports()).values()) {
                for (SchemaImport simport : c) {
                    if (setMtomEnabled(simport.getReferencedSchema())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Caution: recursive
    private static Boolean hasExpectedContentTypes(Element element) {
        if (element != null) {
            if (XMLHelper.hasAttribute(element, WSDL_XMIME_URI, ATTR_EXP_CT)) {
                return true;
            }
            NodeList children = element.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node node = children.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    if (hasExpectedContentTypes((Element)node)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Given a Port construct the Exchange Contracts for all Operations.
     *
     * @param port The WSDL service port.
     * @param service The SwitchYard service.
     * @return A Map of exchange contracts.
     * @throws WebServicePublishException If the WSDL does not match the Service operations. 
     
    public static Map<String, BaseExchangeContract> getContracts(final Port port, final org.switchyard.ServiceReference service) throws WebServicePublishException {
        Map<String, BaseExchangeContract> contracts = new HashMap<String, BaseExchangeContract>();
        List<Operation> operations = port.getBinding().getPortType().getOperations();
        if ((operations == null) || operations.isEmpty()) {
            throw SOAPMessages.MESSAGES.invalidWSDLNoOperationsFound();
        }
        for (Operation operation : operations) {
            String name = operation.getName();
            ServiceOperation targetServiceOperation = service.getInterface().getOperation(name);
            if (targetServiceOperation == null) {
                throw SOAPMessages.MESSAGES.wSDLOperationNotFoundInService(name, service.getName());
            }
            ExchangePattern wsdlExchangePattern = getExchangePattern(operation);
            if (targetServiceOperation.getExchangePattern() != wsdlExchangePattern) {
                throw SOAPMessages.MESSAGES.wSDLOperationDoesNotMatchServiceOperation(name, targetServiceOperation.getName());
            }
            BaseExchangeContract exchangeContract = new BaseExchangeContract(targetServiceOperation);
            BaseInvocationContract soapMetaData = exchangeContract.getInvokerInvocationMetaData();
            List<Part> parts = operation.getInput().getMessage().getOrderedParts(null);
            if (parts.isEmpty()) {
                throw SOAPMessages.MESSAGES.wSDLOperationDoesNotHaveAnyInputMessageParts(name);
            }
            // Only one Part (one child of the soap:body) allowed per WS-I Basic Profile similar to Document/Literal wrapped
            QName inputMessageQName = parts.get(0).getElementName();
            soapMetaData.setInputType(inputMessageQName);
            soapMetaData.setFaultType(SOAP_FAULT_MESSAGE_TYPE);

            if (!isOneWay(operation)) {
                parts = operation.getOutput().getMessage().getOrderedParts(null);
                if (parts.isEmpty()) {
                    throw SOAPMessages.MESSAGES.wSDLOperationDoesNotHaveAnyOuputMessageParts(name);
                }
                // Only one Part (one child of the soap:body) allowed per WS-I Basic Profile similar to Document/Literal wrapped
                QName outputMessageQName = parts.get(0).getElementName();
                soapMetaData.setOutputType(outputMessageQName);
            }
            contracts.put(name, exchangeContract);
        }
        return contracts;
    }
    */

    /**
     * Get the exchange pattern for the specified WS Operation.
     *
     * @param operation The operation to check for.
     * @return The Exchange Pattern.
     */
    public static ExchangePattern getExchangePattern(final Operation operation) {
        if (operation.getStyle().equals(OperationType.ONE_WAY)) {
            return ExchangePattern.IN_ONLY;
        } else {
            return ExchangePattern.IN_OUT;
        }
    }
}
