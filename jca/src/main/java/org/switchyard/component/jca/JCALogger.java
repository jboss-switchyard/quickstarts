package org.switchyard.component.jca;

import javax.transaction.Transaction;

import org.jboss.logging.Cause;
import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;
/**
 * <p/>
 * This file is using the subset 36800-37199 for logger messages.
 * <p/>
 *
 */
@MessageLogger(projectCode = "SWITCHYARD")
public interface JCALogger {
    /**
     * A root logger with the category of the package name.
     */
    JCALogger ROOT_LOGGER = Logger.getMessageLogger(JCALogger.class, JCALogger.class.getPackage().getName());

    /**
     * errorInRelease method definition.
     * @param t the t
     */
    @LogMessage(level = Level.WARN)
    @Message(id = 36800, value = "Error in release ")
    void errorInRelease(@Cause Throwable t);

    /**
     * currentTransactionIsNotSameAsThe method definition.
     * @param currentTx the currentTx
     * @param _startedTx the _startedTx
     */
    @LogMessage(level = Level.WARN)
    @Message(id = 36801, value = "Current transaction %s is not same as the %s I have started. Replacing it.")
    void currentTransactionIsNotSameAsThe(Transaction currentTx, String _startedTx);

    /**
     * messageEndpointFailedToResumeOldTransaction method definition.
     * @param delegate delegate
     * @param currentTx the currentTx
     */
    @LogMessage(level = Level.WARN)
    @Message(id = 36802, value = "MessageEndpoint %s failed to resume old transaction %s")
    void messageEndpointFailedToResumeOldTransaction(String delegate, String currentTx);

    /**
     * failedToRetrieveTransactionStatus method definition.
     * @param e the e
     */
    @LogMessage(level = Level.WARN)
    @Message(id = 36803, value = "Failed to retrieve transaction status")
    void failedToRetrieveTransactionStatus(@Cause Exception e);

    /**
     * transactionHasBeenCommittedByReaperThread method definition.
     * @param counter counter
     */
    @LogMessage(level = Level.INFO)
    @Message(id = 36804, value = "Transaction has been committed by reaper thread [%s]")
    void transactionHasBeenCommittedByReaperThread(int counter);

    /**
     * failedToCommitExpiringTransaction method definition.
     * @param e the e
     */
    @LogMessage(level = Level.ERROR)
    @Message(id = 36805, value = "Failed to commit expiring transaction")
    void failedToCommitExpiringTransaction(@Cause Exception e);

    /**
     * failedToGetRecordFactory method definition.
     * @param eMessage eMessage
     */
    @LogMessage(level = Level.WARN)
    @Message(id = 36806, value = "Failed to get RecordFactory: %s")
    void failedToGetRecordFactory(String eMessage);

    /**
     * failedToCloseInteraction/Connection method definition.
     * @param eMessage eMessage
     */
    @LogMessage(level = Level.WARN)
    @Message(id = 36807, value = "Failed to close Interaction/Connection: %s")
    void failedToCloseInteractionConnection(String eMessage);

    /**
     * failedToCloseJMSSession/connection method definition.
     * @param eMessage eMessage
     */
    @LogMessage(level = Level.WARN)
    @Message(id = 36808, value = "Failed to close JMS session/connection: %s")
    void failedToCloseJMSSessionconnection(String eMessage);

    /**
     * failedToLoadJndiPropertiesFile method definition.
     * @param name file name
     * @param e cause
     */
    @LogMessage(level = Level.WARN)
    @Message(id = 36809, value = "Failed to load JNDI properties from file: %s")
    void failedToLoadJndiPropertiesFile(String name, @Cause Exception e);

    /**
     * contextDestinationNotFound method definition.
     * @param contextDest destination name retrieved from context property
     * @param defaultDest default destination name
     */
    @LogMessage(level = Level.WARN)
    @Message(id = 36810, value = "Destination '%s' could not be found. using '%s' instead")
    void contextDestinationNotFound(String contextDest, String defaultDest);

    /**
     * destinationNotFound method definition.
     * @param destination destination
     * @param purpose purpose of the destination
     */
    @LogMessage(level = Level.WARN)
    @Message(id = 36811, value = "Destination '%s' was not found for '%s'. Ignoring...")
    void destinationNotFound(String destination, String purpose);

    /**
     * failedToSendMessage method definition.
     * @param destination destination
     * @param eMessage exception message
     */
    @LogMessage(level = Level.WARN)
    @Message(id = 36812, value = "Failed to send a message to '%s': %s")
    void failedToSendMessage(String destination, String eMessage);

}

