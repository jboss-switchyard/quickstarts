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
package org.switchyard.component.common.knowledge.exchange;

import static org.switchyard.component.common.knowledge.KnowledgeConstants.DEFAULT;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.switchyard.Exchange;
import org.switchyard.ExchangePhase;
import org.switchyard.HandlerException;
import org.switchyard.ServiceDomain;
import org.switchyard.common.lang.Strings;
import org.switchyard.common.type.Classes;
import org.switchyard.component.common.knowledge.config.model.KnowledgeComponentImplementationModel;
import org.switchyard.component.common.knowledge.session.KnowledgeSession;
import org.switchyard.component.common.knowledge.session.KnowledgeSessionFactory;
import org.switchyard.component.common.knowledge.util.Actions;
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

    private final M _model;
    private final ServiceDomain _domain;
    private ClassLoader _loader;
    private final Map<String, KnowledgeAction> _actions = new HashMap<String, KnowledgeAction>();
    private KnowledgeSessionFactory _sessionFactory;
    private KnowledgeSession _statefulSession;

    /**
     * Constructs a new KnowledgeExchangeHandler with the specified model and service domain.
     * @param model the specified model
     * @param domain the specified service domain
     */
    public KnowledgeExchangeHandler(M model, ServiceDomain domain) {
        super(domain);
        _model = model;
        _domain = domain;
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
    protected ServiceDomain getDomain() {
        return _domain;
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
        return null;
    }

    /**
     * Gets any environment overrides.
     * @return any environment overrides
     */
    protected Map<String, Object> getEnvironmentOverrides() {
        return null;
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
        _loader = Classes.getClassLoader(getClass());
        Resources.installTypes(_loader);
        Actions.registerActions(_model, _actions, getDefaultAction());
        _sessionFactory = KnowledgeSessionFactory.newSessionFactory(_model, _loader, _domain, getPropertyOverrides());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doStop() {
        _loader = null;
        _actions.clear();
        try {
            disposeStatefulSession();
        } finally {
            disposeSessionFactory();
        }
    }

    /**
     * Gets the default knowledge action.
     * @return the default knowledge action
     */
    public abstract KnowledgeAction getDefaultAction();

    private KnowledgeAction getAction(ServiceOperation serviceOperation) {
        if (serviceOperation != null) {
            String operationName = Strings.trimToNull(serviceOperation.getName());
            if (operationName != null) {
                return _actions.get(operationName);
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
            KnowledgeAction action = getAction(contract.getProviderOperation());
            if (action == null) {
                action = getAction(contract.getConsumerOperation());
            }
            if (action == null) {
                // we use "default" here instead of getDefaultAction() so that a
                // user can define a operation="default" in their switchyard.xml
                action = _actions.get(DEFAULT);
            }
            handleAction(exchange, action);
        }
    }

    /**
     * Handles a knowledge action.
     * @param exchange the exchange
     * @param action the action
     * @throws HandlerException oops
     */
    public abstract void handleAction(Exchange exchange, KnowledgeAction action) throws HandlerException;

    /**
     * Gets a primitive boolean context property.
     * @param exchange the exchange
     * @param name the name
     * @return the property
     */
    protected boolean isBoolean(Exchange exchange, String name) {
        Boolean b = getBoolean(exchange, name);
        return b != null && b.booleanValue();
    }

    /**
     * Gets a Boolean context property.
     * @param exchange the exchange
     * @param name the name
     * @return the property
     */
    protected Boolean getBoolean(Exchange exchange, String name) {
        Object value = getObject(exchange, name);
        if (value instanceof Boolean) {
            return (Boolean)value;
        } else if (value instanceof String) {
            return Boolean.valueOf((String)value);
        }
        return null;
    }

    /**
     * Gets an Integer context property.
     * @param exchange the exchange
     * @param name the name
     * @return the property
     */
    protected Integer getInteger(Exchange exchange, String name) {
        Object value = getObject(exchange, name);
        if (value instanceof Integer) {
            return (Integer)value;
        } else if (value instanceof Number) {
            return Integer.valueOf(((Number)value).intValue());
        } else if (value instanceof String) {
            return Integer.valueOf((String)value);
        }
        return null;
    }

    /**
     * Gets a Long context property.
     * @param exchange the exchange
     * @param name the name
     * @return the property
     */
    protected Long getLong(Exchange exchange, String name) {
        Object value = getObject(exchange, name);
        if (value instanceof Long) {
            return (Long)value;
        } else if (value instanceof Number) {
            return Long.valueOf(((Number)value).longValue());
        } else if (value instanceof String) {
            return Long.valueOf((String)value);
        }
        return null;
    }

    /**
     * Gets a String context property.
     * @param exchange the exchange
     * @param name the name
     * @return the property
     */
    protected String getString(Exchange exchange, String name) {
        Object value = getObject(exchange, name);
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
     * @param name the name
     * @param scope the scope
     * @return the property
     */
    protected Object getObject(Exchange exchange, String name) {
        return exchange.getContext().getPropertyValue(name);
    }

}
