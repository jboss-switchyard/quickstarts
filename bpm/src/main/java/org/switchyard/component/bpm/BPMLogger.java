package org.switchyard.component.bpm;

import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;
/**
 * <p/>
 * This file is using the subset 31600-31999 for logger messages.
 * <p/>
 *
 */
@MessageLogger(projectCode = "SWITCHYARD")
public interface BPMLogger {
    /**
     * A root logger with the category of the package name.
     */
    BPMLogger ROOT_LOGGER = Logger.getMessageLogger(BPMLogger.class, BPMLogger.class.getPackage().getName());

    /**
     * null method definition.
     * @param faultAction the faultAction
     * @param fa the fa
     * @param message the message
     * @param name the FaultAction.DEFAULT.name())
     */
    @LogMessage(level = Level.WARN)
    @Message(id = 31601, value = "Unknown %s: %s (%s). Defaulting to %s.")
    void unknownDefaultingTo(String faultAction, String fa, String message, String name);
}
