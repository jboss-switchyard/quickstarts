package org.switchyard.remote;

import static org.jboss.logging.Logger.Level.INFO;
import static org.jboss.logging.Logger.Level.WARN;

import java.io.IOException;

import org.jboss.logging.Logger;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;

/**
 * <p/>
 * This file is using the subset 13600-13799 for logger messages.
 * <p/>
 *
 */
@MessageLogger(projectCode = "SWITCHYARD")
public interface RemoteLogger {
    /**
     * Default root logger. 
     */
    RemoteLogger ROOT_LOGGER = Logger.getMessageLogger(RemoteLogger.class, RemoteLogger.class.getPackage().getName());

    /**
     * remoteEndpointRegistered method definition.
     * @param cacheKey cacheKey
     */
    @LogMessage(level = INFO) 
    @Message(id = 13600, value="Remote endpoint %s is already registered in the cache")
    void remoteEndpointRegistered(String cacheKey);

    /**
     * failedAddEndpoint method definition.
     * @param cacheKey cacheKey
     * @param ioEx ioEx
     */
    @LogMessage(level = WARN)
    @Message(id = 13601, value="Failed to add remote endpoint %s to registry.")
    void failedAddEndpoint(String cacheKey, @Cause IOException ioEx);
}
