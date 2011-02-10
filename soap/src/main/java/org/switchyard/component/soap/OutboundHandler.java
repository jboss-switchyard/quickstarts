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

import java.net.MalformedURLException;
import java.net.URL;
import javax.wsdl.Port;
import javax.wsdl.WSDLException;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
//import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.soap.AddressingFeature;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;
import org.switchyard.BaseHandler;
import org.switchyard.Exchange;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.component.soap.config.model.SOAPBindingModel;
import org.switchyard.component.soap.util.SOAPUtil;
import org.switchyard.component.soap.util.WSDLUtil;

/**
 * Handles invoking external Webservice endpoints.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
public class OutboundHandler extends BaseHandler {

    private static final Logger LOGGER = Logger.getLogger(OutboundHandler.class);
    private MessageComposer _composer;
    private MessageDecomposer _decomposer;
    private Dispatch<SOAPMessage> _dispatcher;
    private Port _port;
    private SOAPBindingModel _config;

    /**
     * Constructor.
     * @param config the configuration settings
     */
    public OutboundHandler(final SOAPBindingModel config) {
        _config = config;
        String composer = config.getComposer();
        String decomposer = config.getDecomposer();

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

        _decomposer = new DefaultMessageDecomposer();
    }

    /**
     * Start lifecycle.
     */
    public void start() {
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
            SOAPMessage request = _decomposer.decompose(exchange.getMessage());
            SOAPMessage response = invokeService(request);
            if (response != null) {
                Message message = _composer.compose(response, exchange);
                exchange.send(message);
            }
        } catch (SOAPException se) {
            throw new HandlerException("Unexpected exception handling SOAP Message", se);
        }
    }

    /**
     * Invoke Webservice via Dispatch API
     * @param soapMessage the SOAP request
     * @return the SOAP response
     * @throws SOAPException If a Dispatch could not be created based on the SOAP message.
     */
    private SOAPMessage invokeService(final SOAPMessage soapMessage) throws SOAPException {
        if (_dispatcher == null) {
            try {
                PortName portName = _config.getPort();
                javax.wsdl.Service wsdlService = WSDLUtil.getService(_config.getWsdl(), portName);
                _port = WSDLUtil.getPort(wsdlService, portName);
                // Update the portName
                portName.setServiceQName(wsdlService.getQName());
                portName.setName(_port.getName());

                URL wsdlUrl = new URL(_config.getWsdl());
                Service service = Service.create(wsdlUrl, portName.getServiceQName());
                _dispatcher = service.createDispatch(portName.getPortQName(), SOAPMessage.class, Service.Mode.MESSAGE, new AddressingFeature(false, false));
                // this does not return a proper qualified Fault element and has no Detail so defering for now
                // BindingProvider bp = (BindingProvider) _dispatcher;
                // bp.getRequestContext().put("jaxws.response.throwExceptionIfSOAPFault", Boolean.FALSE);

            } catch (MalformedURLException e) {
                throw new SOAPException(e);
            } catch (WSDLException wsdle) {
                throw new SOAPException(wsdle);
            }
        }

        SOAPMessage response = null;
        try {
            String operationName = SOAPUtil.getOperationName(soapMessage);
            if (SOAPUtil.isMessageOneWay(_port, operationName)) {
                _dispatcher.invokeOneWay(soapMessage);
                //return empty response
            } else {
                response = _dispatcher.invoke(soapMessage);
            }
        } catch (SOAPFaultException sfex) {
            response = SOAPUtil.generateFault(sfex);
        } catch (Exception ex) {
            throw new SOAPException("Cannot process SOAP request", ex);
        }

        return response;
    }
}
