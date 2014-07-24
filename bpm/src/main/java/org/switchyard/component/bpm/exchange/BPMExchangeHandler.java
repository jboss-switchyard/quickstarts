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

import static org.switchyard.component.common.knowledge.util.Operations.getInput;
import static org.switchyard.component.common.knowledge.util.Operations.getInputMap;
import static org.switchyard.component.common.knowledge.util.Operations.setFaults;
import static org.switchyard.component.common.knowledge.util.Operations.setGlobals;
import static org.switchyard.component.common.knowledge.util.Operations.setOutputs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import javax.xml.namespace.QName;

import org.drools.persistence.jta.JtaTransactionManager;
import org.jbpm.persistence.JpaProcessPersistenceContextManager;
import org.jbpm.persistence.processinstance.JPAProcessInstanceManagerFactory;
import org.jbpm.persistence.processinstance.JPASignalManagerFactory;
import org.jbpm.runtime.manager.impl.AbstractRuntimeManager;
import org.jbpm.runtime.manager.impl.RuntimeEngineImpl;
import org.jbpm.services.task.persistence.JPATaskPersistenceContextManager;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;
// SWITCHYARD-1755: internal api usage still required (public APIs insufficient)
import org.kie.internal.KieInternalServices;
import org.kie.internal.process.CorrelationAwareProcessRuntime;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.process.CorrelationKeyFactory;
import org.kie.internal.runtime.manager.RuntimeEnvironment;
import org.kie.internal.runtime.manager.RuntimeManagerRegistry;
import org.kie.internal.task.api.UserGroupCallback;
import org.switchyard.Context;
import org.switchyard.Exchange;
import org.switchyard.ExchangePattern;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.ServiceDomain;
import org.switchyard.common.lang.Strings;
import org.switchyard.component.bpm.BPMConstants;
import org.switchyard.component.bpm.BPMMessages;
import org.switchyard.component.bpm.BPMOperationType;
import org.switchyard.component.bpm.config.model.BPMComponentImplementationModel;
import org.switchyard.component.bpm.runtime.BPMProcessEventListener;
import org.switchyard.component.bpm.runtime.BPMRuntimeEnvironment;
import org.switchyard.component.bpm.runtime.BPMTaskService;
import org.switchyard.component.bpm.runtime.BPMTaskServiceRegistry;
import org.switchyard.component.bpm.transaction.AS7TransactionHelper;
import org.switchyard.component.bpm.transaction.TransactionManagerLocator;
import org.switchyard.component.bpm.util.EntityManagerFactoryLoader;
import org.switchyard.component.bpm.util.OsgiEntityManagerFactoryLoader;
import org.switchyard.component.bpm.util.UserGroupCallbacks;
import org.switchyard.component.bpm.util.WorkItemHandlers;
import org.switchyard.component.common.knowledge.exchange.KnowledgeExchangeHandler;
import org.switchyard.component.common.knowledge.exchange.KnowledgeOperation;
import org.switchyard.component.common.knowledge.session.KnowledgeDisposal;
import org.switchyard.component.common.knowledge.session.KnowledgeSession;
import org.switchyard.component.common.knowledge.util.Disposals;
import org.switchyard.component.common.knowledge.util.Environments;
import org.switchyard.component.common.knowledge.util.Listeners;

