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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.switchyard.Service;
import org.switchyard.ServiceReference;
import org.switchyard.common.type.Classes;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.ComponentReferenceModel;
import org.switchyard.config.model.composite.ComponentServiceModel;
import org.switchyard.config.model.composite.CompositeModel;
import org.switchyard.config.model.composite.CompositeReferenceModel;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.config.model.composite.InterfaceModel;
import org.switchyard.config.model.switchyard.EsbInterfaceModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.config.model.transform.TransformsModel;
import org.switchyard.config.model.validate.ValidatesModel;
import org.switchyard.deploy.Activator;
import org.switchyard.deploy.Binding;
import org.switchyard.deploy.Implementation;
import org.switchyard.deploy.ServiceHandler;
import org.switchyard.deploy.event.ApplicationDeployedEvent;
import org.switchyard.deploy.event.ApplicationUndeployedEvent;
import org.switchyard.exception.SwitchYardException;
import org.switchyard.extensions.wsdl.WSDLReaderException;
import org.switchyard.extensions.wsdl.WSDLService;
import org.switchyard.internal.ServiceReferenceImpl;
import org.switchyard.metadata.InOnlyOperation;
import org.switchyard.metadata.InOnlyService;
import org.switchyard.metadata.InOutOperation;
import org.switchyard.metadata.InOutService;
import org.switchyard.metadata.ServiceInterface;
import org.switchyard.metadata.java.JavaService;
import org.switchyard.policy.Policy;

