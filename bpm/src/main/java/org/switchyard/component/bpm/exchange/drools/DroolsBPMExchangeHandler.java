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
package org.switchyard.component.bpm.exchange.drools;

import static org.switchyard.component.bpm.ProcessConstants.CONTEXT;
import static org.switchyard.component.bpm.ProcessConstants.EXCHANGE;
import static org.switchyard.component.bpm.ProcessConstants.MESSAGE;
import static org.switchyard.component.bpm.ProcessConstants.MESSAGE_CONTENT_IN;
import static org.switchyard.component.bpm.ProcessConstants.MESSAGE_CONTENT_OUT;
import static org.switchyard.component.bpm.ProcessConstants.PROCESS_ID_VAR;
import static org.switchyard.component.bpm.ProcessConstants.PROCESS_INSTANCE_ID_VAR;
import static org.switchyard.component.bpm.ProcessConstants.SESSION_ID_VAR;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.drools.KnowledgeBase;
import org.drools.agent.KnowledgeAgent;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkflowProcessInstance;
import org.jbpm.persistence.processinstance.JPAProcessInstanceManagerFactory;
import org.jbpm.persistence.processinstance.JPASignalManagerFactory;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.switchyard.Context;
import org.switchyard.Exchange;
import org.switchyard.ExchangePattern;
import org.switchyard.ExchangePhase;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.Scope;
import org.switchyard.ServiceDomain;
import org.switchyard.common.io.resource.Resource;
import org.switchyard.common.io.resource.ResourceType;
import org.switchyard.common.io.resource.SimpleResource;
import org.switchyard.common.lang.Strings;
import org.switchyard.common.type.Classes;
import org.switchyard.common.type.reflect.Construction;
import org.switchyard.component.bpm.ProcessActionType;
import org.switchyard.component.bpm.config.model.BPMComponentImplementationModel;
import org.switchyard.component.bpm.config.model.ParametersModel;
import org.switchyard.component.bpm.config.model.ProcessActionModel;
import org.switchyard.component.bpm.config.model.ResultsModel;
import org.switchyard.component.bpm.config.model.TaskHandlerModel;
import org.switchyard.component.bpm.exchange.BaseBPMExchangeHandler;
import org.switchyard.component.bpm.jta.hibernate.AS7TransactionManagerLookup;
import org.switchyard.component.bpm.task.work.TaskHandler;
import org.switchyard.component.bpm.task.work.TaskManager;
import org.switchyard.component.bpm.task.work.drools.DroolsTaskManager;
import org.switchyard.component.common.rules.config.model.AuditModel;
import org.switchyard.component.common.rules.config.model.MappingModel;
import org.switchyard.component.common.rules.expression.ContextMap;
import org.switchyard.component.common.rules.expression.Expression;
import org.switchyard.component.common.rules.expression.ExpressionFactory;
import org.switchyard.component.common.rules.util.drools.Agents;
import org.switchyard.component.common.rules.util.drools.Audits;
import org.switchyard.component.common.rules.util.drools.Bases;
import org.switchyard.component.common.rules.util.drools.ComponentImplementationConfig;
import org.switchyard.component.common.rules.util.drools.Configs;
import org.switchyard.component.common.rules.util.drools.Environments;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.metadata.ServiceOperation;

