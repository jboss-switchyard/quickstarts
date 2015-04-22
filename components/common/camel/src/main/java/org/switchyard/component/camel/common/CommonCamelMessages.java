package org.switchyard.component.camel.common;

import org.jboss.logging.Cause;
import org.jboss.logging.Messages;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;
import org.switchyard.HandlerException;
import org.switchyard.SwitchYardException;
/**
 * <p/>
 * This file is using the subset 34300-33999 for logger messages.
 * <p/>
 *
 */
@MessageBundle(projectCode = "SWITCHYARD")
public interface CommonCamelMessages {
    /**
     * The default messages.
     */
    CommonCamelMessages MESSAGES = Messages.getBundle(CommonCamelMessages.class);

    /**
     * invalidURINoBindingDataCreatorRegisteredForScheme method definition.
     * @param name the name
     * @param e the e
     * @return IllegalArgumentException
     */
    @Message(id = 34300, value = "Invalid URI, no BindingDataCreator registered for scheme: %s")
    IllegalArgumentException invalidURINoBindingDataCreatorRegisteredForScheme(String name, @Cause Exception e);

    /**
     * resolvingBindingDataCreatorForEndpointOfType method definition.
     * @param name the name
     * @param typeName typeName
     * @return IllegalArgumentException
     */
    @Message(id = 34301, value = "Resolving binding data creator for endpoint of type : %s detected type conflict: Not a BindingDataCreator implementation. Found: %s")
    IllegalArgumentException resolvingBindingDataCreatorForEndpointOfType(String name, String typeName);

    /**
     * failedToStartRouteForService method definition.
     * @param serviceName serviceName
     * @param ex the ex
     * @return SwitchYardException
     */
    @Message(id = 34303, value = "Failed to start route for service %s")
    SwitchYardException failedToStartRouteForService(String serviceName, @Cause Exception ex);

    /**
     * failedToStopRouteForService method definition.
     * @param serviceName serviceName
     * @param ex the ex
     * @return SwitchYardException
     */
    @Message(id = 34304, value = "Failed to stop route for service %s")
    SwitchYardException failedToStopRouteForService(String serviceName, @Cause Exception ex);

    /**
     * bindingArgumentMustNotBeNull method definition.
     * @return IllegalArgumentException
     */
    @Message(id = 34305, value = "binding argument must not be null")
    IllegalArgumentException bindingArgumentMustNotBeNull();

    /**
     * camelContextArgumentMustNotBeNull method definition.
     * @return IllegalArgumentException
     */
    @Message(id = 34306, value = "camelContext argument must not be null")
    IllegalArgumentException camelContextArgumentMustNotBeNull();

    /**
     * bindingUriMustNotBeNull method definition.
     * @return IllegalArgumentException
     */
    @Message(id = 34307, value = "binding uri must not be null")
    IllegalArgumentException bindingUriMustNotBeNull();

    /**
     * failedToStartCamelProducerTemplateFor method definition.
     * @param uri uri
     * @param e the e
     * @return SwitchYardException
     */
    @Message(id = 34308, value = "Failed to start Camel producer template for %s")
    SwitchYardException failedToStartCamelProducerTemplateFor(String uri, @Cause Exception e);

    /**
     * failedToStopCamelProducerTemplateFor method definition.
     * @param uri uri
     * @param e the e
     * @return SwitchYardException
     */
    @Message(id = 34309, value = "Failed to stop Camel producer template for %s")
    SwitchYardException failedToStopCamelProducerTemplateFor(String uri, @Cause Exception e);
    
    /**
     * referenceBindingIsNotStarted method definition.
     * @param referenceName the referenceName
     * @param bindingName the bindingName
     * @return HandlerException
     */
    @Message(id = 34310, value = "Reference binding \"%s/%s\" is not started.")
    HandlerException referenceBindingIsNotStarted(String referenceName, String bindingName);

    /**
     * camelExchangeFailedWithoutAnException method definition.
     * @param camelFault the camelFault
     * @return HandlerException
     */
    @Message(id = 34313, value = "camel exchange failed without an exception: %s")
    HandlerException camelExchangeFailedWithoutAnException(String camelFault);

    /**
     * configurationProvidesPasswordButDoNotSpecifyUser method definition.
     * @return IllegalArgumentException
     */
    @Message(id = 34314, value = "Configuration provides password but do not specify user")
    IllegalArgumentException configurationProvidesPasswordButDoNotSpecifyUser();

    /**
     * failedToParse method definition.
     * @param configName the configName
     * @param parseEx the parseEx
     * @return IllegalArgumentException
     */
    @Message(id = 34315, value = "Failed to parse %s as a date.")
    IllegalArgumentException failedToParse(String configName, @Cause java.text.ParseException parseEx);

    /**
     * couldNotCreateAJtaTransactionManagerAsNoTransactionManagerWasFoundJBOSSUSERTRANSACTION method definition.
     * @param userTransaction userTransaction
     * @param osgitm OSGi TransactionManager name
     * @param defaultUserTransaction defaultUserTransaction
     * @return SwitchYardException
     */
    @Message(id = 34318, value = "Could not create a JtaTransactionManager as no TransactionManager was found in JNDI. Tried [%s, %s, %s]")
    SwitchYardException couldNotCreateAJtaTransactionManagerAsNoTransactionManagerWasFoundJBOSSUSERTRANSACTION(String userTransaction, String osgitm, String defaultUserTransaction);

    /**
     * unexpectedExceptionRetrieving method definition.
     * @param name the name
     * @param e the e
     * @return SwitchYardException
     */
    @Message(id = 34319, value = "Unexpected Exception retrieving '%s' from JNDI namespace.")
    SwitchYardException unexpectedExceptionRetrieving(String name, @Cause Exception e);

    /**
     * unexpectedErrorClosingInitialContext method definition.
     * @param e the e
     * @return SwitchYardException
     */
    @Message(id = 34320, value = "Unexpected error closing InitialContext.")
    SwitchYardException unexpectedErrorClosingInitialContext(@Cause Exception e);

}

