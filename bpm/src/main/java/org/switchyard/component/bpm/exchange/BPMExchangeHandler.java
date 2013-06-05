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
package org.switchyard.component.bpm.exchange;

import static org.switchyard.component.common.knowledge.util.Actions.getInput;
import static org.switchyard.component.common.knowledge.util.Actions.getInputMap;
import static org.switchyard.component.common.knowledge.util.Actions.setGlobals;
import static org.switchyard.component.common.knowledge.util.Actions.setOutputs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import javax.xml.namespace.QName;

import org.drools.persistence.jta.JtaTransactionManager;
import org.jbpm.persistence.JpaProcessPersistenceContextManager;
import org.jbpm.persistence.processinstance.JPAProcessInstanceManagerFactory;
import org.jbpm.persistence.processinstance.JPASignalManagerFactory;
import org.jbpm.shared.services.impl.JbpmJTATransactionManager;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.KieInternalServices;
import org.kie.internal.process.CorrelationAwareProcessRuntime;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.process.CorrelationKeyFactory;
import org.switchyard.Exchange;
import org.switchyard.ExchangePattern;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.ServiceDomain;
import org.switchyard.common.lang.Strings;
import org.switchyard.component.bpm.BPMActionType;
import org.switchyard.component.bpm.BPMConstants;
import org.switchyard.component.bpm.config.model.BPMComponentImplementationModel;
import org.switchyard.component.bpm.runtime.BPMProcessEventListener;
import org.switchyard.component.bpm.runtime.BPMRuntimeManager;
import org.switchyard.component.bpm.runtime.BPMTaskService;
import org.switchyard.component.bpm.runtime.BPMTaskServiceRegistry;
import org.switchyard.component.bpm.transaction.AS7TransactionHelper;
import org.switchyard.component.bpm.util.UserGroupCallbacks;
import org.switchyard.component.bpm.util.WorkItemHandlers;
import org.switchyard.component.common.knowledge.exchange.KnowledgeAction;
import org.switchyard.component.common.knowledge.exchange.KnowledgeExchangeHandler;
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

    private static final KnowledgeAction DEFAULT_ACTION = new KnowledgeAction(BPMActionType.START_PROCESS);

    private final boolean _persistent;
    private final String _processId;
    private BPMProcessEventListener _processEventListener;
    private CorrelationKeyFactory _correlationKeyFactory;
    private EntityManagerFactory _processEntityManagerFactory;
    private EntityManagerFactory _taskEntityManagerFactory;
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
        _correlationKeyFactory = KieInternalServices.Factory.get().newCorrelationKeyFactory();
        if (_persistent) {
            _processEntityManagerFactory = Persistence.createEntityManagerFactory("org.jbpm.persistence.jpa");
            _taskEntityManagerFactory = Persistence.createEntityManagerFactory("org.jbpm.services.task");
            _taskService = BPMTaskService.Factory.newTaskService(
                    _taskEntityManagerFactory,
                    new JbpmJTATransactionManager(),
                    UserGroupCallbacks.newUserGroupCallback(getModel(), getLoader()),
                    getLoader());
            BPMTaskServiceRegistry.putTaskService(getServiceDomain().getName(), getServiceName(), _taskService);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doStop() {
        _processEventListener = null;
        _correlationKeyFactory = null;
        if (_processEntityManagerFactory != null) {
            Disposals.newDisposal(_processEntityManagerFactory).dispose();
            _processEntityManagerFactory = null;
        }
        if (_taskEntityManagerFactory != null) {
            Disposals.newDisposal(_taskEntityManagerFactory).dispose();
            _taskEntityManagerFactory = null;
        }
        
        BPMTaskServiceRegistry.removeTaskService(getServiceDomain().getName(), getServiceName());
        _taskService = null;
        super.doStop();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Properties getPropertyOverrides() {
        if (_persistent) {
            Properties props = new Properties();
            props.setProperty("drools.processInstanceManagerFactory", JPAProcessInstanceManagerFactory.class.getName());
            props.setProperty("drools.processSignalManagerFactory", JPASignalManagerFactory.class.getName());
            return props;
        } else {
            return super.getPropertyOverrides();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Map<String, Object> getEnvironmentOverrides() {
        if (_persistent) {
            UserTransaction ut = AS7TransactionHelper.getUserTransaction();
            TransactionManager tm = AS7TransactionHelper.getTransactionManager();
            Map<String, Object> env = new HashMap<String, Object>();
            env.put(EnvironmentName.ENTITY_MANAGER_FACTORY, _processEntityManagerFactory);
            env.put(EnvironmentName.TRANSACTION, ut);
            env.put(EnvironmentName.TRANSACTION_MANAGER, new JtaTransactionManager(ut, null, tm));
            env.put(EnvironmentName.PERSISTENCE_CONTEXT_MANAGER, new JpaProcessPersistenceContextManager(Environments.getEnvironment(env)));
            return env;
        }
        return super.getEnvironmentOverrides();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KnowledgeAction getDefaultAction() {
        return DEFAULT_ACTION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleAction(Exchange exchange, KnowledgeAction action) throws HandlerException {
        Message inputMessage = exchange.getMessage();
        Message outputMessage = null;
        AS7TransactionHelper utx = new AS7TransactionHelper(_persistent);
        ExchangePattern exchangePattern = exchange.getContract().getProviderOperation().getExchangePattern();
        BPMActionType actionType = (BPMActionType)action.getType();
        switch (actionType) {
            case START_PROCESS: {
                try {
                    utx.begin();
                    KnowledgeSession session = getBPMSession(exchange);
                    setGlobals(inputMessage, action, session);
                    Map<String, Object> inputMap = getInputMap(inputMessage, action);
                    ProcessInstance processInstance;
                    CorrelationKey correlationKey = getCorrelationKey(exchange);
                    if (correlationKey != null) {
                        processInstance = ((CorrelationAwareProcessRuntime)session.getStateful()).startProcess(_processId, correlationKey, inputMap);
                    } else {
                        processInstance = session.getStateful().startProcess(_processId, inputMap);
                    }
                    if (ExchangePattern.IN_OUT.equals(exchangePattern)) {
                        outputMessage = handleOutput(exchange, action, session, processInstance);
                    }
                    utx.commit();
                } catch (RuntimeException re) {
                    utx.rollback();
                    throw re;
                }
                break;
            }
            case SIGNAL_EVENT: {
                try {
                    utx.begin();
                    KnowledgeSession session = getBPMSession(exchange);
                    setGlobals(inputMessage, action, session);
                    Long processInstanceId = getProcessInstanceId(exchange, session);
                    Object eventObject = getInput(inputMessage, action);
                    String eventId = action.getEventId();
                    if (processInstanceId != null) {
                        session.getStateful().signalEvent(eventId, eventObject, processInstanceId);
                    } else {
                        // TODO: should we really be defaulting here to signaling all process instances?
                        session.getStateful().signalEvent(eventId, eventObject);
                    }
                    if (ExchangePattern.IN_OUT.equals(exchangePattern)) {
                        ProcessInstance processInstance = session.getStateful().getProcessInstance(processInstanceId);
                        outputMessage = handleOutput(exchange, action, session, processInstance);
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
                    KnowledgeSession session = getBPMSession(exchange);
                    Long processInstanceId = getProcessInstanceId(exchange, session);
                    if (ExchangePattern.IN_OUT.equals(exchangePattern)) {
                        ProcessInstance processInstance = session.getStateful().getProcessInstance(processInstanceId);
                        outputMessage = handleOutput(exchange, action, session, processInstance);
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
                throw new HandlerException("Unsupported action type: " + actionType);
            }
        }
        if (outputMessage != null) {
            exchange.send(outputMessage);
        }
    }

    private KnowledgeSession getBPMSession(Exchange exchange) {
        KnowledgeSession session;
        if (_persistent) {
            Integer sessionId = getInteger(exchange, BPMConstants.SESSION_ID_PROPERTY);
            session = getPersistentSession(sessionId);
        } else {
            session = getStatefulSession();
        }
        Listeners.registerListener(_processEventListener, session.getStateful());
        BPMRuntimeManager runtimeManager = new BPMRuntimeManager(session.getStateful(), _taskService, _processId);
        WorkItemHandlers.registerWorkItemHandlers(getModel(), getLoader(), session.getStateful(), runtimeManager, getServiceDomain());
        return session;
    }

    private Long getProcessInstanceId(Exchange exchange, KnowledgeSession session) {
        Long processInstanceId = getLong(exchange, BPMConstants.PROCESSS_INSTANCE_ID_PROPERTY);
        if (processInstanceId == null) {
            CorrelationKey correlationKey = getCorrelationKey(exchange);
            if (correlationKey != null) {
                ProcessInstance processInstance = ((CorrelationAwareProcessRuntime)session.getStateful()).getProcessInstance(correlationKey);
                if (processInstance != null) {
                    long pid = processInstance.getId();
                    if (pid > 0) {
                        processInstanceId = Long.valueOf(pid);
                    }
                }
            }
        }
        return processInstanceId;
    }

    private CorrelationKey getCorrelationKey(Exchange exchange) {
        String ckp = getString(exchange, BPMConstants.CORRELATION_KEY_PROPERTY);
        if (ckp != null) {
            List<String> properties = Strings.splitTrimToNull(ckp, " \t\n\r\f");
            if (properties.size() > 0) {
                return  _correlationKeyFactory.newCorrelationKey(properties);
            }
        }
        return null;
    }

    private Message handleOutput(Exchange exchange, KnowledgeAction action, KnowledgeSession session, ProcessInstance processInstance) {
        Message outputMessage = exchange.createMessage();
        Integer sessionId = session.getId();
        if (sessionId != null && sessionId.intValue() > 0) {
            exchange.getContext(outputMessage).setProperty(BPMConstants.SESSION_ID_PROPERTY, sessionId);
        }
        Map<String, Object> contextOverrides = new HashMap<String, Object>();
        if (processInstance != null) {
            long processInstanceId = processInstance.getId();
            if (processInstanceId > 0) {
                exchange.getContext(outputMessage).setProperty(BPMConstants.PROCESSS_INSTANCE_ID_PROPERTY, Long.valueOf(processInstanceId));
            }
            if (processInstance instanceof WorkflowProcessInstanceImpl) {
                Map<String, Object> processInstanceVariables = ((WorkflowProcessInstanceImpl)processInstance).getVariables();
                if (processInstanceVariables != null) {
                    contextOverrides.putAll(processInstanceVariables);
                }
            }
        }
        setOutputs(outputMessage, action, contextOverrides);
        return outputMessage;
    }

}
