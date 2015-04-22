package org.switchyard.runtime;

import static org.jboss.logging.Logger.Level.ERROR;
import static org.jboss.logging.Logger.Level.WARN;

import javax.transaction.SystemException;

import org.jboss.logging.Logger;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;
/**
 * <p/>
 * This file is using the subset 13800-13999 for logger messages.
 * <p/>
 *
 */
@MessageLogger(projectCode = "SWITCHYARD")
public interface RuntimeLogger {
    /**
     * Default runtime logger.
     */
    RuntimeLogger ROOT_LOGGER = Logger.getMessageLogger(RuntimeLogger.class, RuntimeLogger.class.getPackage().getName());

    /**
     * failedToSuspendTransactionOnExchange method definition.
     * @param se se
     */
    @LogMessage(level = ERROR) 
    @Message(id = 13800, value = "Failed to suspend transaction on exchange.")
    void failedToSuspendTransactionOnExchange(@Cause SystemException se);

    /**
     * failedToResumeTransaction method definition.
     * @param e e
     */
    @LogMessage(level = ERROR)
    @Message(id = 13801, value = "Failed to resume transaction after service invocation.")
    void failedToResumeTransaction(@Cause Exception e);

    /**
     * validatorFailed method definition.
     * @param validatorClassName validatorClassName
     * @param detail detail
     */
    @LogMessage(level = WARN)
    @Message(id = 13802, value = "Validator %s failed: %s")
    void validatorFailed(String validatorClassName, String detail);

    /**
     * faultGeneratedDuringExchange method definition.
     * @param faultContent faultContent
     */
    @LogMessage(level = WARN)
    @Message(id = 13803, value = "Fault generated during exchange without a handler: %s")
    void faultGeneratedDuringExchange(String faultContent);

    /**
     * handlerFailedHandleFault method definition.
     * @param refName refName
     * @param e e
     */
    @LogMessage(level = WARN)
    @Message(id = 13804, value = "Handler '%s' failed to handle fault.")
    void handlerFailedHandleFault(String refName, @Cause Exception e);

}
