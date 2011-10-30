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
package org.switchyard.component.rules.exchange.drools;

import static org.switchyard.component.rules.common.RulesConstants.MESSAGE;
import static org.switchyard.component.rules.common.RulesConstants.MESSAGE_CONTENT;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.drools.KnowledgeBase;
import org.drools.agent.KnowledgeAgent;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.StatelessKnowledgeSession;
import org.switchyard.Context;
import org.switchyard.Exchange;
import org.switchyard.ExchangePattern;
import org.switchyard.ExchangePhase;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.ServiceReference;
import org.switchyard.common.io.resource.Resource;
import org.switchyard.common.io.resource.ResourceType;
import org.switchyard.common.type.Classes;
import org.switchyard.component.common.rules.config.model.AuditModel;
import org.switchyard.component.common.rules.util.drools.Agents;
import org.switchyard.component.common.rules.util.drools.Audits;
import org.switchyard.component.common.rules.util.drools.Resources;
import org.switchyard.component.rules.common.RulesActionType;
import org.switchyard.component.rules.config.model.RulesActionModel;
import org.switchyard.component.rules.config.model.RulesComponentImplementationModel;
import org.switchyard.component.rules.exchange.BaseRulesExchangeHandler;
import org.switchyard.metadata.ServiceOperation;

/**
 * A Drools implementation of a Rules ExchangeHandler.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class DroolsRulesExchangeHandler extends BaseRulesExchangeHandler {

    private final Object _statefulLock = new Object();
    private boolean _stateful;
    private String _messageContentName;
    private KnowledgeAgent _kagent;
    private AuditModel _audit;
    private Map<String,RulesActionModel> _actions = new HashMap<String,RulesActionModel>();
    private KnowledgeBase _kbase;
    private StatefulKnowledgeSession _ksession;
    private KnowledgeRuntimeLogger _klogger;

    /**
     * Constructs a new DroolsRulesExchangeHandler.
     */
    public DroolsRulesExchangeHandler() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(QName qname, RulesComponentImplementationModel model) {
        _stateful = model.isStateful();
        _messageContentName = model.getMessageContentName();
        if (_messageContentName == null) {
            _messageContentName = MESSAGE_CONTENT;
        }
        ClassLoader loader = Classes.getClassLoader(getClass());
        ResourceType.install(loader);
        if (model.isAgent()) {
            String agentName;
            try {
                agentName = model.getComponent().getComposite().getName();
            } catch (NullPointerException npe) {
                agentName = null;
            }
            _kagent = Agents.newKnowledgeAgent(agentName, model.getResources(), loader);
            _kbase = _kagent.getKnowledgeBase();
        } else {
            KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            for (Resource resource : model.getResources()) {
                Resources.add(resource, kbuilder, loader);
            }
            _kbase = kbuilder.newKnowledgeBase();
        }
        _audit = model.getAudit();
        for (RulesActionModel ram : model.getRulesActions()) {
            _actions.put(ram.getName(), ram);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(ServiceReference serviceRef) {
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
        Context context = exchange.getContext();
        ServiceOperation serviceOperation = exchange.getContract().getServiceOperation();
        RulesActionModel rulesActionModel = _actions.get(serviceOperation.getName());
        RulesActionType rulesActionType = getRulesActionType(context, rulesActionModel);
        Message message = exchange.getMessage();
        Object content = message.getContent();
        switch (rulesActionType) {
            case EXECUTE_RULES:
                if (_stateful) {
                    synchronized (_statefulLock) {
                        if (!isContinue(context)) {
                            disposeStateful();
                        }
                        StatefulKnowledgeSession ksession = ensureStateful();
                        ksession.getGlobals().set(MESSAGE, message);
                        //ksession.getGlobals().set(MESSAGE_CONTENT, content);
                        ksession.insert(content);
                        ksession.fireAllRules();
                        message = (Message)ksession.getGlobals().get(MESSAGE);
                        //content = ksession.getGlobals().get(MESSAGE_CONTENT);
                        content = message.getContent();
                        if (isDispose(context)) {
                            disposeStateful();
                        }
                    }
                } else {
                    StatelessKnowledgeSession ksession;
                    if (_kagent != null) {
                        ksession = _kagent.newStatelessKnowledgeSession();
                    } else {
                        ksession = _kbase.newStatelessKnowledgeSession();
                    }
                    KnowledgeRuntimeLogger klogger = Audits.getLogger(_audit, ksession);
                    try {
                        ksession.getGlobals().set(MESSAGE, message);
                        //ksession.getGlobals().set(MESSAGE_CONTENT, content);
                        ksession.execute(content);
                        message = (Message)ksession.getGlobals().get(MESSAGE);
                        //content = ksession.getGlobals().get(MESSAGE_CONTENT);
                        content = message.getContent();
                    } finally {
                        if (klogger != null) {
                            klogger.close();
                        }
                    }
                }
                break;
        }
        ExchangePattern exchangePattern = serviceOperation.getExchangePattern();
        if (ExchangePattern.IN_OUT.equals(exchangePattern)) {
            message = exchange.createMessage();
            if (content != null) {
                message.setContent(content);
            }
            exchange.send(message);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop(ServiceReference serviceRef) {
        disposeStateful();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy(ServiceReference serviceRef) {
        _kbase = null;
        _actions.clear();
        _audit = null;
        _messageContentName = null;
        _stateful = false;
        if (_kagent != null) {
            try {
                _kagent.dispose();
            } finally {
                _kagent = null;
            }
        }
    }

    private StatefulKnowledgeSession ensureStateful() {
        if (_ksession == null) {
            _ksession = _kbase.newStatefulKnowledgeSession();
            _klogger = Audits.getLogger(_audit, _ksession);
        }
        return _ksession;
    }

    private void disposeStateful() {
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

}
