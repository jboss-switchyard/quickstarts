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
package org.switchyard.component.rules.exchange;

import static org.switchyard.component.common.knowledge.operation.KnowledgeOperations.getInputList;
import static org.switchyard.component.common.knowledge.operation.KnowledgeOperations.getInputOnlyList;
import static org.switchyard.component.common.knowledge.operation.KnowledgeOperations.getInputOutputMap;
import static org.switchyard.component.common.knowledge.operation.KnowledgeOperations.getListMap;
import static org.switchyard.component.common.knowledge.operation.KnowledgeOperations.setFaults;
import static org.switchyard.component.common.knowledge.operation.KnowledgeOperations.setGlobals;
import static org.switchyard.component.common.knowledge.operation.KnowledgeOperations.setOutputs;
import static org.switchyard.component.common.knowledge.operation.KnowledgeOperations.toVariable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.namespace.QName;

import org.kie.api.KieServices;
import org.kie.api.command.BatchExecutionCommand;
import org.kie.api.command.Command;
import org.kie.api.command.KieCommands;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.internal.runtime.manager.Disposable;
import org.kie.internal.runtime.manager.DisposeListener;
import org.switchyard.Exchange;
import org.switchyard.ExchangePattern;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.ServiceDomain;
import org.switchyard.common.lang.Strings;
import org.switchyard.common.type.Classes;
import org.switchyard.component.common.knowledge.KnowledgeConstants;
import org.switchyard.component.common.knowledge.exchange.KnowledgeExchangeHandler;
import org.switchyard.component.common.knowledge.operation.KnowledgeOperation;
import org.switchyard.component.common.knowledge.operation.KnowledgeOperations;
import org.switchyard.component.common.knowledge.runtime.KnowledgeRuntimeEngine;
import org.switchyard.component.common.knowledge.runtime.KnowledgeRuntimeManager;
import org.switchyard.component.rules.RulesConstants;
import org.switchyard.component.rules.RulesMessages;
import org.switchyard.component.rules.config.model.RulesComponentImplementationModel;
import org.switchyard.component.rules.operation.RulesOperationType;

