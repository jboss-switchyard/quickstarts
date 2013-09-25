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
 
package org.switchyard.component.soap;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.soap.MTOMFeature;

import org.jboss.logging.Logger;
import org.switchyard.Exchange;
import org.switchyard.ExchangeState;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.Scope;
import org.switchyard.ServiceDomain;
import org.switchyard.ServiceReference;
import org.switchyard.SwitchYardException;
import org.switchyard.component.common.DeliveryException;
import org.switchyard.component.common.SynchronousInOutHandler;
import org.switchyard.component.common.composer.MessageComposer;
import org.switchyard.component.soap.composer.SOAPBindingData;
import org.switchyard.component.soap.composer.SOAPComposition;
import org.switchyard.component.soap.composer.SOAPMessageComposer;
import org.switchyard.component.soap.config.model.SOAPBindingModel;
import org.switchyard.component.soap.endpoint.EndpointPublisherFactory;
import org.switchyard.component.soap.endpoint.WSEndpoint;
import org.switchyard.component.soap.util.SOAPUtil;
import org.switchyard.component.soap.util.WSDLUtil;
import org.switchyard.deploy.BaseServiceHandler;
import org.switchyard.label.BehaviorLabel;
import org.switchyard.runtime.event.ExchangeCompletionEvent;
import org.switchyard.security.SecurityContext;
import org.switchyard.security.credential.Credential;
import org.w3c.dom.Node;

