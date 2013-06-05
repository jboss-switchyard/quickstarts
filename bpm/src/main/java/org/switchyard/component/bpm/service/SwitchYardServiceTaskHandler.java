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
package org.switchyard.component.bpm.service;

import static org.switchyard.component.common.knowledge.KnowledgeConstants.PARAMETER;
import static org.switchyard.component.common.knowledge.KnowledgeConstants.RESULT;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.kie.api.runtime.process.ProcessRuntime;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.switchyard.common.lang.Strings;
import org.switchyard.common.xml.XMLHelper;
import org.switchyard.component.common.knowledge.service.SwitchYardServiceInvoker;
import org.switchyard.component.common.knowledge.service.SwitchYardServiceRequest;
import org.switchyard.component.common.knowledge.service.SwitchYardServiceResponse;

/**
 * SwitchYardServiceTaskHandler.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public class SwitchYardServiceTaskHandler implements WorkItemHandler {

    private static final Logger LOGGER = Logger.getLogger(SwitchYardServiceTaskHandler.class);

    /** SwitchYard Service Task. */
    public static final String SWITCHYARD_SERVICE_TASK = "SwitchYard Service Task";

    /** ServiceName. */
    public static final String SERVICE_NAME = "ServiceName";
    /** Operation. */
    public static final String OPERATION = "Operation";
    /** OperationName. */
    public static final String OPERATION_NAME = OPERATION + "Name";
    /** ParameterName. */
    public static final String PARAMETER_NAME = PARAMETER + "Name";
    /** ResultName. */
    public static final String RESULT_NAME = RESULT + "Name";
    /** FaultResultName. */
    public static final String FAULT_RESULT_NAME = "Fault" + RESULT_NAME;
    /** FaultEventId. */
    public static final String FAULT_EVENT_ID = "FaultEventId";
    /** FaultWorkItemAction. */
    public static final String FAULT_WORK_ITEM_ACTION = "FaultWorkItemAction";

    private String _name;
    private SwitchYardServiceInvoker _invoker;
    private ProcessRuntime _processRuntime;

    /**
     * Constructs a new SwitchYardServiceTaskHandler with the name "SwitchYard Service Task".
     */
    public SwitchYardServiceTaskHandler() {
        setName(SWITCHYARD_SERVICE_TASK);
    }

    /**
     * Gets the name.
     * @return the name
     */
    public String getName() {
        return _name;
    }

    /**
     * Sets the name.
     * @param name the name
     */
    public void setName(String name) {
        _name = name;
    }

    /**
     * Gets the invoker.
     * @return the invoker
     */
    public SwitchYardServiceInvoker getInvoker() {
        return _invoker;
    }

    /**
     * Sets the invoker.
     * @param invoker the invoker
     */
    public void setInvoker(SwitchYardServiceInvoker invoker) {
        _invoker = invoker;
    }

    /**
     * Gets the ProcessRuntime.
     * @return the ProcessRuntime
     */
    public ProcessRuntime getProcessRuntime() {
        return _processRuntime;
    }

    /**
     * Sets the ProcessRuntime.
     * @param processRuntime the ProcessRuntime
     */
    public void setProcessRuntime(ProcessRuntime processRuntime) {
        _processRuntime = processRuntime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        // input
        Map<String, Object> parameters = workItem.getParameters();
        Object content = null;
        String parameterName = getParameterName(parameters);
        if (parameterName != null) {
            content = parameters.get(parameterName);
        }
        // invoke
        QName serviceName = getServiceName(parameters);
        String operationName = getOperationName(parameters);
        SwitchYardServiceRequest request = new SwitchYardServiceRequest(serviceName, operationName, content, parameters);
        SwitchYardServiceResponse response = getInvoker().invoke(request);
        // output
        Map<String, Object> results = workItem.getResults();
        if (results == null) {
            results = new HashMap<String, Object>();
        }
        String resultName = getResultName(parameters);
        if (resultName != null) {
            results.put(resultName, response.getContent());
        }
        // fault
        Object fault = response.getFault();
        if (fault == null) {
            manager.completeWorkItem(workItem.getId(), results);
        } else {
            if (fault instanceof Throwable) {
                Throwable t = (Throwable)fault;
                LOGGER.error("Fault encountered: " + t.getMessage(), t);
            } else {
                LOGGER.error("Fault encountered: " + fault);
            }
            String faultResultName = getFaultResultName(parameters);
            if (faultResultName != null) {
                results.put(faultResultName, fault);
            }
            String faultEventId = getFaultEventId(parameters);
            if (faultEventId != null) {
                getProcessRuntime().signalEvent(faultEventId, fault, workItem.getProcessInstanceId());
            }
            FaultWorkItemAction faultWorkItemAction = getFaultWorkItemAction(parameters);
            if (faultWorkItemAction != null) {
                switch (faultWorkItemAction) {
                    case ABORT:
                        manager.abortWorkItem(workItem.getId());
                        break;
                    case COMPLETE:
                        manager.completeWorkItem(workItem.getId(), results);
                        break;
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        // noop
    }

    protected QName getServiceName(Map<String, Object> parameters) {
        return getQName(SERVICE_NAME, parameters, null);
    }

    protected String getOperationName(Map<String, Object> parameters) {
        return getString(OPERATION_NAME, parameters, null);
    }

    protected String getParameterName(Map<String, Object> parameters) {
        return getString(PARAMETER_NAME, parameters, PARAMETER);
    }

    protected String getResultName(Map<String, Object> parameters) {
        return getString(RESULT_NAME, parameters, RESULT);
    }

    private String getFaultResultName(Map<String, Object> parameters) {
        return getString(FAULT_RESULT_NAME, parameters, null);
    }

    private String getFaultEventId(Map<String, Object> parameters) {
        return getString(FAULT_EVENT_ID, parameters, null);
    }

    private FaultWorkItemAction getFaultWorkItemAction(Map<String, Object> parameters) {
        String s = getString(FAULT_WORK_ITEM_ACTION, parameters, null);
        if (s != null) {
            try {
                return FaultWorkItemAction.valueOf(s.toUpperCase());
            } catch (IllegalArgumentException iae) {
                LOGGER.warn(String.format("Unknown %s: %s", FaultWorkItemAction.class.getSimpleName(), iae.getMessage()));
            }
        }
        return null;
    }

    protected QName getQName(String parameterName, Map<String, Object> parameters, QName defaultValue) {
        Object serviceName = parameters.get(parameterName);
        if (serviceName instanceof QName) {
           return (QName)serviceName;
        } else if (serviceName instanceof String) {
            return XMLHelper.createQName((String)serviceName);
        }
        return defaultValue;
    }

    protected String getString(String parameterName, Map<String, Object> parameters, String defaultValue) {
        String value = null;
        Object p = parameters.get(parameterName);
        if (p != null) {
            value = Strings.trimToNull(String.valueOf(p));
        }
        if (value == null) {
            value = defaultValue;
        }
        return value;
    }

    private static enum FaultWorkItemAction {
        ABORT,
        COMPLETE;
    }

}
