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
package org.switchyard.component.bpm.exchange;

import static org.switchyard.component.common.knowledge.operation.KnowledgeOperations.getInput;
import static org.switchyard.component.common.knowledge.operation.KnowledgeOperations.getInputMap;
import static org.switchyard.component.common.knowledge.operation.KnowledgeOperations.setFaults;
import static org.switchyard.component.common.knowledge.operation.KnowledgeOperations.setGlobals;
import static org.switchyard.component.common.knowledge.operation.KnowledgeOperations.setOutputs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.KieInternalServices;
import org.kie.internal.process.CorrelationAwareProcessRuntime;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.process.CorrelationKeyFactory;
import org.switchyard.Context;
import org.switchyard.Exchange;
import org.switchyard.ExchangePattern;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.ServiceDomain;
import org.switchyard.common.lang.Strings;
import org.switchyard.component.bpm.BPMConstants;
import org.switchyard.component.bpm.BPMMessages;
import org.switchyard.component.bpm.config.model.BPMComponentImplementationModel;
import org.switchyard.component.bpm.operation.BPMOperationType;
import org.switchyard.component.common.knowledge.exchange.KnowledgeExchangeHandler;
import org.switchyard.component.common.knowledge.operation.KnowledgeOperation;
import org.switchyard.component.common.knowledge.runtime.KnowledgeRuntimeEngine;
import org.switchyard.component.common.knowledge.runtime.KnowledgeRuntimeManager;
import org.switchyard.component.common.knowledge.runtime.KnowledgeRuntimeManagerRegistry;
import org.switchyard.component.common.knowledge.transaction.TransactionHelper;

