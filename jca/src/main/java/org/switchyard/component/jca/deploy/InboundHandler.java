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

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.resource.ResourceException;
import javax.resource.spi.UnavailableException;
import javax.resource.spi.endpoint.MessageEndpoint;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.transaction.xa.XAResource;

import org.switchyard.ServiceDomain;
import org.switchyard.component.jca.EndpointProxy;
import org.switchyard.deploy.BaseServiceHandler;
import org.switchyard.SwitchYardException;

/**
 * An ExchangeHandler for JCA message inflow that create and activate an MessageEndpoint.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public class InboundHandler extends BaseServiceHandler implements MessageEndpointFactory {
    
    private final JCAInflowDeploymentMetaData _metadata;
    
    /**
     * Constructor.
     * 
     * @param metadata {@link JCAInflowDeploymentMetaData}
     * @param domain the service domain
     */
    public InboundHandler(JCAInflowDeploymentMetaData metadata, ServiceDomain domain) {
        super(domain);
        _metadata = metadata;
    }
    
    /**
     * Activate JCA message inflow endpoint.
     */
    @Override
    protected void doStart() {
        _metadata.getMessageEndpoint().initialize();
        try {
            _metadata.getResourceAdapter().endpointActivation(this, _metadata.getActivationSpec());
        } catch (ResourceException e) {
            throw new SwitchYardException(e);
        }
    }
    
    @Override
    protected void doStop() {
        _metadata.getResourceAdapter().endpointDeactivation(this, _metadata.getActivationSpec());
        _metadata.getMessageEndpoint().uninitialize();
    }

    @Override
    public MessageEndpoint createEndpoint(XAResource xaResource, long timeout)
            throws UnavailableException {
        EndpointProxy handler = new EndpointProxy(_metadata, this, xaResource);
        return (MessageEndpoint) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                                                        new Class<?>[] {_metadata.getListenerInterface(),MessageEndpoint.class},
                                                        handler);
    }

    @Override
    public MessageEndpoint createEndpoint(XAResource xaResource)
            throws UnavailableException {
        return createEndpoint(xaResource, 0);
    }

    @Override
    public boolean isDeliveryTransacted(Method arg0)
            throws NoSuchMethodException {
        return _metadata.isDeliveryTransacted();
    }

}
