package org.switchyard.component.sca;

import org.jboss.logging.Cause;
import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;
/**
 * <p/>
 * This file is using the subset 39200-39599 for logger messages.
 * <p/>
 *
 */
@MessageLogger(projectCode = "SWITCHYARD")
public interface SCALogger {
    /**
     * A root logger with the category of the package name.
     */
    SCALogger ROOT_LOGGER = Logger.getMessageLogger(SCALogger.class, SCALogger.class.getPackage().getName());

    /**
     * unableToResolveCacheContainer method definition.
     * @param cacheName the cacheName
     */
    @LogMessage(level = Level.INFO)
    @Message(id = 39200, value = "Unable to resolve cache-container %s.  clustering for <binding.sca> will not be available for services and references.")
    void unableToResolveCacheContainer(String cacheName);

    /**
     * failedToStartRemoteEndpointListenerForSCAEndpoints method definition.
     * @param ex the ex
     */
    @LogMessage(level = Level.WARN)
    @Message(id = 39201, value = "Failed to start remote endpoint listener for SCA endpoints.")
    void failedToStartRemoteEndpointListenerForSCAEndpoints(@Cause Exception ex);

    /**
     * failedToInitializeRemoteEndpointPublisher method definition.
     * @param ex the ex
     */
    @LogMessage(level = Level.WARN)
    @Message(id = 39202, value = "Failed to initialize remote endpoint publisher")
    void failedToInitializeRemoteEndpointPublisher(@Cause Exception ex);

    /**
     * failedToDestroyRemoteEndpointPublisher method definition.
     * @param ex the ex
     */
    @LogMessage(level = Level.WARN)
    @Message(id = 39203, value = "Failed to destroy remote endpoint publisher")
    void failedToDestroyRemoteEndpointPublisher(@Cause Exception ex);

    /**
     * cannotEnableClusteredSCABindingFor method definition.
     * @param serviceName the serviceName
     */
    @LogMessage(level = Level.WARN)
    @Message(id = 39204, value = "Cannot enable clustered SCA binding for %s.  No distributed cache is avaialble.")
    void cannotEnableClusteredSCABindingFor(String serviceName);

}

