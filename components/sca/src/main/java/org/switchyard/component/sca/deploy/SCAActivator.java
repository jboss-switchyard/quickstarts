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
package org.switchyard.component.sca.deploy;

import javax.xml.namespace.QName;

import org.infinispan.Cache;
import org.switchyard.component.sca.RemoteEndpointPublisher;
import org.switchyard.component.sca.SCAEndpoint;
import org.switchyard.component.sca.SCAInvoker;
import org.switchyard.component.sca.SCALogger;
import org.switchyard.component.sca.SCAMessages;
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.config.model.composite.SCABindingModel;
import org.switchyard.deploy.BaseActivator;
import org.switchyard.deploy.ServiceHandler;
import org.switchyard.remote.RemoteRegistry;
import org.switchyard.remote.infinispan.InfinispanRegistry;

/**
 * Activator for remote endpoints.
 *
 */
public class SCAActivator extends BaseActivator {
    
    static final String[] TYPES = new String[] {"sca"};

    private RemoteRegistry _registry;
    private RemoteEndpointPublisher _endpointPublisher;
    private boolean _disableRemoteTransaction = false;

    /**
     * Create a new RemoteActivator.
     * @param cache Infinispan cache
     */
    public SCAActivator(Cache<String, String> cache) {
        super(TYPES);
        // Configure a registry client to use the specified cache
        if (cache != null) {
            _registry = new InfinispanRegistry(cache);
        }
    }
    
    @Override
    public ServiceHandler activateBinding(QName name, BindingModel config) {
        _endpointPublisher.setDisableRemoteTransaction(_disableRemoteTransaction);

        // Signal the remote endpoint publisher to start. Multiple calls to start are harmless.
        try {
            // Note that stop() occurs as part of the SCAComponent lifecycle.
            _endpointPublisher.start();
        } catch (Exception ex) {
            SCALogger.ROOT_LOGGER.failedToStartRemoteEndpointListenerForSCAEndpoints(ex);
        }
        
        SCABindingModel scab = (SCABindingModel)config;
        if (scab.isServiceBinding()) {
            return new SCAEndpoint(scab, super.getServiceDomain(), _endpointPublisher, _registry);
        } else {
            if ((scab.getTarget() == null) && (scab.getTargetNamespace() == null)) {
                throw SCAMessages.MESSAGES.invalidSCABindingForReferenceTargetServiceOrNamespaceMustBeSpecified();
            }
            return new SCAInvoker(scab, _registry)
                        .setDisableRemoteTransaction(_disableRemoteTransaction);
        }
    }

    @Override
    public void deactivateBinding(QName name, ServiceHandler handler) {
        // Nothing to do here
    }
    
    /**
     * Set the endpoint publisher used by this activator.
     * @param endpointPublisher endpoint publisher
     */
    public void setEndpointPublisher(RemoteEndpointPublisher endpointPublisher) {
        _endpointPublisher = endpointPublisher;
    }
    
    /**
     * Get the endpoint publisher used by this activator.
     * @return endpoint publisher
     */
    public RemoteEndpointPublisher getEndpointPublisher() {
        return _endpointPublisher;
    }
    
    /**
     * Set disalbe remote transaction.
     * @param disable if true, disable remote transaction
     */
    public void setDisableRemoteTransaction(boolean disable) {
        _disableRemoteTransaction = disable;
    }
}
