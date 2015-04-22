package org.switchyard.component.rules;

import java.io.IOException;
import org.jboss.logging.Messages;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;
import org.switchyard.HandlerException;
/**
 * <p/>
 * This file is using the subset 38800-39199 for logger messages.
 * <p/>
 *
 */
@MessageBundle(projectCode = "SWITCHYARD")
public interface RulesMessages {
    /**
     * The default messages.
     */
    RulesMessages MESSAGES = Messages.getBundle(RulesMessages.class);

    /**
     * rulesInterfaceGetNameIsAClass@RulesOnlyAllowedOnInterfaces method definition.
     * @param rulesInterfaceName rulesInterfaceName
     * @return IOException
     */
    @Message(id = 38800, value =  "%s is a class. @Rules only allowed on interfaces.")
    IOException rulesInterfaceGetNameIsAClassRulesOnlyAllowedOnInterfaces(String rulesInterfaceName);

    /**
     * unknownEntryPoint method definition.
     * @param entryPoint the entryPoint
     * @return HandlerException
     */
    @Message(id = 38801, value = "Unknown entry point: %s; please check your rules source.")
    HandlerException unknownEntryPoint(String entryPoint);

    /**
     * unsupportedOperationType method definition.
     * @param operationType the operationType
     * @return HandlerException
     */
    @Message(id = 38802, value = "Unsupported operation type: %s")
    HandlerException unsupportedOperationType(String operationType);

}