/**
 * Handles SOAP requests to invoke a SwitchYard service.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
public class InboundHandler extends BaseServiceHandler {

    private static final Logger LOGGER = Logger.getLogger(InboundHandler.class);
    private static final long DEFAULT_TIMEOUT = 15000;
    private static final int DEFAULT_FAULT_RESONSE_CODE = 500;
    private static final String MESSAGE_NAME = "org.switchyard.soap.messageName";

    private final SOAPBindingModel _config;
    private final String _gatewayName;
    private MessageComposer<SOAPBindingData> _messageComposer;
    private ServiceDomain _domain;
    private ServiceReference _service;
    private long _waitTimeout = DEFAULT_TIMEOUT; // default of 15 seconds
    private WSEndpoint _endpoint;
    private Port _wsdlPort;
    private String _bindingId;
    private Boolean _documentStyle = false;
    private Boolean _unwrapped = false;
    private String _targetNamespace;
    private Feature _feature = new Feature();
    private Map<String, Operation> _operationsMap = new HashMap<String, Operation>();

    private static final ThreadLocal<Set<Credential>> CREDENTIALS = new ThreadLocal<Set<Credential>>();

    /**
     * Gets the thread-local credentials set.
     * @return the thread-local credentials set
     */
    public static Set<Credential> getCredentials() {
        return getCredentials(false);
    }

    private static Set<Credential> getCredentials(boolean unset) {
        Set<Credential> credentials = CREDENTIALS.get();
        if (credentials == null) {
            credentials = new LinkedHashSet<Credential>();
            if (!unset) {
                CREDENTIALS.set(credentials);
            }
        }
        if (unset) {
            CREDENTIALS.set(null);
        }
        return credentials;
    }

    /**
     * Unsets the thread-local credentials.
     */
    public static void unsetCredentials() {
        CREDENTIALS.set(null);
    }

    /**
     * Constructor.
     * @param config the configuration settings
     * @param domain the service domain
     */
    public InboundHandler(final SOAPBindingModel config, ServiceDomain domain) {
        super(domain);
        _config = config;
        _gatewayName = config.getName();
        _domain = domain;
    }

    /**
     * Start lifecycle.
     * @throws WebServicePublishException If unable to publish the endpoint
     */
    @Override
    protected void doStart() throws WebServicePublishException {
        try {
            _service = _domain.getServiceReference(_config.getServiceName());
            PortName portName = _config.getPort();
            Definition definition = WSDLUtil.readWSDL(_config.getWsdl());
            _targetNamespace = definition.getTargetNamespace();
            javax.wsdl.Service wsdlService = WSDLUtil.getService(definition, portName);
            _wsdlPort = WSDLUtil.getPort(wsdlService, portName);
            // Update the portName
            portName.setServiceQName(wsdlService.getQName());
            portName.setName(_wsdlPort.getName());

            String style = WSDLUtil.getStyle(_wsdlPort);
            _documentStyle = style.equals(WSDLUtil.DOCUMENT) ? true : false;
            _unwrapped = _config.isUnwrapped();
            _feature = WSDLUtil.getFeature(definition, _wsdlPort, _documentStyle);

            if (_feature.isAddressingEnabled()) {
                @SuppressWarnings("unchecked")
                List<BindingOperation> bindingOperations = _wsdlPort.getBinding().getBindingOperations();
                for (BindingOperation bindingOp : bindingOperations) {
                    String inputAction = WSDLUtil.getInputAction(_wsdlPort, new QName(_targetNamespace, bindingOp.getOperation().getName()), _targetNamespace, _documentStyle);
                    _operationsMap.put(inputAction, bindingOp.getOperation());
                }
            }

            // Config feature setting overrides WSDL
            MTOMFeature mtom = _feature.getMtom(_config);
            _bindingId = WSDLUtil.getBindingId(_wsdlPort, mtom.isEnabled());

            _endpoint = EndpointPublisherFactory.getEndpointPublisher().publish(_config,
                _bindingId,
                this,
                _feature.getAddressing(),
                mtom);

            // Create and configure the SOAP message composer
            _messageComposer = SOAPComposition.getMessageComposer(_config);
            ((SOAPMessageComposer)_messageComposer).setDocumentStyle(_documentStyle);
            ((SOAPMessageComposer)_messageComposer).setWsdlPort(_wsdlPort);
            ((SOAPMessageComposer)_messageComposer).setMtomEnabled(mtom.isEnabled());
            ((SOAPMessageComposer)_messageComposer).setUnwrapped(_unwrapped);
            if (_config.getMtomConfig() != null) {
                ((SOAPMessageComposer)_messageComposer).setXopExpand(_config.getMtomConfig().isXopExpand());
            }
        } catch (WSDLException e) {
            throw new WebServicePublishException(e);
        }
    }

    /**
     * Stop lifecycle.
     */
    @Override
    protected void doStop() {
        if (_endpoint != null) {
            _endpoint.stop();
        }
        SOAPLogger.ROOT_LOGGER.webService(_config.getPort().toString());
    }

    @Override
    public void handleFault(Exchange exchange) {
        // TODO: Why is this class an ExchangeHandler?  See SOAPActivator
        throw SOAPMessages.MESSAGES.unexpected();
    }

    @Override
    public void handleMessage(Exchange exchange) throws HandlerException {
        // TODO: Why is this class an ExchangeHandler?  See SOAPActivator
        throw SOAPMessages.MESSAGES.unexpected();
    }

    /**
     * The delegate method called by the Webservice implementation.
     * @param soapMessage the SOAP request
     * @return the SOAP response
     */
    public SOAPMessage invoke(final SOAPMessage soapMessage) {
        return invoke(soapMessage, null);
    }

    /**
     * The delegate method called by the Webservice implementation.
     * @param soapMessage the SOAP request
     * @param wsContext the web service context
     * @return the SOAP response
     */
    public SOAPMessage invoke(final SOAPMessage soapMessage, final WebServiceContext wsContext) {
        String operationName = null;
        Operation operation;
        Boolean oneWay = false;
        QName firstBodyElement = null;
        MessageContext msgContext = null;

        // Collect and unset any thread-local credentials
        Set<Credential> credentials = getCredentials(true);

        if (wsContext != null) {
            // Caching the message context
            msgContext = wsContext.getMessageContext();
        }

        if ((soapMessage == null) || (soapMessage.getSOAPPart() == null)) {
            return handleException(oneWay, 
                     SOAPMessages.MESSAGES.noSuchOperation(_wsdlPort.getName().toString()));
        }
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Inbound <-- Request:[" + SOAPUtil.soapMessageToString(soapMessage) + "]");
        }
        try {
            String action = SOAPUtil.getAddressingAction(soapMessage);
            if (_feature.isAddressingEnabled() && (action != null)) {
                // Get the operation using the action
                operation = _operationsMap.get(action);
                if (operation == null) {
                    return handleException(oneWay, 
                            SOAPMessages.MESSAGES.couldNotFindOperation(action)
                                );
                }
            } else {
                firstBodyElement = SOAPUtil.getFirstBodyElement(soapMessage);
                operation = WSDLUtil.getOperationByElement(_wsdlPort, firstBodyElement, _documentStyle);
            }
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
            return handleException(oneWay,                     
                    SOAPMessages.MESSAGES.operationNotAvailableTarget(firstBodyElement.toString(), _service.getName() + "'."));
        }

        try {
            SynchronousInOutHandler inOutHandler = new SynchronousInOutHandler();
            Exchange exchange = _service.createExchange(operationName, inOutHandler);

            // identify ourselves
            exchange.getContext().setProperty(ExchangeCompletionEvent.GATEWAY_NAME, _gatewayName, Scope.EXCHANGE)
                    .addLabels(BehaviorLabel.TRANSIENT.label());

            SOAPBindingData soapBindingData = new SOAPBindingData(soapMessage, wsContext);

            // add any thread-local and/or binding-extracted credentials
            SecurityContext securityContext = SecurityContext.get(exchange);
            securityContext.getCredentials().addAll(credentials);
            securityContext.getCredentials().addAll(soapBindingData.extractCredentials());

            Message message;
            try {
                message = _messageComposer.compose(soapBindingData, exchange);
            } catch (Exception e) {
                throw e instanceof SOAPException ? (SOAPException)e : new SOAPException(e);
            }

            // Do not perfom this check if the message has been unwrapped
            if (!_unwrapped) {
                assertComposedMessageOK(message, operation);
            }

            exchange.getContext(message).setProperty(MESSAGE_NAME, operation.getInput().getMessage().getQName().getLocalPart());

            if (oneWay) {
                exchange.send(message);
                return null;
            } else {
                exchange.send(message);
                try {
                    exchange = inOutHandler.waitForOut(_waitTimeout);
                } catch (DeliveryException e) {
                    return handleException(oneWay, 
                            SOAPMessages.MESSAGES.timedOut(String.valueOf(_waitTimeout), 
                                    _service.getName().toString()));
                }
                
                if (SOAPUtil.getFactory(_bindingId) == null) {
                    throw SOAPMessages.MESSAGES.failedToInstantiateSOAPMessageFactory();
                }
                SOAPMessage soapResponse;
                try {
                    SOAPBindingData bindingData = new SOAPBindingData(SOAPUtil.createMessage(_bindingId));
                    soapResponse = _messageComposer.decompose(exchange, bindingData).getSOAPMessage();
                    if (msgContext != null) {
                        msgContext.put(BaseHandler.STATUS, bindingData.getStatus());
                    }
                } catch (SOAPException soapEx) {
                    throw soapEx;
                } catch (Exception ex) {
                    throw new SwitchYardException(ex);
                }
                if (exchange.getState() == ExchangeState.FAULT && soapResponse.getSOAPBody().getFault() == null) {
                    return handleException(oneWay, 
                            SOAPMessages.MESSAGES.invalidResponseConstruction(_messageComposer.getClass().getName()));
                }
                
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("Inbound --> Response:[" + SOAPUtil.soapMessageToString(soapResponse) + "]");
                }
                return soapResponse;
            }
        } catch (SOAPException se) {
            if (msgContext != null) {
                msgContext.put(BaseHandler.STATUS, DEFAULT_FAULT_RESONSE_CODE);
            }
            return handleException(oneWay, se);
        }
    }

    private void assertComposedMessageOK(Message soapMessage, Operation operation) throws SOAPException {
        Node inputMessage = soapMessage.getContent(Node.class);

        String actualNS = inputMessage.getNamespaceURI();
        String actualLN = inputMessage.getLocalName();
        @SuppressWarnings("unchecked")
        List<Part> parts = operation.getInput().getMessage().getOrderedParts(null);

        if (parts.isEmpty()) {
            throw SOAPMessages.MESSAGES.invalidInputSOAPPayloadForServiceOperation(operation.getName(), _service.getName().toString(), actualLN);
        }

        QName expectedPayloadType = null;

        if (_documentStyle) {
            if (parts.get(0).getElementName() != null) {
                expectedPayloadType = parts.get(0).getElementName();
            } else if (parts.get(0).getTypeName() != null) {
                expectedPayloadType = parts.get(0).getTypeName();
            }
        } else {
            // RPC
            expectedPayloadType = new QName(_targetNamespace, operation.getName());
        }

        String expectedNS = expectedPayloadType.getNamespaceURI();
        String expectedLN = expectedPayloadType.getLocalPart();
        if (!_documentStyle) {
            expectedLN = operation.getName();
        }

        if (expectedNS != null && !expectedNS.equals(actualNS)) {
            throw SOAPMessages.MESSAGES.invalidInputSOAPPayloadNamespaceForServiceOperation(operation.getName(), _service.getName().toString(), expectedNS, actualNS);
        } else if (expectedLN != null && !expectedLN.equals(actualLN)) {
            throw SOAPMessages.MESSAGES.invalidInputSOAPPayloadLocalNamePartForServiceOperation(operation.getName(), _service.getName().toString(), expectedLN, actualLN);
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
