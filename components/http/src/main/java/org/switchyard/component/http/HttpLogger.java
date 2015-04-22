package org.switchyard.component.http;

import java.io.IOException;

import org.jboss.logging.Cause;
import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;
/**
 * <p/>
 * This file is using the subset 36000-36399 for logger messages.
 * <p/>
 *
 */
@MessageLogger(projectCode = "SWITCHYARD")
public interface HttpLogger {
    /**
     * A root logger with the category of the package name.
     */
    HttpLogger ROOT_LOGGER = Logger.getMessageLogger(HttpLogger.class, HttpLogger.class.getPackage().getName());

    /**
     * unableToLaunchStandaloneHttpServer method definition.
     * @param ioe the ioe
     */
    @LogMessage(level = Level.ERROR)
    @Message(id = 36000, value = "Unable to launch standalone http server")
    void unableToLaunchStandaloneHttpServer(@Cause IOException ioe);

    /**
     * unexpectedExceptionWhileReadingRequest method definition.
     * @param e the e
     */
    @LogMessage(level = Level.ERROR)
    @Message(id = 36001, value = "Unexpected Exception while reading request")
    void unexpectedExceptionWhileReadingRequest(@Cause IOException e);

    /**
     * unexpectedExceptionWhileWritingResponse method definition.
     * @param e the e
     */
    @LogMessage(level = Level.ERROR)
    @Message(id = 36002, value = "Unexpected Exception while writing response")
    void unexpectedExceptionWhileWritingResponse(@Cause Exception e);

    /**
     * unexpectedExceptionWhileHandlingHttpRequest method definition.
     * @param e the e
     */
    @LogMessage(level = Level.ERROR)
    @Message(id = 36003, value = "Unexpected Exception while handling http request")
    void unexpectedExceptionWhileHandlingHttpRequest(@Cause Exception e);

    /**
     * unexpectedExceptionInvokingSwitchyardServcie method definition.
     * @param e the e
     */
    @LogMessage(level = Level.ERROR)
    @Message(id = 36004, value = "Unexpected Exception invoking SwitchYard service")
    void unexpectedExceptionInvokingSwitchyardServcie(@Cause Exception e);

}

