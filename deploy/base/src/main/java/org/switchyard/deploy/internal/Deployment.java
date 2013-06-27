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
import org.switchyard.ServiceMetadata;
import org.switchyard.ServiceReference;
import org.switchyard.common.type.Classes;
import org.switchyard.config.model.Model;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.config.model.composite.ComponentImplementationModel;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.ComponentReferenceModel;
import org.switchyard.config.model.composite.ComponentServiceModel;
import org.switchyard.config.model.composite.CompositeModel;
import org.switchyard.config.model.composite.CompositeReferenceModel;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.config.model.composite.ExtensionsModel;
import org.switchyard.config.model.composite.InterfaceModel;
import org.switchyard.config.model.switchyard.EsbInterfaceModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.config.model.switchyard.ThrottlingModel;
import org.switchyard.config.model.transform.TransformsModel;
import org.switchyard.config.model.validate.ValidatesModel;
import org.switchyard.deploy.Activator;
import org.switchyard.deploy.Binding;
import org.switchyard.deploy.ComponentNames;
import org.switchyard.deploy.Implementation;
import org.switchyard.deploy.Lifecycle;
import org.switchyard.deploy.ServiceHandler;
import org.switchyard.deploy.event.ApplicationDeployedEvent;
import org.switchyard.deploy.event.ApplicationUndeployedEvent;
import org.switchyard.exception.SwitchYardException;
import org.switchyard.extensions.wsdl.WSDLReaderException;
import org.switchyard.extensions.wsdl.WSDLService;
import org.switchyard.metadata.InOnlyOperation;
import org.switchyard.metadata.InOnlyService;
import org.switchyard.metadata.InOutOperation;
import org.switchyard.metadata.InOutService;
import org.switchyard.metadata.ServiceInterface;
import org.switchyard.metadata.ServiceMetadataBuilder;
import org.switchyard.metadata.java.JavaService;
import org.switchyard.metadata.qos.Throttling;
import org.switchyard.policy.Policy;
import org.switchyard.policy.Policy.PolicyType;
import org.switchyard.policy.PolicyFactory;

