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

import static org.switchyard.component.rules.RulesConstants.CONTEXT;
import static org.switchyard.component.rules.RulesConstants.EXCHANGE;
import static org.switchyard.component.rules.RulesConstants.MESSAGE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.xml.namespace.QName;

import org.drools.KnowledgeBase;
import org.drools.agent.KnowledgeAgent;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.runtime.Channel;
import org.drools.runtime.Environment;
import org.drools.runtime.Globals;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.StatelessKnowledgeSession;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;
import org.switchyard.Context;
import org.switchyard.Exchange;
import org.switchyard.ExchangePattern;
import org.switchyard.ExchangePhase;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.Scope;
import org.switchyard.ServiceDomain;
import org.switchyard.ServiceReference;
import org.switchyard.common.io.resource.ResourceType;
import org.switchyard.common.type.Classes;
import org.switchyard.common.xml.XMLHelper;
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
import org.switchyard.component.common.rules.util.drools.Events;
import org.switchyard.component.rules.RulesActionType;
import org.switchyard.component.rules.channel.drools.SwitchYardChannel;
import org.switchyard.component.rules.channel.drools.SwitchYardServiceChannel;
import org.switchyard.component.rules.config.model.ChannelModel;
import org.switchyard.component.rules.config.model.FactsModel;
import org.switchyard.component.rules.config.model.GlobalsModel;
import org.switchyard.component.rules.config.model.RulesActionModel;
import org.switchyard.component.rules.config.model.RulesComponentImplementationModel;
import org.switchyard.component.rules.exchange.BaseRulesExchangeHandler;
import org.switchyard.exception.SwitchYardException;
import org.switchyard.metadata.ServiceOperation;

