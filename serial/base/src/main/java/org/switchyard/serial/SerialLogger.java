package org.switchyard.serial;

import static org.jboss.logging.Logger.Level.WARN;

import org.jboss.logging.Logger;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;

/**
 * <p/>
 * This file is using the subset 15000-15199 for logger messages.
 * <p/>
 *
 */
@MessageLogger(projectCode = "SWITCHYARD")
public interface SerialLogger {

    /**
     * Default root logger.
     */
    SerialLogger ROOT_LOGGER = Logger.getMessageLogger(SerialLogger.class, SerialLogger.class.getPackage().getName());

    /**
     * classUnsupportedByFactory method definition.
     * @param className className
     * @param factoryClassName factoryClassName
     */
    @LogMessage(level = WARN)
    @Message(id = 15000, value = "Class [%s] unsupported by Factory [%s]; returning null")
    void classUnsupportedByFactoryReturningNull(String className, String factoryClassName);

}
