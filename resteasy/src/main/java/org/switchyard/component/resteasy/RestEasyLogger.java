package org.switchyard.component.resteasy;

import java.io.IOException;
import org.jboss.logging.Cause;
import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;
/**
 * <p/>
 * This file is using the subset 37600-37999 for logger messages.
 * <p/>
 *
 */
@MessageLogger(projectCode = "SWITCHYARD")
public interface RestEasyLogger {
    /**
     * A root logger with the category of the package name.
     */
    RestEasyLogger ROOT_LOGGER = Logger.getMessageLogger(RestEasyLogger.class, RestEasyLogger.class.getPackage().getName());

    /**
     * defaultRESTEasyMessageComposerDoesnTHandleMultipleInputParameters method definition.
     */
    @LogMessage(level = Level.WARN)
    @Message(id = 37600, value = "Default RESTEasy Message Composer doesn't handle multiple input parameters.")
    void defaultRESTEasyMessageComposerDoesnTHandleMultipleInputParameters();

    /**
     * unableToLaunchStandaloneHttpServer method definition.
     * @param ioe the ioe
     */
    @LogMessage(level = Level.ERROR)
    @Message(id = 37605, value = "Unable to launch standalone http server")
    void unableToLaunchStandaloneHttpServer(@Cause IOException ioe);

    /**
     * unableToFindURIBuilder method definition.
     * @param cnfe the cnfe
     */
    @LogMessage(level = Level.ERROR)
    @Message(id = 37606, value = "Unable to find URI builder")
    void unableToFindURIBuilder(@Cause ClassNotFoundException cnfe);

}

