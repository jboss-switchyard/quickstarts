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
import javax.xml.ws.soap.AddressingFeature;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;
import org.switchyard.Exchange;
import org.switchyard.HandlerException;
import org.switchyard.Message;
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

/**
 * Handles invoking external Webservice endpoints.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
public class OutboundHandler extends BaseServiceHandler {

    private static final Logger LOGGER = Logger.getLogger(OutboundHandler.class);

    private static final String NO_RESPONSE = "No response returned.";

    private final SOAPBindingModel _config;

    private MessageComposer<SOAPBindingData> _messageComposer;
    private Dispatch<SOAPMessage> _dispatcher;
    private Port _wsdlPort;
    private String _bindingId;
    private Boolean _documentStyle;
    private Addressing _addressing = new Addressing();

    /**
     * Constructor.
     * @param config the configuration settings
     */
    public OutboundHandler(final SOAPBindingModel config) {
        _config = config;
    }

    /**
     * Start lifecycle.
     * @throws WebServiceConsumeException If unable to load the WSDL
     */
    public void start() throws WebServiceConsumeException {
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

                _addressing = WSDLUtil.getAddressing(definition, _wsdlPort);
                _bindingId = WSDLUtil.getBindingId(_wsdlPort);
                String style = WSDLUtil.getStyle(_wsdlPort);
                _documentStyle = style.equals(WSDLUtil.DOCUMENT) ? true : false;

                _messageComposer = SOAPComposition.getMessageComposer(_config);
                ((SOAPMessageComposer)_messageComposer).setDocumentStyle(_documentStyle);
                ((SOAPMessageComposer)_messageComposer).setWsdlPort(_wsdlPort);

                URL wsdlUrl = WSDLUtil.getURL(_config.getWsdl());
                LOGGER.info("Creating dispatch with WSDL " + wsdlUrl);
                // make sure we don't pollute the class loader used by the WS subsystem
                Classes.setTCCL(getClass().getClassLoader());
                Service service = Service.create(wsdlUrl, portName.getServiceQName());
                _dispatcher = service.createDispatch(portName.getPortQName(),
                                    SOAPMessage.class,
                                    Service.Mode.MESSAGE,
                                    new AddressingFeature(_addressing.isEnabled(), _addressing.isRequired()));
                // this does not return a proper qualified Fault element and has no Detail so deferring for now
                // _dispatcher.getRequestContext().put("jaxws.response.throwExceptionIfSOAPFault", Boolean.FALSE);

                Binding binding = _dispatcher.getBinding();
                List<Handler> handlers = binding.getHandlerChain();
                handlers.add(new OutboundResponseHandler());

                if (_addressing.isEnabled()) {
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
        try {
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

                if (_addressing.isEnabled()) {
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
            if (!_addressing.isEnabled() && (action != null)) {
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
            if (wsex.getMessage().equals(NO_RESPONSE) && _addressing.isEnabled()) {
                // Ignore it
                LOGGER.warn("Sent a message with ReplyTo to a Request_Response Webservice, so no response returned!");
            } else {
                throw wsex;
            }
        } catch (Exception ex) {
            throw new SOAPException("Cannot process SOAP request", ex);
        }

        return response;
    }
}
