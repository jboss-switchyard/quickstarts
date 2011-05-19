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
import java.util.ServiceLoader;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.FromDefinition;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.spi.PackageScanClassResolver;
import org.switchyard.ExchangeHandler;
import org.switchyard.ServiceReference;
import org.switchyard.camel.component.SwitchYardConsumer;
import org.switchyard.camel.component.SwitchyardEndpoint;
import org.switchyard.component.camel.InboundHandler;
import org.switchyard.component.camel.OutboundHandler;
import org.switchyard.component.camel.RouteFactory;
import org.switchyard.component.camel.config.model.CamelBindingModel;
import org.switchyard.component.camel.config.model.CamelComponentImplementationModel;
import org.switchyard.config.model.Model;
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.config.model.composite.ComponentImplementationModel;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.ComponentReferenceModel;
import org.switchyard.config.model.composite.ComponentServiceModel;
import org.switchyard.config.model.composite.CompositeReferenceModel;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.deploy.BaseActivator;

/**
 * Activates Camel bindings, references and implementations in SwitchYard. 
 * 
 * @author Daniel Bevenius
 *
 */
public class CamelActivator extends BaseActivator {
    
    private static final String CAMEL_TYPE = "camel";
    private static final String DIRECT_TYPE = "direct";
    private static final String FILE_TYPE = "file";
    
    private Map<QName, Set<InboundHandler>> _bindings = new HashMap<QName, Set<InboundHandler>>();
    private Map<QName, Set<OutboundHandler>> _references = new HashMap<QName, Set<OutboundHandler>>();
    private Map<QName, SwitchYardConsumer> _implementations = new HashMap<QName, SwitchYardConsumer>();
    
    private static ThreadLocal<CamelContext> _camelContext = new ThreadLocal<CamelContext>();
    
    private PackageScanClassResolver _packageScanClassResolver;
    
    /**
     * Creates a new activator for Camel endpoint types.
     */
    public CamelActivator() {
        super(new String[] {
                CAMEL_TYPE, 
                DIRECT_TYPE, 
                FILE_TYPE});
        final ServiceLoader<PackageScanClassResolver> resolverLoaders = ServiceLoader.load(PackageScanClassResolver.class);
        for (PackageScanClassResolver packageScanClassResolver : resolverLoaders) {
            _packageScanClassResolver = packageScanClassResolver;
        }
    }
    
    /**
     * @param serviceName The service name
     * @param config The Java Object model representing a service configuration
     * @return {@link ExchangeHandler} the exchange handler.
     */
    @Override
    public ExchangeHandler init(final QName serviceName, final Model config) {
        startCamelContext();
        
        if (isServiceBinding(config)) {
            return handleServiceBindings((CompositeServiceModel)config, serviceName);
        }
        
        if (isReferenceBinding(config)) {
            return handleReferenceBindings((CompositeReferenceModel)config, serviceName);
        }
        
        if (isComponentService(config)) {
            return handleImplementation(config, serviceName);
        } 
        
        if (isComponentReference(config)) {
            return handleComponentReference(config, serviceName);
        }
            
        throw new RuntimeException("No Camel bindings, references or implementations found for [" + serviceName + "] in config [" + config + "]");
    }
    
    private ExchangeHandler handleComponentReference(final Model config, final QName serviceName) {
        return addOutboundHandler(serviceName, ComponentNameComposer.composeComponentUri(serviceName));
    }

    private boolean isComponentReference(final Model config) {
        return config instanceof ComponentReferenceModel;
    }

