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

import org.apache.log4j.Logger;
import org.switchyard.ExchangeHandler;
import org.switchyard.Service;
import org.switchyard.config.model.ModelResource;
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.ComponentReferenceModel;
import org.switchyard.config.model.composite.ComponentServiceModel;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.deploy.Activator;
import org.switchyard.metadata.ServiceInterface;
import org.switchyard.metadata.java.JavaService;

/**
 * Deployment is a framework-independent representation of a deployed SwitchYard 
 * application.  At this point, a deployment is 1:1 with a SwitchYard domain, but
 * this will change to allow multiple deployments to participate in a single, 
 * shared domain.
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class Deployment extends AbstractDeployment {
    
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

    private static Logger _log = Logger.getLogger(Deployment.class);

    private SwitchYardModel _switchyardConfig;
    private Map<String, Activator> _componentActivators =
        new HashMap<String, Activator>();
    private Map<String, Activator> _gatewayActivators = 
        new HashMap<String, Activator>();
    private List<Activation> _services = new LinkedList<Activation>();
    private List<Activation> _serviceBindings = new LinkedList<Activation>();
    private List<Activation> _references = new LinkedList<Activation>();
    private List<Activation> _referenceBindings = new LinkedList<Activation>();

    /**
     * Create a new instance of Deployer from a configuration stream.
     * @param configStream stream containing switchyard config
     */
    public Deployment(InputStream configStream) {
        // parse the config
        try {
            _switchyardConfig = (SwitchYardModel)new ModelResource().pull(configStream);
        } catch (java.io.IOException ioEx) {
            throw new RuntimeException("Failed to read switchyard config.", ioEx);
        }
    }
    
    /**
     * Create a new instance of Deployer from a configuration model.
     * @param configModel switchyard config model
     */
    public Deployment(SwitchYardModel configModel) {
        _switchyardConfig = configModel;
    }
    
    /**
     * Initialize the deployment.  This actually starts everything as well at 
     * the moment.
     */
    public void init() {
        super.init();
        _log.info("Initializing deployment for application " + _switchyardConfig.getName());
        // create a new domain and load activator instances for lifecycle
        createActivators();
        // ordered startup lifecycle
        deployReferenceBindings();
        deployServices();
        deployReferences();
        deployServiceBindings();
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
                    serviceRef = getDomain().registerService(service.getQName(), handler, si);
                } else {
                    serviceRef = getDomain().registerService(service.getQName(), handler);
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
                Service service = getDomain().getService(reference.getQName());
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
                Service serviceRef = getDomain().getService(service.getQName());
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
