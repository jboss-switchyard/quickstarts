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

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.wsdl.Operation;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Endpoint;

import org.apache.log4j.Logger;
import org.switchyard.Exchange;
import org.switchyard.ExchangeState;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.Scope;
import org.switchyard.ServiceDomain;
import org.switchyard.ServiceReference;
import org.switchyard.SynchronousInOutHandler;
import org.switchyard.component.common.composer.MessageComposer;
import org.switchyard.component.soap.composer.SOAPComposition;
import org.switchyard.component.soap.config.model.SOAPBindingModel;
import org.switchyard.component.soap.util.SOAPUtil;
import org.switchyard.component.soap.util.WSDLUtil;
import org.switchyard.deploy.BaseServiceHandler;
import org.switchyard.exception.DeliveryException;
import org.w3c.dom.Node;

/**
 * Hanldes SOAP requests to invoke a SwitchYard service.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
public class InboundHandler extends BaseServiceHandler {

    private static final Logger LOGGER = Logger.getLogger(InboundHandler.class);
    private static final long DEFAULT_TIMEOUT = 15000;
    private static final String MESSAGE_NAME = "MESSAGE_NAME";
    private static final String WSDL_LOCATION = "javax.xml.ws.wsdl.description";

    private final SOAPBindingModel _config;
    private final MessageComposer<SOAPMessage> _messageComposer;

    private ServiceDomain _domain;
    private ServiceReference _service;
    private long _waitTimeout = DEFAULT_TIMEOUT; // default of 15 seconds
    private Endpoint _endpoint;
    private Port _wsdlPort;
    private String _scheme = "http";
    private String _bindingId;

    /**
     * Constructor.
     * @param config the configuration settings
     * @param domain the service domain
     */
    public InboundHandler(final SOAPBindingModel config, ServiceDomain domain) {
        _config = config;
        _domain = domain;
        _messageComposer = SOAPComposition.getMessageComposer(config);
    }

    /**
     * Start lifecycle.
     * @throws WebServicePublishException If unable to publish the endpoint
     */
    public void start() throws WebServicePublishException {
        ClassLoader origLoader = Thread.currentThread().getContextClassLoader();
        try {
            _service = _domain.getServiceReference(_config.getServiceName());
            PortName portName = _config.getPort();
            javax.wsdl.Service wsdlService = WSDLUtil.getService(_config.getWsdl(), portName);
            _wsdlPort = WSDLUtil.getPort(wsdlService, portName);
            // Update the portName
            portName.setServiceQName(wsdlService.getQName());
            portName.setName(_wsdlPort.getName());
            
            BaseWebService wsProvider = new BaseWebService();
            wsProvider.setInvocationClassLoader(Thread.currentThread().getContextClassLoader());
            // Hook the handler
            wsProvider.setConsumer(this);
            
            List<Source> metadata = new ArrayList<Source>();
            StreamSource source = WSDLUtil.getStream(_config.getWsdl());
            metadata.add(source);
            Map<String, Object> properties = new HashMap<String, Object>();
            properties.put(Endpoint.WSDL_SERVICE, portName.getServiceQName());
            properties.put(Endpoint.WSDL_PORT, portName.getPortQName());
            properties.put(WSDL_LOCATION, WSDLUtil.getURL(_config.getWsdl()).toExternalForm());

            String path = "/" + portName.getServiceName();
            if (_config.getContextPath() != null) {
                path = "/" + _config.getContextPath() + "/" + portName.getServiceName();
            }
            String publishUrl = _scheme + "://" + _config.getSocketAddr().getHost() + ":" + _config.getSocketAddr().getPort() + path;

            LOGGER.info("Publishing WebService at " + publishUrl);
            // make sure we don't pollute the class loader used by the WS subsystem
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

            _bindingId = WSDLUtil.getBindingId(_wsdlPort);

            _endpoint = Endpoint.create(_bindingId, wsProvider);
            _endpoint.setMetadata(metadata);
            _endpoint.setProperties(properties);
            _endpoint.publish(publishUrl);
        } catch (MalformedURLException e) {
            throw new WebServicePublishException(e);
        } catch (WSDLException e) {
            throw new WebServicePublishException(e);
        } finally {
            Thread.currentThread().setContextClassLoader(origLoader);
        }
    }

    /**
     * Stop lifecycle.
     */
    public void stop() {
        _endpoint.stop();
        LOGGER.info("WebService " + _config.getPort() + " stopped.");
    }

    @Override
    public void handleFault(Exchange exchange) {
        // TODO: Why is this class an ExchangeHandler?  See SOAPActivator
        throw new IllegalStateException("Unexpected");
    }

    @Override
    public void handleMessage(Exchange exchange) throws HandlerException {
        // TODO: Why is this class an ExchangeHandler?  See SOAPActivator
        throw new IllegalStateException("Unexpected");
    }

    /**
     * The delegate method called by the Webservice implementation.
     * @param soapMessage the SOAP request
     * @return the SOAP response
     */
    public SOAPMessage invoke(final SOAPMessage soapMessage) {
        String operationName = null;
        Operation operation;
        Boolean oneWay = false;
        String firstBodyElement = null;

        if ((soapMessage == null) || (soapMessage.getSOAPPart() == null)) {
            return handleException(oneWay, new SOAPException("No such operation: " + _wsdlPort.getName() + "->null"));
        }
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Request:[" + SOAPUtil.soapMessageToString(soapMessage) + "]");
        }
        try {
            firstBodyElement = SOAPUtil.getFirstBodyElement(soapMessage);
            operation = WSDLUtil.getOperation(_wsdlPort, firstBodyElement);
            if (operation != null) {
                operationName = operation.getName();
                oneWay = WSDLUtil.isOneWay(operation);
                
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Received SOAP message targeted at Webservice operation '" + operationName + "' on port '" + _wsdlPort.getName() + "'.");
                }
            }
        } catch (SOAPException e) {
            LOGGER.error(e);
            return null;
        }

        if (operation == null) {
            return handleException(oneWay, new SOAPException("Operation for '" + firstBodyElement + "' not available on target Service '" + _service.getName() + "'."));
        }

        try {
            SynchronousInOutHandler inOutHandler = new SynchronousInOutHandler();
            Exchange exchange = _service.createExchange(operationName, inOutHandler);
            Message message;
            try {
                message = _messageComposer.compose(soapMessage, exchange, true);
            } catch (Exception e) {
                throw e instanceof SOAPException ? (SOAPException)e : new SOAPException(e);
            }

            assertComposedMessageOK(message, operation);

            exchange.getContext().setProperty(MESSAGE_NAME, 
                    operation.getInput().getMessage().getQName().getLocalPart(),
                    Scope.IN);


            if (oneWay) {
                exchange.send(message);
                return null;
            } else {
                exchange.send(message);
                try {
                    exchange = inOutHandler.waitForOut(_waitTimeout);
                } catch (DeliveryException e) {
                    return handleException(oneWay, new SOAPException("Timed out after " + _waitTimeout + " ms waiting on synchronous response from target service '" + _service.getName() + "'."));
                }

                if (SOAPUtil.getFactory(_bindingId) == null) {
                    throw new SOAPException("Failed to instantiate SOAP Message Factory");
                }
                SOAPMessage soapResponse;
                try {
                    soapResponse = _messageComposer.decompose(exchange, SOAPUtil.createMessage(_bindingId));
                } catch (Exception e) {
                    throw e instanceof SOAPException ? (SOAPException)e : new SOAPException(e);
                }
                if (exchange.getState() == ExchangeState.FAULT && soapResponse.getSOAPBody().getFault() == null) {
                    return handleException(oneWay, new SOAPException("Invalid response SOAPMessage construction.  The associated SwitchYard Exchange is in a FAULT state, "
                                                                     + "but the SOAPMessage is not a Fault message.  The MessageComposer implementation in use ("
                                                                     + _messageComposer.getClass().getName()
                                                                     + ") must generate the SOAPMessage instance properly as a Fault message."));
                }
                
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("Response:[" + SOAPUtil.soapMessageToString(soapResponse) + "]");
                }
                
                return soapResponse;
            }
        } catch (SOAPException se) {
            return handleException(oneWay, se);
        }
    }

    private void assertComposedMessageOK(Message soapMessage, Operation operation) throws SOAPException {
        Node inputMessage = soapMessage.getContent(Node.class);

        if (inputMessage == null) {
            throw new SOAPException("Composer created a null ESB Message payload for service '" + _service.getName() + "'.  Must be of type '" + SOAPMessage.class.getName() + "'.");
        }
        
        String actualNS = inputMessage.getNamespaceURI();
        String actualLN = inputMessage.getLocalName();
        @SuppressWarnings("unchecked")
        List<Part> parts = operation.getInput().getMessage().getOrderedParts(null);

        if (parts.isEmpty()) {
            throw new SOAPException("Invalid input SOAP payload for service operation '" + operation.getName() + "' (service '" + _service.getName()
                                                                              + "').  No such Part '" + actualLN + "'.");
        }

        Part part = parts.get(0);
        QName expectedPayloadType = part.getElementName();
        String expectedNS = expectedPayloadType.getNamespaceURI();
        String expectedLN = expectedPayloadType.getLocalPart();

        if (expectedNS != null && !expectedNS.equals(actualNS)) {
            throw new SOAPException("Invalid input SOAP payload namespace for service operation '" + operation.getName() + "' (service '" + _service.getName()
                                        + "').  Port defines operation namespace as '" + expectedNS + "'.  Actual namespace on input SOAP message '" + actualNS + "'.");
        } else if (expectedLN != null && !expectedLN.equals(actualLN)) {
            throw new SOAPException("Invalid input SOAP payload localNamePart for service operation '" + operation.getName() + "' (service '" + _service.getName()
                    + "').  Port defines operation localNamePart as '" + expectedLN + "'.  Actual localNamePart on input SOAP message '" + actualLN + "'.");
        }
    }

    private SOAPMessage handleException(Boolean oneWay, SOAPException se) {
        if (oneWay) {
            LOGGER.error(se);
        } else {
            try {
                return SOAPUtil.generateFault(se, _bindingId);
            } catch (SOAPException e) {
                LOGGER.error(e);
            }
        }
        return null;
    }
}
