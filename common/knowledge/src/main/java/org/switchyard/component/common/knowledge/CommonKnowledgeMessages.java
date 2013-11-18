package org.switchyard.component.common.knowledge;

import org.jboss.logging.Messages;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;
import org.switchyard.SwitchYardException;

/**
 * <p/>
 * This file is using the subset 34700-34799 for logger messages.
 * <p/>
 *
 */
/**
 * @author tcunning
 *
 */
@MessageBundle(projectCode = "SWITCHYARD")
public interface CommonKnowledgeMessages {
    /**
     * The default messages.
     */
    CommonKnowledgeMessages MESSAGES = Messages.getBundle(CommonKnowledgeMessages.class);

    /**
     * unknownExpressionType method definition.
     * @param expressionType the expressionType
     * @return IllegalArgumentException
     */
    @Message(id = 34700, value = "Unknown expression type: %s")
    IllegalArgumentException unknownExpressionType(String expressionType);

    /**
     * serviceNameNull method definition.
     * @return SwitchYardException
     */
    @Message(id = 34701, value = "ServiceName == null")
    SwitchYardException serviceNameNull();

    /**
     * serviceDomainNull method definition.
     * @return SwitchYardException
     */
    @Message(id = 34702, value = "ServiceDomain == null")
    SwitchYardException serviceDomainNull();

    /**
     * serviceReferenceNull method definition.
     * @param serviceName the serviceName
     * @return SwitchYardException
     */
    @Message(id = 34703, value = "ServiceReference [%s] == null")
    SwitchYardException serviceReferenceNull(String serviceName);

    /**
     * manifestContainerRequiredInConfigurationForPersistentSessions method definition.
     * @return SwitchYardException
     */
    @Message(id = 34706, value = "manifest container required in configuration for persistent sessions")
    SwitchYardException manifestContainerRequiredInConfigurationForPersistentSessions();

    /**
     * containerScanIntervalMustBePositive method definition.
     * @return IllegalArgumentException
     */
    @Message(id = 34707, value = "container scanInterval must be positive")
    IllegalArgumentException containerScanIntervalMustBePositive();

    /**
     * couldNotUseNullNameToRegisterChannel method definition.
     * @param channelClassName channelClassName
     * @return SwitchYardException
     */
    @Message(id = 34708, value = "Could not use null name to register channel: %s")
    SwitchYardException couldNotUseNullNameToRegisterChannel(String channelClassName);

    /**
     * couldNotLoadListenerClass method definition.
     * @param listenerModelClass listenerModelClass
     * @return SwitchYardException
     */
    @Message(id = 34709, value = "Could not load listener class: %s")
    SwitchYardException couldNotLoadListenerClass(String listenerModelClass);

    /**
     * couldNotInstantiateListenerClass method definition.
     * @param listenerClassName listenerClassName
     * @return SwitchYardException
     */
    @Message(id = 34710, value = "Could not instantiate listener class: %s")
    SwitchYardException couldNotInstantiateListenerClass(String listenerClassName);

    /**
     * cannotRegisterOperation method definition.
     * @param type type
     * @param name name
     * @return SwitchYardException
     */
    @Message(id = 34711, value = "cannot register %s operation due to duplicate name: %s")
    SwitchYardException cannotRegisterOperation(String type, String name);
    
    /**
     * problemBuildingKnowledge method definition.
     * @return String
     */
    @Message(id = 34712, value = "Problem building knowledge")
    String problemBuildingKnowledge();

    /**
     * faultEncountered method definition.
     * @return String
     */
    @Message(id = 34713, value = "Fault encountered")
    String faultEncountered();
}

