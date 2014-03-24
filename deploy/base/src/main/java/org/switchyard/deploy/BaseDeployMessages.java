package org.switchyard.deploy;

import org.jboss.logging.annotations.Cause;
import org.jboss.logging.Messages;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;
import org.switchyard.SwitchYardException;
/**
 * <p/>
 * This file is using the subset 12200-12399 for logger messages.
 * <p/>
 *
 */
@MessageBundle(projectCode = "SWITCHYARD")
public interface BaseDeployMessages {
    /**
     * Default messages.
     */
    BaseDeployMessages MESSAGES = Messages.getBundle(BaseDeployMessages.class);

    /**
     * usagePath method definition.
     * @param path path
     * @return Localized string
     */
    @Message(id = 12200, value = "Usage: %s path-to-switchyard-config")
    String usagePath(String path);

    /**
     * notValidConfigFile method definition.
     * @param arg arg
     * @return Localized string
     */
    @Message(id = 12201, value = "'%s' is not a valid SwitchYard configuration file.")
    String notValidConfigFile(String arg);
    
    /**
     * activateBindingNotSupported method definition.
     * @param className className
     * @return UnsupportedOperationException
     */
    @Message(id = 12202 , value = "activateBinding() not supported by %s")
    UnsupportedOperationException activateBindingNotSupported(String className);

    /**
     * activateServiceNotSupported method definition.
     * @param className className
     * @return UnsupportedOperationException
     */
    @Message(id = 12203 , value = "activateService() not supported by %s")
    UnsupportedOperationException activateServiceNotSupported(String className);

    /**
     * deactivateBindingNotSupported method definition.
     * @param className className
     * @return UnsupportedOperationException
     */
    @Message(id = 12204 , value = "deactivateBinding() not supported by %s")
    UnsupportedOperationException deactivateBindingNotSupported(String className);

    /**
     * deactivateServiceNotSupported method definition.
     * @param className className
     * @return UnsupportedOperationException
     */
    @Message(id = 12205 , value = "deactivateService() not supported by %s")
    UnsupportedOperationException deactivateServiceNotSupported(String className);

    /**
     * duplicateSecurityConfigurationNames method definition.
     * @param key key
     * @return IllegalStateException
     */
    @Message(id = 12206 , value = "Duplicate security configuration names calculated: %s")
    IllegalStateException duplicateSecurityConfigurationNames(String key);

    /**
     * invalidHandlerState method definition.
     * @return SwitchYardException
     */
    @Message(id = 12207 , value = "Invalid handler state.")
    SwitchYardException invalidHandlerState();
    
    /**
     * stateCannotBeNull method definition.
     * @return SwitchYardException
     */
    @Message(id = 12208 , value = "state cannot be null.")
    SwitchYardException stateCannotBeNull();
    
    /**
     * activatorNotFoundForType method definition.
     * @param type type
     * @return SwitchYardException
     */
    @Message(id = 12209 , value = "Activator not found for type: %s")
    SwitchYardException activatorNotFoundForType(String type);
    
    /**
     * componentDefNoImpl method definition.
     * @param componentName componentName
     * @return SwitchYardException
     */
    @Message(id = 12210 , value = "Component defintion %s does not included an implementation definition.")
    SwitchYardException componentDefNoImpl(String componentName);
    
    /**
     * failedToLoadServiceInterface method definition.
     * @param className className
     * @return SwitchYardException
     */
    @Message(id = 12211 , value = "Failed to load Service interface class '%s'.")
    SwitchYardException failedToLoadServiceInterface(String className);
    
    /**
     * inputTypeRequired method definition.
     * @param esbInterface esbInterface
     * @return SwitchYardException
     */
    @Message(id = 12212 , value = "inputType required on ESB interface definition: %s")
    SwitchYardException inputTypeRequired(String esbInterface);
       
    /**
     * faultTypeNeedsOutputType method definition.
     * @param esbInterface esbInterface
     * @return SwitchYardException
     */
    @Message(id = 12213 , value = "faultType must be acommpanied by outputType in ESB interface: %s")
    SwitchYardException faultTypeNeedsOutputType(String esbInterface);
    
    /**
     * componentReferenceBindingsNotAllowed method definition.
     * @param model model
     * @param reference reference
     * @return SwitchYardException
     */
    @Message(id = 12215 , value = "Component Reference bindings are not allowed.   Found %s on reference %s")
    SwitchYardException componentReferenceBindingsNotAllowed(String model, String reference);
    
