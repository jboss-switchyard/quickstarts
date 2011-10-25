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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

import javax.naming.NamingException;
import javax.xml.namespace.QName;

import org.apache.camel.CamelContext;
import org.apache.camel.Message;
import org.apache.camel.impl.CompositeRegistry;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.model.FromDefinition;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.spi.PackageScanClassResolver;
import org.apache.camel.spi.Registry;
import org.switchyard.ExchangeHandler;
import org.switchyard.ServiceReference;
import org.switchyard.component.camel.InboundHandler;
import org.switchyard.component.camel.OutboundHandler;
import org.switchyard.component.camel.RouteFactory;
import org.switchyard.component.camel.SwitchYardConsumer;
import org.switchyard.component.camel.SwitchyardEndpoint;
import org.switchyard.component.camel.composer.CamelComposition;
import org.switchyard.component.camel.config.model.CamelBindingModel;
import org.switchyard.component.camel.config.model.CamelComponentImplementationModel;
import org.switchyard.composer.MessageComposer;
import org.switchyard.config.model.Model;
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.config.model.composite.ComponentImplementationModel;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.ComponentReferenceModel;
import org.switchyard.config.model.composite.ComponentServiceModel;
import org.switchyard.config.model.composite.CompositeReferenceModel;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.deploy.BaseActivator;
import org.switchyard.exception.SwitchYardException;

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
    
    private CamelContext _camelContext;
    
    /**
     * Creates a new activator for Camel endpoint types.
     */
    public CamelActivator() {
        super(new String[] {
                CAMEL_TYPE, 
                DIRECT_TYPE, 
                FILE_TYPE});
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
            return handleImplementation((ComponentServiceModel)config, serviceName);
        }

        if (isComponentReference(config)) {
            return handleComponentReference((ComponentReferenceModel)config, serviceName);
        }

        throw new SwitchYardException("No Camel bindings, references or implementations found for [" + serviceName + "] in config [" + config + "]");
    }

    private void startCamelContext() {
        try {
            _camelContext =  new DefaultCamelContext(getRegistry());
        
            final PackageScanClassResolver packageScanClassResolver = getPackageScanClassResolver();
            if (packageScanClassResolver != null) {
                _camelContext.setPackageScanClassResolver(packageScanClassResolver);
            }
            _camelContext.start();
        } catch (final Exception e) {
            throw new SwitchYardException(e);
        }
    }
    
    /**
     * Get the first PackageScanClassResolver Service found on the classpath.
     * @return The first PackageScanClassResolver Service found on the classpath.
     */
    public static PackageScanClassResolver getPackageScanClassResolver() {
        final ServiceLoader<PackageScanClassResolver> resolverLoaders = ServiceLoader.load(PackageScanClassResolver.class, CamelActivator.class.getClassLoader());
    
        for (PackageScanClassResolver packageScanClassResolver : resolverLoaders) {
            return packageScanClassResolver;
        }
    
        return null;
    }

    private ExchangeHandler handleComponentReference(final ComponentReferenceModel config, final QName serviceName) {
        final MessageComposer<Message> messageComposer = CamelComposition.getMessageComposer();
        return addOutboundHandler(serviceName, ComponentNameComposer.composeComponentUri(serviceName), messageComposer);
    }
    
    private Registry getRegistry() throws NamingException {
        final ServiceLoader<Registry> registriesLoaders = ServiceLoader.load(Registry.class, getClass().getClassLoader());
        final List<Registry> registries = new ArrayList<Registry>();
        registries.add(new JndiRegistry());

        for (Registry registry : registriesLoaders) {
            registries.add(registry);
        }
        
        return new CompositeRegistry(registries);
    }

    private boolean isComponentReference(final Model config) {
        return config instanceof ComponentReferenceModel;
    }

    private ExchangeHandler handleImplementation(final ComponentServiceModel config, final QName serviceName) {
        final ComponentImplementationModel implementation = getComponentImplementationModel(config);
        if (implementation instanceof CamelComponentImplementationModel) {
            final CamelComponentImplementationModel ccim = (CamelComponentImplementationModel) implementation;
            try {
                final String endpointUri = ComponentNameComposer.composeComponentUri(serviceName);
                final RouteDefinition routeDef = getRouteDefinition(ccim);
                addFromEndpointToRouteDefinition(routeDef, endpointUri);
                _camelContext.addRouteDefinition(routeDef);
                final SwitchyardEndpoint endpoint = (SwitchyardEndpoint) _camelContext.getEndpoint(endpointUri);
                endpoint.setMessageComposer(CamelComposition.getMessageComposer());
                final SwitchYardConsumer consumer = endpoint.getConsumer();
                _implementations.put(serviceName, consumer);
                return consumer;
            } catch (final Exception e) {
                throw new SwitchYardException(e.getMessage(), e);
            }
        }
        return null;
    }

    private void addFromEndpointToRouteDefinition(final RouteDefinition rd, final String fromEndpointUri) throws Exception {
        final List<FromDefinition> inputs = rd.getInputs();

        // Make sure the route starts with a single switchyard:// endpoint
        if (inputs.size() == 0) {
            inputs.add(new FromDefinition(fromEndpointUri));
        } else if (inputs.size() == 1) {
            String routeURI = inputs.get(0).getUri();
            if (!fromEndpointUri.equals(routeURI)) {
                throw new SwitchYardException("Endpoint URI on route " + routeURI
                        + " does not match expected URI : " + fromEndpointUri);
            }
        } else {
            throw new SwitchYardException("A route can only have one 'from' endpoint");
        }

    }

    /**
     * There are two options for Camel implementation : Spring XML or Java DSL.
     * This method figures out which one were dealing with and returns the
     * corresponding RouteDefinition.
     */
    private RouteDefinition getRouteDefinition(CamelComponentImplementationModel model) {
        RouteDefinition routeDef = model.getRoute();
        if (routeDef == null) {
            routeDef = RouteFactory.createRoute(model.getJavaClass(), model.getComponent().getTargetNamespace());
        }
        return routeDef;
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
        final Model modelParent = config.getModelParent();
        final ComponentModel componentModel = (ComponentModel) modelParent;
        return componentModel.getImplementation();
    }

    private InboundHandler createInboundHandler(final List<BindingModel> bindings, final QName name) {
        for (BindingModel bindingModel : bindings) {
            if (isCamelBindingModel(bindingModel)) {
                final CamelBindingModel camelBindingModel = (CamelBindingModel) bindingModel;
                try {
                    final Set<InboundHandler> handlers = getInboundHandlersForService(name);
                    final InboundHandler inboundHandler = new InboundHandler(camelBindingModel, _camelContext, name);
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
                final MessageComposer<Message> messageComposer = CamelComposition.getMessageComposer(bindingModel);
                return addOutboundHandler(name, endpointUri, messageComposer);
            }
        }
        return null;
    }

    private OutboundHandler addOutboundHandler(final QName name, final String uri, MessageComposer<Message> messageComposer) {
        try {
            final Set<OutboundHandler> handlers = getOutboundHandlersForService(name);
            final OutboundHandler outboundHandler = new OutboundHandler(uri, _camelContext, messageComposer);
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
        ServiceReferences.add(serviceReference.getName(), serviceReference);
        startInboundHandlers(serviceReference);
    }

    @Override
    public void stop(ServiceReference serviceReference) {
        stopInboundHandlers(serviceReference);
        ServiceReferences.remove(serviceReference.getName());
    }

    private void startInboundHandlers(final ServiceReference serviceReference) {
        final Set<InboundHandler> handlers = _bindings.get(serviceReference.getName());
        if (handlers != null) {
            for (InboundHandler inboundHandler : handlers) {
                try {
                    inboundHandler.start(serviceReference);
                } catch (Exception e) {
                    throw new SwitchYardException(e);
                }
            }
        }
    }

    @Override
    public void destroy(final ServiceReference service) {
        _bindings.remove(service.getName());
        stopCamelContext();
    }

    private void stopInboundHandlers(ServiceReference serviceReference) {
        final Set<InboundHandler> handlers = _bindings.get(serviceReference.getName());
        if (handlers != null) {
            for (InboundHandler inboundHandler : handlers) {
                try {
                    inboundHandler.stop(serviceReference);
                } catch (Exception e) {
                    throw new SwitchYardException(e);
                }
            }
        }
    }

    private void stopCamelContext() {
        try {
            _camelContext.stop();
        } catch (final Exception e) {
            throw new SwitchYardException(e);
        }
    }
    
    /**
     * Set the {@link CamelContext} to be used by this Activator.
     * 
     * @param camelContext the CamelContext.
     */
    public void setCamelContext(final CamelContext camelContext) {
        _camelContext = camelContext;
    }
    
    /**
     * Gets the {@link CamelContext} used by this Activator.
     * 
     * @return CamelContext the {@link CamelContext} used by this Activator.
     */
    public CamelContext getCamelContext() {
        return _camelContext;
    }

}
