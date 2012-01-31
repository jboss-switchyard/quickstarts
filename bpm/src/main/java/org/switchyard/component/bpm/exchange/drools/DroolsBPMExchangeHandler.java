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

import static org.switchyard.component.bpm.ProcessConstants.MESSAGE_CONTENT_IN;
import static org.switchyard.component.bpm.ProcessConstants.MESSAGE_CONTENT_OUT;
import static org.switchyard.component.bpm.ProcessConstants.PROCESS_ID_VAR;
import static org.switchyard.component.bpm.ProcessConstants.PROCESS_INSTANCE_ID_VAR;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.drools.KnowledgeBase;
import org.drools.agent.KnowledgeAgent;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.runtime.Environment;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkflowProcessInstance;
import org.switchyard.Context;
import org.switchyard.Exchange;
import org.switchyard.ExchangePattern;
import org.switchyard.ExchangePhase;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.Scope;
import org.switchyard.ServiceDomain;
import org.switchyard.ServiceReference;
import org.switchyard.common.io.resource.Resource;
import org.switchyard.common.io.resource.ResourceType;
import org.switchyard.common.io.resource.SimpleResource;
import org.switchyard.common.type.Classes;
import org.switchyard.common.type.reflect.Construction;
import org.switchyard.component.bpm.ProcessActionType;
import org.switchyard.component.bpm.config.model.BPMComponentImplementationModel;
import org.switchyard.component.bpm.config.model.ProcessActionModel;
import org.switchyard.component.bpm.config.model.TaskHandlerModel;
import org.switchyard.component.bpm.exchange.BaseBPMExchangeHandler;
import org.switchyard.component.bpm.task.work.TaskHandler;
import org.switchyard.component.bpm.task.work.TaskManager;
import org.switchyard.component.bpm.task.work.drools.DroolsTaskManager;
import org.switchyard.component.common.rules.config.model.AuditModel;
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

    private final ServiceDomain _serviceDomain;
    private String _processId;
    private String _messageContentInName;
    private String _messageContentOutName;
    private KnowledgeAgent _kagent;
    private AuditModel _audit;
    private String _targetNamespace;
    private List<TaskHandlerModel> _taskHandlerModels = new ArrayList<TaskHandlerModel>();
    private List<TaskHandler> _taskHandlers = new ArrayList<TaskHandler>();
    private Map<String,ProcessActionModel> _actionModels = new HashMap<String,ProcessActionModel>();
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
        _processId = model.getProcessId();
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
        ClassLoader loader = Classes.getClassLoader(getClass());
        ResourceType.install(loader);
        ComponentImplementationConfig cic = new ComponentImplementationConfig(model, loader);
        Map<String, Object> env = new HashMap<String, Object>();
        //env.put(EnvironmentName.ENTITY_MANAGER_FACTORY, Persistence.createEntityManagerFactory("org.jbpm.persistence.jpa"));
        //env.put(EnvironmentName.TRANSACTION_MANAGER, AS7TransactionManagerLookup.getTransactionManager());
        cic.setEnvironmentOverrides(env);
        Properties props = new Properties();
        //props.setProperty("drools.processInstanceManagerFactory", JPAProcessInstanceManagerFactory.class.getName());
        //props.setProperty("drools.processSignalManagerFactory", JPASignalManagerFactory.class.getName());
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
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(ServiceReference serviceRef) {
        _ksession = _kbase.newStatefulKnowledgeSession(_ksessionConfig, _environment);
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
            tm.registerHandler(th);
            th.init();
            _taskHandlers.add(th);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleMessage(Exchange exchange) throws HandlerException {
        if (!ExchangePhase.IN.equals(exchange.getPhase())) {
            return;
        }
        Context context = exchange.getContext();
        ServiceOperation serviceOperation = exchange.getContract().getServiceOperation();
        ProcessActionModel processActionModel = _actionModels.get(serviceOperation.getName());
        ProcessActionType processActionType = getProcessActionType(context, processActionModel);
        Message messageIn = exchange.getMessage();
        Long processInstanceId = null;
        ProcessInstance processInstance = null;
        switch (processActionType) {
            case START_PROCESS:
                if (_processId != null) {
                    Object messageContentIn = messageIn.getContent();
                    if (messageContentIn != null) {
                        Map<String,Object> parameters = new HashMap<String,Object>();
                        parameters.put(_messageContentInName, messageContentIn);
                        processInstance = _ksession.startProcess(_processId, parameters);
                    } else {
                        processInstance = _ksession.startProcess(_processId);
                    }
                    processInstanceId = Long.valueOf(processInstance.getId());
                } else {
                    throwNullParameterException(processActionType, PROCESS_ID_VAR);
                }
                break;
            case SIGNAL_EVENT:
                String processEventType = getProcessEventType(context, processActionModel);
                Object processEvent = getProcessEvent(context, messageIn);
                processInstanceId = getProcessInstanceId(context);
                if (processInstanceId != null) {
                    _ksession.signalEvent(processEventType, processEvent, processInstanceId.longValue());
                } else {
                    throwNullParameterException(processActionType, PROCESS_INSTANCE_ID_VAR);
                }
                break;
            case ABORT_PROCESS_INSTANCE:
                processInstanceId = getProcessInstanceId(context);
                if (processInstanceId != null) {
                    _ksession.abortProcessInstance(processInstanceId.longValue());
                } else {
                    throwNullParameterException(processActionType, PROCESS_INSTANCE_ID_VAR);
                }
                break;
        }
        if (processInstanceId != null) {
            context.setProperty(PROCESS_INSTANCE_ID_VAR, processInstanceId, Scope.EXCHANGE);
            ExchangePattern exchangePattern = serviceOperation.getExchangePattern();
            if (ExchangePattern.IN_OUT.equals(exchangePattern)) {
                if (processInstance == null) {
                    processInstance = _ksession.getProcessInstance(processInstanceId.longValue());
                }
                Message messageOut = exchange.createMessage();
                Object messageContentOut = null;
                if (processInstance != null) {
                    messageContentOut = ((WorkflowProcessInstance)processInstance).getVariable(_messageContentOutName);
                }
                if (messageContentOut != null) {
                    messageOut.setContent(messageContentOut);
                }
                exchange.send(messageOut);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop(ServiceReference serviceRef) {
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
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy(ServiceReference serviceRef) {
        _kbase = null;
        _taskHandlers.clear();
        _taskHandlerModels.clear();
        _actionModels.clear();
        _messageContentInName = null;
        _messageContentOutName = null;
        _processId = null;
        if (_kagent != null) {
            try {
                _kagent.dispose();
            } finally {
                _kagent = null;
            }
        }
    }

}
