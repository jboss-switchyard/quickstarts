/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */

package org.switchyard.deploy.internal;

import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.switchyard.ExchangeHandler;
import org.switchyard.ServiceReference;
import org.switchyard.config.model.ModelResource;
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.ComponentReferenceInterfaceModel;
import org.switchyard.config.model.composite.ComponentReferenceModel;
import org.switchyard.config.model.composite.ComponentServiceModel;
import org.switchyard.config.model.composite.CompositeReferenceModel;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.config.model.transform.TransformsModel;
import org.switchyard.deploy.Activator;
import org.switchyard.extensions.wsdl.WSDLReaderException;
import org.switchyard.extensions.wsdl.WSDLService;
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
    
    private static final String CAMEL_ACTIVATOR_CLASS = 
        "org.switchyard.component.camel.deploy.CamelActivator";
    
    /**
     * Interface type used by a Java interface, e.g. "interface.java"
     */
    private static final String JAVA_INTERFACE = "java";

    /**
     * Interface type used by a WSDL interface, e.g. "interface.wsdl"
     */
    private static final String WSDL_INTERFACE = "wsdl";

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
            _switchyardConfig = new ModelResource<SwitchYardModel>().pull(configStream);
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
     * Initialize the deployment.
     */
    public void init() {
        super.init();
        _log.debug("Initializing deployment for application " + _switchyardConfig.getName());
        // create a new domain and load transformer and activator instances for lifecycle
        registerTransformers();
        createActivators();
    }
    
    /**
     * Starts the deployment.  All services are registered and the appropriate 
     * activators are triggered.
     */
    public void start() {
        _log.debug("Starting deployment for application " + _switchyardConfig.getName());
        // ordered startup lifecycle
        try {
            deployReferenceBindings();
            deployServices();
            deployReferences();
            deployServiceBindings();
        } catch (RuntimeException e1) {
            // Undo partial deployment...
            _log.debug("Undeploying partially deployed artifacts of failed deployment for application " + _switchyardConfig.getName());
            try {
                stop();
            } catch (RuntimeException e2) {
                // Nothing we can do...
                _log.debug("Failed to properly undeploy a partial/failed deployment for application " +  _switchyardConfig.getName(), e2);
            }
            // Rethrow the exception...
            throw e1;
        }
    }

    /**
     * Stops the deployment.  All services are unregistered and the appropriate
     * activators are triggered.
     */
    public void stop() {
        _log.debug("Stopping deployment for application " + _switchyardConfig.getName());
        undeployServiceBindings();
        undeployServices();
        undeployReferences();
        undeployReferenceBindings();
    }

    /**
     * Tear everything down.
     */
    public void destroy() {
        _log.debug("Destroying deployment for application " + _switchyardConfig.getName());
        
        destroyDomain();
        
        // Clean up our list of activations, just in case something's left
        _services.clear();
        _serviceBindings.clear();
        _references.clear();
        _referenceBindings.clear();

        getTransformerRegistryLoader().unregisterTransformers();
    }
    
    void createGatewayActivator(String type, String runtimeClass) {
        try {
            _gatewayActivators.put(
                    type,
                    (Activator)loadClass(runtimeClass).newInstance());
        } catch (Exception ex) {
            _log.debug("Failed to load Gateway Activator class '" + runtimeClass + "' for component type '" + type + "'.");
        }
    }
    
    void createComponentActivator(String type, String runtimeClass) {
        try {
            _componentActivators.put(
                    type,
                    (Activator)loadClass(runtimeClass).newInstance());
        } catch (Exception ex) {
            _log.debug("Failed to load Component Activator class '" + runtimeClass + "' for component type '" + type + "'.");
        }
    }
    
    Activator getComponentActivator(String type) {
        return _componentActivators.get(type);
    }

    Activator getGatewayActivator(String type) {
        return _gatewayActivators.get(type);
    }
    
    private void createActivators() {
        createComponentActivator("bean", BEAN_ACTIVATOR_CLASS);
        createGatewayActivator("soap", SOAP_ACTIVATOR_CLASS);
        createGatewayActivator("camel", CAMEL_ACTIVATOR_CLASS);
        createGatewayActivator("direct", CAMEL_ACTIVATOR_CLASS);
        createGatewayActivator("file", CAMEL_ACTIVATOR_CLASS);
    }

    private void registerTransformers() {
        _log.debug("Registering configured Transformers ...");
        TransformsModel transforms = _switchyardConfig.getTransforms();
        getTransformerRegistryLoader().registerTransformers(transforms);
    }

    private void deployReferenceBindings() {
        _log.debug("Deploying reference bindings ...");
        // activate bindings for each service
        for (CompositeReferenceModel reference : _switchyardConfig.getComposite().getReferences()) {
            for (BindingModel binding : reference.getBindings()) {
                QName refQName = reference.getQName();
                _log.debug("Deploying binding " + binding.getType() + " for reference " + reference.getName());
                
                Activator activator = _gatewayActivators.get(binding.getType());
                ExchangeHandler handler = activator.init(refQName, reference);
                
                ServiceInterface si = loadComponentRefServiceInterface(reference.getComponentReference());
                ServiceReference serviceRef = si != null ?
                        getDomain().registerService(refQName, handler, si):
                        getDomain().registerService(refQName, handler);
                        
                Activation activation = new Activation(serviceRef, activator);
                activation.start();
                
                _referenceBindings.add(activation);
            }
        }
    }
    
    private ServiceInterface loadComponentRefServiceInterface(ComponentReferenceModel compRef) {
        ComponentReferenceInterfaceModel referenceInterfaceModel = compRef.getInterface();
        if (referenceInterfaceModel != null) {
            if (isJavaInterface(referenceInterfaceModel.getType())) {
                String interfaceName = compRef.getInterface().getInterface();
                return JavaService.fromClass(loadClass(interfaceName));
            }
        }
        return null;
    }
    
    private boolean isJavaInterface(final String type) {
	    return type.equals(JAVA_INTERFACE);
    }

    private void deployServices() {
        _log.debug("Deploying services ...");
        // deploy services to each implementation found in the application
        for (ComponentModel component : _switchyardConfig.getComposite().getComponents()) {
            Activator activator = getActivator(component);
            // register a service for each one declared in the component
            for (ComponentServiceModel service : component.getServices()) {
                _log.debug("Registering service " + service.getName()  
                       + " for component " + component.getImplementation().getType());
                ExchangeHandler handler = activator.init(service.getQName(), service);
                ServiceReference serviceRef = null;
                if (isJavaInterface(service.getInterface().getType())) {
                    ServiceInterface si = JavaService.fromClass(
                            loadClass(service.getInterface().getInterface()));
                    serviceRef = getDomain().registerService(service.getQName(), handler, si);
                } else if (service.getInterface().getType().equals(WSDL_INTERFACE)) {
                    try {
                        ServiceInterface si = WSDLService.fromWSDL(service.getInterface().getInterface());
                        serviceRef = getDomain().registerService(service.getQName(), handler, si);
                    } catch (WSDLReaderException wsdlre) {
                        throw new RuntimeException(wsdlre);
                    }
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
            Activator activator = getActivator(component);
            // register a service for each one declared in the component
            for (ComponentReferenceModel reference : component.getReferences()) {
                _log.debug("Registering reference " + reference.getName()  
                       + " for component " + component.getImplementation().getType());
                ServiceReference service = getDomain().getService(reference.getQName());
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
                ServiceReference serviceRef = getDomain().getService(service.getQName());
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

    private Activator getActivator(ComponentModel component) {
        String type = component.getImplementation().getType();
        Activator activator = _componentActivators.get(type);

        if (activator == null) {
            throw new RuntimeException("Unknown configuration component type '" + type + "'.  No Activator implementation registered for this type.");
        }

        return activator;
    }
}

class Activation {
    private ServiceReference _service;
    private Activator _activator;
    
    Activation(ServiceReference service, Activator activator) {
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
    
    ServiceReference getService() {
        return _service;
    }
    
    Activator getActivator() {
        return _activator;
    }
}
