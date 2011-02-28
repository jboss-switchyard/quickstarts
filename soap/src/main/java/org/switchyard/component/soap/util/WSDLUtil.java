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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.OperationType;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.switchyard.ExchangePattern;
import org.switchyard.component.soap.PortName;
import org.switchyard.component.soap.WebServicePublishException;
import org.switchyard.metadata.BaseExchangeContract;
import org.switchyard.metadata.BaseInvocationContract;
import org.switchyard.metadata.ServiceOperation;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * Contains utility methods to examine/manipulate WSDLs.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
public final class WSDLUtil {

    private static final Logger LOGGER = Logger.getLogger(WSDLUtil.class);

    /**
     * SOAP Fault type QName.
     */
    public static final QName SOAP_FAULT_MESSAGE_TYPE = 
        QName.valueOf("{http://schemas.xmlsoap.org/soap/envelope/}Fault");

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
                    "Unable to read WSDL at '"
                    + wsdlLocation, e);
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
                    "Unable to resolve WSDL document at '"
                    + wsdlURI, e);
        }
    }

    /**
     * Convert a path/uri to a URL.
     *
     * @param path a path or URI.
     * @return the URL.
     * @throws MalformedURLException If the url path is not valid
     */
    private static URL getURL(final String path) throws MalformedURLException {
        if (path.startsWith("http://") || path.startsWith("https://") || path.startsWith("file://")) {
            return new URL(null, path);
        } else {
            URL url = Thread.currentThread().getContextClassLoader().getResource(path);
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
        Service service = null;
        if (portName.getServiceQName().equals(new QName(""))) {
            service = (Service) definition.getServices().values().iterator().next();
            portName.setServiceQName(service.getQName());
        } else {
            String namespace = portName.getNamespaceURI();        
            if (namespace.equals(XMLConstants.NULL_NS_URI)) {
                namespace = definition.getTargetNamespace();
            }
            QName serviceQName = new QName(namespace, portName.getServiceName());
            Iterator<Service> services = definition.getServices().values().iterator();
            while (services.hasNext()) {
                Service wsdlService = services.next();
                if (wsdlService.getQName().equals(serviceQName)) {
                    service =  wsdlService;
                    break;
                }
            }
        }
        if (service == null) {
            throw new WSDLException("Could not find service " + portName + " in the WSDL " + wsdlLocation, null);
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
            port = (Port) wsdlService.getPorts().values().iterator().next();
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
            throw new WSDLException("Could not find port " + portName + " in the Service " + wsdlService.getQName(), null);
        }
        return port;
    }

    /**
     * Get the SOAP {@link Operation} instance for the specified SOAP operation name.
     * @param port The WSDL port.
     * @param operationName The operation name.
     * @return The Operation instance, or null if the operation was not found on the port.
     */
    public static Operation getOperation(Port port, String operationName) {
        return port.getBinding().getPortType().getOperation(operationName, null, null);
    }

    /**
     * Check if we are invoking a @Oneway annotated method.
     *
     * @param port The WSDL service port.
     * @param operationName The name of the operation obtained from SOAP message.
     * @return True if there is no response to be expected.
     */
    public static boolean isOneWay(final Port port, final String operationName) {
        // Overloaded methods not supported
        // Encrypted messages will be treated as request-response as it cannot be decrypted
        Operation operation = getOperation(port, operationName);
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
     * Get the methods Input message's name.
     *
     * @param port The WSDL service port.
     * @param operationName The name of the operation obtained from SOAP message.
     * @return The local name of the input message.
     */
    public static String getMessageLocalName(final Port port, final String operationName) {
        QName messageName = getMessageQName(port, operationName);
        if (messageName != null) {
            return messageName.getLocalPart();
        }
        return null;
    }

    /**
     * Get the methods Input message's name.
     *
     * @param port The WSDL service port.
     * @param operationName The name of the operation obtained from SOAP message.
     * @return The QName name of the input message.
     */
    public static QName getMessageQName(final Port port, final String operationName) {
        QName messageName = null;
        // Overloaded methods not supported
        // Encrypted messages will be treated as request-response as it cannot be decrypted
        Operation operation = getOperation(port, operationName);
        if (operation != null) {
            messageName = operation.getInput().getMessage().getQName();
        }
        return messageName;
    }

    /**
     * Given a Port construct the Exchange Contracts for all Operations.
     *
     * @param port The WSDL service port.
     * @param service The SwitchYard service.
     * @return A Map of exchange contracts.
     * @throws WebServicePublishException If the WSDL does not match the Service operations. 
     */
    public static Map<String, BaseExchangeContract> getContracts(final Port port, final org.switchyard.ServiceReference service) throws WebServicePublishException {
        Map<String, BaseExchangeContract> contracts = new HashMap<String, BaseExchangeContract>();
        List<Operation> operations = port.getBinding().getPortType().getOperations();
        if ((operations == null) || operations.isEmpty()) {
            throw new WebServicePublishException("Invalid WSDL. No operations found.");
        }
        for (Operation operation : operations) {
            String name = operation.getName();
            ServiceOperation targetServiceOperation = service.getInterface().getOperation(name);
            if (targetServiceOperation == null) {
                throw new WebServicePublishException("WSDL Operation " + name + " not found in Service " + service.getName());
            }
            ExchangePattern wsdlExchangePattern = getExchangePattern(operation);
            if (targetServiceOperation.getExchangePattern() != wsdlExchangePattern) {
                throw new WebServicePublishException("WSDL Operation " + name + " does not match Service Operation " + targetServiceOperation.getName());
            }
            BaseExchangeContract exchangeContract = new BaseExchangeContract(targetServiceOperation);
            BaseInvocationContract soapMetaData = exchangeContract.getInvokerInvocationMetaData();
            QName inputMessageQName = operation.getInput().getMessage().getQName();
            soapMetaData.setInputType(inputMessageQName);
            soapMetaData.setFaultType(SOAP_FAULT_MESSAGE_TYPE);

            if (!isOneWay(operation)) {
                QName outputMessageQName = operation.getOutput().getMessage().getQName();
                soapMetaData.setOutputType(outputMessageQName);
            }
            contracts.put(name, exchangeContract);
        }
        return contracts;
    }

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
