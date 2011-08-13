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

import static org.switchyard.component.bpm.common.ProcessConstants.MESSAGE_CONTENT_VAR;
import static org.switchyard.component.bpm.common.ProcessConstants.PROCESS_ID_VAR;
import static org.switchyard.component.bpm.common.ProcessConstants.PROCESS_INSTANCE_ID_VAR;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkItemManager;
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
import org.switchyard.common.type.reflect.Construction;
import org.switchyard.component.bpm.common.ProcessActionType;
import org.switchyard.component.bpm.config.model.BpmComponentImplementationModel;
import org.switchyard.component.bpm.config.model.ProcessActionModel;
import org.switchyard.component.bpm.config.model.TaskHandlerModel;
import org.switchyard.component.bpm.exchange.BaseBpmExchangeHandler;
import org.switchyard.component.bpm.task.TaskHandler;
import org.switchyard.component.bpm.task.drools.DroolsWorkItemHandler;
import org.switchyard.metadata.ServiceOperation;

/**
 * A Drools implmentation of a BPM ExchangeHandler.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class DroolsBpmExchangeHandler extends BaseBpmExchangeHandler {

    private final ServiceDomain _serviceDomain;
    private String _processId;
    private String _messageContentName;
    private List<TaskHandler> _taskHandlers = new ArrayList<TaskHandler>();
    private Map<String,ProcessActionModel> _actions = new HashMap<String,ProcessActionModel>();
    private KnowledgeBase _kbase;
    private StatefulKnowledgeSession _ksession;

    /**
     * Constructs a new DroolsBpmExchangeHandler within the specified ServiceDomain.
     * @param serviceDomain the specified ServiceDomain
     */
    public DroolsBpmExchangeHandler(ServiceDomain serviceDomain) {
        _serviceDomain = serviceDomain;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(QName qname, BpmComponentImplementationModel model) {
        _processId = model.getProcessId();
        _messageContentName = model.getMessageContentName();
        if (_messageContentName == null) {
            _messageContentName = MESSAGE_CONTENT_VAR;
        }
        for (TaskHandlerModel tihm : model.getTaskHandlers()) {
            TaskHandler tih = Construction.construct(tihm.getClazz());
            String name = tihm.getName();
            if (name != null) {
                tih.setName(name);
            }
            tih.setMessageContentName(_messageContentName);
            tih.setServiceDomain(_serviceDomain);
            _taskHandlers.add(tih);
        }
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        addResource(model.getProcessDefinition(), kbuilder);
        for (Resource resource : model.getResources()) {
            addResource(resource, kbuilder);
        }
        _kbase = kbuilder.newKnowledgeBase();
        for (ProcessActionModel pam : model.getProcessActions()) {
            _actions.put(pam.getName(), pam);
        }
    }

    private void addResource(Resource resource, KnowledgeBuilder kbuilder) {
        if (resource != null) {
            org.drools.io.Resource kres = ResourceFactory.newUrlResource(resource.getLocationURL(getClass()));
            ResourceType resourceType = resource.getType();
            if (resourceType == null) {
                resourceType = ResourceType.valueOf("BPMN2");
            }
            org.drools.builder.ResourceType kresType = org.drools.builder.ResourceType.getResourceType(resourceType.getName());
            kbuilder.add(kres, kresType);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(ServiceReference serviceRef) {
        _ksession = _kbase.newStatefulKnowledgeSession();
        WorkItemManager wim = _ksession.getWorkItemManager();
        for (TaskHandler th : _taskHandlers) {
            wim.registerWorkItemHandler(th.getName(), new DroolsWorkItemHandler(_ksession, th));
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
        ProcessActionModel processActionModel = _actions.get(serviceOperation.getName());
        ProcessActionType processActionType = getProcessActionType(context, processActionModel);
        Message message = exchange.getMessage();
        Long processInstanceId = null;
        ProcessInstance processInstance = null;
        switch (processActionType) {
            case START_PROCESS:
                if (_processId != null) {
                    Object content = message.getContent();
                    if (content != null) {
                        Map<String,Object> parameters = new HashMap<String,Object>();
                        parameters.put(_messageContentName, content);
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
                Object processEvent = getProcessEvent(context, message);
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
            context.setProperty(PROCESS_INSTANCE_ID_VAR, processInstanceId, Scope.OUT);
            ExchangePattern exchangePattern = serviceOperation.getExchangePattern();
            if (ExchangePattern.IN_OUT.equals(exchangePattern)) {
                if (processInstance == null) {
                    processInstance = _ksession.getProcessInstance(processInstanceId.longValue());
                }
                message = exchange.createMessage();
                Object content = null;
                if (processInstance != null) {
                    content = ((WorkflowProcessInstance)processInstance).getVariable(_messageContentName);
                }
                if (content != null) {
                    message.setContent(content);
                }
                exchange.send(message);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop(ServiceReference serviceRef) {
        if (_ksession != null) {
            try {
                _ksession.halt();
            } finally {
                try {
                    _ksession.dispose();
                } finally {
                    _ksession = null;
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
        _actions.clear();
        _messageContentName = null;
        _processId = null;
    }

}