/**
 * A "rules" implementation of a KnowledgeExchangeHandler.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class RulesExchangeHandler extends KnowledgeExchangeHandler {

    private static final AtomicInteger FIRE_UNTIL_HALT_COUNT = new AtomicInteger();
    private static final KnowledgeOperation DEFAULT_OPERATION = new KnowledgeOperation(RulesOperationType.EXECUTE);

    private KnowledgeRuntimeManager _perRequestRuntimeManager = null;
    private KnowledgeRuntimeManager _singletonRuntimeManager = null;
    private Thread _fireUntilHaltThread = null;

    /**
     * Constructs a new RulesExchangeHandler with the specified model, service domain, and service name.
     * @param model the specified model
     * @param serviceDomain the specified service domain
     * @param serviceName the specified service name
     */
    public RulesExchangeHandler(RulesComponentImplementationModel model, ServiceDomain serviceDomain, QName serviceName) {
        super(model, serviceDomain, serviceName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doStart() {
        super.doStart();
        _perRequestRuntimeManager = newPerRequestRuntimeManager();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doStop() {
        try {
            _perRequestRuntimeManager.close();
        } finally {
            try {
                disposeSingletonRuntimeEngine();
            } finally {
                super.doStop();
            }
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
        Message inputMessage = exchange.getMessage();
        ExchangePattern exchangePattern = exchange.getContract().getProviderOperation().getExchangePattern();
        Map<String, Object> expressionVariables = new HashMap<String, Object>();
        RulesOperationType operationType = (RulesOperationType)operation.getType();
        switch (operationType) {
            case EXECUTE: {
                KnowledgeRuntimeEngine runtime = getPerRequestRuntimeEngine();
                //sessionIdentifier = runtime.getSessionIdentifier();
                setGlobals(inputMessage, operation, runtime, false);
                try {
                    KieSession session = runtime.getKieSession();
                    if (ExchangePattern.IN_ONLY.equals(exchangePattern)) {
                        List<Object> facts = getInputList(inputMessage, operation, runtime);
                        for (Object fact : facts) {
                            session.insert(fact);
                        }
                        session.fireAllRules();
                    } else if (ExchangePattern.IN_OUT.equals(exchangePattern)) {
                        KieCommands cmds = KieServices.Factory.get().getCommands();
                        List<Command<?>> batch = new ArrayList<Command<?>>();
                        Map<String, Object> inouts = getInputOutputMap(inputMessage, operation, runtime);
                        for (Entry<String, Object> inout : inouts.entrySet()) {
                            batch.add(cmds.newInsert(inout.getValue(), inout.getKey()));
                        }
                        List<Object> facts = getInputOnlyList(inputMessage, operation, runtime);
                        batch.add(cmds.newInsertElements(facts));
                        batch.add(cmds.newFireAllRules());
                        BatchExecutionCommand exec = cmds.newBatchExecution(batch, KnowledgeConstants.RESULT);
                        ExecutionResults results = session.execute(exec);
                        for (String id : inouts.keySet()) {
                            expressionVariables.put(id, results.getValue(id));
                        }
                        expressionVariables.putAll(getGlobalVariables(runtime));
                    }
                } finally {
                    disposePerRequestRuntimeEngine(runtime);
                }
                break;
            }
            case INSERT:
            case FIRE_ALL_RULES: {
                KnowledgeRuntimeEngine runtime = getSingletonRuntimeEngine();
                if (ExchangePattern.IN_OUT.equals(exchangePattern)) {
                    synchronized (this) {
                        fireAllRules(inputMessage, operation);
                        expressionVariables.putAll(getGlobalVariables(runtime));
                    }
                } else {
                    if (KnowledgeOperations.containsGlobals(inputMessage, operation, runtime)) {
                        synchronized (this) {
                            fireAllRules(inputMessage, operation);
                        }
                    } else {
                        fireAllRules(inputMessage, operation);
                    }

                }

                if (isDispose(exchange, inputMessage)) {
                    disposeSingletonRuntimeEngine();
                }

                break;
            }
            case FIRE_UNTIL_HALT: {
                KnowledgeRuntimeEngine runtime = getSingletonRuntimeEngine();
                if (ExchangePattern.IN_OUT.equals(exchangePattern)) {
                    synchronized (this) {
                        fireUntilHalt(inputMessage, exchange, operation);
                        expressionVariables.putAll(getGlobalVariables(runtime));
                    }
                } else {
                    if (KnowledgeOperations.containsGlobals(inputMessage, operation, runtime)) {
                        synchronized (this) {
                            fireUntilHalt(inputMessage, exchange, operation);
                        }
                    } else {
                        fireUntilHalt(inputMessage, exchange, operation);
                    }

                }

                if (isDispose(exchange, inputMessage)) {
                    disposeSingletonRuntimeEngine();
                }
                break;
            }
            default: {
                throw RulesMessages.MESSAGES.unsupportedOperationType(operationType.toString());
            }
        }
        if (ExchangePattern.IN_OUT.equals(exchangePattern)) {
            Message outputMessage = exchange.createMessage();
            /*
            Context outputContext = exchange.getContext(outputMessage);
            if (sessionIdentifier != null) {
                outputContext.setProperty(RulesConstants.SESSION_ID_PROPERTY, sessionIdentifier);
            }
            */
            setFaults(outputMessage, operation, expressionVariables);
            if (outputMessage.getContent() != null) {
                exchange.sendFault(outputMessage);
            } else {
                setOutputs(outputMessage, operation, expressionVariables);
                exchange.send(outputMessage);
            }

        }

    }

    private KnowledgeRuntimeEngine getPerRequestRuntimeEngine() {
        return (KnowledgeRuntimeEngine)_perRequestRuntimeManager.getRuntimeEngine();
    }

    private void disposePerRequestRuntimeEngine(KnowledgeRuntimeEngine perRequestRuntimeEngine) {
        _perRequestRuntimeManager.disposeRuntimeEngine(perRequestRuntimeEngine);
    }

    private synchronized KnowledgeRuntimeEngine getSingletonRuntimeEngine() {
        if (_singletonRuntimeManager == null) {
            _singletonRuntimeManager = newSingletonRuntimeManager();
        }
        return (KnowledgeRuntimeEngine)_singletonRuntimeManager.getRuntimeEngine();
    }

    private synchronized void disposeSingletonRuntimeEngine() {
        if (_singletonRuntimeManager != null) {
            try {
                _singletonRuntimeManager.disposeRuntimeEngine(_singletonRuntimeManager.getRuntimeEngine());
            } finally {
                try {
                    _singletonRuntimeManager.close();
                } finally {
                    _singletonRuntimeManager = null;
                }
            }
        }
    }

    /*
    private boolean isContinue(Exchange exchange, Message message) {
        return isBoolean(exchange, message, RulesConstants.CONTINUE_PROPERTY);
    }
    */

    private boolean isDispose(Exchange exchange, Message message) {
        return isBoolean(exchange, message, RulesConstants.DISPOSE_PROPERTY);
    }

    private final class FireUntilHalt implements Runnable, DisposeListener {

        private final RulesExchangeHandler _handler;
        private final KnowledgeRuntimeEngine _runtime;
        private final ClassLoader _loader;

        private FireUntilHalt(RulesExchangeHandler handler, KnowledgeRuntimeEngine runtime, ClassLoader loader) {
            _handler = handler;
            _runtime = runtime;
            _loader = loader;
        }

        @Override
        public void run() {
            ClassLoader originalLoader = Classes.setTCCL(_loader);
            try {
                _runtime.getKieSession().fireUntilHalt();
            } finally {
                try {
                    _handler.disposeSingletonRuntimeEngine();
                } finally {
                    Classes.setTCCL(originalLoader);
                }
            }
        }

        @Override
        public void onDispose(RuntimeEngine runtime) {
            _handler._fireUntilHaltThread = null;
        }

        private Thread startThread() {
            Thread thread = new Thread(this);
            String name = new StringBuilder()
                .append(_handler.getClass().getSimpleName())
                .append(':')
                .append(getClass().getSimpleName())
                .append(':')
                .append(FIRE_UNTIL_HALT_COUNT.incrementAndGet())
                .toString();
            thread.setName(name);
            thread.setDaemon(true);
            thread.start();
            return thread;
        }

    }


    private void fireUntilHalt(Message inputMessage, Exchange exchange,
            KnowledgeOperation operation) throws HandlerException {
        KnowledgeRuntimeEngine runtime = getSingletonRuntimeEngine();
        // sessionIdentifier = runtime.getSessionIdentifier();
        setGlobals(inputMessage, operation, runtime, true);
        KieSession session = runtime.getKieSession();
        if (_fireUntilHaltThread == null
                && runtime.getWrapped() instanceof Disposable) {
            ClassLoader fireUntilHaltLoader = Classes.getTCCL();
            if (fireUntilHaltLoader == null) {
                fireUntilHaltLoader = getLoader();
            }
            FireUntilHalt fireUntilHalt = new FireUntilHalt(this,
                    runtime, fireUntilHaltLoader);
            ((Disposable) runtime.getWrapped())
                    .addDisposeListener(fireUntilHalt);
            _fireUntilHaltThread = fireUntilHalt.startThread();
        }
        final String undefinedVariable = toVariable(exchange);
        Map<String, List<Object>> inputMap = getListMap(
                inputMessage,
                operation.getInputExpressionMappings(), true,
                undefinedVariable);
        if (inputMap.size() > 0) {
            for (Entry<String, List<Object>> inputEntry : inputMap
                    .entrySet()) {
                String key = inputEntry.getKey();
                if (undefinedVariable.equals(key)) {
                    String eventId = Strings.trimToNull(operation
                            .getEventId());
                    if (eventId != null) {
                        key = eventId;
                    }
                }
                List<Object> facts = inputEntry.getValue();
                if (undefinedVariable.equals(key)) {
                    for (Object fact : facts) {
                        session.insert(fact);
                    }
                } else {
                    EntryPoint entryPoint = session
                            .getEntryPoint(key);
                    if (entryPoint != null) {
                        for (Object fact : facts) {
                            entryPoint.insert(fact);
                        }
                    } else {
                        throw RulesMessages.MESSAGES
                                .unknownEntryPoint(key);
                    }
                }
            }
        } else {
            List<Object> facts = getInputList(inputMessage,
                    operation, runtime);
            for (Object fact : facts) {
                session.insert(fact);
            }
        }
    }

    private int fireAllRules(Message inputMessage, KnowledgeOperation operation) {
        KnowledgeRuntimeEngine runtime = getSingletonRuntimeEngine();
        RulesOperationType operationType = (RulesOperationType) operation
                .getType();
        // sessionIdentifier = runtime.getSessionIdentifier();
        setGlobals(inputMessage, operation, runtime, true);
        KieSession session = runtime.getKieSession();
        List<Object> facts = getInputList(inputMessage, operation, runtime);
        for (Object fact : facts) {
            session.insert(fact);
        }
        if (RulesOperationType.FIRE_ALL_RULES.equals(operationType)) {
            return session.fireAllRules();
        }
        return 0;
    }

}
