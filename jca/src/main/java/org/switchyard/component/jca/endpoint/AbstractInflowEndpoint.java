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
package org.switchyard.component.jca.endpoint;

import java.util.Set;

import javax.xml.namespace.QName;

import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.ServiceDomain;
import org.switchyard.ServiceReference;
import org.switchyard.component.common.composer.MessageComposer;
import org.switchyard.component.jca.composer.JCABindingData;
import org.switchyard.component.common.selector.OperationSelector;
import org.switchyard.component.common.selector.OperationSelectorFactory;
import org.switchyard.component.jca.composer.JCAComposition;
import org.switchyard.component.jca.config.model.JCABindingModel;
import org.switchyard.exception.SwitchYardException;
import org.switchyard.metadata.ServiceOperation;
import org.switchyard.policy.PolicyUtil;
import org.switchyard.policy.TransactionPolicy;

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
            throw new IllegalStateException("initialize() must be called before exchange.");
        }
        
        if (operation == null) {
            final Set<ServiceOperation> operations = _serviceRef.getInterface().getOperations();
            if (operations.size() != 1) {
                final StringBuilder msg = new StringBuilder();
                msg.append("No operationSelector was configured for the JCA Component and the Service Interface ");
                msg.append("contains more than one operation: ").append(operations);
                msg.append("Please add an operationSelector element.");
                throw new SwitchYardException(msg.toString());
            }
            final ServiceOperation serviceOperation = operations.iterator().next();
            operation = serviceOperation.getName();
        }
        
        Exchange exchange = _serviceRef.createExchange(operation, handler);
        if (_transacted) {
            PolicyUtil.provide(exchange, TransactionPolicy.PROPAGATES_TRANSACTION);
            PolicyUtil.provide(exchange, TransactionPolicy.MANAGED_TRANSACTION_GLOBAL);
        }
        return exchange;
        
    }
    
    protected Exchange createExchange(String operation) {
        return createExchange(operation, null);
    }
   
    protected <D extends JCABindingData> MessageComposer<D> getMessageComposer(Class<D> clazz) {
        return JCAComposition.getMessageComposer(clazz);
    }

    protected <D extends JCABindingData> OperationSelector<D> getOperationSelector(Class<D> clazz) {
        return OperationSelectorFactory.getOperationSelectorFactory(clazz)
                                        .newOperationSelector(_jcaBindingModel.getOperationSelector());
    }
}