    private ExchangeHandler handleImplementation(final Model config, final QName serviceName) {
        final ComponentImplementationModel implementation = getComponentImplementationModel(config);
        if (implementation instanceof CamelComponentImplementationModel) {
            final CamelComponentImplementationModel ccim = (CamelComponentImplementationModel) implementation;
            
            try {
                final String endpointUri = ComponentNameComposer.composeComponentUri(serviceName);
                final RouteDefinition routeDef = getRouteDefinition(ccim, serviceName);
                addFromEndpointToRouteDefinition(routeDef, endpointUri);
                final CamelContext camelContext = _camelContext.get();
                camelContext.addRouteDefinition(routeDef);
                final SwitchyardEndpoint endpoint = (SwitchyardEndpoint) camelContext.getEndpoint(endpointUri);
                final SwitchYardConsumer consumer = endpoint.getConsumer();
                _implementations.put(serviceName, consumer);
                return consumer;
            } catch (final Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        return null;
    }
    
    private void addFromEndpointToRouteDefinition(final RouteDefinition rd, final String fromEndpointUri) throws Exception {
        final List<FromDefinition> inputs = rd.getInputs();
        if (!inputs.isEmpty()) {
            throw new RuntimeException("A Route must not define any 'from' endpoints as the 'from' endpoint will be created by SwithYard");
        }
        inputs.add(new FromDefinition(fromEndpointUri));
    }
    
    /**
     * There are two options for Camel implementation : Spring XML or Java DSL.
     * This method figures out which one were dealing with and returns the 
     * corresponding RouteDefinition.
     */
    private RouteDefinition getRouteDefinition(CamelComponentImplementationModel model, QName serviceName) {
        if (model.getRoute() != null) {
            return model.getRoute();
        } else {
            return RouteFactory.createRoute(model.getJavaClass(), serviceName);
        }
    }
    
    private boolean isComponentService(final Model config) {
        return config instanceof ComponentServiceModel;
    }

    private boolean isReferenceBinding(final Model config) {
        return config instanceof CompositeReferenceModel;
    }

    private boolean isServiceBinding(final Model config) {
        return config instanceof CompositeServiceModel;
    }

    private ExchangeHandler handleReferenceBindings(final CompositeReferenceModel config, final QName serviceName) {
        final List<BindingModel> bindings = config.getBindings();
        if (!bindings.isEmpty()) {
            return createOutboundHandler(bindings, serviceName);
        }
            
        return null;
    }

    private ExchangeHandler handleServiceBindings(final CompositeServiceModel serviceModel, final QName serviceName) {
        final List<BindingModel> bindings = serviceModel.getBindings();
        if (!bindings.isEmpty()) {
            return createInboundHandler(bindings, serviceName);
        }
            
        return null;
    }

    private ComponentImplementationModel getComponentImplementationModel(final Model config) {
        final Model modelParent = ((ComponentServiceModel)config).getModelParent();
        final ComponentModel componentModel = (ComponentModel) modelParent;
        return componentModel.getImplementation();
    }
    
    private void startCamelContext() {
        if (getCamelContext() == null) {
            setCamelContext(new DefaultCamelContext());
        }
        try {
            final CamelContext camelContext = getCamelContext();
            if (_packageScanClassResolver != null) {
                camelContext.setPackageScanClassResolver(_packageScanClassResolver);
            }
            camelContext.start();
        } catch (Exception e1) {
            throw new IllegalStateException(e1);
        }
    }
    
    private InboundHandler createInboundHandler(final List<BindingModel> bindings, final QName name) {
        for (BindingModel bindingModel : bindings) {
            if (isCamelBindingModel(bindingModel)) {
                final CamelBindingModel camelBindingModel = (CamelBindingModel) bindingModel;
                try {
                    final Set<InboundHandler> handlers = getInboundHandlersForService(name);
                    final InboundHandler inboundHandler = new InboundHandler(camelBindingModel, _camelContext.get());
                    if (!handlers.contains(inboundHandler)) {
                        handlers.add(inboundHandler);
                        _bindings.put(name, handlers);
                        return inboundHandler;
                    }
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
        }
        return null;
    }
    
    private boolean isCamelBindingModel(final BindingModel bm) {
        return bm instanceof CamelBindingModel;
    }
    
    private OutboundHandler createOutboundHandler(final List<BindingModel> bindings, final QName name) {
        for (BindingModel bindingModel : bindings) {
            if (isCamelBindingModel(bindingModel)) {
                final CamelBindingModel camelBinding = (CamelBindingModel) bindingModel;
                final String endpointUri = camelBinding.getComponentURI().toString();
                return addOutboundHandler(name, endpointUri);
            }
        }
        return null;
    }
    
    private OutboundHandler addOutboundHandler(final QName name, final String uri)  {
        try {
            final Set<OutboundHandler> handlers = getOutboundHandlersForService(name);
            final OutboundHandler outboundHandler = new OutboundHandler(uri, _camelContext.get());
            if (!handlers.contains(outboundHandler)) {
                handlers.add(outboundHandler);
                _references.put(name, handlers);
                return outboundHandler;
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return null;
    }
    
    private Set<OutboundHandler> getOutboundHandlersForService(final QName serviceName) {
        Set<OutboundHandler> handlers = _references.get(serviceName);
        if (handlers == null) {
            handlers = new HashSet<OutboundHandler>();
        }
        return handlers;
    }
    
    private Set<InboundHandler> getInboundHandlersForService(final QName serviceName) {
        Set<InboundHandler> handlers = _bindings.get(serviceName);
        if (handlers == null) {
            handlers = new HashSet<InboundHandler>();
        }
        return handlers;
    }
    
    @Override
    public void start(final ServiceReference serviceReference) {
        addServiceReference(serviceReference);
        startOutboundHandlers(serviceReference);
    }
    
    private void addServiceReference(final ServiceReference serviceReference) {
        final QName serviceName = serviceReference.getName();
        ServiceReferences.add(serviceName, serviceReference);
    }
    
    private void startOutboundHandlers(final ServiceReference serviceReference) {
        final Set<InboundHandler> handlers = _bindings.get(serviceReference.getName());
        if (handlers != null) {
            for (InboundHandler inboundHandler : handlers) {
                try {
                    inboundHandler.start(serviceReference);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    
    @Override
    public void stop(ServiceReference serviceReference) {
        final Set<InboundHandler> handlers = _bindings.get(serviceReference.getName());
        if (handlers != null) {
            for (InboundHandler inboundHandler : handlers) {
                try {
                    inboundHandler.stop(serviceReference);
                    _bindings.remove(serviceReference.getName());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        ServiceReferences.remove(serviceReference.getName());
    }

    @Override
    public void destroy(ServiceReference service) {
        ServiceReferences.clear();
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