/**
 * A Drools implementation of a Rules ExchangeHandler.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class DroolsRulesExchangeHandler extends BaseRulesExchangeHandler {

    private final Lock _fireLock = new ReentrantLock();
    private final Lock _stateLock = new ReentrantLock();
    private ComponentImplementationConfig _componentImplementationConfig;
    private String _targetNamespace;
    private ServiceDomain _domain;
    private KnowledgeAgent _kagent;
    private AuditModel _audit;
    private Map<String,RulesActionModel> _actions = new HashMap<String,RulesActionModel>();
    private Map<String,Channel> _channels = new HashMap<String,Channel>();
    private Map<String, Scope> _globalContextScopes = new HashMap<String, Scope>();
    private Map<String, Expression> _globalExpressions = new HashMap<String, Expression>();
    private boolean _useFactMappings = false;
    private Map<String, Scope> _factContextScopes = new HashMap<String, Scope>();
    private Map<String, Expression> _factExpressions = new HashMap<String, Expression>();
    private KnowledgeBase _kbase;
    private KnowledgeSessionConfiguration _ksessionConfig;
    private Environment _environment;
    private StatefulKnowledgeSession _ksession;
    private Thread _ksessionThread;
    private KnowledgeRuntimeLogger _klogger;

    /**
     * Constructs a new DroolsRulesExchangeHandler.
     */
    public DroolsRulesExchangeHandler() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(QName qname, RulesComponentImplementationModel model, ServiceDomain domain) {
        _targetNamespace = model.getComponent().getTargetNamespace();
        _domain = domain;
        ClassLoader loader = Classes.getClassLoader(getClass());
        ResourceType.install(loader);
        _componentImplementationConfig = new ComponentImplementationConfig(model, loader);
        if (model.isAgent()) {
            _kagent = Agents.newAgent(_componentImplementationConfig);
            _kbase = _kagent.getKnowledgeBase();
        } else {
            _kbase = Bases.newBase(_componentImplementationConfig);
        }
        _ksessionConfig = Configs.getSessionConfiguration(_componentImplementationConfig);
        _environment = Environments.getEnvironment(_componentImplementationConfig);
        _audit = model.getAudit();
        for (RulesActionModel ram : model.getRulesActions()) {
            _actions.put(ram.getName(), ram);
        }
        for (ChannelModel cm : model.getChannels()) {
            Class<?> clazz = cm.getClazz(_componentImplementationConfig.getLoader());
            if (clazz == null) {
                clazz = SwitchYardServiceChannel.class;
            } else if (!Channel.class.isAssignableFrom(clazz)) {
                throw new IllegalArgumentException(Channel.class.getName() + " is not assignable from " + clazz.getName());
            }
            Channel channel;
            try {
                channel = (Channel)clazz.newInstance();
            } catch (Exception e) {
                throw new SwitchYardException(e);
            }
            if (channel instanceof SwitchYardChannel) {
                ((SwitchYardChannel)channel).setModel(cm);
            }
            _channels.put(cm.getName(), channel);
        }
        GlobalsModel globals = model.getGlobals();
        if (globals != null) {
            ExpressionFactory factory = ExpressionFactory.instance();
            for (MappingModel mapping : globals.getMappings()) {
                _globalContextScopes.put(mapping.getVariable(), mapping.getContextScope());
                _globalExpressions.put(mapping.getVariable(), factory.create(mapping));
            }
        }
        FactsModel facts = model.getFacts();
        if (facts != null) {
            _useFactMappings = true;
            ExpressionFactory factory = ExpressionFactory.instance();
            for (MappingModel mapping : facts.getMappings()) {
                _factContextScopes.put(mapping.getVariable(), mapping.getContextScope());
                _factExpressions.put(mapping.getVariable(), factory.create(mapping));
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
        Context context = exchange.getContext();
        ServiceOperation serviceOperation = exchange.getContract().getProviderOperation();
        RulesActionModel rulesActionModel = _actions.get(serviceOperation.getName());
        RulesActionType rulesActionType = getRulesActionType(context, rulesActionModel);
        Message message = exchange.getMessage();
        Object content = message.getContent();
        switch (rulesActionType) {
            case EXECUTE:
                StatelessKnowledgeSession ksessionStateless = getStatelessSession();
                KnowledgeRuntimeLogger klogger = Audits.getLogger(_audit, ksessionStateless);
                try {
                    Globals globals = ksessionStateless.getGlobals();
                    setGlobals(globals, exchange, true);
                    List<Object> facts = getFacts(content, exchange);
                    ksessionStateless.execute(facts);
                    message = (Message)globals.get(MESSAGE);
                    content = message != null ? message.getContent() : null;
                } finally {
                    if (klogger != null) {
                        klogger.close();
                    }
                }
                break;
            case FIRE_ALL_RULES:
                _fireLock.lock();
                try {
                    /*
                    if (!isContinue(context)) {
                        disposeStateful();
                    }
                    */
                    final StatefulKnowledgeSession ksessionStateful = getStatefulSession();
                    Globals globals = ksessionStateful.getGlobals();
                    setGlobals(globals, exchange, true);
                    List<Object> facts = getFacts(content, exchange);
                    for (Object fact : facts) {
                        ksessionStateful.insert(fact);
                    }
                    ksessionStateful.fireAllRules();
                    message = (Message)globals.get(MESSAGE);
                    content = message != null ? message.getContent() : null;
                    if (isDispose(context)) {
                        disposeStatefulSession();
                    }
                } finally {
                    _fireLock.unlock();
                }
                break;
            case FIRE_UNTIL_HALT:
                _fireLock.lock();
                try {
                    /*
                    if (!isContinue(context)) {
                        disposeStateful();
                    }
                    */
                    boolean ksessionNew = _ksession == null;
                    final StatefulKnowledgeSession ksessionStateful = getStatefulSession();
                    Globals globals = ksessionStateful.getGlobals();
                    setGlobals(globals, exchange, false);
                    if (ksessionNew) {
                        final ClassLoader properLoader = Classes.getClassLoader(getClass());
                        _ksessionThread = new Thread(new Runnable() {
                            public void run() {
                                ClassLoader originalLoader = Classes.setTCCL(properLoader);
                                try {
                                    ksessionStateful.fireUntilHalt();
                                } catch (NullPointerException ne) {
                                    // keep checkstyle happy
                                    ne.getMessage();
                                } finally {
                                    Classes.setTCCL(originalLoader);
                                }
                            }
                        });
                        _ksessionThread.setName(new StringBuilder().append(getClass().getSimpleName())
                            .append(":fireUntilHalt(").append(System.identityHashCode(ksessionStateful)).append(")").toString());
                        _ksessionThread.setDaemon(true);
                        _ksessionThread.start();
                    }
                    List<Object> facts = getFacts(content, exchange);
                    String ep = getEntryPoint(rulesActionModel);
                    if (ep != null) {
                        WorkingMemoryEntryPoint wmep = ksessionStateful.getWorkingMemoryEntryPoint(ep);
                        if (wmep != null) {
                            for (Object fact : facts) {
                                wmep.insert(fact);
                            }
                        } else {
                            throw new HandlerException("Unknown entry point: " + ep + "; please check your rules source.");
                        }
                    } else {
                        for (Object fact : facts) {
                            ksessionStateful.insert(fact);
                        }
                    }
                    content = null;
                    if (isDispose(context)) {
                        disposeStatefulSession();
                    }
                } finally {
                    _fireLock.unlock();
                }
                break;
            default:
                throw new UnsupportedOperationException("Unsupported rules action type: " + rulesActionType);
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

    private void setGlobals(Globals globals, Exchange exchange, boolean includeTrifecta) {
        if (includeTrifecta) {
            globals.set(EXCHANGE, exchange);
            globals.set(CONTEXT, exchange.getContext());
            globals.set(MESSAGE, exchange.getMessage());
        }
        for (Entry<String, Expression> entry : _globalExpressions.entrySet()) {
            Map<String, Object> vars = new HashMap<String, Object>();
            vars.put(EXCHANGE, exchange);
            vars.put(CONTEXT, new ContextMap(exchange.getContext(), _globalContextScopes.get(entry.getKey())));
            vars.put(MESSAGE, exchange.getMessage());
            Object global = entry.getValue().evaluate(vars);
            globals.set(entry.getKey(), global);
        }
    }

    private List<Object> getFacts(Object content, Exchange exchange) {
        List<Object> facts = new ArrayList<Object>();
        if (_useFactMappings) {
            for (Entry<String, Expression> entry : _factExpressions.entrySet()) {
                Map<String, Object> vars = new HashMap<String, Object>();
                vars.put(EXCHANGE, exchange);
                vars.put(CONTEXT, new ContextMap(exchange.getContext(), _factContextScopes.get(entry.getKey())));
                vars.put(MESSAGE, exchange.getMessage());
                Object fact = entry.getValue().evaluate(vars);
                addFact(fact, facts);
            }
        } else {
            addFact(content, facts);
        }
        return facts;
    }

    private void addFact(Object fact, List<Object> facts) {
        if (fact != null) {
            if (fact instanceof Iterable) {
                for (Object f : (Iterable<?>)fact) {
                    if (f != null) {
                        facts.add(f);
                    }
                }
            } else {
                facts.add(fact);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        disposeStatefulSession();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        _kbase = null;
        _actions.clear();
        _channels.clear();
        _audit = null;
        _globalContextScopes.clear();
        _globalExpressions.clear();
        _useFactMappings = false;
        _factContextScopes.clear();
        _factExpressions.clear();
        if (_kagent != null) {
            try {
                _kagent.dispose();
            } finally {
                _kagent = null;
            }
        }
        _componentImplementationConfig = null;
    }

    private StatelessKnowledgeSession getStatelessSession() {
        StatelessKnowledgeSession ksessionStateless;
        if (_kagent != null) {
            ksessionStateless = _kagent.newStatelessKnowledgeSession(_ksessionConfig);
        } else {
            ksessionStateless = _kbase.newStatelessKnowledgeSession(_ksessionConfig);
        }
        Events.addEventListeners(_componentImplementationConfig, ksessionStateless);
        return ksessionStateless;
    }

    private StatefulKnowledgeSession getStatefulSession() {
        _stateLock.lock();
        try {
            if (_ksession == null) {
                _ksession = _kbase.newStatefulKnowledgeSession(_ksessionConfig, _environment);
                _klogger = Audits.getLogger(_audit, _ksession);
                Events.addEventListeners(_componentImplementationConfig, _ksession);
                for (Entry<String,Channel> c : _channels.entrySet()) {
                    String name = c.getKey();
                    Channel channel = c.getValue();
                    if (name != null && channel != null) {
                        if (channel instanceof SwitchYardChannel) {
                            SwitchYardChannel syc = (SwitchYardChannel)channel;
                            QName qname = XMLHelper.createQName(_targetNamespace, syc.getModel().getReference());
                            ServiceReference reference = _domain.getServiceReference(qname);
                            syc.setReference(reference);
                        }
                        _ksession.registerChannel(name, channel);
                    }
                }
            }
            return _ksession;
        } finally {
            _stateLock.unlock();
        }
    }

    private void disposeStatefulSession() {
        _stateLock.lock();
        try {
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
            _stateLock.unlock();
        }
    }

}
