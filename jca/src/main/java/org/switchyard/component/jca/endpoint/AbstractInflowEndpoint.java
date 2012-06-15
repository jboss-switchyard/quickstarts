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

import javax.xml.namespace.QName;

import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.ServiceDomain;
import org.switchyard.ServiceReference;
import org.switchyard.component.common.composer.MessageComposer;
import org.switchyard.component.jca.composer.JCAComposition;
import org.switchyard.component.jca.config.model.JCABindingModel;
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
    private String _operationName;
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
     * set operation name.
     * @param name operation name
     * @return this instance
     */
    public AbstractInflowEndpoint setOperationName(String name) {
        _operationName = name;
        return this;
    }
    
    /**
     * get operation name.
     * @return String representation of operation name
     */
    public String getOperationName() {
        return _operationName;
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
    
    protected Exchange createExchange(ExchangeHandler handler) {
        if (_serviceRef == null) {
            throw new IllegalStateException("initialize() must be called before exchange.");
        }
        
        Exchange exchange = _serviceRef.createExchange(_operationName, handler);
        if (_transacted) {
            PolicyUtil.provide(exchange, TransactionPolicy.PROPAGATES_TRANSACTION);
        } else {
            PolicyUtil.provide(exchange, TransactionPolicy.SUSPENDS_TRANSACTION);
        }
        return exchange;
        
    }
    
    protected Exchange createExchange() {
        return createExchange(null);
    }
   
    protected <T> MessageComposer<T> getMessageComposer(Class<T> clazz) {
        return JCAComposition.getMessageComposer(clazz);
    }

}
