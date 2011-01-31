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
package org.switchyard.component.camel.deploy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.switchyard.ExchangeHandler;
import org.switchyard.ServiceReference;
import org.switchyard.camel.component.SwitchyardEndpointStrategy;
import org.switchyard.component.camel.InboundHandler;
import org.switchyard.component.camel.OutboundHandler;
import org.switchyard.component.camel.config.model.CamelBindingModel;
import org.switchyard.config.model.Model;
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.deploy.Activator;

/**
 * Activates Camel bindings and references in Switchyward. 
 * 
 * @author Daniel Bevenius
 *
 */
public class CamelActivator implements Activator {
    
    private Map<QName, Set<InboundHandler>> _inboundGateways = new HashMap<QName, Set<InboundHandler>>();
    
    @SuppressWarnings ("unused")
    private Map<QName, OutboundHandler> _outboundGateways = new HashMap<QName, OutboundHandler>();
    
    private static ThreadLocal<CamelContext> _camelContext = new ThreadLocal<CamelContext>();

    /**
     * @param name The service name
     * @param config The Java Object model representing a service configuration
     * @return {@link ExchangeHandler} the exchange handler.
     */
    @Override
    public ExchangeHandler init(QName name, Model config) {
        
        initializeCamelContext();
        startCamelContext();
        
        if (config instanceof CompositeServiceModel) {
            final CompositeServiceModel serviceModel = (CompositeServiceModel) config;
            final List<BindingModel> bindings = serviceModel.getBindings();
            if (!bindings.isEmpty()) {
                return createBindingHandler(bindings, name);
            }
        }
        
        throw new RuntimeException("No Camel bindings found for service " + name);
    }
    
    private void initializeCamelContext() {
        if (getCamelContext() == null) {
            setCamelContext(new DefaultCamelContext());
        }
    }
    
    private void startCamelContext() {
        try {
            CamelContext camelContext = getCamelContext();
            camelContext.start();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
    
    private InboundHandler createBindingHandler(List<BindingModel> bindings, QName name)
    {
        for (BindingModel bindingModel : bindings) {
            if (bindingModel instanceof CamelBindingModel) {
                final CamelBindingModel camelBindingModel = (CamelBindingModel) bindingModel;
                try {
                    final Set<InboundHandler> handlers = getInboundHandlersForService(name);
                    final InboundHandler inboundHandler = new InboundHandler(camelBindingModel, _camelContext.get());
                    if (handlers.contains(inboundHandler) == false) {
                        handlers.add(inboundHandler);
                        _inboundGateways.put(name, handlers);
				        return inboundHandler;
                    }
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
        }
        return null;
    }
    
    private Set<InboundHandler> getInboundHandlersForService(final QName serviceName)
    {
        Set<InboundHandler> handlers = _inboundGateways.get(serviceName);
        if (handlers == null) {
            handlers = new HashSet<InboundHandler>();
        }
        return handlers;
    }
    
    @Override
    public void start(final ServiceReference serviceReference) {
        makeServiceReferenceAvailableToCamelSwitchyardComponent(serviceReference);
        final Set<InboundHandler> handlers = _inboundGateways.get(serviceReference.getName());
        for (InboundHandler inboundHandler : handlers) {
            try {
                inboundHandler.start(serviceReference);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    private void makeServiceReferenceAvailableToCamelSwitchyardComponent(ServiceReference reference) {
        getCamelContext().addRegisterEndpointCallback(new SwitchyardEndpointStrategy(reference));
    }
    
    @Override
    public void stop(ServiceReference serviceReference) {
        final Set<InboundHandler> handlers = _inboundGateways.get(serviceReference.getName());
        if (handlers != null) {
	        for (InboundHandler inboundHandler : handlers) {
	            try {
	                inboundHandler.stop(serviceReference);
	                _inboundGateways.remove(serviceReference.getName());
	            } catch (Exception e) {
	                throw new RuntimeException(e);
	            }
	        }
        }
    }

    @Override
    public void destroy(ServiceReference service) {
    }

    /**
     * Gets the ThreadLocal {@link CamelContext}.
     * 
     * @return {@link CamelContext} The CamelContext associated with the current thread
     * or null if not CamelContext has been associated with the current thread.
     */
    public static CamelContext getCamelContext() {
        return _camelContext.get();
    }

    /**
     * Sets the CamelContext as a ThreadLocal object.
     * 
     * @param camelContext The {@link CamelContext} to associate with the current thread.
     */
    public static void setCamelContext(CamelContext camelContext) {
        _camelContext.set(camelContext);
    }

}
