/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.component.common.knowledge.service;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.switchyard.Context;
import org.switchyard.Exchange;
import org.switchyard.ExchangePattern;
import org.switchyard.Message;
import org.switchyard.Property;
import org.switchyard.Scope;
import org.switchyard.ServiceDomain;
import org.switchyard.ServiceReference;
import org.switchyard.SynchronousInOutHandler;
import org.switchyard.common.lang.Strings;
import org.switchyard.common.xml.XMLHelper;
import org.switchyard.exception.SwitchYardException;

/**
 * SwitchYardServiceInvoker.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public class SwitchYardServiceInvoker {

    private final ServiceDomain _serviceDomain;
    private final String _targetNamespace;

    /**
     * Constructs a new SwitchYardServiceInvoker with the specified service domain.
     * @param serviceDomain the service domain
     */
    public SwitchYardServiceInvoker(ServiceDomain serviceDomain) {
        this(serviceDomain, null);
    }

    /**
     * Constructs a new SwitchYardServiceInvoker with the specified service domain and target namespace.
     * @param serviceDomain the service domain
     * @param targetNamespace the target namespace
     */
    public SwitchYardServiceInvoker(ServiceDomain serviceDomain, String targetNamespace) {
        _serviceDomain = serviceDomain;
        _targetNamespace = targetNamespace;
    }

    /**
     * Gets the service domain.
     * @return the service domain
     */
    public ServiceDomain getServiceDomain() {
        return _serviceDomain;
    }

    /**
     * Gets the target namespace.
     * @return the target namespace
     */
    public String getTargetNamespace() {
        return _targetNamespace;
    }

    /**
     * Invokes the request and returns the response.
     * @param request the request
     * @return the response
     */
    public SwitchYardServiceResponse invoke(SwitchYardServiceRequest request) {
        Map<String, Object> contextOut = new HashMap<String, Object>();
        Object contentOutput = null;
        Object fault = null;
        try {
            QName serviceName = request.getServiceName();
            if (serviceName == null) {
                throw new SwitchYardException("ServiceName == null");
            } else if (Strings.trimToNull(serviceName.getNamespaceURI()) == null) {
                String tns = getTargetNamespace();
                if (tns != null) {
                    serviceName = XMLHelper.createQName(tns, serviceName.getLocalPart());
                }
            }
            ServiceDomain serviceDomain = getServiceDomain();
            if (serviceDomain == null) {
                throw new SwitchYardException("ServiceDomain == null");
            }
            ServiceReference serviceReference = serviceDomain.getServiceReference(serviceName);
            if (serviceReference == null) {
                throw new SwitchYardException("ServiceReference [" + serviceName + "] == null");
            }
            final Exchange exchangeIn;
            FaultHandler handler = new FaultHandler();
            String serviceOperationName = request.getServiceOperationName();
            if (serviceOperationName != null) {
                exchangeIn = serviceReference.createExchange(serviceOperationName, handler);
            } else {
                exchangeIn = serviceReference.createExchange(handler);
            }
            Context contextIn = exchangeIn.getContext();
            for (Map.Entry<String,Object> entry : request.getContext().entrySet()) {
                contextIn.setProperty(entry.getKey(), entry.getValue(), Scope.IN);
            }
            Message messageIn = exchangeIn.createMessage();
            Object contentInput = request.getContent();
            if (contentInput != null) {
                messageIn.setContent(contentInput);
            }
            exchangeIn.send(messageIn);
            if (ExchangePattern.IN_OUT.equals(exchangeIn.getContract().getConsumerOperation().getExchangePattern())) {
                Exchange exchangeOut = handler.waitForOut();
                Message messageOut = exchangeOut.getMessage();
                contentOutput = messageOut.getContent();
                for (Property property : exchangeOut.getContext().getProperties(Scope.OUT)) {
                    contextOut.put(property.getName(), property.getValue());
                }
            }
            fault = handler.getFault();
        } catch (Throwable t) {
            fault = t;
        }
        return new SwitchYardServiceResponse(contentOutput, contextOut, fault);
    }

    private static final class FaultHandler extends SynchronousInOutHandler {
        private Object _fault;
        private Object getFault() {
            return _fault;
        }
        @Override
        public void handleFault(Exchange exchange) {
            _fault = exchange.getMessage().getContent();
            super.handleFault(exchange);
        }
    }

}
