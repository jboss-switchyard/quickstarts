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
package org.switchyard.component.bpm.task.work;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.switchyard.Context;
import org.switchyard.Exchange;
import org.switchyard.ExchangePattern;
import org.switchyard.Message;
import org.switchyard.Property;
import org.switchyard.Scope;
import org.switchyard.ServiceReference;
import org.switchyard.SynchronousInOutHandler;
import org.switchyard.common.lang.Strings;
import org.switchyard.common.xml.XMLHelper;

/**
 * Executes SwitchYard Services.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class SwitchYardServiceTaskHandler extends BaseTaskHandler {

    private static final Logger LOGGER = Logger.getLogger(SwitchYardServiceTaskHandler.class);

    /** SwitchYard Service. */
    public static final String SWITCHYARD_SERVICE = "SwitchYard Service";
    /** ServiceName. */
    public static final String SERVICE_NAME = "ServiceName";
    /** ServiceOperationName. */
    public static final String SERVICE_OPERATION_NAME = "ServiceOperationName";
    /** FaultResultName. */
    public static final String FAULT_RESULT_NAME = "FaultResultName";
    /** FaultEventType. */
    public static final String FAULT_EVENT_TYPE = "FaultEventType";
    /** CompleteAfterFault. */
    public static final String COMPLETE_AFTER_FAULT = "CompleteAfterFault";

    /**
     * Constructs a new SwitchYardServiceTaskHandler with the default name ("SwitchYard Service").
     */
    public SwitchYardServiceTaskHandler() {
        super(SWITCHYARD_SERVICE);
    }

    /**
     * Constructs a new SwitchYardServiceTaskHandler with the specified name.
     * @param name the specified name
     */
    public SwitchYardServiceTaskHandler(String name) {
        super(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void executeTask(Task task, TaskManager manager) {
        Map<String,Object> parameters = task.getParameters();
        Map<String,Object> results = task.getResults();
        if (results == null) {
            results = new HashMap<String, Object>();
        }
        Object fault = null;
        try {
            QName serviceName = getServiceName(parameters);
            if (serviceName == null) {
                throw new IllegalStateException(SERVICE_NAME + " == null");
            }
            ServiceReference serviceRef = getServiceDomain().getServiceReference(serviceName);
            if (serviceRef == null) {
                throw new IllegalStateException("serviceRef (" + serviceName + ") == null");
            }
            String operation = (String)parameters.get(SERVICE_OPERATION_NAME);
            final Exchange exchangeIn;
            FaultHandler handler = new FaultHandler();
            if (operation != null) {
                exchangeIn = serviceRef.createExchange(operation, handler);
            } else {
                exchangeIn = serviceRef.createExchange(handler);
            }
            Context contextIn = exchangeIn.getContext();
            for (Map.Entry<String,Object> entry : parameters.entrySet()) {
                contextIn.setProperty(entry.getKey(), entry.getValue(), Scope.IN);
            }
            Message messageIn = exchangeIn.createMessage();
            String messageContentInName = getMessageContentInName();
            Object messageContentIn = parameters.get(messageContentInName);
            if (messageContentIn != null) {
                messageIn.setContent(messageContentIn);
            }
            if (ExchangePattern.IN_OUT.equals(exchangeIn.getContract().getConsumerOperation().getExchangePattern())) {
                exchangeIn.send(messageIn);
                Exchange exchangeOut = handler.waitForOut();
                Message messageOut = exchangeOut.getMessage();
                Object messageContentOut = messageOut.getContent();
                String messageContentOutName = getMessageContentOutName();
                results.put(messageContentOutName, messageContentOut);
                Context contextOut = exchangeOut.getContext();
                for (Property property : contextOut.getProperties(Scope.OUT)) {
                    results.put(property.getName(), property.getValue());
                }
            } else {
                exchangeIn.send(messageIn);
            }
            fault = handler.getFault();
        } catch (Throwable t) {
            LOGGER.error(t);
            fault = t;
        }
        if (fault != null) {
            String faultResultName = getFaultResultName(parameters);
            if (faultResultName != null) {
                results.put(faultResultName, fault);
            }
            String faultEventType = getFaultEventType(parameters);
            if (faultEventType != null) {
                manager.signalEvent(faultEventType, fault, task.getProcessInstanceId());
            }
            if (completeAfterFault(parameters)) {
                manager.completeTask(task.getId(), results);
            }
        } else {
            manager.completeTask(task.getId(), results);
        }
    }

    private QName getServiceName(Map<String, Object> parameters) {
        QName serviceName = null;
        Object p = parameters.get(SERVICE_NAME);
        if (p instanceof QName) {
            serviceName = (QName)p;
        } else if (p instanceof String) {
            serviceName = XMLHelper.createQName((String)p);
        }
        if (serviceName != null && Strings.trimToNull(serviceName.getNamespaceURI()) == null) {
            String tns = getTargetNamespace();
            if (tns != null) {
                serviceName = XMLHelper.createQName(tns, serviceName.getLocalPart());
            }
        }
        return serviceName;
    }

    private String getFaultResultName(Map<String, Object> parameters) {
        Object p = parameters.get(FAULT_RESULT_NAME);
        if (p != null) {
            return Strings.trimToNull(String.valueOf(p));
        }
        return null;
    }

    private String getFaultEventType(Map<String, Object> parameters) {
        Object p = parameters.get(FAULT_EVENT_TYPE);
        if (p != null) {
            return Strings.trimToNull(String.valueOf(p));
        }
        return null;
    }

    private boolean completeAfterFault(Map<String, Object> parameters) {
        Object p = parameters.get(COMPLETE_AFTER_FAULT);
        if (p != null) {
            if (p instanceof Boolean) {
                return ((Boolean)p).booleanValue();
            } else if (p instanceof String) {
                return Boolean.valueOf(((String)p).trim()).booleanValue();
            }
        }
        return true;
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
