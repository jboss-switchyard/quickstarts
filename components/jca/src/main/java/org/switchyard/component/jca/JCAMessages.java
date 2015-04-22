package org.switchyard.component.jca;

import javax.naming.NamingException;

import org.jboss.logging.Cause;
import org.jboss.logging.Messages;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;
import org.switchyard.HandlerException;
import org.switchyard.SwitchYardException;
/**
 * <p/>
 * This file is using the subset 37200-37599 for logger messages.
 * <p/>
 *
 */
@MessageBundle(projectCode = "SWITCHYARD")
public interface JCAMessages {
    /**
     * The default messages.
     */
    JCAMessages MESSAGES = Messages.getBundle(JCAMessages.class);

    /**
     * theSizeOfMessageContentExceedsBytesThatIsNotSupportedByThisMessageComposer method definition.
     * @param maxValue maxValue
     * @return SwitchYardException
     */
    @Message(id = 37200, value = "The size of message content exceeds %s bytes, that is not supported by this MessageComposer")
    SwitchYardException theSizeOfMessageContentExceedsBytesThatIsNotSupportedByThisMessageComposer(String maxValue);

    /**
     * resourceAdapterRepositoryMustBeInjectedToActivateJCAComponent method definition.
     * @return IllegalStateException
     */
    @Message(id = 37202, value = "ResourceAdapterRepository must be injected to activate JCA component.")
    IllegalStateException resourceAdapterRepositoryMustBeInjectedToActivateJCAComponent();

    /**
     * unableToFindTransactionManagerInJNDIAt method definition.
     * @param transactionManager transactionManager
     * @param e the e
     * @return IllegalArgumentException
     */
    @Message(id = 37203, value = "Unable to find TransactionManager in JNDI at %s")
    IllegalArgumentException unableToFindTransactionManagerInJNDIAt(String transactionManager, @Cause NamingException e);

    /**
     * uniqueKeyForResourceAdapter method definition.
     * @param raName the raName
     * @return IllegalArgumentException
     */
    @Message(id = 37204, value = "Unique key for ResourceAdapter '%s' couldn't be found.")
    IllegalArgumentException uniqueKeyForResourceAdapter(String raName);

    /**
     * listenerTypeIsNotSupportedByResourceAdapter method definition.
     * @param listenerTypeName listenerTypeName
     * @param raName the raName
     * @return IllegalArgumentException
     */
    @Message(id = 37206, value = "listener type %s is not supported by ResourceAdapter %s")
    IllegalArgumentException listenerTypeIsNotSupportedByResourceAdapter(String listenerTypeName, String raName);

    /**
     * couldnTAcquireTheResourceAdapter method definition.
     * @param raName the raName
     * @param e the e
     * @return IllegalArgumentException
     */
    @Message(id = 37207, value = "Couldn't acquire the ResourceAdapter '%s'.")
    IllegalArgumentException couldnTAcquireTheResourceAdapter(String raName, @Cause Exception e);

    /**
     * endpointClass method definition.
     * @param endpointClassName the endpointClassName
     * @param e the e
     * @return IllegalArgumentException
     */
    @Message(id = 37208, value = "Endpoint class '%s' couldn't be instantiated.")
    IllegalArgumentException endpointClass(String endpointClassName, @Cause Exception e);

    /**
     * nonManagedScenarioIsNotSupportedYet method definition.
     * @return IllegalArgumentException
     */
    @Message(id = 37209, value = "Non-Managed Scenario is not supported yet")
    IllegalArgumentException nonManagedScenarioIsNotSupportedYet();

    /**
     * outboundProcessorClass method definition.
     * @param processorClassName the processorClassName
     * @param e the e
     * @return IllegalArgumentException
     */
    @Message(id = 37212, value = "Outbound Processor class '%s' couldn't be instantiated.")
    IllegalArgumentException outboundProcessorClass(String processorClassName, @Cause Exception e);

    /**
     * referenceBindingNotStarted method definition.
     * @param referenceName the referenceName
     * @param bindingName the bindingName
     * @return HandlerException
     */
    @Message(id = 37213, value = "Reference binding \"%s/%s\" is not started.")
    HandlerException referenceBindingNotStarted(String referenceName, String bindingName);

