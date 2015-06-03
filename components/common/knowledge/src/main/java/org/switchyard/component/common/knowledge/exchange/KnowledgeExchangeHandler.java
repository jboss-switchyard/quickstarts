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
package org.switchyard.component.common.knowledge.exchange;

import static org.switchyard.component.common.knowledge.KnowledgeConstants.DEFAULT;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.kie.api.runtime.Globals;
import org.switchyard.Context;
import org.switchyard.Exchange;
import org.switchyard.ExchangePhase;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.ServiceDomain;
import org.switchyard.common.io.resource.ResourceType;
import org.switchyard.common.lang.Strings;
import org.switchyard.common.type.Classes;
import org.switchyard.component.common.knowledge.config.model.KnowledgeComponentImplementationModel;
import org.switchyard.component.common.knowledge.operation.KnowledgeOperation;
import org.switchyard.component.common.knowledge.operation.KnowledgeOperations;
import org.switchyard.component.common.knowledge.runtime.KnowledgeRuntimeEngine;
import org.switchyard.component.common.knowledge.runtime.KnowledgeRuntimeManager;
import org.switchyard.component.common.knowledge.runtime.KnowledgeRuntimeManagerFactory;
import org.switchyard.component.common.knowledge.runtime.KnowledgeRuntimeManagerType;
import org.switchyard.deploy.BaseServiceHandler;
import org.switchyard.deploy.ServiceHandler;
import org.switchyard.metadata.ExchangeContract;
import org.switchyard.metadata.ServiceOperation;

