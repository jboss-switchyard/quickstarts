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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.soap.MTOMFeature;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.apache.cxf.configuration.security.ProxyAuthorizationPolicy;
import org.apache.cxf.jaxws.DispatchImpl;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.cxf.transports.http.configuration.ProxyServerType;
import org.apache.log4j.Logger;
import org.switchyard.Exchange;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.Scope;
import org.switchyard.common.type.Classes;
import org.switchyard.component.common.composer.MessageComposer;
import org.switchyard.component.soap.composer.SOAPBindingData;
import org.switchyard.component.soap.composer.SOAPComposition;
import org.switchyard.component.soap.composer.SOAPFaultInfo;
import org.switchyard.component.soap.composer.SOAPMessageComposer;
import org.switchyard.component.soap.config.model.SOAPBindingModel;
import org.switchyard.component.soap.util.SOAPUtil;
import org.switchyard.component.soap.util.WSDLUtil;
import org.switchyard.deploy.BaseServiceHandler;
import org.switchyard.label.BehaviorLabel;
import org.switchyard.runtime.event.ExchangeCompletionEvent;

/**
 * Handles invoking external Webservice endpoints.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
public class OutboundHandler extends BaseServiceHandler {

    private static final Logger LOGGER = Logger.getLogger(OutboundHandler.class);

    private static final String NO_RESPONSE = "No response returned.";

    private final SOAPBindingModel _config;
    private final String _bindingName;
    private final String _referenceName;
    private MessageComposer<SOAPBindingData> _messageComposer;
    private Dispatch<SOAPMessage> _dispatcher;
    private Port _wsdlPort;
    private String _bindingId;
    private Boolean _documentStyle;
    private Feature _feature = new Feature();

    /**
     * Constructor.
     * @param config the configuration settings
     */
    public OutboundHandler(final SOAPBindingModel config) {
        _config = config;
        _bindingName = config.getName();
        _referenceName = config.getReference().getName();
    }

    /**
     * Start lifecycle.
     * @throws WebServiceConsumeException If unable to load the WSDL
     */
    @Override
    protected void doStart() throws WebServiceConsumeException {
        if (_dispatcher == null) {
            ClassLoader origLoader = Classes.getTCCL();
            try {
                Definition definition = WSDLUtil.readWSDL(_config.getWsdl());
                PortName portName = _config.getPort();
                javax.wsdl.Service wsdlService = WSDLUtil.getService(definition, portName);
                _wsdlPort = WSDLUtil.getPort(wsdlService, portName);
                // Update the portName
                portName.setServiceQName(wsdlService.getQName());
                portName.setName(_wsdlPort.getName());

                String style = WSDLUtil.getStyle(_wsdlPort);
                _documentStyle = style.equals(WSDLUtil.DOCUMENT) ? true : false;
                _feature = WSDLUtil.getFeature(definition, _wsdlPort, _documentStyle);

                // Config feature setting overrides WSDL
                MTOMFeature mtom = _feature.getMtom(_config);
                _bindingId = WSDLUtil.getBindingId(_wsdlPort, mtom.isEnabled());

                _messageComposer = SOAPComposition.getMessageComposer(_config);
                ((SOAPMessageComposer)_messageComposer).setDocumentStyle(_documentStyle);
                ((SOAPMessageComposer)_messageComposer).setWsdlPort(_wsdlPort);
                ((SOAPMessageComposer)_messageComposer).setMtomEnabled(mtom.isEnabled());
                if (_config.getMtomConfig() != null) {
                    ((SOAPMessageComposer)_messageComposer).setXopExpand(_config.getMtomConfig().isXopExpand());
                }

                URL wsdlUrl = WSDLUtil.getURL(_config.getWsdl());
                LOGGER.info("Creating dispatch with WSDL " + wsdlUrl);
                // make sure we don't pollute the class loader used by the WS subsystem
                Classes.setTCCL(getClass().getClassLoader());

                Service service = Service.create(wsdlUrl, portName.getServiceQName());

                _dispatcher = service.createDispatch(portName.getPortQName(),
                                    SOAPMessage.class,
                                    Service.Mode.MESSAGE,
                                    _feature.getAddressing(),
                                    mtom);

                // this does not return a proper qualified Fault element and has no Detail so deferring for now
                // _dispatcher.getRequestContext().put("jaxws.response.throwExceptionIfSOAPFault", Boolean.FALSE);

                Binding binding = _dispatcher.getBinding();
                List<Handler> handlers = binding.getHandlerChain();
                handlers.add(new OutboundResponseHandler());

                if (_feature.isAddressingEnabled()) {
                    // Add handler to process WS-A headers
                    handlers.add(new AddressingHandler());
                } else {
                    // Defaulting to use soapAction property in request header
                    _dispatcher.getRequestContext().put(BindingProvider.SOAPACTION_USE_PROPERTY, Boolean.TRUE);
                }
                binding.setHandlerChain(handlers);

                if (_config.getEndpointAddress() != null) {
                    _dispatcher.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, _config.getEndpointAddress());
                }

                Integer timeout = _config.getTimeout();
                HTTPConduit conduit = (HTTPConduit)((DispatchImpl)_dispatcher).getClient().getConduit();
                // Proxy authentication
                if (_config.getProxyConfig() != null) {
                    HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
                    httpClientPolicy.setProxyServerType(ProxyServerType.fromValue(_config.getProxyConfig().getType()));
                    httpClientPolicy.setProxyServer(_config.getProxyConfig().getHost());
                    if (_config.getProxyConfig().getPort() != null) {
                        httpClientPolicy.setProxyServerPort(Integer.valueOf(_config.getProxyConfig().getPort()).intValue());
                    }
                    conduit.setClient(httpClientPolicy);
                    if (_config.getProxyConfig().getUser() != null) {
                        ProxyAuthorizationPolicy policy = new ProxyAuthorizationPolicy();
                        policy.setUserName(_config.getProxyConfig().getUser());
                        policy.setPassword(_config.getProxyConfig().getPassword());
                        conduit.setProxyAuthorization(policy);
                    }
                }
                if (_config.hasAuthentication()) {
                    AuthorizationPolicy policy = new AuthorizationPolicy();
                    // Set authentication
                    if (_config.isBasicAuth()) {
                        policy.setUserName(_config.getBasicAuthConfig().getUser());
                        policy.setPassword(_config.getBasicAuthConfig().getPassword());
                        policy.setAuthorizationType("Basic");
                    } else {
                        policy.setUserName(_config.getNtlmAuthConfig().getDomain() + "\\" + _config.getNtlmAuthConfig().getUser());
                        policy.setPassword(_config.getNtlmAuthConfig().getPassword());
                        HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
                        if (timeout != null) {
                            httpClientPolicy.setConnectionTimeout(timeout);
                        } else {
                            httpClientPolicy.setConnectionTimeout(36000);
                        }
                        httpClientPolicy.setAllowChunking(false);
                        conduit.setClient(httpClientPolicy);
                    }
                    conduit.setAuthorization(policy);
                }
                if (timeout != null) {
                    if (conduit.getClient() != null) {
                        conduit.getClient().setConnectionTimeout(timeout);
                        conduit.getClient().setReceiveTimeout(timeout);
                    } else {
                        HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
                        httpClientPolicy.setConnectionTimeout(timeout);
                        httpClientPolicy.setReceiveTimeout(timeout);
                        conduit.setClient(httpClientPolicy);
                    }
                }

            } catch (MalformedURLException e) {
                throw new WebServiceConsumeException(e);
            } catch (WSDLException wsdle) {
                throw new WebServiceConsumeException(wsdle);
            } finally {
                Classes.setTCCL(origLoader);
            }
        }
    }

    /**
     * Stop lifecycle.
     */
    public void stop() {
    }

    /**
     * The handler method that invokes the actual Webservice when the
     * component is used as a WS consumer.
     * @param exchange the Exchange
     * @throws HandlerException handler exception
     */
    @Override
    public void handleMessage(final Exchange exchange) throws HandlerException {
        // identify ourselves
        exchange.getContext().setProperty(ExchangeCompletionEvent.GATEWAY_NAME, _bindingName, Scope.EXCHANGE)
                .addLabels(BehaviorLabel.TRANSIENT.label());

        try {
            if (getState() != State.STARTED) {
                throw new HandlerException(String.format("Reference binding \"%s/%s\" is not started.", _referenceName,
                        _bindingName));
            }
            if (SOAPUtil.getFactory(_bindingId) == null) {
                throw new SOAPException("Failed to instantiate SOAP Message Factory");
            }

            SOAPMessage request;
            Boolean oneWay = false;
            Boolean replyToSet = false;
            String action = null;
            try {
                request = _messageComposer.decompose(exchange, new SOAPBindingData(SOAPUtil.createMessage(_bindingId))).getSOAPMessage();

                QName firstBodyElement = SOAPUtil.getFirstBodyElement(request);
                action = WSDLUtil.getSoapAction(_wsdlPort, firstBodyElement, _documentStyle);
                oneWay = WSDLUtil.isOneWay(_wsdlPort, firstBodyElement, _documentStyle);

                if (_feature.isAddressingEnabled()) {
                    _dispatcher.getRequestContext().put(SOAPUtil.SWITCHYARD_CONTEXT, exchange.getContext());
                    // It is a one way if a replyto address is set
                    if ((exchange.getContext().getPropertyValue(SOAPUtil.WSA_REPLYTO_STR) != null) 
                        || (exchange.getContext().getPropertyValue(SOAPUtil.WSA_REPLYTO_STR.toLowerCase()) != null)) {
                        replyToSet = true;
                    }
                    String toAddress = SOAPUtil.getToAddress(exchange.getContext());
                    if (toAddress != null) {
                        _dispatcher.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, toAddress);
                    }
                }
            } catch (Exception e) {
                throw e instanceof SOAPException ? (SOAPException)e : new SOAPException(e);
            }
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Outbound ---> Request:[" + SOAPUtil.soapMessageToString(request) + "]" + (oneWay ? " oneWay " : ""));
            }
            SOAPMessage response = invokeService(request, oneWay, action);
            if (response != null) {
                // This property vanishes once message composer processes this message
                // so caching it here
                Boolean hasFault = response.getSOAPBody().hasFault();
                Message message;
                try {
                    SOAPBindingData bindingData = new SOAPBindingData(response);
                    if (hasFault) {
                        SOAPFaultInfo faultInfo = new SOAPFaultInfo();
                        faultInfo.copyFaultInfo(response);
                        bindingData.setSOAPFaultInfo(faultInfo);
                    }
                    String status = (String)response.getProperty(BaseHandler.STATUS);
                    if (!hasFault && (status != null)) {
                        bindingData.setStatus(Integer.valueOf(status));
                    }
                    message = _messageComposer.compose(bindingData, exchange);
                } catch (Exception e) {
                    throw e instanceof SOAPException ? (SOAPException)e : new SOAPException(e);
                }
                if (hasFault) {
                    exchange.sendFault(message);
                } else {
                    exchange.send(message);
                }
            }

            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Outbound <--- Response:[" + SOAPUtil.soapMessageToString(response) + "]");
            }

        } catch (SOAPException se) {
            throw new HandlerException("Unexpected exception handling SOAP Message", se);
        }
    }

    /**
     * Invoke Webservice via Dispatch API
     * @param soapMessage the SOAP request
     * @param oneWay if it is request only operation
     * @param action the SOAP Action
     * @return the SOAP response
     * @throws SOAPException If a Dispatch could not be created based on the SOAP message.
     */
    private SOAPMessage invokeService(final SOAPMessage soapMessage, final Boolean oneWay, final String action) throws SOAPException {

        SOAPMessage response = null;
        try {
            if (!_feature.isAddressingEnabled() && (action != null)) {
                _dispatcher.getRequestContext().put(BindingProvider.SOAPACTION_URI_PROPERTY, "\"" + action + "\"");
            }

            if (oneWay) {
                _dispatcher.invokeOneWay(soapMessage);
                //return empty response
            }  else {
                response = _dispatcher.invoke(soapMessage);
            }
        } catch (SOAPFaultException sfex) {
            response = SOAPUtil.generateFault(sfex, _bindingId);
        } catch (WebServiceException wsex) {
            if (wsex.getMessage().equals(NO_RESPONSE) && _feature.isAddressingEnabled()) {
                // Ignore it
                LOGGER.warn("Sent a message with ReplyTo to a Request_Response Webservice, so no response returned!");
            } else {
                throw new SOAPException(wsex);
            }
        } catch (Exception ex) {
            throw new SOAPException("Cannot process SOAP request", ex);
        }

        return response;
    }
}
