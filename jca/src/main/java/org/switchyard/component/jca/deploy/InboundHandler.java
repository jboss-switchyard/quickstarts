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
