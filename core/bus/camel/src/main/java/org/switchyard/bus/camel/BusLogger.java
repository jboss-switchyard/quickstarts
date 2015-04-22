package org.switchyard.bus.camel;

import static org.jboss.logging.Logger.Level.ERROR;
import static org.jboss.logging.Logger.Level.INFO;

import org.jboss.logging.Logger;

import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;
/**
 * <p/>
 * This file is using the subset 11000-11199 for logger messages.
 * <p/>
 *
 */
@MessageLogger(projectCode = "SWITCHYARD")
public interface BusLogger {
    /**
     * Basic root logger.
     */
    BusLogger ROOT_LOGGER = Logger.getMessageLogger(BusLogger.class, BusLogger.class.getPackage().getName());

    /**
     * exceptionDuringFaultResponse method definition.
     * @param t t
     */
    @LogMessage(level = ERROR) 
    @Message(id = 11000, value="Unexpected exception thrown during handling FAULT response. "
            + "This exception can not be handled, thus it's marked as handled and only logged. "
            + "If you don't want see messages like this consider handling "
            + "exceptions in your handler logic")
    void exceptionDuringFaultResponse(@Cause Throwable t);

    /**
     * listenerFailedHandleException method definition.
     * @param key key
     * @param klass klass
     */
    @LogMessage(level = ERROR) 
    @Message(id = 11001, value="Error listener %s failed to handle exception %s")
    void listenerFailedHandleException(String key, Class klass);

    /**
     * removeRoute method definition.
     * @param endpoint endpoint
     */
    @LogMessage(level = INFO)
    @Message(id = 11002, value="Removing route %s")
    void removeRoute(String endpoint);
    
    /**
     * alreadyInFaultState method definition.
     * @param error error
     */
    @LogMessage(level = ERROR)
    @Message(id = 11003, value="Interceptor threw an exception while exchange is already in fault state %s")
    void alreadyInFaultState(Exception error);

    /**
     * failedToHandlException method definition.
     * @param listenerKey listenerKey
     * @param exceptionClass exceptionClass
     */
    @LogMessage(level = ERROR)
    @Message(id = 11004, value="Error listener %s failed to handle exception %s")
    void failedToHandlException(String listenerKey, Class exceptionClass);

}
