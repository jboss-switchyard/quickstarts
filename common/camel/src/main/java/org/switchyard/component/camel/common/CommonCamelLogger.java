package org.switchyard.component.camel.common;

import org.jboss.logging.Cause;
import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;
/**
 * <p/>
 * This file is using the subset 34200-34299 for logger messages.
 * <p/>
 *
 */
@MessageLogger(projectCode = "SWITCHYARD")
public interface CommonCamelLogger {
    /**
     * A root logger with the category of the package name.
     */
    CommonCamelLogger ROOT_LOGGER = Logger.getMessageLogger(CommonCamelLogger.class, CommonCamelLogger.class.getPackage().getName());

    /**
     * failedToRemoveRouteForService method definition.
     * @param serviceName the serviceName
     * @param ex the ex
     */
    @LogMessage(level = Level.WARN)
    @Message(id = 34200, value = "Failed to remove route for service %s")
    void failedToRemoveRouteForService(String serviceName, @Cause Exception ex);

}

