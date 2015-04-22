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
package org.switchyard.component.jca.endpoint;

import java.util.Set;

import javax.xml.namespace.QName;

import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.Scope;
import org.switchyard.ServiceDomain;
import org.switchyard.ServiceReference;
import org.switchyard.component.common.composer.MessageComposer;
import org.switchyard.component.common.selector.OperationSelectorFactory;
import org.switchyard.component.jca.JCAMessages;
import org.switchyard.component.jca.composer.JCABindingData;
import org.switchyard.component.jca.composer.JCAComposition;
import org.switchyard.component.jca.config.model.JCABindingModel;
import org.switchyard.label.BehaviorLabel;
import org.switchyard.metadata.ServiceOperation;
import org.switchyard.policy.PolicyUtil;
import org.switchyard.policy.TransactionPolicy;
import org.switchyard.runtime.event.ExchangeCompletionEvent;
import org.switchyard.selector.OperationSelector;

/**
 * Abstract message endpoint class for JCA inflow.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public abstract class AbstractInflowEndpoint {

    private JCABindingModel _jcaBindingModel;
    private ServiceDomain _domain;
    private QName _serviceQName;
    private ServiceReference _serviceRef;
    private boolean _transacted = false;
    private ClassLoader _appClassLoader;
    private String _gatewayName;
    
    /**
     * initialize.
     */
    public void initialize() {
        _serviceRef = _domain.getServiceReference(_serviceQName);
    }
    
    /**
     * uninitialize.
     */
    public void uninitialize() {
    }
    
    /**
     * set {@link JCABindingModel}.
     * 
     * @param model {@link JCABindingModel}
     * @return {@link AbstractInflowEndpoint} to support method chaining
     */
    public AbstractInflowEndpoint setJCABindingModel(JCABindingModel model) {
        _jcaBindingModel = model;
        _gatewayName = model.getName();
        return this;
    }
    
    /**
     * get {@link JCABindingModel}.
     * 
     * @return {@link JCABindingModel}
     */
    public JCABindingModel getJCABindingModel() {
        return _jcaBindingModel;
    }
    
    /**
     * set ServiceDomain.
     * @param domain ServiceDomain
     * @return this instance
     */
    public AbstractInflowEndpoint setServiceDomain(ServiceDomain domain) {
        _domain = domain;
        return this;
    }
    
    /**
     * get ServiceDomain.
     * @return ServiceDomain
     */
    public ServiceDomain getServiceDomain() {
        return _domain;
    }

    /**
     * set service QName.
     * @param qname service QName
     * @return this instance
     */
    public AbstractInflowEndpoint setServiceQName(QName qname) {
        _serviceQName = qname;
        return this;
    }
    
    /**
     * get Service QName.
     * @return Service QName
     */
    public QName getServiceQName() {
        return _serviceQName;
    }

    /**
     * set service reference.
     * @param ref service reference
     * @return this instance
     */
    public AbstractInflowEndpoint setServiceReference(ServiceReference ref) {
        _serviceRef = ref;
        return this;
    }
    
    /**
     * get ServiceReference.
     * @return ServiceReference
     */
    public ServiceReference getServiceReference() {
        return _serviceRef;
    }

    /**
     * return whether the delivery is transacted or not.
     * 
     * @return true if transacted
     */
    public boolean isDeliveryTransacted() {
        return _transacted;
    }
    
    /**
     * set whether the delivery is transacted or not.
     * 
     * @param transacted true if transacted
     * @return this instance
     */
    public AbstractInflowEndpoint setDeliveryTransacted(boolean transacted) {
        _transacted = transacted;
        return this;
    }
    
    /**
     * set application class loader.
     * 
     * @param loader application class loader
     * @return this instance
     */
    public AbstractInflowEndpoint setApplicationClassLoader(ClassLoader loader) {
        _appClassLoader = loader;
        return this;
    }
    
    /**
     * get application class loader.
     * 
     * @return application class loader
     */
    public ClassLoader getApplicationClassLoader() {
        return _appClassLoader;
    }
    
    protected Exchange createExchange(String operation, ExchangeHandler handler) {
        if (_serviceRef == null) {
            throw JCAMessages.MESSAGES.initializeMustBeCalledBeforeExchange();
        }
        
        if (operation == null) {
            final Set<ServiceOperation> operations = _serviceRef.getInterface().getOperations();
            if (operations.size() != 1) {
                throw JCAMessages.MESSAGES.noOperationSelectorConfigured(operations.toString());
            }
            final ServiceOperation serviceOperation = operations.iterator().next();
            operation = serviceOperation.getName();
        }
        
        Exchange exchange = _serviceRef.createExchange(operation, handler);
        if (_transacted) {
            PolicyUtil.provide(exchange, TransactionPolicy.PROPAGATES_TRANSACTION);
            PolicyUtil.provide(exchange, TransactionPolicy.MANAGED_TRANSACTION_GLOBAL);
        }

        // identify ourselves
        exchange.getContext()
                .setProperty(ExchangeCompletionEvent.GATEWAY_NAME, _gatewayName, Scope.EXCHANGE)
                .addLabels(BehaviorLabel.TRANSIENT.label());

        return exchange;
        
    }
    
    protected Exchange createExchange(String operation) {
        return createExchange(operation, null);
    }
   
    protected <D extends JCABindingData> MessageComposer<D> getMessageComposer(Class<D> clazz) {
        return JCAComposition.getMessageComposer(_jcaBindingModel, clazz);
    }

    protected <D extends JCABindingData> OperationSelector<D> getOperationSelector(Class<D> clazz) {
        return OperationSelectorFactory.getOperationSelectorFactory(clazz)
                                        .newOperationSelector(_jcaBindingModel.getOperationSelector());
    }
}
