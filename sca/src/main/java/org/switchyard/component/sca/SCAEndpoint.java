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
package org.switchyard.component.sca;

import java.util.List;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.switchyard.Service;
import org.switchyard.ServiceDomain;
import org.switchyard.config.model.composite.SCABindingModel;
import org.switchyard.deploy.BaseServiceHandler;
import org.switchyard.SwitchYardException;
import org.switchyard.remote.RemoteEndpoint;
import org.switchyard.remote.RemoteRegistry;

/**
 * Represents a service endpoint binding using <binding.sca>.
 */
public class SCAEndpoint extends BaseServiceHandler {
    
    private static Logger _log = Logger.getLogger(SCAEndpoint.class);
    
    private RemoteEndpointPublisher _endpointPublisher;
    private ServiceDomain _domain;
    private RemoteRegistry _registry;
    private RemoteEndpoint _endpoint;
    private SCABindingModel _bindingModel;

    /**
     * Create a new handler for binding.sca service endpoints.
     * @param bindingModel The configuration model.
     * @param domain service domain used for the service
     * @param endpointPublisher endpoint publisher
     * @param registry remote registry
     */
    public SCAEndpoint(SCABindingModel bindingModel, 
            ServiceDomain domain,
            RemoteEndpointPublisher endpointPublisher,
            RemoteRegistry registry) {
        super(domain);
        _bindingModel = bindingModel;
        _endpointPublisher = endpointPublisher;
        _domain = domain;
        _registry = registry;
    }
    
    @Override
    protected void doStart() {
        QName serviceName = _bindingModel.getService().getQName();
        _endpointPublisher.addService(serviceName, _domain);
        List<Service> services = _domain.getServices(serviceName);
        if (services.isEmpty()) {
            throw new SwitchYardException("Failed to resolve service in domain " + serviceName);
        }
        
        if (_bindingModel.isClustered()) {
            // The registry can be null in test environments or if the cache is misconfigured
            if (_registry != null) {
                _endpoint = RemoteEndpoint.fromService(services.get(0));
                _endpoint.setEndpoint(_endpointPublisher.getAddress());
                _registry.addEndpoint(_endpoint);
            } else {
                _log.warn("Cannot enable clustered SCA binding for " + serviceName
                        + ".  No distributed cache is avaialble.");
            }
        }
    }
    
    @Override
    protected void doStop() {
        if (_bindingModel.isClustered() && _registry != null) {
            _registry.removeEndpoint(_endpoint);
        }
        _endpointPublisher.removeService(_bindingModel.getService().getQName(), _domain);
    }
    
    /**
     * Get the EndpointPublisher instance used by this endpoint.
     * @return endpoint publisher
     */
    public RemoteEndpointPublisher getEndpointPublisher() {
        return _endpointPublisher;
    }
}
