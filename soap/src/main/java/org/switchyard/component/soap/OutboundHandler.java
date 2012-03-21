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

import javax.wsdl.Port;
import javax.wsdl.WSDLException;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.soap.AddressingFeature;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;
import org.switchyard.Exchange;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.component.soap.composer.SOAPComposition;
import org.switchyard.component.soap.config.model.SOAPBindingModel;
import org.switchyard.component.soap.util.SOAPUtil;
import org.switchyard.component.soap.util.WSDLUtil;
import org.switchyard.composer.MessageComposer;
import org.switchyard.deploy.BaseServiceHandler;

/**
 * Handles invoking external Webservice endpoints.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
public class OutboundHandler extends BaseServiceHandler {

    private static final Logger LOGGER = Logger.getLogger(OutboundHandler.class);

    private final SOAPBindingModel _config;
    private final MessageComposer<SOAPMessage> _messageComposer;

    private Dispatch<SOAPMessage> _dispatcher;
    private Port _port;

    /**
     * Constructor.
     * @param config the configuration settings
     */
    public OutboundHandler(final SOAPBindingModel config) {
        _config = config;
        _messageComposer = SOAPComposition.getMessageComposer(config);
    }

    /**
     * Start lifecycle.
     * @throws WebServiceConsumeException If unable to load the WSDL
     */
    public void start() throws WebServiceConsumeException {
        if (_dispatcher == null) {
            ClassLoader origLoader = Thread.currentThread().getContextClassLoader();
            try {
                PortName portName = _config.getPort();
                javax.wsdl.Service wsdlService = WSDLUtil.getService(_config.getWsdl(), portName);
                _port = WSDLUtil.getPort(wsdlService, portName);
                // Update the portName
                portName.setServiceQName(wsdlService.getQName());
                portName.setName(_port.getName());

                URL wsdlUrl = WSDLUtil.getURL(_config.getWsdl());
                LOGGER.info("Creating dispatch with WSDL " + wsdlUrl);
                // make sure we don't pollute the class loader used by the WS subsystem
                Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
                Service service = Service.create(wsdlUrl, portName.getServiceQName());
                _dispatcher = service.createDispatch(portName.getPortQName(), SOAPMessage.class, Service.Mode.MESSAGE, new AddressingFeature(false, false));
                // this does not return a proper qualified Fault element and has no Detail so deferring for now
                // BindingProvider bp = (BindingProvider) _dispatcher;
                // bp.getRequestContext().put("jaxws.response.throwExceptionIfSOAPFault", Boolean.FALSE);

            } catch (MalformedURLException e) {
                throw new WebServiceConsumeException(e);
            } catch (WSDLException wsdle) {
                throw new WebServiceConsumeException(wsdle);
            } finally {
                Thread.currentThread().setContextClassLoader(origLoader);
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
            if (SOAPUtil.SOAP_MESSAGE_FACTORY == null) {
                throw new SOAPException("Failed to instantiate SOAP Message Factory");
            }

            SOAPMessage request;
            try {
                request = _messageComposer.decompose(exchange, SOAPUtil.SOAP_MESSAGE_FACTORY.createMessage());
            } catch (Exception e) {
                throw e instanceof SOAPException ? (SOAPException)e : new SOAPException(e);
            }
            
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Request:[" + SOAPUtil.soapMessageToString(request) + "]");
            }
            
            SOAPMessage response = invokeService(request);
            if (response != null) {
                Message message;
                try {
                    message = _messageComposer.compose(response, exchange, true);
                } catch (Exception e) {
                    throw e instanceof SOAPException ? (SOAPException)e : new SOAPException(e);
                }
                exchange.send(message);
            }
            
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Response:[" + SOAPUtil.soapMessageToString(response) + "]");
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

        SOAPMessage response = null;
        try {
            String firstBodyElement = SOAPUtil.getFirstBodyElement(soapMessage);
            if (WSDLUtil.isOneWay(_port, firstBodyElement)) {
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