    /**
     * initializeMustBeCalledBeforeExchange method definition.
     * @return IllegalStateException
     */
    @Message(id = 37214, value = "initialize() must be called before exchange.")
    IllegalStateException initializeMustBeCalledBeforeExchange();

    /**
     * missingAfterDeliveryFromThePreviousBeforeDeliveryForMessageEndpoint method definition.
     * @param delegate the delegate
     * @return IllegalStateException
     */
    @Message(id = 37219, value = "Missing afterDelivery from the previous beforeDelivery for message endpoint %s")
    IllegalStateException missingAfterDeliveryFromThePreviousBeforeDeliveryForMessageEndpoint(String delegate);

    /**
     * afterDeliveryWithoutAPreviousBeforeDeliveryForMessageEndpoint method definition.
     * @param delegate delegate
     * @return IllegalStateException
     */
    @Message(id = 37221, value = "afterDelivery without a previous beforeDelivery for message endpoint %s")
    IllegalStateException afterDeliveryWithoutAPreviousBeforeDeliveryForMessageEndpoint(String delegate);

    /**
     * multipleMessageDeliveryBetweenBeforeAndAfterDeliveryIsNotAllowedForMessageEndpoint method definition.
     * @param delegate the delegate
     * @return IllegalStateException
     */
    @Message(id = 37223, value = "Multiple message delivery between before and after delivery is not allowed for message endpoint %s")
    IllegalStateException multipleMessageDeliveryBetweenBeforeAndAfterDeliveryIsNotAllowedForMessageEndpoint(String delegate);

    /**
     * threadCurrentThreadGetNameCouldnTAcquireAThreadLockSinceThisIsAlreadyInUseByAnotherThreadInUseThreadGetName method definition.
     * @param threadName threadName
     * @param objectName objectName
     * @param inUseThreadName inUseThreadName
     * @return IllegalStateException
     */
    @Message(id = 37224, value = "%s couldn't acquire a thread lock since %s is already in use by another thread: %s")
    IllegalStateException threadCurrentThreadGetNameCouldnTAcquireAThreadLockSinceThisIsAlreadyInUseByAnotherThreadInUseThreadGetName(String threadName, String objectName, String inUseThreadName);

    /**
     * methodNewTransactionCouldnTBeStartedDueToTheStatusOfExistingTransactionStatusCodeTxStatusSeeJavaxTransactionStatus method definition.
     * @return IllegalStateException
     */
    @Message(id = 37225, value = "")
    IllegalStateException methodNewTransactionCouldnTBeStartedDueToTheStatusOfExistingTransactionStatusCodeTxStatusSeeJavaxTransactionStatus();

    /**
     * batchCommitModeCannotBeUsedWithSourceManagedTransactionPleaseTurnOffTheBatchCommit method definition.
     * @return IllegalStateException
     */
    @Message(id = 37226, value = "Batch commit mode cannot be used with source managed transaction. Please turn off the batch commit.")
    IllegalStateException batchCommitModeCannotBeUsedWithSourceManagedTransactionPleaseTurnOffTheBatchCommit();

    /**
     * recordTypeIsNotSupported method definition.
     * @param recordTypeName the recordTypeName
     * @return SwitchYardException
     */
    @Message(id = 37227, value = "record type '%sis not supported")
    SwitchYardException recordTypeIsNotSupported(String recordTypeName);

    /**
     * couldNotInitializeConnectionSpec method definition.
     * @param e the e
     * @return IllegalArgumentException
     */
    @Message(id = 37228, value = "Could not initialize ConnectionSpec")
    IllegalArgumentException couldNotInitializeConnectionSpec(@Cause Exception e);

    /**
     * couldNotInitializeInteractionSpec method definition.
     * @param e the e
     * @return IllegalArgumentException
     */
    @Message(id = 37229, value = "Could not initialize InteractionSpec")
    IllegalArgumentException couldNotInitializeInteractionSpec(@Cause Exception e);

    /**
     * failedToInitialize method definition.
     * @param className className
     * @param e the e
     * @return SwitchYardException
     */
    @Message(id = 37230, value = "Failed to initialize %s")
    SwitchYardException failedToInitialize(String className, @Cause Exception e);