/**
 * Deployment is a framework-independent representation of a deployed SwitchYard 
 * application.  At this point, a deployment is 1:1 with a SwitchYard domain, but
 * this will change to allow multiple deployments to participate in a single, 
 * shared domain.
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class Deployment extends AbstractDeployment {
    /**
     * Interface type used by a Java interface, e.g. "interface.java"
     */
    private static final String JAVA_INTERFACE = "java";

    /**
     * Interface type used by a WSDL interface, e.g. "interface.wsdl"
     */
    private static final String WSDL_INTERFACE = "wsdl";

    private static Logger _log = Logger.getLogger(Deployment.class);

    private Map<String, Activator> _activators = new HashMap<String, Activator>();
    private List<Activation> _services = new LinkedList<Activation>();
    private List<Activation> _serviceBindings = new LinkedList<Activation>();
    private List<Activation> _referenceBindings = new LinkedList<Activation>();
    
    /**
     * Create a new instance of Deployer from a configuration stream.
     * @param configStream stream containing switchyard config
     * @throws IOException Error reading configuration model.
     */
    public Deployment(InputStream configStream) throws IOException {
        super(new ModelPuller<SwitchYardModel>().pull(configStream));
    }
    
    /**
     * Create a new instance of Deployer from a configuration model.
     * @param configModel switchyard config model
     */
    public Deployment(SwitchYardModel configModel) {
        super(configModel);
    }
    
    /**
     * Initialize the deployment.
     * 
     * @param activators The list of component activators.
     */
    protected void doInit(List<Activator> activators) {
        _log.debug("Initializing deployment " + getName());
        // create a new domain and load transformer , validator and activator instances for lifecycle
        registerTransformers();
        registerValidators();
        if (activators != null) {
            for (Activator activator : activators) {
                Collection<String> activationTypes = activator.getActivationTypes();
                if (activationTypes != null) {
                    for (String type : activationTypes) {
                        _log.debug("Registered activation type " + type
                                + " for activator " + activator.getClass() + " on deployment " + getName());
                        _activators.put(type, activator);
                    }
                }
            }
        }

        getDomain().getEventPublisher().publish(new ApplicationDeployedEvent(this));
    }
    
    /**
     * Starts the deployment.  All services are registered and the appropriate 
     * activators are triggered.
     */
    public void start() {
        _log.debug("Starting deployment " + getName());
        // ordered startup lifecycle
        try {
            deployReferenceBindings();
            deployImplementations();
            deployServiceBindings();
        } catch (RuntimeException e1) {
            // Undo partial deployment...
            _log.debug("Undeploying partially deployed artifacts of failed deployment " + getConfig().getQName());
            try {
                stop();
            } catch (RuntimeException e2) {
                // Nothing we can do...
                _log.debug("Failed to properly undeploy a partial/failed deployment " +  getConfig().getQName(), e2);
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
        _log.debug("Stopping deployment " + getName());
        undeployServiceBindings();
        undeployImplementations();
        undeployReferenceBindings();
    }

    /**
     * Tear everything down.
     */
    public void destroy() {
        _log.debug("Destroying deployment " + getName());
        
        // Clean up our list of activations, just in case something's left
        _serviceBindings.clear();
        _services.clear();
        _referenceBindings.clear();

        getValidatorRegistryLoader().unregisterValidators();
        getTransformerRegistryLoader().unregisterTransformers();

        getDomain().getEventPublisher().publish(new ApplicationUndeployedEvent(this));
        getDomain().destroy();
    }
    
    /**
     * Find the deployment activator for the specified type.
     * 
     * @param type The activator component type.
     * @return Activator the Activator instance, or null if no activator exists for the specified type.
     * @throws SwitchYardException if the component definition does not contain
     * an implementation type
     */
    public Activator findActivator(String type) throws SwitchYardException {
        if (_activators.containsKey(type)) {
            return _activators.get(type);
        } else if (failOnMissingActivator()) {
            throw new SwitchYardException("Activator not found for type: " + type);
        } else {
            return null;
        }
    }
    
    /**
     * Returns a list of activator types required by this deployment.  The list
     * is built from implementation, service binding, and reference binding 
     * types in the application.
     * @return list of activator type names
     */
    public List<String> getActivationTypes() {
        HashSet<String> types = new HashSet<String>();
        CompositeModel composite = getConfig().getComposite();
        
        // reference bindings
        for (CompositeReferenceModel reference : composite.getReferences()) {
            for (BindingModel binding : reference.getBindings()) {
                types.add(binding.getType());
            }
        }
        // service bindings
        for (CompositeServiceModel service : composite.getServices()) {
            for (BindingModel binding : service.getBindings()) {
                types.add(binding.getType());
            }
        }
        // implementations
        for (ComponentModel component : composite.getComponents()) {
            if (component.getImplementation() != null) {
                types.add(component.getImplementation().getType());
            }
        }
        
        return new ArrayList<String>(types);
    }
    
    /**
     * Finds an activator that handles the component implementation type.
     * @throws SwitchYardException if the component definition does not contain
     * an implementation type
     */
    private Activator findActivator(ComponentModel component) throws SwitchYardException {
        if (component.getImplementation() == null) {
            throw new SwitchYardException("Component defintion " + component.getName() 
                    + " does not included an implementation definition.");
        }
        return findActivator(component.getImplementation().getType());
    }

    private void registerTransformers() {
        _log.debug("Registering configured Transformers for deployment " + getName());
        TransformsModel transforms = getConfig().getTransforms();
        getTransformerRegistryLoader().registerTransformers(transforms);
    }

    private void registerValidators() {
        _log.debug("Registering configured Validators for deployment " + getName());
        ValidatesModel validates = getConfig().getValidates();
        getValidatorRegistryLoader().registerValidators(validates);
    }

    private void deployReferenceBindings() {
        _log.debug("Deploying reference bindings for deployment " + getName());
        // activate bindings for each service
        for (CompositeReferenceModel reference : getConfig().getComposite().getReferences()) {
            for (BindingModel binding : reference.getBindings()) {
                QName refQName = reference.getQName();
                _log.debug("Deploying binding " + binding.getType() + " for reference " 
                        + reference.getQName() + " for deployment " + getName());
                
                Activator activator = findActivator(binding.getType());
                if (activator == null) {
                    continue;
                }

                ServiceHandler handler = activator.activateBinding(reference.getQName(), binding);
                Activation activation = new Activation(activator, reference.getQName(), handler);
                ServiceInterface si = getCompositeReferenceInterface(reference);
                Binding bindingMetadata = new Binding(binding);
                Service svc = getDomain().registerService(refQName, si, handler, null, bindingMetadata);
                activation.addService(svc);
                _referenceBindings.add(activation);
                        
                handler.start();
            }
        }
    }
    
    private ServiceInterface getCompositeReferenceInterface(CompositeReferenceModel compositeRefModel) {
        ServiceInterface serviceInterface = null;
        
        if (hasCompositeReferenceInterface(compositeRefModel)) {
            serviceInterface = loadServiceInterface(compositeRefModel.getInterface());
        } else if (hasComponentReferenceInterface(compositeRefModel.getComponentReference())) {
            serviceInterface = loadServiceInterface(compositeRefModel.getComponentReference().getInterface());
        }
        return serviceInterface;
    }
    
    private ServiceInterface getCompositeServiceInterface(CompositeServiceModel compositeServiceModel) {
        ServiceInterface serviceInterface = null;
        
        if (hasCompositeServiceInterface(compositeServiceModel)) {
            serviceInterface = loadServiceInterface(compositeServiceModel.getInterface());
        } else if (hasComponentServiceInterface(compositeServiceModel.getComponentService())) {
            serviceInterface = loadServiceInterface(compositeServiceModel.getComponentService().getInterface());
        }
        return serviceInterface;
    }
    
    private boolean hasComponentReferenceInterface(ComponentReferenceModel componentRef) {
        return componentRef != null && componentRef.getInterface() != null; 
    }
    
    private boolean hasCompositeReferenceInterface(CompositeReferenceModel compositeRef) {
        return compositeRef != null && compositeRef.getInterface() != null; 
    }
    
    private boolean hasComponentServiceInterface(ComponentServiceModel componentService) {
        return componentService != null && componentService.getInterface() != null; 
    }
    
    private boolean hasCompositeServiceInterface(CompositeServiceModel compositeService) {
        return compositeService != null && compositeService.getInterface() != null; 
    }
    
    private ServiceInterface getComponentReferenceInterface(ComponentReferenceModel reference) {
        ServiceInterface referenceInterface = null;
        
        if (reference != null && reference.getInterface() != null) {
            referenceInterface = loadServiceInterface(reference.getInterface());
        }
        return referenceInterface;
    }
    
    private ServiceInterface getComponentServiceInterface(ComponentServiceModel service) {
        ServiceInterface serviceInterface = null;
        
        if (service != null && service.getInterface() != null) {
            serviceInterface = loadServiceInterface(service.getInterface());
        }
        return serviceInterface;
    }
    
    private ServiceInterface loadServiceInterface(InterfaceModel intfModel) {
        ServiceInterface serviceInterface = null;
        
        if (intfModel != null) {
            if (isJavaInterface(intfModel.getType())) {
                String interfaceClass = intfModel.getInterface();
                Class<?> serviceInterfaceType = loadClass(interfaceClass);

                if (serviceInterfaceType == null) {
                    throw new SwitchYardException("Failed to load Service interface class '" + interfaceClass + "'.");
                }
                serviceInterface = JavaService.fromClass(serviceInterfaceType);
            } else if (intfModel.getType().equals(WSDL_INTERFACE)) {
                try {
                    serviceInterface = WSDLService.fromWSDL(intfModel.getInterface());
                } catch (WSDLReaderException wsdlre) {
                    throw new SwitchYardException(wsdlre);
                }
            } else if (intfModel.getType().equals(EsbInterfaceModel.ESB)) {
                EsbInterfaceModel esbIntf = (EsbInterfaceModel)intfModel;
                validateEsbInterface(esbIntf);
                if (esbIntf.getOutputType() == null) {
                    serviceInterface = new InOnlyService(new InOnlyOperation(
                            ServiceInterface.DEFAULT_OPERATION, esbIntf.getInputType()));
                } else {
                    serviceInterface = new InOutService(new InOutOperation(
                            ServiceInterface.DEFAULT_OPERATION, 
                            esbIntf.getInputType(), esbIntf.getOutputType(), esbIntf.getFaultType()));
                }
            }
        }
        
        return serviceInterface;
    }
    
    // Checks for invalid input/output/fault combinations on ESB interfaces.
    private void validateEsbInterface(EsbInterfaceModel esbIntf) throws SwitchYardException {
        if (esbIntf.getInputType() == null) {
            throw new SwitchYardException("inputType required on ESB interface definition: " + esbIntf);
        }
        
        if (esbIntf.getFaultType() != null && esbIntf.getOutputType() == null) {
            throw new SwitchYardException("faultType must be acommpanied by outputType in ESB interface: " + esbIntf);
        }
    }
    
    private boolean isJavaInterface(final String type) {
        return type.equals(JAVA_INTERFACE);
    }
    
    private void deployImplementations() {
        // discover any service promotions
        Map<ComponentServiceModel,CompositeServiceModel> servicePromotions = 
                new HashMap<ComponentServiceModel,CompositeServiceModel>();
        for (CompositeServiceModel compositeService : getConfig().getComposite().getServices()) {
            ComponentServiceModel componentService = compositeService.getComponentService();
            if (componentService != null) {
                servicePromotions.put(componentService, compositeService);
            }
        }
        
        for (ComponentModel component : getConfig().getComposite().getComponents()) {
            Activator activator = findActivator(component);
            if (activator == null) {
                continue;
            }

            Implementation impl = new Implementation(component.getImplementation());
            List<ServiceReference> references = new LinkedList<ServiceReference>();

            // register a reference for each one declared in the component
            for (ComponentReferenceModel reference : component.getReferences()) {
                _log.debug("Registering reference " + reference.getQName()
                       + " for component " + component.getImplementation().getType() + " for deployment " + getName());
                ServiceInterface refIntf = getComponentReferenceInterface(reference);
                ServiceReference svcRef = getDomain().registerServiceReference(
                        reference.getQName(), refIntf, null, null, impl);
                references.add(svcRef);
            }
            
            // register a service for each one declared in the component
            for (ComponentServiceModel service : component.getServices()) {
                _log.debug("Registering service " + service.getQName()
                       + " for component " + component.getImplementation().getType() + " for deployment " + getName());

                ServiceHandler handler = activator.activateService(service.getQName(), component);
                Activation activation = new Activation(activator, service.getQName(), handler);
                ServiceInterface serviceIntf = getComponentServiceInterface(service);
                List<Policy> requires = getPolicyRequirements(service);
                Service svc = getDomain().registerService(service.getQName(), serviceIntf, handler, requires, impl);
                activation.addService(svc);
                activation.addReferences(references);
                
                // register any service promotions, avoiding duplicate service names
                CompositeServiceModel promotion = servicePromotions.get(service);
                if (promotion != null && !promotion.getQName().equals(service.getQName())) {
                    getDomain().registerService(promotion.getQName(), serviceIntf, handler);
                }
                
                _services.add(activation);
                handler.start();
            }
            
            // we don't have a distinct call for activateReference right now,
            // so this catches cases where an implementation has one or more
            // references, but no services.  (this is pretty crappy)
            if (component.getServices().isEmpty()) {
                activator.activateService(null, component);
            }
        }
    }

    private void deployServiceBindings() {
        _log.debug("Deploying service bindings for deployment " + getName());
        // activate bindings for each service
        for (CompositeServiceModel service : getConfig().getComposite().getServices()) {
            // Create the reference for the composite service
            ServiceReference reference = getDomain().registerServiceReference(
                    service.getQName(), getCompositeServiceInterface(service));
            
            for (BindingModel binding : service.getBindings()) {
                _log.debug("Deploying binding " + binding.getType() + " for service " 
                        + service.getQName() + " for deployment " + getName());
                
                Activator activator = findActivator(binding.getType());
                if (activator == null) {
                    continue;
                }

                // Hack to set consumer metadata on reference
                Binding bindingMetadata = new Binding(binding);
                ((ServiceReferenceImpl)reference).setConsumerMetadata(bindingMetadata);
                
                ServiceHandler handler = activator.activateBinding(service.getQName(), binding);
                Activation activation = new Activation(activator, service.getQName(), handler);
                activation.addReference(reference);
                _serviceBindings.add(activation);
                
                handler.start();
            }
        }
    }

    private void undeployServiceBindings() {
       _log.debug("Undeploying service bindings for deployment " + getName());
       try {
           for (Activation activation : _serviceBindings) {
               activation.getHandler().stop();
               activation.getActivator().deactivateBinding(
                       activation.getName(), activation.getHandler());

               for (ServiceReference reference : activation.getReferences()) {
                   reference.unregister();
               }
           }
       } finally {
           _serviceBindings.clear();
       }
    }

    private void undeployImplementations() {
        _log.debug("Undeploying services for deployment " + getName());
        try {
            for (Activation activation : _services) {
                activation.getHandler().stop();
                activation.getActivator().deactivateService(
                        activation.getName(), activation.getHandler());
                
                for (Service service : activation.getServices()) {
                    service.unregister();
                }
                
                for (ServiceReference reference : activation.getReferences()) {
                    reference.unregister();
                }
            }
        } finally {
            _services.clear();
        }
    }

    private void undeployReferenceBindings() {
        _log.debug("Undeploying reference bindings for deployment " + getName());
        try {
            for (Activation activation : _referenceBindings) {
                activation.getHandler().stop();
                activation.getActivator().deactivateBinding(
                        activation.getName(), activation.getHandler());

                for (Service service : activation.getServices()) {
                    service.unregister();
                }
            }
        } finally {
            _referenceBindings.clear();
        }
    }

    private Class<?> loadClass(String className) {
        return Classes.forName(className, getClass());
    }
    
    private List<Policy> getPolicyRequirements(ComponentServiceModel model) {
        LinkedList<Policy> requires = new LinkedList<Policy>();
        for (final String policyName : model.getPolicyRequirements()) {
            requires.add(new ServicePolicy(policyName));
        }
        return requires;
    }

}

class ServicePolicy implements Policy {
    private String _policyName;
    
    ServicePolicy(String policyName) {
        _policyName = policyName;
    }

    public String getName() {
        return _policyName;
    }
    public String toString() {
        return _policyName;
    }
    
}

class Activation {
    private Activator _activator;
    private QName _name;
    private ServiceHandler _handler;
    private List<Service> _services = new LinkedList<Service>();
    private List<ServiceReference> _references = new LinkedList<ServiceReference>();
    
    Activation(Activator activator, QName name, ServiceHandler handler) {
        _activator = activator;
        _name = name;
        _handler = handler;
    }
    
    Activator getActivator() {
        return _activator;
    }
    
    QName getName() {
        return _name;
    }
    
    ServiceHandler getHandler() {
        return _handler;
    }
    
    Activation addService(Service service) {
        _services.add(service);
        return this;
    }
    
    Activation addReferences(List<ServiceReference> references) {
        _references.addAll(references);
        return this;
    }
    
    Activation addReference(ServiceReference reference) {
        _references.add(reference);
        return this;
    }
    
    List<ServiceReference> getReferences() {
        return _references;
    }
    
    List <Service> getServices() {
        return _services;
    }
}
