/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.component.bpm.work;

import static org.switchyard.component.common.knowledge.KnowledgeConstants.CONTENT_INPUT;
import static org.switchyard.component.common.knowledge.KnowledgeConstants.CONTENT_OUTPUT;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.kie.runtime.process.WorkItem;
import org.kie.runtime.process.WorkItemManager;
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
 * A WorkItemHandler that can send to a SwitchYard Service.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class SwitchYardServiceWorkItemHandler extends BaseSwitchYardWorkItemHandler {

    private static final Logger LOGGER = Logger.getLogger(SwitchYardServiceWorkItemHandler.class);

    /** SwitchYard Service. */
    public static final String SWITCHYARD_SERVICE = "SwitchYard Service";
    /** Service Name. */
    public static final String SERVICE_NAME = "ServiceName";
    /** Service Operation Name. */
    public static final String SERVICE_OPERATION_NAME = "ServiceOperationName";
    /** Content Input Name. */
    public static final String CONTENT_INPUT_NAME = "ContentInputName";
    /** Content Output Name. */
    public static final String CONTENT_OUTPUT_NAME = "ContentOutputName";
    /** Fault Result Name. */
    public static final String FAULT_RESULT_NAME = "FaultResultName";
    /** Fault Signal Id. */
    public static final String FAULT_SIGNAL_ID = "FaultSignalId";
    /** Complete After Fault. */
    public static final String COMPLETE_AFTER_FAULT = "CompleteAfterFault";

    /**
     * Constructs a new SwitchYardServiceWorkItemHandler with the name "SwitchYard Service".
     */
    public SwitchYardServiceWorkItemHandler() {
        setName(SWITCHYARD_SERVICE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        Map<String,Object> parameters = workItem.getParameters();
        Map<String,Object> results = workItem.getResults();
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
                throw new IllegalStateException("ServiceReference [" + serviceName + "] == null");
            }
            String operation = getServiceOperationName(parameters);
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
            String contentInputName = getContentInputName(parameters);
            if (contentInputName != null) {
                Object contentInput = parameters.get(contentInputName);
                if (contentInput != null) {
                    messageIn.setContent(contentInput);
                }
            }
            if (ExchangePattern.IN_OUT.equals(exchangeIn.getContract().getConsumerOperation().getExchangePattern())) {
                exchangeIn.send(messageIn);
                Exchange exchangeOut = handler.waitForOut();
                Message messageOut = exchangeOut.getMessage();
                String contentOutputName = getContentOutputName(parameters);
                if (contentOutputName != null) {
                    Object contentOutput = messageOut.getContent();
                    results.put(contentOutputName, contentOutput);
                }
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
            String faultSignalId = getFaultSignalId(parameters);
            if (faultSignalId != null) {
                getProcessRuntime().signalEvent(faultSignalId, fault, workItem.getProcessInstanceId());
            }
            if (completeAfterFault(parameters)) {
                manager.completeWorkItem(workItem.getId(), results);
            }
        } else {
            manager.completeWorkItem(workItem.getId(), results);
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

    private String getServiceOperationName(Map<String, Object> parameters) {
        return getString(SERVICE_OPERATION_NAME, parameters, null);
    }

    private String getContentInputName(Map<String, Object> parameters) {
        return getString(CONTENT_INPUT_NAME, parameters, CONTENT_INPUT);
    }

    private String getContentOutputName(Map<String, Object> parameters) {
        return getString(CONTENT_OUTPUT_NAME, parameters, CONTENT_OUTPUT);
    }

    private String getFaultResultName(Map<String, Object> parameters) {
        return getString(FAULT_RESULT_NAME, parameters, null);
    }

    private String getFaultSignalId(Map<String, Object> parameters) {
        return getString(FAULT_SIGNAL_ID, parameters, null);
    }

    private String getString(String name, Map<String, Object> parameters, String defaultValue) {
        String value = null;
        Object p = parameters.get(name);
        if (p != null) {
            value = Strings.trimToNull(String.valueOf(p));
        }
        if (value == null) {
            value = defaultValue;
        }
        return value;
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