    /**
     * failedToProcessCCIOutboundInteraction method definition.
     * @param e the e
     * @return HandlerException
     */
    @Message(id = 37231, value = "Failed to process CCI outbound interaction")
    HandlerException failedToProcessCCIOutboundInteraction(@Cause Exception e);

    /**
     * destinationPropertyMustBeSpecifiedInProcessorProperties method definition.
     * @return SwitchYardException
     */
    @Message(id = 37232, value = "destination property must be specified in Processor properties")
    SwitchYardException destinationPropertyMustBeSpecifiedInProcessorProperties();

    /**
     * failedToProcessJMSOutboundInteraction method definition.
     * @param e the e
     * @return HandlerException
     */
    @Message(id = 37234, value = "Failed to process JMS outbound interaction")
    HandlerException failedToProcessJMSOutboundInteraction(@Cause Exception e);
    
    /**
     * theSizeOfMessageContentExceedsBytesThatIsNotSupportedByThisOperationSelector method definition.
     * @param maxValue maxValue
     * @return Exception
     */
    @Message(id = 37235, value = "The size of message content exceeds %s bytes, that is not supported by this OperationSelector")
    Exception theSizeOfMessageContentExceedsBytesThatIsNotSupportedByThisOperationSelector(String maxValue);

    /**
     * noOperationSelectorConfigured method definition.
     * @param operations operations
     * @return SwitchYardException
     */
    @Message(id = 37236, value = "No operationSelector was configured for the JCA Component and the Service Interface contains more than one operation: "
            + "%s.   Please add an operationSelector element.")
    SwitchYardException noOperationSelectorConfigured(String operations);

    /**
     * noInboundConnectionConfigured method definition.
     * @return IllegalArgumentException
     */
    @Message(id = 37237, value = "No inboundConnection was configured for the JCA Component")
    IllegalArgumentException noInboundConnectionConfigured();

    /**
     * noResourceAdapterConfigured method definition.
     * @return IllegalArgumentException
     */
    @Message(id = 37238, value = "No resourceAdapter was configured for the JCA Component")
    IllegalArgumentException noResourceAdapterConfigured();

    /**
     * noResourceAdapterNameConfigured method definition.
     * @return IllegalArgumentException
     */
    @Message(id = 37239, value = "No resourceAdapter name was configured for the JCA Component")
    IllegalArgumentException noResourceAdapterNameConfigured();

    /**
     * noInboundInteractionConfigured method definition.
     * @return IllegalArgumentException
     */
    @Message(id = 37240, value = "No inboundInteraction was configured for the JCA Component")
    IllegalArgumentException noInboundInteractionConfigured();

    /**
     * noListenerClassFound method definition. 
     * @param listener listener class name
     * @param e root cause
     * @return IllegalArgumentException
     */
    @Message(id = 37241, value = "Listener class '%s' could not be loaded.")
    IllegalArgumentException noListenerClassFound(String listener, @Cause Exception e);

    /**
     * noEndpointConfigured method definition.
     * @return IllegalArgumentException
     */
    @Message(id = 37242, value = "No endpoint was configured for the JCA Component")
    IllegalArgumentException noEndpointConfigured();

    /**
     * noOutboundInteractionConfigured method definition.
     * @return IllegalArgumentException
     */
    @Message(id = 37243, value = "No outboundInteraction was configured for the JCA Component")
    IllegalArgumentException noOutboundInteractionConfigured();

    /**
     * noProcessorConfigured method definition.
     * @return IllegalArgumentException
     */
    @Message(id = 37244, value = "No processor was configured for the JCA Component")
    IllegalArgumentException noProcessorConfigured();

    /**
     * noConnectionConfigured method definition.
     * @return IllegalArgumentException
     */
    @Message(id = 37245, value = "No connection was configured for the JCA Component")
    IllegalArgumentException noConnectionConfigured();

    /**
     * noOutboundConnectionConfigured method definition.
     * @return IllegalArgumentException
     */
    @Message(id = 37246, value = "No outboundConnection was configured for the JCA Component")
    IllegalArgumentException noOutboundConnectionConfigured();

}

