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

import static org.switchyard.component.common.knowledge.KnowledgeConstants.FAULT;
import static org.switchyard.component.common.knowledge.KnowledgeConstants.PARAMETER;
import static org.switchyard.component.common.knowledge.KnowledgeConstants.RESULT;

import java.util.Map;

import javax.xml.namespace.QName;

import org.jbpm.bpmn2.handler.WorkItemHandlerRuntimeException;
import org.kie.api.runtime.process.ProcessRuntime;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.switchyard.SwitchYardException;
import org.switchyard.common.lang.Strings;
import org.switchyard.common.xml.XMLHelper;
import org.switchyard.component.common.knowledge.CommonKnowledgeLogger;
import org.switchyard.deploy.ComponentNames;

/**
 * SwitchYardServiceTaskHandler.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public class SwitchYardServiceTaskHandler implements WorkItemHandler {

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
    /** FaultName. */
    public static final String FAULT_NAME = FAULT + "Name";
    /** FaultEventId. */
    public static final String FAULT_EVENT_ID = FAULT + "EventId";
    /** FaultAction. */
    public static final String FAULT_ACTION = FAULT + "Action";

    private QName _componentName;
    private SwitchYardServiceInvoker _invoker;
    private ProcessRuntime _processRuntime;

    /**
     * Constructs a new SwitchYardServiceTaskHandler.
     */
    public SwitchYardServiceTaskHandler() {}

    /**
     * Gets the component name.
     * @return the component name
     */
    public QName getComponentName() {
        return _componentName;
    }

    /**
     * Set the component name.
     * @param componentName the component name
     */
    public void setComponentName(QName componentName) {
        _componentName = componentName;
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
        // parameters (input)
        Map<String, Object> parameters = workItem.getParameters();
        Object content = null;
        String parameterName = getParameterName(parameters);
        if (parameterName != null) {
            content = parameters.get(parameterName);
        }
        // service invocation
        QName serviceName = getServiceName(parameters);
        if (serviceName != null && _componentName != null) {
            serviceName = ComponentNames.qualify(_componentName, ComponentNames.unqualify(serviceName));
        }
        String operationName = getOperationName(parameters);
        SwitchYardServiceRequest request = new SwitchYardServiceRequest(serviceName, operationName, content);
        SwitchYardServiceResponse response = getInvoker().invoke(request);
        // results (output)
        Map<String, Object> results = workItem.getResults();
        String resultName = getResultName(parameters);
        if (!response.hasFault()) {
            // result (success)
            if (resultName != null) {
                Object result = response.getContent();
                results.put(resultName, result);
            }
            manager.completeWorkItem(workItem.getId(), results);
        } else {
            // fault (failure)
            String fmsg = response.getFaultMessage();
            response.logFaultMessage(fmsg);
            Object fault = response.getFault();
            String faultName = getFaultName(parameters);
            if (faultName != null) {
                results.put(faultName, fault);
            }
            String faultEventId = getFaultEventId(parameters);
            if (faultEventId != null) {
                getProcessRuntime().signalEvent(faultEventId, fault, workItem.getProcessInstanceId());
            }
            FaultAction faultAction = getFaultAction(parameters);
            switch (faultAction) {
                case ABORT: {
                    manager.abortWorkItem(workItem.getId());
                    break;
                }
                case COMPLETE: {
                    manager.completeWorkItem(workItem.getId(), results);
                    break;
                }
                case SKIP: {
                    break;
                }
                case THROW: {
                    final RuntimeException runtimeException;
                    if (fault instanceof RuntimeException) {
                        runtimeException = (RuntimeException)fault;
                    } else {
                        final Throwable cause;
                        if (fault instanceof Throwable) {
                            cause = (Throwable)fault;
                        } else {
                            cause = new SwitchYardException(fmsg);
                            cause.fillInStackTrace();
                        }
                        WorkItemHandlerRuntimeException wihre = new WorkItemHandlerRuntimeException(cause, fmsg);
                        wihre.setStackTrace(cause.getStackTrace());
                        wihre.setInformation(SERVICE_NAME, serviceName != null ? serviceName.toString() : null);
                        wihre.setInformation(OPERATION_NAME, operationName);
                        wihre.setInformation(PARAMETER_NAME, parameterName);
                        wihre.setInformation(RESULT_NAME, resultName);
                        wihre.setInformation(FAULT_NAME, faultName);
                        wihre.setInformation(FAULT_EVENT_ID, faultEventId);
                        wihre.setInformation(FAULT_ACTION, faultAction.name());
                        wihre.setInformation(WorkItemHandlerRuntimeException.WORKITEMHANDLERTYPE, getClass().getSimpleName());
                        runtimeException = wihre;
                    }
                    throw runtimeException;
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

    private String getParameterName(Map<String, Object> parameters) {
        return getString(PARAMETER_NAME, parameters, PARAMETER);
    }

    private String getResultName(Map<String, Object> parameters) {
        return getString(RESULT_NAME, parameters, RESULT);
    }

    private String getFaultName(Map<String, Object> parameters) {
        return getString(FAULT_NAME, parameters, FAULT);
    }

    private String getFaultEventId(Map<String, Object> parameters) {
        return getString(FAULT_EVENT_ID, parameters, null);
    }

    private FaultAction getFaultAction(Map<String, Object> parameters) {
        FaultAction faultAction;
        String fa = getString(FAULT_ACTION, parameters, FaultAction.DEFAULT.name());
        try {
            faultAction = FaultAction.valueOf(fa.toUpperCase());
        } catch (Throwable t) {
            CommonKnowledgeLogger.ROOT_LOGGER.unknownDefaultingTo(FAULT_ACTION, fa, t.getMessage(), FaultAction.DEFAULT.name());
            faultAction = FaultAction.DEFAULT;
        }
        return faultAction;
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

    private static enum FaultAction {
        ABORT,
        COMPLETE,
        SKIP,
        THROW;
        private static final FaultAction DEFAULT = THROW;
    }

}