    /**
     * unableCollectRequirements method definition.
     * @param reference reference
     * @param e e
     * @return SwitchYardException
     */
    @Message(id = 12216 , value = "Unable to collect requirements for %s")
    SwitchYardException unableCollectRequirements(String reference, @Cause Exception e);
    
    
    /**
     * multipleServicesFound method definition.
     * @param componentName componentName
     * @return SwitchYardException
     */
    @Message(id = 12217 , value =  "Multiple services in the Component '%s' - Just one service is allowed")
    SwitchYardException multipleServicesFound(String componentName);
   
    
    /**
     * componentServiceBindingsNotAllowed method definition.
     * @param model model
     * @param service service
     * @return SwitchYardException
     */
    @Message(id = 12218 , value = "Component Service bindings are not allowed.   Found %s on service %s")
    SwitchYardException componentServiceBindingsNotAllowed(String model, String service);




    /**
     * policyNotInteraction method definition.
     * @param policyType policyType
     * @return SwitchYardException
     */
    @Message(id = 12219 , value = "Policy '%s' is not an interaction policy.")
    SwitchYardException policyNotInteraction(String policyType);

    /**
     * interactionPolicyShouldBeRequestedWith method definition.
     * @param policy policy
     * @param required required
     * @return SwitchYardException
     */
    @Message(id = 12220 , value = "Interaction Policy '%s' should be requested with '%s'")
    SwitchYardException interactionPolicyShouldBeRequestedWith(String policy, String required);

    /**
     * interactionPolicyRequiresImplPolicy method definition.
     * @param policy policy
     * @param required required
     * @param implementation implementation
     * @return SwitchYardException
     */
    @Message(id = 12221 , value =  "Interaction Policy '%s' requires '%s' Implementation Policy, but it does not exist. %s")
    SwitchYardException interactionPolicyRequiresImplPolicy(String policy, String required, String implementation);
    
    /**
     * interactionPolicyNotCompatible method definition.
     * @param policy policy
     * @param policyTwo policyTwo
     * @return SwitchYardException
     */
    @Message(id = 12222 , value =   "Interaction Policy '%s' and '%s' are not compatible.")
    SwitchYardException interactionPolicyNotCompatible(String policy, String policyTwo);
    
    /**
     * policyNotImplementationPolicy method definition.
     * @param policy policy
     * @return SwitchYardException
     */
    @Message(id = 12223 , value =   "Policy '%s' is not an implementation policy.")
    SwitchYardException policyNotImplementationPolicy(String policy);

    /**
     * implementationPolicyShouldBeRequestedWith method definition.
     * @param policy policy
     * @param required required
     * @return SwitchYardException
     */
    @Message(id = 12224 , value = "Implementation Policy '%s' should be requested with '%s'")
    SwitchYardException implementationPolicyShouldBeRequestedWith(String policy, String required);

    /**
     * implementationPolicyRequiresInterPolicy method definition.
     * @param policy policy
     * @param required required
     * @param interaction interaction
     * @return SwitchYardException
     */
    @Message(id = 12225 , value =  "Implementation Policy '%s' requires '%s' Interaction Policy, but it does not exist. %s")
    SwitchYardException implementationPolicyRequiresInterPolicy(String policy, String required, String interaction);
    
    /**
     * implementationPolicyNotCompatible method definition.
     * @param policy policy
     * @param policyTwo policyTwo
     * @return SwitchYardException
     */
    @Message(id = 12226 , value =   "Implementation Policy '%s' and '%s' are not compatible.")
    SwitchYardException implementationPolicyNotCompatible(String policy, String policyTwo);
    
    /**
     * implementationPolicyNotCompatibleWithInteraction method definition.
     * @param policy policy
     * @param policyTwo policyTwo
     * @return SwitchYardException
     */
    @Message(id = 12227 , value =   "Implementation Policy '%s' is not compatible with Interaction Policy '%s'.")
    SwitchYardException implementationPolicyNotCompatibleWithInteraction(String policy, String policyTwo);


    /**
     * serviceRegHidesService method definition.
     * @param name name
     * @param service service
     * @return SwitchYardException
     */
    @Message(id = 12228 , value = "Service registration with name %s hides %s")
    SwitchYardException serviceRegHidesService(String name, String service);


}
