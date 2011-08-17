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
package org.switchyard.component.bpm.task;

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
import org.switchyard.common.xml.XMLHelper;
import org.switchyard.exception.DeliveryException;
import org.switchyard.metadata.BaseExchangeContract;
import org.switchyard.metadata.ExchangeContract;
import org.switchyard.metadata.ServiceOperation;

/**
 * Executes SwitchYard Services.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class SwitchYardServiceTaskHandler extends BaseTaskHandler {

    private static final Logger LOGGER = Logger.getLogger(SwitchYardServiceTaskHandler.class);

    /** SwitchYard Service . */
    public static final String SWITCHYARD_SERVICE = "SwitchYard Service";
    /** ServiceName . */
    public static final String SERVICE_NAME = "ServiceName";
    /** ServiceOperationName . */
    public static final String SERVICE_OPERATION_NAME = "ServiceOperationName";
    /** Process variable name used for the input message content on a service invocation. */
    public static final String INPUT_MESSAGE_VAR = "InputMessageVariable";
    /** Process variable name used for the output message content on a service invocation. */
    public static final String OUTPUT_MESSAGE_VAR = "OutputMessageVariable";

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
    public void executeTask(Task task, TaskManager taskManager) {
        String problem = null;
        Map<String,Object> parameters = task.getParameters();
        Map<String,Object> results = null;
        QName serviceName = getServiceName(parameters);
        if (serviceName != null) {
            ServiceReference serviceRef = getServiceDomain().getService(serviceName);
            if (serviceRef != null) {
                ExchangeContract exchangeContract = getExchangeContract(serviceRef, parameters);
                ExchangePattern exchangePattern = exchangeContract.getServiceOperation().getExchangePattern();
                SynchronousInOutHandler inOutHandler = null;

                final Exchange exchangeIn;
                if (ExchangePattern.IN_OUT.equals(exchangePattern)) {
                    inOutHandler = new SynchronousInOutHandler();
                    exchangeIn = serviceRef.createExchange(exchangeContract, inOutHandler);
                } else {
                    exchangeIn = serviceRef.createExchange(exchangeContract);
                }
                Context context = exchangeIn.getContext();
                for (Map.Entry<String,Object> entry : parameters.entrySet()) {
                    context.setProperty(entry.getKey(), entry.getValue(), Scope.IN);
                }
                Message message = exchangeIn.createMessage();
                Object content = task.getProcessInstanceVariable(getInputContentName(task));
                if (content != null) {
                    message.setContent(content);
                }
                if (inOutHandler != null && ExchangePattern.IN_OUT.equals(exchangePattern)) {
                    exchangeIn.send(message);

                    try {
                        Exchange exchangeOut = inOutHandler.waitForOut();

                        context = exchangeOut.getContext();
                        message = exchangeOut.getMessage();

                        content = message.getContent();
                        task.setProcessInstanceVariable(getOutputContentName(task), content);
                        results = new HashMap<String,Object>();
                        for (Property property : context.getProperties(Scope.OUT)) {
                            results.put(property.getName(), property.getValue());
                        }
                    } catch (DeliveryException e) {
                        problem = e.getMessage();
                    }
                } else {
                    exchangeIn.send(message);
                }
            } else {
                problem = "serviceRef (" + serviceName + ") == null";
            }
        } else {
            problem = SERVICE_NAME + " == null";
        }
        if (problem == null) {
            taskManager.completeTask(task.getId(), results);
        } else {
            LOGGER.error(problem);
            taskManager.abortTask(task.getId());
        }
    }

    private QName getServiceName(Map<String, Object> parameters) {
        Object p = parameters.get(SERVICE_NAME);
        if (p instanceof QName) {
            return (QName)p;
        } else if (p instanceof String) {
            return XMLHelper.createQName((String)p);
        }
        return null;
    }
    
    private String getInputContentName(Task task) {
        // check for a task-level specification of input name
        String contentName = (String)task.getParameter(INPUT_MESSAGE_VAR);
        if (contentName == null) {
            contentName = getMessageContentName();
        }
        
        return contentName;
    }
    
    private String getOutputContentName(Task task) {
        // check for a task-level specification of output name
        String contentName = (String)task.getParameter(OUTPUT_MESSAGE_VAR);
        if (contentName == null) {
            contentName = getMessageContentName();
        }
        
        return contentName;
    }

    private ExchangeContract getExchangeContract(ServiceReference serviceRef, Map<String, Object> parameters) {
        final ExchangeContract exchangeContract;
        Object on = parameters.get(SERVICE_OPERATION_NAME);
        if (on instanceof String) {
            String operationName = (String)on;
            ServiceOperation operation = serviceRef.getInterface().getOperation(operationName);
            if (operation == null) {
                throw new RuntimeException("operationName [" + operationName + "] == null");
            }
            exchangeContract = new BaseExchangeContract(operation);
        } else {
            exchangeContract = ExchangeContract.IN_ONLY;
        }
        return exchangeContract;
    }
}
