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
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.UnavailableException;
import javax.resource.spi.endpoint.MessageEndpoint;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.transaction.TransactionManager;
import javax.transaction.xa.XAResource;

import org.switchyard.component.jca.EndpointProxy;
import org.switchyard.component.jca.endpoint.AbstractInflowEndpoint;
import org.switchyard.deploy.BaseServiceHandler;
import org.switchyard.exception.SwitchYardException;

/**
 * An ExchangeHandler for JCA message inflow that create and activate an MessageEndpoint.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public class InboundHandler extends BaseServiceHandler implements MessageEndpointFactory {
    
    private final ResourceAdapter _resourceAdapter;
    private final ActivationSpec _activationSpec;
    private final Class<?> _interface;
    private final AbstractInflowEndpoint _delegate;
    private final TransactionManager _transactionManager;
    private final ClassLoader _appClassLoader;
    private final boolean _transacted;
    
    /**
     * Constructor.
     * 
     * @param metadata {@link JCAInflowDeploymentMetaData}
     */
    public InboundHandler(JCAInflowDeploymentMetaData metadata) {
        _interface = metadata.getListenerInterface();
        _resourceAdapter = metadata.getResourceAdapter();
        _activationSpec = metadata.getActivationSpec();
        _delegate = metadata.getMessageEndpoint();
        _transactionManager = metadata.getTransactionManager();
        _appClassLoader = metadata.getApplicationClassLoader();
        _transacted = metadata.isDeliveryTransacted();
    }
    
    /**
     * Activate JCA message inflow endpoint.
     */
    public void start() {
        _delegate.initialize();
        try {
            _resourceAdapter.endpointActivation(this, _activationSpec);
        } catch (ResourceException e) {
            throw new SwitchYardException(e);
        }
    }
    
    @Override
    public void stop() {
        _resourceAdapter.endpointDeactivation(this, _activationSpec);
        _delegate.uninitialize();
    }

    @Override
    public MessageEndpoint createEndpoint(XAResource xaResource, long timeout)
            throws UnavailableException {
        EndpointProxy handler = new EndpointProxy(this, _delegate, _transactionManager, xaResource, _appClassLoader);
        return (MessageEndpoint) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                                                        new Class<?>[] {_interface,MessageEndpoint.class},
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
        return _transacted;
    }

}
