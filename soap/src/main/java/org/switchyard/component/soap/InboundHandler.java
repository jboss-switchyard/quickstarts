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
 
package org.switchyard.component.soap;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.wsdl.Operation;
import javax.wsdl.Port;
import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Endpoint;

import org.apache.log4j.Logger;
import org.switchyard.BaseHandler;
import org.switchyard.Context;
import org.switchyard.Exchange;
import org.switchyard.ExchangePattern;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.Service;
import org.switchyard.ServiceDomain;
import org.switchyard.component.soap.util.SOAPUtil;
import org.switchyard.component.soap.util.WSDLUtil;
import org.switchyard.metadata.BaseExchangeContract;
import org.switchyard.internal.ServiceDomains;

import org.switchyard.internal.transform.TransformSequence;
import org.switchyard.metadata.BaseInvocationContract;
import org.switchyard.metadata.ServiceInterface;
import org.switchyard.metadata.ServiceOperation;

/**
 * Hanldes SOAP requests to invoke a SwitchYard service.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
public class InboundHandler extends BaseHandler {

    /**
     * SOAP Fault type QName.
     */
    public static final String SOAP_FAULT_MESSAGE_TYPE = "{http://schemas.xmlsoap.org/soap/envelope/}Fault";

    private static final Logger LOGGER = Logger.getLogger(InboundHandler.class);
    private static final int DEFAULT_PORT = 8080;
    private static final long DEFAULT_TIMEOUT = 15000;
    private static final int DEFAULT_SLEEP = 100;
    private static final String MESSAGE_NAME = "MESSAGE_NAME";

    private static ThreadLocal<SOAPMessage> _response = new ThreadLocal<SOAPMessage>();

    private MessageComposer _composer;
    private MessageDecomposer _decomposer;
    private ServiceDomain _domain;
    private String _wsdlLocation;
    private QName _serviceName;
    private Service _service;
    private long _waitTimeout = DEFAULT_TIMEOUT; // default of 15 seconds
    private Endpoint _endpoint;
    private Port _wsdlPort;
    private String _scheme = "http";
    private String _host;
    private int _serverPort;
    private String _contextPath;
    private HttpServer _server;
    private PortName _portName;

    /**
     * Constructor.
     * @param config the configuration settings
     */
    public InboundHandler(final Map<String, String> config) {
        String localService = config.get("localService");
        String port = config.get("port");
        String composer = config.get("composer");
        String decomposer = config.get("decomposer");
        _host = config.get("host");
        _contextPath = config.get("context");
        _portName = new PortName(config.get("wsPort"));

        if (composer != null && composer.length() > 0) {
            try {
                Class<? extends MessageComposer> composerClass = Class.forName(composer).asSubclass(MessageComposer.class);
                _composer = composerClass.newInstance();
            } catch (Exception cnfe) {
                LOGGER.error("Could not instantiate composer", cnfe);
            }
        }
        if (_composer == null) {
            _composer = new DefaultMessageComposer();
        }
        if (decomposer != null && decomposer.length() > 0) {
            try {
                Class<? extends MessageDecomposer> decomposerClass = Class.forName(decomposer).asSubclass(MessageDecomposer.class);
                _decomposer = decomposerClass.newInstance();
            } catch (Exception cnfe) {
                LOGGER.error("Could not instantiate decomposer", cnfe);
            }
        }
        if (_decomposer == null) {
            _decomposer = new DefaultMessageDecomposer();
        }

        _domain = ServiceDomains.getDomain();
        _wsdlLocation = config.get("wsdlLocation");
        _serviceName = new QName(localService);
        if (port == null) {
            _serverPort = DEFAULT_PORT;
        } else {
            _serverPort = Integer.parseInt(port);
        }
        if (_host == null) {
            _host = "localhost";
        }
    }

    /**
     * Start lifecycle.
     * @throws WebServicePublishException If unable to publish the endpoint
     */
    public void start() throws WebServicePublishException {
        try {
            javax.wsdl.Service wsdlService = WSDLUtil.getService(_wsdlLocation, _portName);
            _wsdlPort = WSDLUtil.getPort(wsdlService, _portName);
            // Update the portName
            _portName.setServiceQName(wsdlService.getQName());
            _portName.setName(_wsdlPort.getName());
            
            BaseWebService wsProvider = new BaseWebService();
            // Hook the handler
            wsProvider.setConsumer(this);
            
            // lookup the SwitchYard service
            _service = _domain.getService(_serviceName);
            if (_service == null) {
                throw new WebServicePublishException(
                        "Target service not registered: " + _serviceName);
            }

            _endpoint = Endpoint.create(wsProvider);
            List<Source> metadata = new ArrayList<Source>();
            StreamSource source = WSDLUtil.getStream(_wsdlLocation);
            metadata.add(source);
            _endpoint.setMetadata(metadata);
            Map<String, Object> properties = new HashMap<String, Object>();
            properties.put(Endpoint.WSDL_SERVICE, _portName.getServiceQName());
            properties.put(Endpoint.WSDL_PORT, _portName.getPortQName());
            _endpoint.setProperties(properties);

            _server = HttpServer.create(new InetSocketAddress(_host, _serverPort), 0);
            _server.start();
            String path = "/" + _portName.getServiceName();
            if (_contextPath != null) {
                path = "/" + _contextPath + "/" + _portName.getServiceName();
            }
            HttpContext context = _server.createContext(path);
            _endpoint.publish(context);
            LOGGER.info("WebService published at " + _scheme + "://" + _host + ":" + _serverPort + path);
        } catch (WSDLException e) {
            throw new WebServicePublishException(e);
        } catch (IOException ioe) {
            throw new WebServicePublishException(ioe);
        }
    }

    /**
     * Stop lifecycle.
     */
    public void stop() {
        _endpoint.stop();
        _server.stop(0);
        LOGGER.info("WebService " + _portName + " stopped.");
    }

    /**
     * The handler method that handles responses from a WebService.
     * @param exchange the Exchange
     * @throws HandlerException handler exception
     */
    @Override
    public void handleMessage(final Exchange exchange) throws HandlerException {
        assertTransformsApplied(exchange);
        try {
            _response.set(_decomposer.decompose(exchange.getMessage()));
        } catch (SOAPException se) {
            throw new HandlerException("Unexpected exception generating SOAP Message", se);
        }
    }

    /**
     * The handler method that handles faults from a WebService.
     * @param exchange the Exchange
     */
    @Override
    public void handleFault(final Exchange exchange) {
        try {
            _response.set(_decomposer.decompose(exchange.getMessage()));
        } catch (SOAPException se) {
            try {
                _response.set(SOAPUtil.generateFault(se));
            } catch (SOAPException e) {
                LOGGER.error(e);
            }
        }
    }

    /**
     * The delegate method called by the Webservice implementation.
     * @param soapMessage the SOAP request
     * @return the SOAP response
     */
    public SOAPMessage invoke(final SOAPMessage soapMessage) {
        String operationName;
        ExchangePattern soapInvocationExchangePattern;
        ServiceOperation targetServiceOperation;

        // Clear the response...
        _response.remove();

        try {
            operationName = SOAPUtil.getOperationName(soapMessage);
            soapInvocationExchangePattern = SOAPUtil.getExchangePattern(_wsdlPort, operationName);
        } catch (SOAPException e) {
            LOGGER.error(e);
            return null;
        }

        try {
            targetServiceOperation = getServiceOperation(operationName);
        } catch (SOAPException e) {
            handleException(soapInvocationExchangePattern, e);
            return _response.get();
        }

        if (targetServiceOperation == null) {
            handleException(soapInvocationExchangePattern, new SOAPException("Operation '" + operationName + "' not available on target Service '" + _service.getName() + "'."));
            return _response.get();
        }

        try {
            BaseExchangeContract exchangeContract = new BaseExchangeContract(targetServiceOperation);
            BaseInvocationContract soapMetaData = exchangeContract.getInvokerInvocationMetaData();
            Operation operation = SOAPUtil.getOperation(_wsdlPort, operationName);
            QName inputMessageQName = operation.getInput().getMessage().getQName();
            Exchange exchange;

            exchange = _domain.createExchange(_service, exchangeContract, this);
            Message message = _composer.compose(soapMessage, exchange);

            if (!assertComposedMessageOK(message, operation, soapInvocationExchangePattern)) {
                return _response.get();
            }

            Context msgCtx = message.getContext();
            msgCtx.setProperty(MESSAGE_NAME, inputMessageQName.getLocalPart());

            // Init the input and expected fault types for the exchange...
            soapMetaData.setInputType(inputMessageQName.toString()).setFaultType(SOAP_FAULT_MESSAGE_TYPE);

            if (soapInvocationExchangePattern == ExchangePattern.IN_ONLY) {
                exchange.send(message);
            } else {
                QName outputMessageQName = operation.getOutput().getMessage().getQName();

                soapMetaData.setOutputType(outputMessageQName.toString());
                exchange.send(message);

                if (exchangeContract.getServiceOperation().getExchangePattern() == ExchangePattern.IN_OUT) {
                    waitForResponse();
                } else {
                    // TODO: We need to resolve this situation.  The soapInvocationExchangePattern is IN_OUT, but the target ServiceOperation is IN_ONLY.
                    // We could use transformation logic here... transforming from "null" to soap port "out" type.
                    // Perhaps this could be accommodated automatically in Keith's rework of the handlers?  After executing the
                    // last handler on the IN, and seeing that the contract defines a response type, yet the pattern is IN_ONLY... could we
                    // auto execute a fake OUT send on the exchange, passing a null payload perhaps ???
                    LOGGER.debug("Unexpected condition.  The soapInvocationExchangePattern is IN_OUT, but the target ServiceOperation is IN_ONLY.");
                }
            }

            return _response.get();

        } catch (SOAPException se) {
            handleException(soapInvocationExchangePattern, se);
        } finally {
            _response.remove();
        }

        return null;
    }

    private boolean assertComposedMessageOK(Message soapMessage, Operation operation, ExchangePattern soapInvocationExchangePattern) {
        Object content = soapMessage.getContent();

        if (content == null) {
            handleException(soapInvocationExchangePattern, new SOAPException("Composer created a null ESB Message payload for service '" + _service.getName() + "'.  Must be of type '" + SOAPMessage.class.getName() + "'."));
            return false;
        } else if (!(content instanceof Node)) {
            handleException(soapInvocationExchangePattern, new SOAPException("Composer created invalid ESB Message payload type '" + content.getClass().getName() + "' for service '" + _service.getName() + "'.  Must be of type '" + Node.class.getName() + "'."));
            return false;
        }

        Node inputMessage = (Node) content;
        QName expectedPayloadType = operation.getInput().getMessage().getQName();
        String expectedNS = expectedPayloadType.getNamespaceURI();
        String expectedLN = expectedPayloadType.getLocalPart();
        String actualNS = inputMessage.getNamespaceURI();
        String actualLN = inputMessage.getLocalName();

        if (expectedNS != null && !expectedNS.equals(actualNS)) {
            handleException(soapInvocationExchangePattern, new SOAPException("Invalid input SOAP payload namespace for service operation '" + operation.getName() + "' (service '" + _service.getName()
                                                                              + "').  Port defines operation namespace as '" + expectedNS + "'.  Actual namespace on input SOAP message '" + actualNS + "'."));
            return false;
        } else if (expectedLN != null && !expectedLN.equals(actualLN)) {
            handleException(soapInvocationExchangePattern, new SOAPException("Invalid input SOAP payload localNamePart for service operation '" + operation.getName() + "' (service '" + _service.getName()
                                                                              + "').  Port defines operation localNamePart as '" + expectedLN + "'.  Actual localNamePart on input SOAP message '" + actualLN + "'."));
            return false;
        }

        return true;
    }

    private void handleException(ExchangePattern soapInvocationExchangePattern, SOAPException se) {
        if (soapInvocationExchangePattern == ExchangePattern.IN_ONLY) {
            LOGGER.error(se);
        } else {
            try {
                _response.set(SOAPUtil.generateFault(se));
            } catch (SOAPException e) {
                LOGGER.error(e);
            }
        }
    }

    /**
     * Sleep until we get a response or timeout has reached.
     */
    private void waitForResponse() {
        long start = System.currentTimeMillis();

        while (System.currentTimeMillis() < start + _waitTimeout) {
            if (_response.get() != null) {
                return;
            }
            try {
                Thread.sleep(DEFAULT_SLEEP);
            } catch (InterruptedException e) {
                //ignore
                continue;
            }
        }
    }

    private ServiceOperation getServiceOperation(String operationName) throws SOAPException {
        ServiceInterface serviceInterface = _service.getInterface();
        return serviceInterface.getOperation(operationName);
    }

    private void assertTransformsApplied(Exchange exchange) throws HandlerException {
        if (!TransformSequence.assertTransformsApplied(exchange)) {
            String actualPayloadType = TransformSequence.getCurrentMessageType(exchange);
            String expectedPayloadType = TransformSequence.getTargetMessageType(exchange);
            String operationName = exchange.getContract().getServiceOperation().getName();

            throw new HandlerException("Error invoking '" + operationName + "'.  Response requires a payload type of '" + expectedPayloadType + "'.  Actual payload type is '" + actualPayloadType + "'.  You must define and register a Transformer to transform between these types.");
        }
    }
}
