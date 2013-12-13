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
import java.util.Properties;

import javax.xml.namespace.QName;

import org.kie.api.runtime.Globals;
import org.switchyard.Context;
import org.switchyard.Exchange;
import org.switchyard.ExchangePhase;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.ServiceDomain;
import org.switchyard.common.lang.Strings;
import org.switchyard.common.type.Classes;
import org.switchyard.component.common.knowledge.config.model.KnowledgeComponentImplementationModel;
import org.switchyard.component.common.knowledge.session.KnowledgeSession;
import org.switchyard.component.common.knowledge.session.KnowledgeSessionFactory;
import org.switchyard.component.common.knowledge.util.Environments;
import org.switchyard.component.common.knowledge.util.Operations;
import org.switchyard.component.common.knowledge.util.Resources;
import org.switchyard.deploy.BaseServiceHandler;
import org.switchyard.deploy.ServiceHandler;
import org.switchyard.metadata.ExchangeContract;
import org.switchyard.metadata.ServiceOperation;

/**
 * An abstract "knowledge" implementation of an ExchangeHandler.
 * 
 * @param <M> the model implementation
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public abstract class KnowledgeExchangeHandler<M extends KnowledgeComponentImplementationModel> extends BaseServiceHandler implements ServiceHandler {

    private final String _deploymentId;
    private final M _model;
    private final ServiceDomain _serviceDomain;
    private final QName _serviceName;
    private ClassLoader _loader;
    private final Map<String, KnowledgeOperation> _operations = new HashMap<String, KnowledgeOperation>();
    private KnowledgeSessionFactory _sessionFactory;
    private KnowledgeSession _statefulSession;

    /**
     * Constructs a new KnowledgeExchangeHandler with the specified model, service domain, and service name.
     * @param model the specified model
     * @param serviceDomain the specified service domain
     * @param serviceName the specified service name
     */
    public KnowledgeExchangeHandler(M model, ServiceDomain serviceDomain, QName serviceName) {
        super(serviceDomain);
        // TODO: revisit how deploymentId is created and used
        _deploymentId = serviceName.toString();
        _model = model;
        _serviceDomain = serviceDomain;
        _serviceName = serviceName;
    }

    /**
     * Gets the deployment id.
     * @return the deployment id
     */
    protected String getDeploymentId() {
        return _deploymentId;
    }

    /**
     * Gets the model.
     * @return the model
     */
    protected M getModel() {
        return _model;
    }

    /**
     * Gets the service domain.
     * @return the service domain
     */
    protected ServiceDomain getServiceDomain() {
        return _serviceDomain;
    }

    /**
     * Gets the service name.
     * @return the service name
     */
    protected QName getServiceName() {
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
     * Gets any property overrides.
     * @return any property overrides
     */
    protected Properties getPropertyOverrides() {
        return new Properties();
    }

    /**
     * Gets any environment overrides.
     * @return any environment overrides
     */
    protected Map<String, Object> getEnvironmentOverrides() {
        Map<String, Object> env = new HashMap<String, Object>();
        env.put(Environments.DEPLOYMENT_ID, getDeploymentId());
        return env;
    }

    /**
     * Gets a new stateless knowledge session.
     * @return a new stateless knowledge session
     */
    protected KnowledgeSession newStatelessSession() {
        return _sessionFactory.newStatelessSession();
    }

    /**
     * Gets the stateful knowledge session.
     * @return the stateful knowledge session
     */
    protected KnowledgeSession getStatefulSession() {
        if (_statefulSession == null) {
            _statefulSession = _sessionFactory.newStatefulSession(getEnvironmentOverrides());
        }
        return _statefulSession;
    }

    /**
     * Gets the persistent knowledge session
     * @return the persistent knowledge session
     */
    protected KnowledgeSession getPersistentSession(Integer sessionId) {
        if (_statefulSession != null) {
            if (!_statefulSession.isPersistent() || (sessionId != null && !sessionId.equals(_statefulSession.getId()))) {
                disposeStatefulSession();
            }
        }
        if (_statefulSession == null) {
            _statefulSession = _sessionFactory.getPersistentSession(getEnvironmentOverrides(), sessionId);
        }
        return _statefulSession;
    }

    /**
     * Disposes the stateful session.
     */
    protected void disposeStatefulSession() {
        if (_statefulSession != null) {
            try {
                _statefulSession.dispose();
            } finally {
                _statefulSession = null;
            }
        }
    }

    /**
     * Disposes the session factory.
     */
    private void disposeSessionFactory() {
        if (_sessionFactory != null) {
            try {
                _sessionFactory.dispose();
            } finally {
                _sessionFactory = null;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doStart() {
        _loader = Classes.getClassLoader(getDeploymentClassLoader(), getClass().getClassLoader());
        Resources.installTypes(_loader);
        Operations.registerOperations(_model, _operations, getDefaultOperation());
        _sessionFactory = KnowledgeSessionFactory.newSessionFactory(_model, _loader, _serviceDomain, getPropertyOverrides());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doStop() {
        _loader = null;
        _operations.clear();
        try {
            disposeStatefulSession();
        } finally {
            disposeSessionFactory();
        }
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
     * Gets the global variables from the session.
     * @param session the session
     * @return the global variables
     */
    protected Map<String, Object> getGlobalVariables(KnowledgeSession session) {
        Map<String, Object> globalVariables = new HashMap<String, Object>();
        if (session != null) {
            Globals globals = session.getGlobals();
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