/**
 * Deployment is a framework-independent representation of a deployed SwitchYard 
 * application.  At this point, a deployment is 1:1 with a SwitchYard domain, but
 * this will change to allow multiple deployments to participate in a single, 
 * shared domain.
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class Deployment extends AbstractDeployment {

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
    
    @Override
    public Lifecycle getGatwayLifecycle(final QName serviceName, final String bindingName) {
        // TODO: look at a more efficient way of doing this
        for (Activation binding : _serviceBindings) {
            if (bindingName.equals(binding.getBindingModel().getName()) && serviceName.equals(binding.getName())) {
                return binding.getHandler();
            }
        }
        for (Activation binding : _referenceBindings) {
            if (bindingName.equals(binding.getBindingModel().getName()) && serviceName.equals(binding.getName())) {
                return binding.getHandler();
            }
        }
        return null;
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
            int bindingCount = 0;
            for (BindingModel binding : reference.getBindings()) {
                QName refQName = reference.getQName();
                ++bindingCount;
                if (binding.getName() == null) {
                    _log.debug("Initializing binding name for binding " + bindingCount + " for reference "
                            + reference.getQName() + " for deployment " + getName());
                    binding.setName("_" + reference.getName() + "_" + binding.getType() + "_" + bindingCount);
                }
                _log.debug("Deploying binding " + binding.getName() + " for reference " 
                        + reference.getQName() + " for deployment " + getName());
                
                Activator activator = findActivator(binding.getType());
                if (activator == null) {
                    continue;
                }

                ServiceHandler handler = activator.activateBinding(reference.getQName(), binding);
                Activation activation = new Activation(activator, reference.getQName(), binding, handler);
                ServiceInterface si = getCompositeReferenceInterface(reference);
                Binding bindingMetadata = new Binding(binding);
                validateServiceRegistration(refQName);
                ServiceMetadata metadata = ServiceMetadataBuilder.create().registrant(bindingMetadata).build();
                Service svc = getDomain().registerService(refQName, si, handler, metadata);
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

    private Throttling getCompositeServiceThrottling(CompositeServiceModel compositeServiceModel) {
        final ExtensionsModel extensions = compositeServiceModel.getExtensions();
        if (extensions == null) {
            return null;
        }
        final ThrottlingModel throttling = extensions.getThrottling();
        if (throttling == null) {
            return null;
        }
        final Long timePeriod = throttling.getTimePeriod();
        final Throttling retVal = new Throttling();
        retVal.setMaxRequests(throttling.getMaxRequests());
        if (timePeriod != null) {
            retVal.setTimePeriod(timePeriod);
        }
        return retVal;
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
            } else if (InterfaceModel.WSDL.equals(intfModel.getType())) {
                try {
                    serviceInterface = WSDLService.fromWSDL(intfModel.getInterface());
                } catch (WSDLReaderException wsdlre) {
                    throw new SwitchYardException(wsdlre);
                }
            } else if (EsbInterfaceModel.ESB.equals(intfModel.getType())) {
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
    private void validateEsbInterface(EsbInterfaceModel esbIntf)  {
        if (esbIntf.getInputType() == null) {
            throw new SwitchYardException("inputType required on ESB interface definition: " + esbIntf);
        }
        
        if (esbIntf.getFaultType() != null && esbIntf.getOutputType() == null) {
            throw new SwitchYardException("faultType must be acommpanied by outputType in ESB interface: " + esbIntf);
        }
    }
    
    private boolean isJavaInterface(final String type) {
        return InterfaceModel.JAVA.equals(type);
    }
    
    private void deployImplementations() {
        
        for (ComponentModel component : getConfig().getComposite().getComponents()) {
            Activator activator = findActivator(component);
            if (activator == null) {
                continue;
            }

            List<Policy> requiresImpl = null;
            try {
                requiresImpl = getPolicyRequirements(component.getImplementation());
            } catch (Exception e) {
                throw new SwitchYardException(e);
            }
            
            Implementation impl = new Implementation(component.getImplementation());
            List<ServiceReference> references = new LinkedList<ServiceReference>();
            
            // register a reference for each one declared in the component
            for (ComponentReferenceModel reference : component.getReferences()) {
                // Create the reference name qualified with component name to ensure uniqueness
                QName refName = ComponentNames.qualify(component.getQName(),  reference.getQName());
                               
                _log.debug("Registering reference " + refName + " for component " 
                        + component.getImplementation().getType() + " for deployment " + getName());
            
                // Component Reference bindings not allowed, check to see if we find one and throw an exception
                List<Model> models = reference.getModelChildren();
                for (Model model : models) {
                    if (BindingModel.class.isAssignableFrom(model.getClass())) {
                        throw new SwitchYardException("Component Reference bindings are not allowed.   Found " + model.toString()
                                + " on reference " + reference.toString());
                    }
                }
                
                List<Policy> requires = null;
                try {
                    requires = getPolicyRequirements(reference);
                } catch (Exception e) {
                    throw new SwitchYardException("Unable to collect requirements for " + reference, e);
                }
                validatePolicy(requires, requiresImpl);

                ServiceInterface refIntf = getComponentReferenceInterface(reference);
                ServiceMetadata metadata = ServiceMetadataBuilder.create()
                        .security(getDomain().getServiceSecurity(reference.getSecurity()))
                        .requiredPolicies(requires).registrant(impl)
                        .build();
                ServiceReference svcRef = getDomain().registerServiceReference(refName, refIntf, null, metadata);

                boolean wired = false;
                // wire a reference if the name is different from promoted name
                for (CompositeReferenceModel compositeReference : getConfig().getComposite().getReferences()) {
                    ComponentReferenceModel componentReference = compositeReference.getComponentReference();
                    if (componentReference != null && componentReference.equals(reference)) {
                        if (!componentReference.getQName().equals(compositeReference.getQName())) {
                            svcRef.wire(compositeReference.getQName());
                            wired = true;
                            break;
                        }
                    }
                }
                
                // if we didn't wire to a promoted reference, then default to unqualified service name
                if (!wired) {
                    svcRef.wire(ComponentNames.unqualify(svcRef));
                }
                references.add(svcRef);
            }
            
            // register a service for each one declared in the component
            if (component.getServices().size() > 1) {
                throw new SwitchYardException("Multiple services in the Component '"
                        + component.getName() + "' - Just one service is allowed");

            } else if (component.getServices().size() == 1) {
                ComponentServiceModel service = component.getServices().get(0);
                _log.debug("Registering service " + service.getQName()
                       + " for component " + component.getImplementation().getType() + " for deployment " + getName());

                
                // Component Service bindings not allowed, check to see if we find one and throw an exception
                List<Model> models = service.getModelChildren();
                for (Model model : models) {
                    if (BindingModel.class.isAssignableFrom(model.getClass())) {
                        throw new SwitchYardException("Component Service bindings are not allowed.   Found " + model.toString()
                                + " on Service " + service.toString());
                    }
                }
                
                
                List<Policy> requires = null;
                try {
                    requires = getPolicyRequirements(service);
                    validatePolicy(requires, requiresImpl);
                } catch (Exception e) {
                    throw new SwitchYardException(e);
                }
                requires.addAll(requiresImpl);

                ServiceHandler handler = activator.activateService(service.getQName(), component);
                Activation activation = new Activation(activator, service.getQName(), null, handler);
                ServiceInterface serviceIntf = getComponentServiceInterface(service);
                ServiceMetadata metadata = ServiceMetadataBuilder.create()
                        .security(getDomain().getServiceSecurity(service.getSecurity()))
                        .requiredPolicies(requires)
                        .registrant(impl)
                        .build();
                
                Service svc = getDomain().registerService(service.getQName(), serviceIntf, handler, metadata);
                activation.addService(svc);
                activation.addReferences(references);
                
                // register any service promotions
                for (CompositeServiceModel compositeService : getConfig().getComposite().getServices()) {
                    ComponentServiceModel componentService = compositeService.getComponentService();
                    if (componentService != null && componentService.equals(service)) {
                        // avoid duplicates
                        if (!service.getQName().equals(compositeService.getQName())) {
                            validateServiceRegistration(compositeService.getQName());
                            Service promotedService = getDomain().registerService(
                                    compositeService.getQName(), serviceIntf, handler);
                            activation.addPromotion(promotedService);
                        }
                    }
                }
                
                _services.add(activation);
                handler.start();

            } else {
                // we don't have a distinct call for activateReference right now,
                // so this catches cases where an implementation has one or more
                // references, but no services.  (this is pretty crappy)
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
            ServiceMetadataBuilder.update(reference.getServiceMetadata()).throttling(
                    getCompositeServiceThrottling(service));
            int bindingCount = 0;
            for (BindingModel binding : service.getBindings()) {
                ++bindingCount;
                if (binding.getName() == null) {
                    _log.debug("Initializing binding name for binding " + bindingCount + " for service "
                            + service.getQName() + " for deployment " + getName());
                    binding.setName("_" + service.getName() + "_" + binding.getType() + "_" + bindingCount);
                }
                _log.debug("Deploying binding " + binding.getName() + " for service " 
                        + service.getQName() + " for deployment " + getName());
                
                Activator activator = findActivator(binding.getType());
                if (activator == null) {
                    continue;
                }

                // Hack to set consumer metadata on reference
                Binding bindingMetadata = new Binding(binding);
                ServiceMetadataBuilder.update(reference.getServiceMetadata())
                    .registrant(bindingMetadata);
                
                ServiceHandler handler = activator.activateBinding(service.getQName(), binding);
                Activation activation = new Activation(activator, service.getQName(), binding, handler);
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
                try {
                    activation.getHandler().stop();
                } catch (Throwable e) {
                    _log.error("Error stopping service binding.", e);
                }
                try {
                    activation.getActivator().deactivateBinding(activation.getName(), activation.getHandler());
                } catch (Throwable e) {
                    _log.error("Error deactivating service binding.", e);
                }

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
                try {
                    activation.getHandler().stop();
                } catch (Throwable e) {
                    _log.error("Error stopping service.", e);
                }
                try {
                    activation.getActivator().deactivateService(activation.getName(), activation.getHandler());
                } catch (Throwable e) {
                    _log.error("Error deactivating service.", e);
                }

                for (Service service : activation.getServices()) {
                    service.unregister();
                }

                for (ServiceReference reference : activation.getReferences()) {
                    reference.unregister();
                }

                for (Service service : activation.getPromotions()) {
                    service.unregister();
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
                try {
                    activation.getHandler().stop();
                } catch (Throwable e) {
                    _log.error("Error stopping reference binding.", e);
                }
                try {
                    activation.getActivator().deactivateBinding(activation.getName(), activation.getHandler());
                } catch (Throwable e) {
                    _log.error("Error deactivating reference binding.", e);
                }

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
    
    private List<Policy> getPolicyRequirements(ComponentServiceModel serviceModel) throws Exception {
        LinkedList<Policy> requires = new LinkedList<Policy>();
        for (final String policyName : serviceModel.getPolicyRequirements()) {
            requires.add(PolicyFactory.getPolicy(policyName));
        }
        return requires;
    }

    private List<Policy> getPolicyRequirements(ComponentImplementationModel implModel) throws Exception {
        LinkedList<Policy> requires = new LinkedList<Policy>();
        for (final String policyName : implModel.getPolicyRequirements()) {
            requires.add(PolicyFactory.getPolicy(policyName));
        }
        return requires;
    }
    
    private List<Policy> getPolicyRequirements(ComponentReferenceModel referenceModel) throws Exception {
        LinkedList<Policy> requires = new LinkedList<Policy>();
        for (final String policyName : referenceModel.getPolicyRequirements()) {
            requires.add(PolicyFactory.getPolicy(policyName));
        }
        return requires;
    }

    private void validatePolicy(List<Policy> interaction, List<Policy> implementation) {
        for (int i=0; interaction != null && i<interaction.size(); i++) {
            if (!interaction.get(i).supports(PolicyType.INTERACTION)) {
                throw new SwitchYardException("Policy '" + interaction.get(i) + "' is not an interaction policy.");
            }

            Policy required = interaction.get(i).getPolicyDependency();
            if (required != null) {
                if (required.supports(PolicyType.INTERACTION) && !interaction.contains(required)) {
                    throw new SwitchYardException("Interaction Policy '" + interaction.get(i) + "' should be requested with '" + required);
                    
                } else if (required.supports(PolicyType.IMPLEMENTATION) && !implementation.contains(required)) {
                    throw new SwitchYardException("Interaction Policy '" + interaction.get(i) + "' requires '" + required
                            + "' Implementation Policy, but it does not exist. " + implementation);
                }
            }
            
            for (int j=i+1; j<interaction.size(); j++) {
                if (!interaction.get(i).isCompatibleWith(interaction.get(j))) {
                    throw new SwitchYardException("Interaction Policy '" + interaction.get(i) + "' and '" + interaction.get(j) + " are not compatible.");
                }
            }
        }

        for (int i=0; implementation != null && i<implementation.size(); i++) {
            if (!implementation.get(i).supports(PolicyType.IMPLEMENTATION)) {
                throw new SwitchYardException("Policy '" + implementation.get(i) + "' is not an implementation policy.");
            }
            
            Policy required = implementation.get(i).getPolicyDependency();
            if (required != null) {
                if (required.supports(PolicyType.IMPLEMENTATION) && !implementation.contains(required)) {
                    throw new SwitchYardException("Implementation Policy '" + implementation.get(i) + "' should be requested with '" + required);
                } else if (required.supports(PolicyType.INTERACTION) && !interaction.contains(required)) {
                    throw new SwitchYardException("Implementation Policy '" + implementation.get(i) + "' requires '" + required
                            + "' Interaction Policy, but it does not exist. " + interaction);
                }
            }
            
            for (int j=i+1; j<implementation.size(); j++) {
                if (!implementation.get(i).isCompatibleWith(implementation.get(j))) {
                    throw new SwitchYardException("Implementation Policy '" + implementation.get(i) + "' and '" + implementation.get(j) + " are not compatible.");
                }
            }

            for (int j=0; interaction != null && j<interaction.size(); j++) {
                if (!implementation.get(i).isCompatibleWith(interaction.get(j))) {
                    throw new SwitchYardException("Implementation Policy '" + implementation.get(i) + "' and Interaciton Policy'" + interaction.get(j) + " are not compatible.");
                }
            }
        }
    }

    private void validateServiceRegistration(QName name) {
        for (ComponentModel component : getConfig().getComposite().getComponents()) {
            for (ComponentServiceModel service : component.getServices()) {
                if (service.getQName().equals(name)) {
                    throw new SwitchYardException("Service registration with name " + name + " hides " + service);
                }
            }
        }
    }
}

class Activation {
    private Activator _activator;
    private QName _name;
    private BindingModel _bindingModel;
    private ServiceHandler _handler;
    private List<Service> _services = new LinkedList<Service>();
    private List<Service> _promotions = new LinkedList<Service>();
    private List<ServiceReference> _references = new LinkedList<ServiceReference>();
    
    Activation(Activator activator, QName name, BindingModel bindingModel, ServiceHandler handler) {
        _activator = activator;
        _name = name;
        _bindingModel = bindingModel;
        _handler = handler;
    }
    
    Activator getActivator() {
        return _activator;
    }
    
    QName getName() {
        return _name;
    }
    
    BindingModel getBindingModel() {
        return _bindingModel;
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
    
    Activation addPromotion(Service service) {
        _promotions.add(service);
        return this;
    }
    
    List<ServiceReference> getReferences() {
        return _references;
    }

    List <Service> getServices() {
        return _services;
    }

    List <Service> getPromotions() {
        return _promotions;
    }
}
