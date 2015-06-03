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
package org.switchyard.component.jca.deploy;

import javax.resource.spi.ActivationSpec;
import javax.resource.spi.ResourceAdapter;
import javax.transaction.TransactionManager;

import org.switchyard.component.jca.endpoint.AbstractInflowEndpoint;

/**
 * JCA inflow deployment metadata.
 *
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
public class JCAInflowDeploymentMetaData {

    private Class<?> _listenerInterface = null;
    
    private AbstractInflowEndpoint _messageEndpoint = null;
    
    private ResourceAdapter _resourceAdapter = null;
    
    private ActivationSpec _activationSpec = null;
    
    private ClassLoader _applicationClassLoader = null;

    private TransactionManager _transactionManager = null;

    private boolean _transacted = false;
    
    private boolean _useBatchCommit = false;
    
    private int _batchSize = 0;
    
    private long _batchTimeout = 0;
    
    /**
     * get listener interface.
     * 
     * @return listener interface
     */
    public Class<?> getListenerInterface() {
        return _listenerInterface;
    }

    /**
     * set listener interface.
     * 
     * @param listenerInterface listener interface
     * @return {@link JCAInflowDeploymentMetaData} to support method chaining
     */
    public JCAInflowDeploymentMetaData setListenerInterface(Class<?> listenerInterface) {
        this._listenerInterface = listenerInterface;
        return this;
    }

    /**
     * get message endpoint.
     * 
     * @return concrete subclass of {@link AbstractInflowEndpoint}
     */
    public AbstractInflowEndpoint getMessageEndpoint() {
        return _messageEndpoint;
    }

    /**
     * set message endpoint.
     * 
     * @param messageEndpoint concrete subclass of {@link AbstractInflowEndpoint} to set
     * @return {@link JCAInflowDeploymentMetaData} to support method chaining
     */
    public JCAInflowDeploymentMetaData setMessageEndpoint(AbstractInflowEndpoint messageEndpoint) {
        this._messageEndpoint = messageEndpoint;
        return this;
    }

    /**
     * get {@link ResourceAdapter}.
     * 
     * @return [@link ResourceAdapter}
     */
    public ResourceAdapter getResourceAdapter() {
        return _resourceAdapter;
    }

    /**
     * set {@link ResourceAdapter}.
     * 
     * @param resourceAdapter {@link ResourceAdapter} to set
     * @return {@link JCAInflowDeploymentMetaData} to support method chaining
     */
    public JCAInflowDeploymentMetaData setResourceAdapter(ResourceAdapter resourceAdapter) {
        this._resourceAdapter = resourceAdapter;
        return this;
    }

    /**
     * get {@link ActivationSpec}.
     * 
     * @return {@link ActivationSpec}
     */
    public ActivationSpec getActivationSpec() {
        return _activationSpec;
    }

    /**
     * set {@link ActivationSpec}.
     * 
     * @param activationSpec {@link ActivationSpec} to set.
     * @return {@link JCAInflowDeploymentMetaData} to support method chaining
     */
    public JCAInflowDeploymentMetaData setActivationSpec(ActivationSpec activationSpec) {
        this._activationSpec = activationSpec;
        return this;
    }

    /**
     * get application class loader.
     * 
     * @return application class loader
     */
    public ClassLoader getApplicationClassLoader() {
        return _applicationClassLoader;
    }

    /**
     * set application class loader.
     * 
     * @param applicationClassLoader {@link ClassLoader} to set
     * @return {@link JCAInflowDeploymentMetaData} to support method chaining
     */
    public JCAInflowDeploymentMetaData setApplicationClassLoader(ClassLoader applicationClassLoader) {
        this._applicationClassLoader = applicationClassLoader;
        return this;
    }

    /**
     * get {@link TransactionManager}.
     * 
     * @return {@link TransactionManager}
     */
    public TransactionManager getTransactionManager() {
        return _transactionManager;
    }

    /**
     * set {@link TransactionManager}.
     * 
     * @param transactionManager {@link TransactionManager} to set
     * @return {@link JCAInflowDeploymentMetaData} to support method chaining
     */
    public JCAInflowDeploymentMetaData setTransactionManager(TransactionManager transactionManager) {
        this._transactionManager = transactionManager;
        return this;
    }

    /**
     * get is delivery transacted.
     * 
     * @return true if transacted 
     */
    public boolean isDeliveryTransacted() {
        return _transacted;
    }

    /**
     * set delivery transacted.
     * 
     * @param transacted true if transacted
     * @return {@link JCAInflowDeploymentMetaData} to support method chaining
     */
    public JCAInflowDeploymentMetaData setDeliveryTransacted(boolean transacted) {
        _transacted = transacted;
        return this;
    }
    
    /**
     * get if bacth commit is enabled.
     * @return true if bactch commit is enabled
     */
    public boolean useBatchCommit() {
        return _useBatchCommit;
    }

    /**
     * set if batch commit is enabled.
     * @param useBatchCommit true if batch commit should be enabled
     */
    public void setUseBatchCommit(boolean useBatchCommit) {
        this._useBatchCommit = useBatchCommit;
    }

    /**
     * get batch size.
     * @return batch size
     */
    public int getBatchSize() {
        return _batchSize;
    }

    /**
     * set batch size.
     * @param batchSize batch size
     */
    public void setBatchSize(int batchSize) {
        this._batchSize = batchSize;
    }

    /**
     * get batch timeout.
     * @return batch timeout in milliseconds
     */
    public long getBatchTimeout() {
        return _batchTimeout;
    }

    /**
     * set batch timeout.
     * @param batchTimeout batch timeout in milliseconds 
     */
    public void setBatchTimeout(long batchTimeout) {
        this._batchTimeout = batchTimeout;
    }

}
