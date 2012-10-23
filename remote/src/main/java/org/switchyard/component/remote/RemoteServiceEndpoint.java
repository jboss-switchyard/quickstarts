/*
 * JBoss, Home of Professional Open Source Copyright 2009, Red Hat Middleware
 * LLC, and individual contributors by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.switchyard.component.remote;

import java.util.List;

import javax.xml.namespace.QName;

import org.switchyard.Service;
import org.switchyard.ServiceDomain;
import org.switchyard.component.remote.config.model.RemoteBindingModel;
import org.switchyard.deploy.BaseServiceHandler;
import org.switchyard.exception.SwitchYardException;
import org.switchyard.remote.RemoteRegistry;
import org.switchyard.remote.RemoteEndpoint;

/**
 * An ExchangeHandler that can speak to remote clustered SY endpoints.
 */
public class RemoteServiceEndpoint extends BaseServiceHandler {
    
    private RemoteBindingModel _bindingModel;
    private RemoteEndpointPublisher _endpointPublisher;
    private ServiceDomain _domain;
    private RemoteRegistry _registry;
    private RemoteEndpoint _endpoint;

    /**
     * Create a new RemoteBinding handler.
     * @param bindingModel The configuration model.
     * @param domain service domain used for the service
     * @param endpointPublisher endpoint publisher
     * @param registry remote registry
     */
    public RemoteServiceEndpoint(RemoteBindingModel bindingModel, 
            ServiceDomain domain,
            RemoteEndpointPublisher endpointPublisher,
            RemoteRegistry registry) {
        _bindingModel = bindingModel;
        _endpointPublisher = endpointPublisher;
        _domain = domain;
        _registry = registry;
    }
    
    @Override
    public void start() {
        QName serviceName = _bindingModel.getService().getQName();
        _endpointPublisher.addService(serviceName, _domain);
        List<Service> services = _domain.getServices(serviceName);
        if (services.isEmpty()) {
            throw new SwitchYardException("Failed to resolve service in domain " + serviceName);
        }
        _endpoint = RemoteEndpoint.fromService(services.get(0));
        _endpoint.setEndpoint(_endpointPublisher.getAddress());
        _registry.addEndpoint(_endpoint);
    }
    
    @Override
    public void stop() {
        _registry.removeEndpoint(_endpoint);
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
