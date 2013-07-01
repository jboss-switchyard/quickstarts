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
package org.switchyard.component.common.knowledge.service;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.switchyard.Exchange;
import org.switchyard.ExchangePattern;
import org.switchyard.Message;
import org.switchyard.Property;
import org.switchyard.ServiceDomain;
import org.switchyard.ServiceReference;
import org.switchyard.SwitchYardException;
import org.switchyard.common.lang.Strings;
import org.switchyard.common.xml.XMLHelper;
import org.switchyard.component.common.SynchronousInOutHandler;

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
        Object contentOut = null;
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
            String operationName = request.getOperationName();
            if (operationName != null) {
                exchangeIn = serviceReference.createExchange(operationName, handler);
            } else {
                exchangeIn = serviceReference.createExchange(handler);
            }
            Message messageIn = exchangeIn.createMessage();
            for (Map.Entry<String,Object> entry : request.getContext().entrySet()) {
                exchangeIn.getContext(messageIn).setProperty(entry.getKey(), entry.getValue());
            }
            Object contentIn = request.getContent();
            if (contentIn != null) {
                messageIn.setContent(contentIn);
            }
            exchangeIn.send(messageIn);
            if (ExchangePattern.IN_OUT.equals(exchangeIn.getContract().getConsumerOperation().getExchangePattern())) {
                Exchange exchangeOut = handler.waitForOut();
                Message messageOut = exchangeOut.getMessage();
                contentOut = messageOut.getContent();
                for (Property property : exchangeOut.getContext(messageOut).getProperties()) {
                    contextOut.put(property.getName(), property.getValue());
                }
            }
            fault = handler.getFault();
        } catch (Throwable t) {
            fault = t;
        }
        return new SwitchYardServiceResponse(contentOut, contextOut, fault);
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
