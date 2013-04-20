package org.switchyard.common;

import static org.jboss.logging.Logger.Level.ERROR;
import static org.jboss.logging.Logger.Level.WARN;

import org.jboss.logging.Logger;

import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;

/**
 * <p/>
 * This file is using the subset 11200-11399 for logger messages.
 * <p/>
 *
 */
@MessageLogger(projectCode = "SWITCHYARD")
public interface CommonCoreLogger {
    /**
     * Default root logger.
     */
    CommonCoreLogger ROOT_LOGGER = Logger.getMessageLogger(CommonCoreLogger.class, CommonCoreLogger.class.getPackage().getName());

    /**
     * exceptionSystemProperties method definition.
     * @param se SecurityException
     */
    @LogMessage(level = ERROR) 
    @Message(id = 11200, value="SecurityException while getting System Properties; will default to empty Properties")
    void exceptionSystemProperties(@Cause SecurityException se);

    /**
     * unknownClasspathURL method definition.
     * @param path path
     */
    @LogMessage(level = WARN)
    @Message(id = 11201, value="Unknown Classpath URL File '%s'.")
    void unknownClasspathURL(String path);

    /**
     * problemReadingStream method definition.
     * @param url url
     * @param message message
     */
    @LogMessage(level = WARN)
    @Message(id = 11202, value="problem reading %s stream: %s")
    void problemReadingStream(String url, String message);

    /**
     * problemClosingStream method definition.
     * @param url url
     * @param message message
     */
    @LogMessage(level = WARN)
    @Message(id = 11203, value="problem closing %s stream: %s")
    void problemClosingStream(String url, String message);
}
