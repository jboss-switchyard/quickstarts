package org.switchyard.component.common;

import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;
/**
 * <p/>
 * This file is using the subset 34400-34499 for logger messages.
 * <p/>
 *
 */
@MessageLogger(projectCode = "SWITCHYARD")
public interface CommonCommonLogger {
    /**
     * A root logger with the category of the package name.
     */
    CommonCommonLogger ROOT_LOGGER = Logger.getMessageLogger(CommonCommonLogger.class, CommonCommonLogger.class.getPackage().getName());

    /**
     * couldNotInstantiateContextMapper method definition.
     * @param customClassName customClassName
     * @param eMessage eMessage
     */
    @LogMessage(level = Level.ERROR)
    @Message(id = 34400, value = "Could not instantiate ContextMapper: %s - %s")
    void couldNotInstantiateContextMapper(String customClassName, String eMessage);

    /**
     * couldNotInstantiateMessageComposer method definition.
     * @param customClassName customClassName
     * @param eMessage eMessage
     */
    @LogMessage(level = Level.ERROR)
    @Message(id = 34401, value = "Could not instantiate MessageComposer: %s - %s")
    void couldNotInstantiateMessageComposer(String customClassName, String eMessage);

    /**
     * couldNotInstantiateOperationSelector method definition.
     * @param customClassName customClassName
     * @param eMessage eMessage
     */
    @LogMessage(level = Level.ERROR)
    @Message(id = 34402, value = "Could not instantiate OperationSelector: %s - %s")
    void couldNotInstantiateOperationSelector(String customClassName, String eMessage);

}

