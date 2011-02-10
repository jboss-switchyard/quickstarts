/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.switchyard.deploy.internal;

import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import org.apache.log4j.Logger;
import org.switchyard.ExchangeHandler;
import org.switchyard.Service;
import org.switchyard.ServiceDomain;
import org.switchyard.config.model.ModelResource;
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.ComponentReferenceModel;
import org.switchyard.config.model.composite.ComponentServiceModel;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.deploy.Activator;
import org.switchyard.internal.DefaultEndpointProvider;
import org.switchyard.internal.DefaultServiceRegistry;
import org.switchyard.internal.DomainImpl;
import org.switchyard.internal.transform.BaseTransformerRegistry;
import org.switchyard.metadata.ServiceInterface;
import org.switchyard.metadata.java.JavaService;
import org.switchyard.spi.EndpointProvider;
import org.switchyard.spi.ServiceRegistry;

/**
 * Deployment is a framework-independent representation of a deployed SwitchYard 
 * application.  At this point, a deployment is 1:1 with a SwitchYard domain, but
 * this will change to allow multiple deployments to participate in a single, 
 * shared domain.
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class Deployment {
    
    /**
     * Activator class for implementation.bean.  This really needs to go in a config file.
     */
    private static final String BEAN_ACTIVATOR_CLASS = 
        "org.switchyard.component.bean.deploy.BeanComponentActivator";
    /**
     * Activator class for binding.soap.  This really needs to go in a config file.
     */
    private static final String SOAP_ACTIVATOR_CLASS = 
        "org.switchyard.component.soap.deploy.SOAPActivator";
    
    /**
     * Interface type used by a Java interface, e.g. "interface.java"
     */
    private static final String JAVA_INTERFACE = "java";

    /**
     * Root domain property.
     */
    public static final String ROOT_DOMAIN = "org.switchyard.domains.root";
    /**
     * Endpoint provider class name key.
     */
    public static final String ENDPOINT_PROVIDER_CLASS_NAME
        = "org.switchyard.endpoint.provider.class.name";
    /**
     * Registry class name property.
     */
    public static final String REGISTRY_CLASS_NAME
        = "org.switchyard.registry.class.name";

    private static Logger _log = Logger.getLogger(Deployment.class);

    private SwitchYardModel _switchyardConfig;
    private ServiceDomain _serviceDomain;
    private Map<String, Activator> _componentActivators = 
        new HashMap<String, Activator>();
    private Map<String, Activator> _gatewayActivators = 
        new HashMap<String, Activator>();
    private List<Activation> _services = new LinkedList<Activation>();
    private List<Activation> _serviceBindings = new LinkedList<Activation>();
    private List<Activation> _references = new LinkedList<Activation>();
    private List<Activation> _referenceBindings = new LinkedList<Activation>();

    /**
     * Create a new instance of Deployer.
     */
    public Deployment() {
    }

    /**
     * Initialize the deployment.  This actually starts everything as well at 
     * the moment.
     * @param switchyardConfig switchyard app descriptor
     */
    public void init(InputStream switchyardConfig) {
        try {
            // parse the config
            _switchyardConfig = (SwitchYardModel)new ModelResource().pull(switchyardConfig);
            _log.info("Initializing deployment for application " + _switchyardConfig.getName());
            // create a new domain and load activator instances for lifecycle
            createDomain();
            createActivators();
            // ordered startup lifecycle
            deployReferenceBindings();
            deployServices();
            deployReferences();
            deployServiceBindings();
        } catch (java.io.IOException ioEx) {
            throw new RuntimeException("Failed to read switchyard config.", ioEx);
        }
    }

    /**
     * Tear everything down.
     */
    public void destroy() {
        _log.info("Destroying deployment for application " + _switchyardConfig.getName());
        undeployServiceBindings();
        undeployServices();
        undeployReferences();
        undeployReferenceBindings();
        destroyDomain();
        
        // Clean up our list of activations, just in case something's left
        _services.clear();
        _serviceBindings.clear();
        _references.clear();
        _referenceBindings.clear();
    }
    
    /**
     * Used in unit tests to verify state in domain.
     */
    ServiceDomain getDomain() {
        return _serviceDomain;
    }

    private void createDomain() {
        String registryClassName = System.getProperty(REGISTRY_CLASS_NAME, DefaultServiceRegistry.class.getName());
        String endpointProviderClassName = System.getProperty(ENDPOINT_PROVIDER_CLASS_NAME, DefaultEndpointProvider.class.getName());

        try {
            ServiceRegistry registry = getRegistry(registryClassName);
            EndpointProvider endpointProvider = getEndpointProvider(endpointProviderClassName);
            BaseTransformerRegistry transformerRegistry = new BaseTransformerRegistry();

            _serviceDomain = new DomainImpl(ROOT_DOMAIN, registry, endpointProvider, transformerRegistry);
        } catch (NullPointerException npe) {
            throw new RuntimeException(npe);
        }

    }

    private void createActivators() {
        try {
            _componentActivators.put(
                    "bean", 
                    (Activator)loadClass(BEAN_ACTIVATOR_CLASS).newInstance());
            
            _gatewayActivators.put(
                    "soap", 
                    (Activator)loadClass(SOAP_ACTIVATOR_CLASS).newInstance());
        } catch (Exception ex) {
            throw new RuntimeException("Failed to load activator class for component", ex);
        }
    }

    private void deployReferenceBindings() {
        _log.debug("Deploying reference bindings ...");
       
    }

    private void deployServices() {
        _log.debug("Deploying services ...");
        // deploy services to each implementation found in the application
        for (ComponentModel component : _switchyardConfig.getComposite().getComponents()) {
            Activator activator = _componentActivators.get(
                    component.getImplementation().getType());
            // register a service for each one declared in the component
            for (ComponentServiceModel service : component.getServices()) {
                _log.debug("Registering service " + service.getName()  
                       + " for component " + component.getImplementation().getType());
                ExchangeHandler handler = activator.init(service.getQName(), service);
                Service serviceRef = null;
                if (service.getInterface().getType().equals(JAVA_INTERFACE)) {
                    ServiceInterface si = JavaService.fromClass(
                            loadClass(service.getInterface().getInterface()));
                    serviceRef = _serviceDomain.registerService(service.getQName(), handler, si);
                } else {
                    serviceRef = _serviceDomain.registerService(service.getQName(), handler);
                }
                Activation activation = new Activation(serviceRef, activator);
                activation.start();
                _services.add(activation);
            }
        }
        
    }
    
    private void deployReferences() {
        _log.debug("Deploying references ...");
        for (ComponentModel component : _switchyardConfig.getComposite().getComponents()) {
            Activator activator = _componentActivators.get(
                    component.getImplementation().getType());
            // register a service for each one declared in the component
            for (ComponentReferenceModel reference : component.getReferences()) {
                _log.debug("Registering reference " + reference.getName()  
                       + " for component " + component.getImplementation().getType());
                Service service = _serviceDomain.getService(reference.getQName());
                activator.init(reference.getQName(), reference);
                Activation activation = new Activation(service, activator);
                activation.start();
                _references.add(activation);
            }
        }
    }

    private void deployServiceBindings() {
        _log.debug("Deploying service bindings ...");
        // activate bindings for each service
        for (CompositeServiceModel service : _switchyardConfig.getComposite().getServices()) {
            for (BindingModel binding : service.getBindings()) {
                _log.debug("Deploying binding " + binding.getType() + " for service " + service.getName());
                Activator activator = _gatewayActivators.get(binding.getType());
                Service serviceRef = _serviceDomain.getService(service.getQName());
                activator.init(serviceRef.getName(), service);
                Activation activation = new Activation(serviceRef, activator);
                activation.start();
                _serviceBindings.add(activation);
            }
        }
    }

    private void undeployServiceBindings() {
        _log.debug("Undeploying reference bindings ...");
       for (Activation activation : _serviceBindings) {
           activation.stop();
           activation.destroy();
       }
    }

    private void undeployServices() {
        _log.debug("Undeploying services ...");
        for (Activation activation : _services) {
            activation.stop();
            activation.destroy();
        }
    }

    private void undeployReferences() {
        _log.debug("Undeploying references ...");
        for (Activation activation : _references) {
            activation.stop();
            activation.destroy();
        }
    }

    private void undeployReferenceBindings() {
        _log.debug("Undeploying reference bindings ...");
        for (Activation activation : _referenceBindings) {
            activation.stop();
            activation.destroy();
        }
    }

    private void destroyDomain() {

    }

    /**
     * Returns an instance of the ServiceRegistry.
     * @param registryClass class name of the serviceregistry
     * @return ServiceRegistry
     */
    private static ServiceRegistry getRegistry(final String registryClass) {
        ServiceLoader<ServiceRegistry> registryServices
                = ServiceLoader.load(ServiceRegistry.class);
        for (ServiceRegistry serviceRegistry : registryServices) {
            if (registryClass.equals(serviceRegistry.getClass().getName())) {
                return serviceRegistry;
            }
        }
        return null;
    }


    /**
     * Returns an instance of the EndpointProvider.
     * @param providerClass class name of the endpointprovider implementation
     * @return EndpointProvider
     */
    private static EndpointProvider
    getEndpointProvider(final String providerClass) {
        ServiceLoader<EndpointProvider> providerServices
                = ServiceLoader.load(EndpointProvider.class);
        for (EndpointProvider provider : providerServices) {
            if (providerClass.equals(provider.getClass().getName())) {
                return provider;
            }
        }
        return null;
    }
    
    private Class<?> loadClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException cnfEx) {
            throw new RuntimeException(cnfEx);
        }
    }
}

class Activation {
    private Service _service;
    private Activator _activator;
    
    Activation(Service service, Activator activator) {
        _service = service;
        _activator = activator;
    }
    
    void start() {
        _activator.start(_service);
    }
    
    void stop() {
        _activator.stop(_service);
    }
    
    void destroy() {
        _activator.destroy(_service);
    }
    
    Service getService() {
        return _service;
    }
    
    Activator getActivator() {
        return _activator;
    }
}