/**
 * A "bpm" implementation of a KnowledgeExchangeHandler.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class BPMExchangeHandler extends KnowledgeExchangeHandler {

    private static final KnowledgeOperation DEFAULT_OPERATION = new KnowledgeOperation(BPMOperationType.START_PROCESS);

    private final boolean _persistent;
    private final String _processId;
    private final CorrelationKeyFactory _correlationKeyFactory;
    private KnowledgeRuntimeManager _runtimeManager;

    /**
     * Constructs a new BPMExchangeHandler with the specified model, service domain, and service name.
     * @param model the specified model
     * @param serviceDomain the specified service domain
     * @param serviceName the specified service name
     */
    public BPMExchangeHandler(BPMComponentImplementationModel model, ServiceDomain serviceDomain, QName serviceName) {
        super(model, serviceDomain, serviceName);
        _persistent = model.isPersistent();
        _processId = model.getProcessId();
        _correlationKeyFactory = KieInternalServices.Factory.get().newCorrelationKeyFactory();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doStart() {
        super.doStart();
        _runtimeManager = newSingletonRuntimeManager();
        // TODO: SWITCHYARD-1584
        //_runtimeManager = newPerProcessInstanceRuntimeManager();
        //_runtimeManager = _persistent ? newPerProcessInstanceRuntimeManager() : newSingletonRuntimeManager();
        KnowledgeRuntimeManagerRegistry.putRuntimeManager(getServiceDomain().getName(), getServiceName(), _runtimeManager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doStop() {
        KnowledgeRuntimeManagerRegistry.removeRuntimeManager(getServiceDomain().getName(), getServiceName());
        try {
            _runtimeManager.close();
        } finally {
            _runtimeManager = null;
            super.doStop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KnowledgeOperation getDefaultOperation() {
        return DEFAULT_OPERATION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleOperation(Exchange exchange, KnowledgeOperation operation) throws HandlerException {
        //Long sessionIdentifier = null;
        Long processInstanceId = null;
        Message inputMessage = exchange.getMessage();
        ExchangePattern exchangePattern = exchange.getContract().getProviderOperation().getExchangePattern();
        Map<String, Object> expressionVariables = new HashMap<String, Object>();
        TransactionHelper txh = new TransactionHelper(_persistent);
        BPMOperationType operationType = (BPMOperationType)operation.getType();
        switch (operationType) {
            case START_PROCESS: {
                try {
                    txh.begin();
                    KnowledgeRuntimeEngine runtime = getRuntimeEngine();
                    //sessionIdentifier = runtime.getSessionIdentifier();
                    setGlobals(inputMessage, operation, runtime);
                    Map<String, Object> inputMap = getInputMap(inputMessage, operation, runtime);
                    ProcessInstance processInstance;
                    CorrelationKey correlationKey = getCorrelationKey(exchange, inputMessage);
                    if (correlationKey != null) {
                        processInstance = ((CorrelationAwareProcessRuntime)runtime.getKieSession()).startProcess(_processId, correlationKey, inputMap);
                    } else {
                        processInstance = runtime.getKieSession().startProcess(_processId, inputMap);
                    }
                    processInstanceId = Long.valueOf(processInstance.getId());
                    if (ExchangePattern.IN_OUT.equals(exchangePattern)) {
                        expressionVariables.putAll(getGlobalVariables(runtime));
                        expressionVariables.putAll(getProcessInstanceVariables(processInstance));
                    }
                    if (!_persistent) {
                        _runtimeManager.disposeRuntimeEngine(runtime);
                    }
                    txh.commit();
                } catch (RuntimeException re) {
                    txh.rollback();
                    throw re;
                }
                break;
            }
            case SIGNAL_EVENT:
            case SIGNAL_EVENT_ALL: {
                try {
                    txh.begin();
                    KnowledgeRuntimeEngine runtime;
                    if (BPMOperationType.SIGNAL_EVENT.equals(operationType)) {
                        runtime = getRuntimeEngine(exchange, inputMessage);
                    } else { //BPMOperationType.SIGNAL_EVENT_ALL
                        runtime = getRuntimeEngine();
                    }
                    //sessionIdentifier = runtime.getSessionIdentifier();
                    setGlobals(inputMessage, operation, runtime);
                    Object eventObject = getInput(inputMessage, operation, runtime);
                    String eventId = operation.getEventId();
                    if (BPMOperationType.SIGNAL_EVENT.equals(operationType)) {
                        processInstanceId = getProcessInstanceId(exchange, inputMessage, runtime);
                        if (processInstanceId == null) {
                            throw BPMMessages.MESSAGES.cannotSignalEventUnknownProcessInstanceIdOrUnknownunmatchedCorrelationKey();
                        }
                        if (ExchangePattern.IN_OUT.equals(exchangePattern)) {
                            ProcessInstance processInstance = runtime.getKieSession().getProcessInstance(processInstanceId);
                            processInstance.signalEvent(eventId, eventObject);
                            expressionVariables.putAll(getGlobalVariables(runtime));
                            expressionVariables.putAll(getProcessInstanceVariables(processInstance));
                        } else {
                            runtime.getKieSession().signalEvent(eventId, eventObject, processInstanceId);
                        }
                    } else { //BPMOperationType.SIGNAL_EVENT_ALL
                        runtime.getKieSession().signalEvent(eventId, eventObject);
                        if (ExchangePattern.IN_OUT.equals(exchangePattern)) {
                            expressionVariables.putAll(getGlobalVariables(runtime));
                        }
                    }
                    if (!_persistent) {
                        _runtimeManager.disposeRuntimeEngine(runtime);
                    }
                    txh.commit();
                } catch (RuntimeException re) {
                    txh.rollback();
                    throw re;
                }
                break;
            }
            case ABORT_PROCESS_INSTANCE: {
                try {
                    txh.begin();
                    KnowledgeRuntimeEngine runtime = getRuntimeEngine(exchange, inputMessage);
                    //sessionIdentifier = runtime.getSessionIdentifier();
                    processInstanceId = getProcessInstanceId(exchange, inputMessage, runtime);
                    if (processInstanceId == null) {
                        throw BPMMessages.MESSAGES.cannotAbortProcessInstance();
                    }
                    if (ExchangePattern.IN_OUT.equals(exchangePattern)) {
                        expressionVariables.putAll(getGlobalVariables(runtime));
                        ProcessInstance processInstance = runtime.getKieSession().getProcessInstance(processInstanceId);
                        expressionVariables.putAll(getProcessInstanceVariables(processInstance));
                    }
                    runtime.getKieSession().abortProcessInstance(processInstanceId);
                    if (!_persistent) {
                        _runtimeManager.disposeRuntimeEngine(runtime);
                    }
                    txh.commit();
                } catch (RuntimeException re) {
                    txh.rollback();
                    throw re;
                }
                break;
            }
            default: {
                throw BPMMessages.MESSAGES.unsupportedOperationType(operationType);
            }
        }
        if (ExchangePattern.IN_OUT.equals(exchangePattern)) {
            Message outputMessage = exchange.createMessage();
            Context outputContext = exchange.getContext(outputMessage);
            /*
            if (sessionIdentifier != null) {
                outputContext.setProperty(BPMConstants.SESSION_ID_PROPERTY, sessionIdentifier);
            }
            */
            if (processInstanceId != null) {
                outputContext.setProperty(BPMConstants.PROCESSS_INSTANCE_ID_PROPERTY, processInstanceId);
            }
            setFaults(outputMessage, operation, expressionVariables);
            if (outputMessage.getContent() != null) {
                exchange.sendFault(outputMessage);
            } else {
                setOutputs(outputMessage, operation, expressionVariables);
                exchange.send(outputMessage);
            }
        }
    }

    private KnowledgeRuntimeEngine getRuntimeEngine() {
        return (KnowledgeRuntimeEngine)_runtimeManager.getRuntimeEngine();
    }

    private KnowledgeRuntimeEngine getRuntimeEngine(Exchange exchange, Message message) throws HandlerException {
        RuntimeEngine runtimeEngine = null;
        Long processInstanceId = getProcessInstanceId(exchange, message);
        if (processInstanceId != null) {
            runtimeEngine = _runtimeManager.getRuntimeEngine(processInstanceId);
        }
        if (runtimeEngine == null) {
            CorrelationKey correlationKey = getCorrelationKey(exchange, message);
            if (correlationKey != null) {
                runtimeEngine = _runtimeManager.getRuntimeEngine(correlationKey);
            }
        }
        if (runtimeEngine == null) {
            throw new HandlerException("runtimeEngine == null");
        }
        return (KnowledgeRuntimeEngine)runtimeEngine;
    }

    private CorrelationKey getCorrelationKey(Exchange exchange, Message message) {
        String ckp = getString(exchange, message, BPMConstants.CORRELATION_KEY_PROPERTY);
        if (ckp != null) {
            List<String> properties = Strings.splitTrimToNull(ckp, " \t\n\r\f");
            if (properties.size() > 0) {
                return _correlationKeyFactory.newCorrelationKey(properties);
            }
        }
        return null;
    }

    private Long getProcessInstanceId(Exchange exchange, Message message) {
        return getLong(exchange, message, BPMConstants.PROCESSS_INSTANCE_ID_PROPERTY);
    }

    private Long getProcessInstanceId(Exchange exchange, Message message, KnowledgeRuntimeEngine session) {
        Long processInstanceId = getProcessInstanceId(exchange, message);
        if (processInstanceId == null) {
            CorrelationKey correlationKey = getCorrelationKey(exchange, message);
            if (correlationKey != null) {
                processInstanceId = getProcessInstanceId(correlationKey, session);
            }
        }
        return processInstanceId;
    }

    private Long getProcessInstanceId(CorrelationKey correlationKey, KnowledgeRuntimeEngine session) {
        if (correlationKey != null) {
            ProcessInstance processInstance = ((CorrelationAwareProcessRuntime)session.getKieSession()).getProcessInstance(correlationKey);
            if (processInstance != null) {
                return Long.valueOf(processInstance.getId());
            }
        }
        return null;
    }

    private Map<String, Object> getProcessInstanceVariables(ProcessInstance processInstance) {
        Map<String, Object> processInstanceVariables = new HashMap<String, Object>();
        if (processInstance instanceof WorkflowProcessInstanceImpl) {
            Map<String, Object> var = ((WorkflowProcessInstanceImpl)processInstance).getVariables();
            if (var != null) {
                processInstanceVariables.putAll(var);
            }
        }
        return processInstanceVariables;
    }

}