/**
 * An abstract "knowledge" implementation of an ExchangeHandler.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public abstract class KnowledgeExchangeHandler extends BaseServiceHandler implements ServiceHandler {

    private final KnowledgeComponentImplementationModel _model;
    private final ServiceDomain _serviceDomain;
    private final QName _serviceName;
    private final Map<String, KnowledgeOperation> _operations = new HashMap<String, KnowledgeOperation>();
    private ClassLoader _loader;
    private KnowledgeRuntimeManagerFactory _runtimeManagerFactory;

    /**
     * Constructs a new KnowledgeExchangeHandler with the specified model, service domain, and service name.
     * @param model the specified model
     * @param serviceDomain the specified service domain
     * @param serviceName the specified service name
     */
    public KnowledgeExchangeHandler(KnowledgeComponentImplementationModel model, ServiceDomain serviceDomain, QName serviceName) {
        super(serviceDomain);
        _model = model;
        _serviceDomain = serviceDomain;
        _serviceName = serviceName;
    }

    /**
     * Gets the service domain.
     * @return the service domain
     */
    public ServiceDomain getServiceDomain() {
        return _serviceDomain;
    }

    /**
     * Gets the service name.
     * @return the service name
     */
    public QName getServiceName() {
        return _serviceName;
    }

    /**
     * Gets the class loader.
     * @return the class loader
     */
    protected ClassLoader getLoader() {
        return _loader;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doStart() {
        _loader = Classes.getClassLoader(getDeploymentClassLoader(), getClass().getClassLoader());
        ResourceType.install(_loader);
        KnowledgeOperations.registerOperations(_model, _operations, getDefaultOperation());
        _runtimeManagerFactory = new KnowledgeRuntimeManagerFactory(_loader, _serviceDomain, _serviceName, _model);
    }

    /**
     * Creates a new Singleton KnowledgeRuntimeManager.
     * @return the Singleton KnowledgeRuntimeManager
     */
    protected KnowledgeRuntimeManager newSingletonRuntimeManager() {
        return _runtimeManagerFactory.newRuntimeManager(KnowledgeRuntimeManagerType.SINGLETON);
    }

    /**
     * Creates a new PerRequest KnowledgeRuntimeManager.
     * @return the PerRequest KnowledgeRuntimeManager
     */
    protected KnowledgeRuntimeManager newPerRequestRuntimeManager() {
        return _runtimeManagerFactory.newRuntimeManager(KnowledgeRuntimeManagerType.PER_REQUEST);
    }

    /**
     * Creates a new PerProcessInstance KnowledgeRuntimeManager.
     * @return the PerProcessInstance KnowledgeRuntimeManager
     */
    protected KnowledgeRuntimeManager newPerProcessInstanceRuntimeManager() {
        return _runtimeManagerFactory.newRuntimeManager(KnowledgeRuntimeManagerType.PER_PROCESS_INSTANCE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doStop() {
        _loader = null;
        _operations.clear();
    }

    /**
     * Gets the default knowledge operation.
     * @return the default knowledge operation
     */
    public abstract KnowledgeOperation getDefaultOperation();

    private KnowledgeOperation getOperation(ServiceOperation serviceOperation) {
        if (serviceOperation != null) {
            String operationName = Strings.trimToNull(serviceOperation.getName());
            if (operationName != null) {
                return _operations.get(operationName);
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void handleMessage(Exchange exchange) throws HandlerException {
        if (ExchangePhase.IN.equals(exchange.getPhase())) {
            ExchangeContract contract = exchange.getContract();
            KnowledgeOperation operation = getOperation(contract.getProviderOperation());
            if (operation == null) {
                operation = getOperation(contract.getConsumerOperation());
            }
            if (operation == null) {
                // we use "default" here instead of getDefaultOperation() so that a
                // user can define a name="default" in their switchyard.xml
                operation = _operations.get(DEFAULT);
            }
            handleOperation(exchange, operation);
        }
    }

    /**
     * Handles a knowledge operation.
     * @param exchange the exchange
     * @param operation the operation
     * @throws HandlerException oops
     */
    public abstract void handleOperation(Exchange exchange, KnowledgeOperation operation) throws HandlerException;

    /**
     * Gets a primitive boolean context property.
     * @param exchange the exchange
     * @param message the message
     * @param name the name
     * @return the property
     */
    protected boolean isBoolean(Exchange exchange, Message message, String name) {
        Boolean b = getBoolean(exchange, message, name);
        return b != null && b.booleanValue();
    }

    /**
     * Gets a Boolean context property.
     * @param exchange the exchange
     * @param message the message
     * @param name the name
     * @return the property
     */
    protected Boolean getBoolean(Exchange exchange, Message message, String name) {
        Object value = getObject(exchange, message, name);
        if (value instanceof Boolean) {
            return (Boolean)value;
        } else if (value instanceof String) {
            return Boolean.valueOf(((String)value).trim());
        }
        return false;
    }

    /**
     * Gets an Integer context property.
     * @param exchange the exchange
     * @param message the message
     * @param name the name
     * @return the property
     */
    protected Integer getInteger(Exchange exchange, Message message, String name) {
        Object value = getObject(exchange, message, name);
        if (value instanceof Integer) {
            return (Integer)value;
        } else if (value instanceof Number) {
            return Integer.valueOf(((Number)value).intValue());
        } else if (value instanceof String) {
            return Integer.valueOf(((String)value).trim());
        }
        return null;
    }

    /**
     * Gets a Long context property.
     * @param exchange the exchange
     * @param message the message
     * @param name the name
     * @return the property
     */
    protected Long getLong(Exchange exchange, Message message, String name) {
        Object value = getObject(exchange, message, name);
        if (value instanceof Long) {
            return (Long)value;
        } else if (value instanceof Number) {
            return Long.valueOf(((Number)value).longValue());
        } else if (value instanceof String) {
            return Long.valueOf(((String)value).trim());
        }
        return null;
    }

    /**
     * Gets a String context property.
     * @param exchange the exchange
     * @param message the message
     * @param name the name
     * @return the property
     */
    protected String getString(Exchange exchange, Message message, String name) {
        Object value = getObject(exchange, message, name);
        if (value instanceof String) {
            return (String)value;
        } else if (value != null) {
            return String.valueOf(value);
        }
        return null;
    }

    /**
     * Gets an Object context property.
     * @param exchange the exchange
     * @param message the message
     * @param name the name
     * @return the property
     */
    protected Object getObject(Exchange exchange, Message message, String name) {
        Context context = message != null ? exchange.getContext(message) : exchange.getContext();
        return context.getPropertyValue(name);
    }

    /**
     * Gets the global variables from the knowledge runtime engine.
     * @param runtimeEngine the knowledge runtime engine
     * @return the global variables
     */
    protected Map<String, Object> getGlobalVariables(KnowledgeRuntimeEngine runtimeEngine) {
        Map<String, Object> globalVariables = new HashMap<String, Object>();
        if (runtimeEngine != null) {
            Globals globals = runtimeEngine.getSessionGlobals();
            if (globals != null) {
                for (String key : globals.getGlobalKeys()) {
                    Object value = globals.get(key);
                    globalVariables.put(key, value);
                }
            }
        }
        return globalVariables;
    }

}
