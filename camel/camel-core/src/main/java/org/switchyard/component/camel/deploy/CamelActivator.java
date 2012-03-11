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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import javax.naming.NamingException;
import javax.xml.namespace.QName;

import org.apache.camel.CamelContext;
import org.apache.camel.Message;
import org.apache.camel.impl.CompositeRegistry;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.model.FromDefinition;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.ToDefinition;
import org.apache.camel.spi.PackageScanClassResolver;
import org.apache.camel.spi.Registry;
import org.switchyard.ServiceDomain;
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
import org.switchyard.config.Configuration;
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.ComponentReferenceModel;
import org.switchyard.config.model.composite.ComponentServiceModel;
import org.switchyard.deploy.BaseActivator;
import org.switchyard.deploy.ServiceHandler;
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
    
    private CamelContext _camelContext;
    private Configuration _environment;
    
    /**
     * Creates a new activator for Camel endpoint types.
     */
    public CamelActivator() {
        super(new String[] {
                CAMEL_TYPE, 
                DIRECT_TYPE, 
                FILE_TYPE});
    }

    @Override
    public ServiceHandler activateBinding(QName serviceName, BindingModel config) {
        start();
        CamelBindingModel binding = (CamelBindingModel)config;
        binding.setEnvironment(_environment);

        if (binding.isServiceBinding()) {
            return new InboundHandler(binding, _camelContext, serviceName);
        } else {
            return createOutboundHandler(binding, binding.getReference().getQName());
        }
    }
    
    @Override
    public ServiceHandler activateService(QName serviceName, ComponentModel config) {
        start();
        ServiceHandler handler = null;
        
        // process service
        for (ComponentServiceModel service : config.getServices()) {
            if (service.getQName().equals(serviceName)) {
                handler = handleImplementation(service, serviceName);
                break;
            }
        }
        
        return handler;
    }
    
    
    @Override
    public void setServiceDomain(ServiceDomain serviceDomain) {
        super.setServiceDomain(serviceDomain);
        ServiceReferences.setDomain(serviceDomain);
    }

    /**
     * Starts the camel context for this activator instance.
     */
    public void start() {
        startCamelContext();
    }
    
    /**
     * Stops the camel context for this activator instance.
     */
    public void stop() {
        stopCamelContext();
    }

    @Override
    public void deactivateBinding(QName name, ServiceHandler handler) {
        // anything to do here?
    }

    @Override
    public void deactivateService(QName name, ServiceHandler handler) {
        // anything to do here?
    }

    private synchronized void startCamelContext() {
        if (_camelContext == null) {
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
    }

    private synchronized void stopCamelContext() {
        if (_camelContext != null) {
            try {
                _camelContext.stop();
                _camelContext = null;
            } catch (Exception ex) {
                throw new SwitchYardException("CamelActivator failed to stop CamelContext.", ex);
            }
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
    
    private Registry getRegistry() throws NamingException {
        final ServiceLoader<Registry> registriesLoaders = ServiceLoader.load(Registry.class, getClass().getClassLoader());
        final List<Registry> registries = new ArrayList<Registry>();
        registries.add(new JndiRegistry());

        for (Registry registry : registriesLoaders) {
            registries.add(registry);
        }
        
        return new CompositeRegistry(registries);
    }

    private ServiceHandler handleImplementation(final ComponentServiceModel config, final QName serviceName) {
        
        final CamelComponentImplementationModel ccim = 
                (CamelComponentImplementationModel)config.getComponent().getImplementation();
        try {
            final String endpointUri = ComponentNameComposer.composeComponentUri(serviceName);
            final RouteDefinition routeDef = getRouteDefinition(ccim);
            checkSwitchYardReferencedServiceExist(routeDef, ccim);
            addFromEndpointToRouteDefinition(routeDef, endpointUri);
            _camelContext.addRouteDefinition(routeDef);
            final SwitchyardEndpoint endpoint = (SwitchyardEndpoint) _camelContext.getEndpoint(endpointUri);
            endpoint.setMessageComposer(CamelComposition.getMessageComposer());
            final SwitchYardConsumer consumer = endpoint.getConsumer();
            return consumer;
        } catch (final Exception e) {
            throw new SwitchYardException(e.getMessage(), e);
        }
    }

    @SuppressWarnings("rawtypes")
    private void checkSwitchYardReferencedServiceExist(
            final RouteDefinition routeDef, 
            final CamelComponentImplementationModel ccim) {
        
        final List<ProcessorDefinition> outputs = routeDef.getOutputs();
        for (ProcessorDefinition processorDef : outputs) {
            if (processorDef instanceof ToDefinition) {
                final ToDefinition to = (ToDefinition) processorDef;
                final URI componentUri = URI.create(to.getUri());
                if (componentUri.getScheme().equals(ComponentNameComposer.SWITCHYARD_COMPONENT_NAME)) {
                    final String serviceName = componentUri.getHost();
                    final String namespace = ComponentNameComposer.getNamespaceFromURI(componentUri);
                    final QName refServiceName = new QName(namespace, serviceName);
                    if (!containsServiceRef(ccim.getComponent().getReferences(), serviceName)) {
                        throw new SwitchYardException("Could find the service reference for '" + serviceName + "'" 
                        + " which is referenced in " + to);
                    }
                    
                    final ServiceReference service = getServiceDomain().getServiceReference(refServiceName);
                    if (service == null) {
                        throw new SwitchYardException("Could find the service name '" + serviceName + "'" 
                        + " which is referenced in " + to);
                    }
                }
            }
        }
    }
    
    private boolean containsServiceRef(final List<ComponentReferenceModel> refs, final String serviceName) {
        for (ComponentReferenceModel refModel : refs) {
            if (refModel.getName().equals(serviceName)) {
                return true;
            }
        }
        return false;
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

    private ServiceHandler createOutboundHandler(final CamelBindingModel binding, final QName name) {
        final String endpointUri = binding.getComponentURI().toString();
        final MessageComposer<Message> messageComposer = CamelComposition.getMessageComposer(binding);
        return new OutboundHandler(endpointUri, _camelContext, messageComposer);
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

    /**
     * Set the Environment configuration for the activator.
     * @param config The global environment configuration.
     */
    public void setEnvironment(Configuration config) {
        _environment = config;
    }

}