/**
 * A Drools implementation of a BPM ExchangeHandler.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class DroolsBPMExchangeHandler extends BaseBPMExchangeHandler {

    private static final Logger LOGGER = Logger.getLogger(DroolsBPMExchangeHandler.class);

    private static final String IGNORE_VARIABLE_PREFIX = "ignore-variable-";
    private static final AtomicInteger IGNORE_VARIABLE_COUNT = new AtomicInteger();

    private final Lock _stateLock = new ReentrantLock();
    private final Lock _processLock = new ReentrantLock();
    private final ServiceDomain _serviceDomain;
    private ClassLoader _loader;
    private String _processId;
    private boolean _persistent;
    private Integer _sessionId;
    private EntityManagerFactory _entityManagerFactory;
    private String _messageContentInName;
    private String _messageContentOutName;
    private KnowledgeAgent _kagent;
    private AuditModel _audit;
    private String _targetNamespace;
    private List<TaskHandlerModel> _taskHandlerModels = new ArrayList<TaskHandlerModel>();
    private List<TaskHandler> _taskHandlers = new ArrayList<TaskHandler>();
    private Map<String,ProcessActionModel> _actionModels = new HashMap<String,ProcessActionModel>();
    private Map<String, Scope> _parameterContextScopes = new HashMap<String, Scope>();
    private Map<String, Expression> _parameterExpressions = new HashMap<String, Expression>();
    private Map<String, Scope> _resultContextScopes = new HashMap<String, Scope>();
    private Map<String, Expression> _resultExpressions = new HashMap<String, Expression>();
    private KnowledgeBase _kbase;
    private KnowledgeSessionConfiguration _ksessionConfig;
    private Environment _environment;
    private StatefulKnowledgeSession _ksession;
    private KnowledgeRuntimeLogger _klogger;

    /**
     * Constructs a new DroolsBPMExchangeHandler within the specified ServiceDomain.
     * @param serviceDomain the specified ServiceDomain
     */
    public DroolsBPMExchangeHandler(ServiceDomain serviceDomain) {
        _serviceDomain = serviceDomain;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(QName qname, BPMComponentImplementationModel model) {
        _loader = Classes.getClassLoader(getClass());
        _processId = model.getProcessId();
        _persistent = model.isPersistent();
        _sessionId = model.getSessionId();
        _messageContentInName = model.getMessageContentInName();
        if (_messageContentInName == null) {
            _messageContentInName = MESSAGE_CONTENT_IN;
        }
        _messageContentOutName = model.getMessageContentOutName();
        if (_messageContentOutName == null) {
            _messageContentOutName = MESSAGE_CONTENT_OUT;
        }
        ComponentModel cm = model.getComponent();
        _targetNamespace = cm != null ? cm.getTargetNamespace() : null;
        _taskHandlerModels.addAll(model.getTaskHandlers());
        ResourceType.install(_loader);
        ComponentImplementationConfig cic = new ComponentImplementationConfig(model, _loader);
        Map<String, Object> env = new HashMap<String, Object>();
        Properties props = new Properties();
        if (_persistent) {
            _entityManagerFactory = Persistence.createEntityManagerFactory("org.jbpm.persistence.jpa");
            env.put(EnvironmentName.ENTITY_MANAGER_FACTORY, _entityManagerFactory);
            env.put(EnvironmentName.TRANSACTION_MANAGER, AS7TransactionManagerLookup.getTransactionManager());
            env.put(EnvironmentName.TRANSACTION, AS7TransactionManagerLookup.getUserTransaction());
            props.setProperty("drools.processInstanceManagerFactory", JPAProcessInstanceManagerFactory.class.getName());
            props.setProperty("drools.processSignalManagerFactory", JPASignalManagerFactory.class.getName());
        }
        cic.setEnvironmentOverrides(env);
        cic.setPropertiesOverrides(props);
        Resource procDef = model.getProcessDefinition();
        if (procDef.getType() == null) {
            procDef = new SimpleResource(procDef.getLocation(), "BPMN2");
        }
        if (model.isAgent()) {
            _kagent = Agents.newAgent(cic, procDef);
            _kbase = _kagent.getKnowledgeBase();
        } else {
            _kbase = Bases.newBase(cic, procDef);
        }
        _ksessionConfig = Configs.getSessionConfiguration(cic);
        _environment = Environments.getEnvironment(cic);
        _audit = model.getAudit();
        for (ProcessActionModel pam : model.getProcessActions()) {
            _actionModels.put(pam.getName(), pam);
        }
        ExpressionFactory factory = ExpressionFactory.instance();
        ParametersModel parameters = model.getParameters();
        if (parameters != null) {
            
            for (MappingModel mapping : parameters.getMappings()) {
                _parameterContextScopes.put(mapping.getVariable(), mapping.getContextScope());
                _parameterExpressions.put(mapping.getVariable(), factory.create(mapping));
            }
        }
        ResultsModel results = model.getResults();
        if (results != null) {
            for (MappingModel mapping : results.getMappings()) {
                String var = Strings.trimToNull(mapping.getVariable());
                if (var == null) {
                    var = IGNORE_VARIABLE_PREFIX + IGNORE_VARIABLE_COUNT.incrementAndGet();
                }
                _resultContextScopes.put(var, mapping.getContextScope());
                _resultExpressions.put(var, factory.create(mapping));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        // nothing necessary
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleMessage(Exchange exchange) throws HandlerException {
        if (!ExchangePhase.IN.equals(exchange.getPhase())) {
            return;
        }
        UserTransaction userTx = null;
        Context context = exchange.getContext();
        ServiceOperation serviceOperation = exchange.getContract().getServiceOperation();
        ProcessActionModel processActionModel = _actionModels.get(serviceOperation.getName());
        ProcessActionType processActionType = getProcessActionType(context, processActionModel);
        Message messageIn = exchange.getMessage();
        Integer sessionId = getSessionId(context, _sessionId);
        Long processInstanceId = null;
        ProcessInstance processInstance = null;
        switch (processActionType) {
            case START_PROCESS:
                if (_processId != null) {
                    _processLock.lock();
                    try {
                        userTx = beginUserTransaction();
                        final StatefulKnowledgeSession ksessionStateful = getStatefulSession(sessionId);
                        sessionId = Integer.valueOf(ksessionStateful.getId());
                        Map<String,Object> parameters = new HashMap<String,Object>();
                        Map<String, Object> vars = new HashMap<String, Object>();
                        Object messageContentIn = messageIn.getContent();
                        if (messageContentIn != null) {
                            parameters.put(_messageContentInName, messageContentIn);
                            vars.put(_messageContentInName, messageContentIn);
                        }
                        vars.put(EXCHANGE, exchange);
                        vars.put(MESSAGE, messageIn);
                        for (Entry<String, Expression> pe : _parameterExpressions.entrySet()) {
                            vars.put(CONTEXT, new ContextMap(context, _parameterContextScopes.get(pe.getKey())));
                            Object parameter = pe.getValue().evaluate(vars);
                            parameters.put(pe.getKey(), parameter);
                        }
                        processInstance = ksessionStateful.startProcess(_processId, parameters);
                        processInstanceId = Long.valueOf(processInstance.getId());
                        commitUserTransaction(userTx);
                    } catch (RuntimeException re) {
                        rollbackUserTransaction(userTx);
                        throw re;
                    } finally {
                        _processLock.unlock();
                    }
                } else {
                    throwNullParameterException(processActionType, PROCESS_ID_VAR);
                }
                break;
            case SIGNAL_EVENT:
                String processEventType = getProcessEventType(context, processActionModel);
                Object processEvent = getProcessEvent(context, messageIn);
                processInstanceId = getProcessInstanceId(context);
                if (processInstanceId != null) {
                    _processLock.lock();
                    try {
                        userTx = beginUserTransaction();
                        final StatefulKnowledgeSession ksessionStateful = getStatefulSession(sessionId);
                        sessionId = Integer.valueOf(ksessionStateful.getId());
                        ksessionStateful.signalEvent(processEventType, processEvent, processInstanceId.longValue());
                        commitUserTransaction(userTx);
                    } catch (RuntimeException re) {
                        rollbackUserTransaction(userTx);
                        throw re;
                    } finally {
                        _processLock.unlock();
                    }
                } else {
                    throwNullParameterException(processActionType, PROCESS_INSTANCE_ID_VAR);
                }
                break;
            case ABORT_PROCESS_INSTANCE:
                processInstanceId = getProcessInstanceId(context);
                if (processInstanceId != null) {
                    _processLock.lock();
                    try {
                        userTx = beginUserTransaction();
                        final StatefulKnowledgeSession ksessionStateful = getStatefulSession(sessionId);
                        sessionId = Integer.valueOf(ksessionStateful.getId());
                        ksessionStateful.abortProcessInstance(processInstanceId.longValue());
                        commitUserTransaction(userTx);
                    } catch (RuntimeException re) {
                        rollbackUserTransaction(userTx);
                        throw re;
                    } finally {
                        _processLock.unlock();
                    }
                } else {
                    throwNullParameterException(processActionType, PROCESS_INSTANCE_ID_VAR);
                }
                break;
        }
        if (processInstanceId != null) {
            context.setProperty(PROCESS_INSTANCE_ID_VAR, processInstanceId, Scope.EXCHANGE);
            if (_persistent) {
                context.setProperty(SESSION_ID_VAR, sessionId, Scope.EXCHANGE);
            }
            ExchangePattern exchangePattern = serviceOperation.getExchangePattern();
            if (ExchangePattern.IN_OUT.equals(exchangePattern)) {
                Message messageOut = exchange.createMessage();
                Object messageContentOut = null;
                _processLock.lock();
                try {
                    userTx = beginUserTransaction();
                    if (processInstance == null) {
                        final StatefulKnowledgeSession ksessionStateful = getStatefulSession(sessionId);
                        processInstance = ksessionStateful.getProcessInstance(processInstanceId.longValue());
                    }
                    if (processInstance != null) {
                        messageContentOut = ((WorkflowProcessInstance)processInstance).getVariable(_messageContentOutName);
                    }
                    commitUserTransaction(userTx);
                } catch (RuntimeException re) {
                    rollbackUserTransaction(userTx);
                    throw re;
                } finally {
                    _processLock.unlock();
                }
                if (messageContentOut != null) {
                    messageOut.setContent(messageContentOut);
                }
                Map<String, Object> vars = new HashMap<String, Object>();
                if (processInstance instanceof WorkflowProcessInstanceImpl) {
                    Map<String, Object> piVars = ((WorkflowProcessInstanceImpl)processInstance).getVariables();
                    if (piVars != null) {
                        vars.putAll(piVars);
                    }
                }
                vars.put(EXCHANGE, exchange);
                vars.put(MESSAGE, messageOut);
                for (Entry<String, Expression> re : _resultExpressions.entrySet()) {
                    vars.put(CONTEXT, new ContextMap(context, _resultContextScopes.get(re.getKey())));
                    Object result = re.getValue().evaluate(vars);
                    if (!re.getKey().startsWith(IGNORE_VARIABLE_PREFIX)) {
                        context.setProperty(re.getKey(), result, Scope.EXCHANGE);
                    }
                }
                exchange.send(messageOut);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        disposeStatefulSession(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        _kbase = null;
        _parameterContextScopes.clear();
        _parameterExpressions.clear();
        _resultContextScopes.clear();
        _resultExpressions.clear();
        _taskHandlers.clear();
        _taskHandlerModels.clear();
        _actionModels.clear();
        _messageContentInName = null;
        _messageContentOutName = null;
        if (_entityManagerFactory != null) {
            try {
                if (_entityManagerFactory.isOpen()) {
                    _entityManagerFactory.close();
                }
            } catch (Throwable t) {
                LOGGER.error("Problem closing EntityManagerFactory", t);
            } finally {
                _entityManagerFactory = null;
            }
        }
        _processId = null;
        _persistent = false;
        _sessionId = null;
        if (_kagent != null) {
            try {
                _kagent.dispose();
            } catch (Throwable t) {
                LOGGER.error("Problem disposing KnowledgeAgent", t);
            } finally {
                _kagent = null;
            }
        }
    }

    private StatefulKnowledgeSession getStatefulSession(Integer sessionId) {
        _stateLock.lock();
        try {
            if (_ksession != null && sessionId != null) {
                if (_ksession.getId() != sessionId.intValue()) {
                    LOGGER.info("stateful knowledge session with id: " + _ksession.getId() + " does not match requested session id: " + sessionId + " (will dispose and load)");
                    disposeStatefulSession(false);
                }
            }
            if (_ksession == null) {
                if (_persistent) {
                    if (sessionId != null) {
                        _ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(sessionId.intValue(), _kbase, _ksessionConfig, _environment);
                    } else {
                        _ksession = JPAKnowledgeService.newStatefulKnowledgeSession(_kbase, _ksessionConfig, _environment);
                    }
                } else {
                    _ksession = _kbase.newStatefulKnowledgeSession(_ksessionConfig, _environment);
                }
                _klogger = Audits.getLogger(_audit, _ksession);
                TaskManager tm = new DroolsTaskManager(_ksession);
                for (TaskHandlerModel thm : _taskHandlerModels) {
                    TaskHandler th = Construction.construct(thm.getClazz());
                    String name = thm.getName();
                    if (name != null) {
                        th.setName(name);
                    }
                    th.setMessageContentInName(_messageContentInName);
                    th.setMessageContentOutName(_messageContentOutName);
                    th.setTargetNamespace(_targetNamespace);
                    th.setServiceDomain(_serviceDomain);
                    th.setLoader(_loader);
                    tm.registerHandler(th);
                    th.init();
                    _taskHandlers.add(th);
                }
            }
            return _ksession;
        } finally {
            _stateLock.unlock();
        }
    }

    private void disposeStatefulSession(boolean useStateLock) {
        if (useStateLock) {
            _stateLock.lock();
        }
        try {
            for (TaskHandler th : _taskHandlers) {
                try {
                    th.destroy();
                } catch (Throwable t) {
                    LOGGER.error("problem destroying TaskHandler: " + th.getName(), t);
                }
            }
            if (_ksession != null) {
                try {
                    _ksession.halt();
                } finally {
                    try {
                        _ksession.dispose();
                    } finally {
                        _ksession = null;
                        if (_klogger != null) {
                            try {
                                _klogger.close();
                            } finally {
                                _klogger = null;
                            }
                        }
                    }
                }
            }
        } finally {
            if (useStateLock) {
                _stateLock.unlock();
            }
        }
    }

    private UserTransaction beginUserTransaction() throws HandlerException {
        UserTransaction userTx = null;
        if (_persistent) {
            try {
                userTx = AS7TransactionManagerLookup.getUserTransaction();
                userTx.begin();
            } catch (SystemException se) {
                throw new HandlerException("UserTransaction begin failed", se);
            } catch (NotSupportedException nse) {
                throw new HandlerException("UserTransaction begin failed", nse);
            }
        }
        return userTx;
    }

    private void commitUserTransaction(UserTransaction userTx) throws HandlerException {
        if (userTx != null) {
            try {
                userTx.commit();
            } catch (SystemException se) {
                throw new HandlerException("UserTransaction commit failed", se);
            } catch (HeuristicRollbackException hre) {
                throw new HandlerException("UserTransaction commit failed", hre);
            } catch (HeuristicMixedException hme) {
                throw new HandlerException("UserTransaction commit failed", hme);
            } catch (RollbackException re) {
                throw new HandlerException("UserTransaction commit failed", re);
            }
        }
    }

    private void rollbackUserTransaction(UserTransaction userTx) throws HandlerException {
        if (userTx != null) {
            try {
                userTx.rollback();
            } catch (SystemException se) {
                throw new HandlerException("UserTransaction rollback failed", se);
            }
        }
    }

}
