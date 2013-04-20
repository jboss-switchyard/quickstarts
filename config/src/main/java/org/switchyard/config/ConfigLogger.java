package org.switchyard.config;

import static org.jboss.logging.Logger.Level.WARN;

import org.jboss.logging.Logger;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;

/**
 * <p/>
 * This file is using the subset 11800-11999 for logger messages.
 * <p/>
 *
 */
@MessageLogger(projectCode = "SWITCHYARD")
public interface ConfigLogger {
    /**
     * Default root logger.
     */
    ConfigLogger ROOT_LOGGER = Logger.getMessageLogger(ConfigLogger.class, ConfigLogger.class.getPackage().getName());

    /**
     * missingComponentReference method definition.
     * @param reference reference
     * @param component component
     */
    @LogMessage(level = WARN) 
    @Message(id = 11800, value="Missing component reference [%s] for component [%s]")
    void missingComponentReference(String reference, String component);

    /**
     * missingComponentService method definition.
     * @param componentServiceName componentServiceName
     * @param componentName componentName
     */
    @LogMessage(level = WARN)
    @Message(id = 11801, value="missing component service [%s] for component [%s]")
    void missingComponentService(String componentServiceName, String componentName);
}
