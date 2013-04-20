package org.switchyard.bus.camel;

import java.util.List;
import java.util.Set;
import javax.xml.namespace.QName;
import org.apache.camel.Processor;

import org.jboss.logging.Messages;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;
import org.switchyard.ExchangeHandler;
import org.switchyard.SwitchYardException;

/**
 * <p/>
 * This file is using the subset 10800-10999 for logger messages.
 * <p/>
 *
 */
@MessageBundle(projectCode = "SWITCHYARD")
public interface BusMessages {
    /**
     * Default messages.
     */
    BusMessages MESSAGES = Messages.getBundle(BusMessages.class);

    /**
     * maxOneExceptionHandler method definition.
     * @param set set
     * @return SwitchYardException
     */
    @Message(id = 10800, value = "Only one exception handler can be defined. Found %s")
    SwitchYardException maxOneExceptionHandler(Set set);

    /**
     * failedToStartBus method definition.
     * @param e e
     * @return SwitchYardException
     */
    @Message(id = 10801, value = "Failed to start Camel Exchange Bus")
    SwitchYardException failedToStartBus(@Cause Exception e);

    /**
     * failedToStopBus method definition.
     * @param e e
     * @return SwitchYardException
     */
    @Message(id = 10802, value = "Failed to stop Camel Exchange Bus")
    SwitchYardException failedToStopBus(@Cause Exception e);

    /**
     * failedToCreateRoute method definition.
     * @param name name
     * @param e e
     * @return SwitchYardException
     */
    @Message(id = 10803, value = "Failed to create Camel route for service %s")
    SwitchYardException failedToCreateRoute(QName name, @Cause Exception e);

    /**
     * faultProcessorString method definition.
     * @param processor processor
     * @return String
     */
    @Message(id = 10804, value = "FaultProcessor [%s]")
    String faultProcessorString(Processor processor);

    /**
     * handlerProcessorString method definition.
     * @param handlers handlers
     * @return String
     */
    @Message(id = 10805, value = "HandlerProcessor [%s]")
    String handlerProcessorString(List<ExchangeHandler> handlers);
 
    /**
     * providerProcessorString method definition.
     * @param hashCode hashCode
     * @return String
     */
    @Message(id = 10806, value = "ProviderProcessor@ %d")
    String providerProcessorString(int hashCode);

    /**
     * methodMustBeOverridden method definition.
     * @return AbstractMethodError
     */
    @Message(id = 10807, value = "Method must be overriden")
    AbstractMethodError methodMustBeOverridden();
    
    /**
     * cannontChangeProviderMetadata method definition.
     * @return IllegalStateException
     */
    @Message(id = 10808, value = "Cannot change provider metadata after provider has been invoked!")
    IllegalStateException cannotChangeProviderMetadata();

    /**
     * cannotSendMessageTwice method definition.
     * @return IllegalStateException
     */
    @Message(id = 10809, value = "Can not send same message twice. Use Message.copy() instead")
    IllegalStateException cannotSendMessageTwice();

    /**
     * cannotSendMesageTwice method definition.
     * @return IllegalStateException
     */
    @Message(id = 10810, value = "CamelExchange accepts only CamelMessages")
    IllegalStateException camelExchangeOnlyCamelMessages();

    /**
     * nullTypeArgument method definition.
     * @return IllegalStateException
     */
    @Message(id = 10811, value = "null 'type' argument.")
    IllegalStateException nullTypeArgument();
    
    /**
     * cannotConvertNoTransformRegistry method definition.
     * @param className className
     * @param typeName typeName
     * @return SwitchYardException
     */
    @Message(id = 10812, value = "Cannot convert from '%s' to '%s'.  No TransformRegistry available.")
    SwitchYardException cannotConvertNoTransformRegistry(String className, String typeName);
    
    
    /**
     * transformerMustBeRegistered method definition.
     * @param className className
     * @param typeName typeName
     * @param fromType fromType
     * @param toType toType
     * @return SwitchYardException
     */
    @Message(id = 10813, value = "Cannot convert from '%s' to '%s'.  No registered Transformer available for transforming from '%s' to '%s'.  A Transformer must be registered.")
    SwitchYardException transformerMustBeRegistered(String className, String typeName, String fromType, String toType);
    

    /**
     * transformerReturnedNull method definition.
     * @param className className
     * @param typeName typeName
     * @param transformerName transformerName
     * @return SwitchYardException
     */
    @Message(id = 10814, value = "Error converting from '%s' to '%s'.  Transformer '%s' returned null.")
    SwitchYardException transformerReturnedNull(String className, String typeName, String transformerName);
    
    /**
     * transformerReturnedIncompatibleType method definition.
     * @param className className
     * @param typeName typeName
     * @param transformerName transformerName
     * @param transformedContentName transformedContentName
     * @return SwitchYardException
     */
    @Message(id = 10815, value = "Error converting from '%s' to '%s'.  Transformer '%s' returned incompatible type '%s'.")
    SwitchYardException transformerReturnedIncompatibleType(String className, String typeName, String transformerName, String transformedContentName);
    
    /**
     * transformerReturnedIncompatibleType method definition.
     * @return IllegalArgumentException
     */
    @Message(id = 10816, value = "Camel Bus accepts only CamelExchanges")
    IllegalArgumentException onlyCamelExchanges();
    
    
}
