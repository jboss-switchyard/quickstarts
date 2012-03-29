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
package org.switchyard.component.jca.deploy;

import javax.resource.spi.ActivationSpec;
import javax.resource.spi.ResourceAdapter;
import javax.transaction.TransactionManager;

import org.switchyard.component.jca.endpoint.AbstractInflowEndpoint;

/**
 * JCA deployment metadata.
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
}
