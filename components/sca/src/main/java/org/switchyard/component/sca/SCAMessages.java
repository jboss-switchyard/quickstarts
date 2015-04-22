package org.switchyard.component.sca;

import org.jboss.logging.Cause;
import org.jboss.logging.Messages;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;
import org.switchyard.HandlerException;
import org.switchyard.SwitchYardException;
/**
 * <p/>
 * This file is using the subset 39600-39999 for logger messages.
 * <p/>
 *
 */
@MessageBundle(projectCode = "SWITCHYARD")
public interface SCAMessages {
    /**
     * The default messages.
     */
    SCAMessages MESSAGES = Messages.getBundle(SCAMessages.class);

    /**
     * invalidSCABindingForReferenceTargetServiceOrNamespaceMustBeSpecified method definition.
     * @return IllegalArgumentException
     */
    @Message(id = 39600, value = "Invalid SCA binding for reference - target service or namespace must be specified")
    IllegalArgumentException invalidSCABindingForReferenceTargetServiceOrNamespaceMustBeSpecified();

    /**
     * failedToResolveServiceInDomain method definition.
     * @param serviceName the serviceName
     * @return SwitchYardException
     */
    @Message(id = 39601, value = "Failed to resolve service in domain %s")
    SwitchYardException failedToResolveServiceInDomain(String serviceName);

    /**
     * referenceBindingNotStarted method definition.
     * @param referenceName referenceName
     * @param bindingName bindingName
     * @return HandlerException
     */
    @Message(id = 39602, value = "Reference binding \"%s/%s\" is not started.")
    HandlerException referenceBindingNotStarted(String referenceName, String bindingName);

    /**
     * serviceReferenceNotFoundInDomain method definition.
     * @param serviceName the serviceName
     * @param exchangeProviderDomainName exchangeProviderDomainName
     * @return HandlerException
     */
    @Message(id = 39604, value = "Service reference %s not found in domain %s")
    HandlerException serviceReferenceNotFoundInDomain(String serviceName, String exchangeProviderDomainName);

    /**
     * loadBalanceClassDoesNotImplementLoadBalanceStrategy method definition.
     * @param strategy the strategy
     * @return SwitchYardException
     */
    @Message(id = 39605, value = "Load balance class does not implement LoadBalanceStrategy: %s")
    SwitchYardException loadBalanceClassDoesNotImplementLoadBalanceStrategy(String strategy);

    /**
     * unableToInstantiateStrategyClass method definition.
     * @param strategy the strategy
     * @param ex the ex
     * @return SwitchYardException
     */
    @Message(id = 39606, value = "Unable to instantiate strategy class: %s")
    SwitchYardException unableToInstantiateStrategyClass(String strategy, @Cause Exception ex);

    /**
     * requiredHeaderIsMissingOrEmpty method definition.
     * @param serviceHeader serviceHeader
     * @return SwitchYardException
     */
    @Message(id = 39607, value = "Required '%s' header is missing or empty")
    SwitchYardException requiredHeaderIsMissingOrEmpty(String serviceHeader);

    /**
     * unableToFindServiceDomainForService method definition.
     * @param service the service
     * @return SwitchYardException
     */
    @Message(id = 39608, value = "Unable to find ServiceDomain for service: %s. Verify the service name and namespace are registered in the runtime.")
    SwitchYardException unableToFindServiceDomainForService(String service);

    /**
     * runtimeFaultOccurredWithoutExceptionDetails method definition.
     * @return HandlerException
     */
    @Message(id = 39609, value = "Runtime fault occurred without exception details!")
    HandlerException runtimeFaultOccurredWithoutExceptionDetails();
}