/**
 * A "bpm" implementation of a KnowledgeExchangeHandler.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class BPMExchangeHandler extends KnowledgeExchangeHandler<BPMComponentImplementationModel> {

    private static final KnowledgeOperation DEFAULT_OPERATION = new KnowledgeOperation(BPMOperationType.START_PROCESS);

    private static final EntityManagerFactoryLoader EMF_LOADER;
    private final boolean _persistent;
    private final String _processId;
    private BPMProcessEventListener _processEventListener;
    private UserGroupCallback _userGroupCallback;
    private CorrelationKeyFactory _correlationKeyFactory;
    private EntityManagerFactory _entityManagerFactory;
    private BPMTaskService _taskService;

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
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doStart() {
        super.doStart();
        _processEventListener = new BPMProcessEventListener(getServiceDomain().getEventPublisher());
        _userGroupCallback = UserGroupCallbacks.newUserGroupCallback(getModel(), getLoader());
        _correlationKeyFactory = KieInternalServices.Factory.get().newCorrelationKeyFactory();
        if (_persistent) {
            // XXX: this is contributed by the application
            _entityManagerFactory = EMF_LOADER.getEntityManagerFactory(getServiceDomain());
        }
        _taskService = BPMTaskService.Factory.newTaskService(Environments.getEnvironment(getEnvironmentOverrides()), _entityManagerFactory, _userGroupCallback, getLoader());
        BPMTaskServiceRegistry.putTaskService(getServiceDomain().getName(), getServiceName(), _taskService);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doStop() {
        super.doStop();
        _processEventListener = null;
        _userGroupCallback = null;
        _correlationKeyFactory = null;
        if (_entityManagerFactory != null) {
            Disposals.newDisposal(_entityManagerFactory).dispose();
            _entityManagerFactory = null;
        }
        _taskService = null;
        BPMTaskServiceRegistry.removeTaskService(getServiceDomain().getName(), getServiceName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Properties getPropertyOverrides() {
        Properties props = super.getPropertyOverrides();
        if (_persistent) {
            props.setProperty("drools.processInstanceManagerFactory", JPAProcessInstanceManagerFactory.class.getName());
            props.setProperty("drools.processSignalManagerFactory", JPASignalManagerFactory.class.getName());
        }
        return props;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Map<String, Object> getEnvironmentOverrides() {
        Map<String, Object> env = super.getEnvironmentOverrides();
        if (_persistent) {
            UserTransaction ut = TransactionManagerLocator.INSTANCE.getUserTransaction();
            TransactionManager tm = TransactionManagerLocator.INSTANCE.getTransactionManager();
            env.put(EnvironmentName.ENTITY_MANAGER_FACTORY, _entityManagerFactory);
            env.put(EnvironmentName.TRANSACTION, ut);
            env.put(EnvironmentName.TRANSACTION_MANAGER, new JtaTransactionManager(ut, null, tm));
            env.put(EnvironmentName.PERSISTENCE_CONTEXT_MANAGER, new JpaProcessPersistenceContextManager(Environments.getEnvironment(env)));
            env.put(EnvironmentName.TASK_PERSISTENCE_CONTEXT_MANAGER, new JPATaskPersistenceContextManager(Environments.getEnvironment(env)));
        }
        return env;
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
        Integer sessionId = null;
        Long processInstanceId = null;
        Message inputMessage = exchange.getMessage();
        ExchangePattern exchangePattern = exchange.getContract().getProviderOperation().getExchangePattern();
        Map<String, Object> expressionVariables = new HashMap<String, Object>();
        AS7TransactionHelper utx = new AS7TransactionHelper(_persistent);
        BPMOperationType operationType = (BPMOperationType)operation.getType();
        switch (operationType) {
            case START_PROCESS: {
                try {
                    utx.begin();
                    KnowledgeSession session = getBPMSession(exchange, inputMessage);
                    sessionId = session.getId();
                    setGlobals(inputMessage, operation, session);
                    Map<String, Object> inputMap = getInputMap(inputMessage, operation, session);
                    ProcessInstance processInstance;
                    CorrelationKey correlationKey = getCorrelationKey(exchange, inputMessage);
                    if (correlationKey != null) {
                        processInstance = ((CorrelationAwareProcessRuntime)session.getStateful()).startProcess(_processId, correlationKey, inputMap);
                    } else {
                        processInstance = session.getStateful().startProcess(_processId, inputMap);
                    }
                    processInstanceId = Long.valueOf(processInstance.getId());
                    if (ExchangePattern.IN_OUT.equals(exchangePattern)) {
                        expressionVariables.putAll(getGlobalVariables(session));
                        expressionVariables.putAll(getProcessInstanceVariables(processInstance));
                    }
                    utx.commit();
                } catch (RuntimeException re) {
                    utx.rollback();
                    throw re;
                }
                break;
            }
            case SIGNAL_EVENT:
            case SIGNAL_EVENT_ALL: {
                try {
                    utx.begin();
                    KnowledgeSession session = getBPMSession(exchange, inputMessage);
                    sessionId = session.getId();
                    setGlobals(inputMessage, operation, session);
                    Object eventObject = getInput(inputMessage, operation, session);
                    String eventId = operation.getEventId();
                    if (BPMOperationType.SIGNAL_EVENT.equals(operationType)) {
                        processInstanceId = getProcessInstanceId(exchange, inputMessage, session);
                        if (processInstanceId == null) {
                            throw BPMMessages.MESSAGES.cannotSignalEventUnknownProcessInstanceIdOrUnknownunmatchedCorrelationKey();
                        }
                        if (ExchangePattern.IN_OUT.equals(exchangePattern)) {
                            ProcessInstance processInstance = session.getStateful().getProcessInstance(processInstanceId);
                            processInstance.signalEvent(eventId, eventObject);
                            expressionVariables.putAll(getGlobalVariables(session));
                            expressionVariables.putAll(getProcessInstanceVariables(processInstance));
                        } else {
                            session.getStateful().signalEvent(eventId, eventObject, processInstanceId);
                        }
                    } else if (BPMOperationType.SIGNAL_EVENT_ALL.equals(operationType)) {
                        session.getStateful().signalEvent(eventId, eventObject);
                        if (ExchangePattern.IN_OUT.equals(exchangePattern)) {
                            expressionVariables.putAll(getGlobalVariables(session));
                        }
                    }
                    utx.commit();
                } catch (RuntimeException re) {
                    utx.rollback();
                    throw re;
                }
                break;
            }
            case ABORT_PROCESS_INSTANCE: {
                try {
                    utx.begin();
                    KnowledgeSession session = getBPMSession(exchange, inputMessage);
                    sessionId = session.getId();
                    processInstanceId = getProcessInstanceId(exchange, inputMessage, session);
                    if (processInstanceId == null) {
                        throw BPMMessages.MESSAGES.cannotAbortProcessInstance();
                    }
                    if (ExchangePattern.IN_OUT.equals(exchangePattern)) {
                        expressionVariables.putAll(getGlobalVariables(session));
                        ProcessInstance processInstance = session.getStateful().getProcessInstance(processInstanceId);
                        expressionVariables.putAll(getProcessInstanceVariables(processInstance));
                    }
                    session.getStateful().abortProcessInstance(processInstanceId);
                    utx.commit();
                } catch (RuntimeException re) {
                    utx.rollback();
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
            if (sessionId != null && sessionId.intValue() > 0) {
                outputContext.setProperty(BPMConstants.SESSION_ID_PROPERTY, sessionId);
            }
            if (processInstanceId != null && processInstanceId.longValue() > 0) {
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

    private KnowledgeSession getBPMSession(Exchange exchange, Message message) {
        KnowledgeSession session;
        if (_persistent) {
            session = getPersistentSession(getSessionId(exchange, message));
        } else {
            session = getStatefulSession();
        }
        if (!session.isRemote()) {
            // none of the below is supported by the kie remote api
            Listeners.registerListener(_processEventListener, session.getStateful());
            // TODO: the use of BPMRuntimeEnvironment should be removed after SWITCHYARD-1584,
            // and the RuntimeEnvironmentBuilderFactory and RuntimeManagerFactory should be used
            final KieSession kieSession = session.getStateful();
            final String deploymentId = getDeploymentId();
            final RuntimeManagerRegistry runtimeManagerRegistry = RuntimeManagerRegistry.get();
            if (runtimeManagerRegistry.isRegistered(deploymentId)) {
                runtimeManagerRegistry.remove(deploymentId);
            }
            RuntimeEnvironment runtimeEnvironment = new BPMRuntimeEnvironment(session.getStateful(), _entityManagerFactory, _userGroupCallback, getLoader());
            AbstractRuntimeManager runtimeManager = new AbstractRuntimeManager(runtimeEnvironment, deploymentId) {
                private RuntimeEngineImpl _runtimeEngine = null;
                @Override
                public void init() {
                    _runtimeEngine = new RuntimeEngineImpl(kieSession, _taskService) {
                        @Override
                        public void dispose() {
                            // no-op
                        }
                    };
                    _runtimeEngine.setManager(this);
                }
                @Override
                public void validate(KieSession ksession, org.kie.api.runtime.manager.Context<?> context) throws IllegalStateException {
                    // no-op
                }
                @Override
                public RuntimeEngine getRuntimeEngine(org.kie.api.runtime.manager.Context<?> context) {
                    return _runtimeEngine;
                }
                @Override
                public void disposeRuntimeEngine(RuntimeEngine runtime) {
                    // no-op
                }
                @Override
                public void close() {
                    runtimeManagerRegistry.remove(deploymentId);
                }
            };
            runtimeManager.init();
            if (!runtimeManagerRegistry.isRegistered(deploymentId)) {
                runtimeManagerRegistry.register(runtimeManager);
                session.addDisposals(new KnowledgeDisposal() {
                    @Override
                    public void dispose() {
                        runtimeManagerRegistry.remove(deploymentId);
                    }
                });
            }
            WorkItemHandlers.registerWorkItemHandlers(getModel(), getLoader(), session.getStateful(), runtimeManager, getServiceDomain());
        }
        return session;
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

    private Integer getSessionId(Exchange exchange, Message message) {
        return getInteger(exchange, message, BPMConstants.SESSION_ID_PROPERTY);
    }

    private Long getProcessInstanceId(Exchange exchange, Message message) {
        return getLong(exchange, message, BPMConstants.PROCESSS_INSTANCE_ID_PROPERTY);
    }

    private Long getProcessInstanceId(Exchange exchange, Message message, KnowledgeSession session) {
        Long processInstanceId = getProcessInstanceId(exchange, message);
        if (processInstanceId == null) {
            CorrelationKey correlationKey = getCorrelationKey(exchange, message);
            if (correlationKey != null) {
                processInstanceId = getProcessInstanceId(correlationKey, session);
            }
        }
        return processInstanceId;
    }

    private Long getProcessInstanceId(CorrelationKey correlationKey, KnowledgeSession session) {
        if (correlationKey != null) {
            ProcessInstance processInstance = ((CorrelationAwareProcessRuntime)session.getStateful()).getProcessInstance(correlationKey);
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

    static {
        // hacky way to integrate with OSGi.
        EntityManagerFactoryLoader loader = new EntityManagerFactoryLoader();
        try {
            loader = new OsgiEntityManagerFactoryLoader();
        } catch (Exception e) {
            // above fails if osgi types are not available.
            e.fillInStackTrace();
        }
        EMF_LOADER = loader;
    }
}
