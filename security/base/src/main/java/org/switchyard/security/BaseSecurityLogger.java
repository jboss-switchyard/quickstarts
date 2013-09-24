package org.switchyard.security;

import static org.jboss.logging.Logger.Level.ERROR;
import static org.jboss.logging.Logger.Level.INFO;
import static org.jboss.logging.Logger.Level.WARN;

import javax.security.auth.login.LoginException;

import org.jboss.logging.Logger;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;

/**
 * <p/>
 * This file is using the subset 14200-14399 for logger messages.
 * <p/>
 *
 */
@MessageLogger(projectCode = "SWITCHYARD")
public interface BaseSecurityLogger {
    /**
     * Default root logger.
     */
    BaseSecurityLogger ROOT_LOGGER = Logger.getMessageLogger(BaseSecurityLogger.class, BaseSecurityLogger.class.getPackage().getName());

    /**
     * authenticateLoginException method definition.
     * @param message message
     * @param le le 
     */
    @LogMessage(level = ERROR)
    @Message(id = 14200, value = "authenticate LoginException: %s")
    void authenticateLoginException(String message, @Cause LoginException le);

    /**
     * usingSecurityProviderImplementation method definition.
     * @param implementationName implementationName
     */
    @LogMessage(level = INFO)
    @Message(id = 14201, value = "Using SecurityProvider implementation: %s")
    void usingSecurityProviderImplementation(String implementationName);

    /**
     * charSetNameIllegal method definition.
     * @param charsetName charsetName
     */
    @LogMessage(level = ERROR)
    @Message(id = 14202, value = "charsetName [%s] + is illegal or unsupported; using platform-default")
    void charSetNameIllegal(String charsetName);

    /**
     * charSetNull method definition.
     */
    @LogMessage(level = WARN)
    @Message(id = 14203, value = "charsetName is null; using platform-default")
    void charSetNull();

    /**
     * configurationNumberFormatException method definition.
     * @param name name
     * @param value value
     * @param nfe nfe
     */
    @LogMessage(level = ERROR)
    @Message(id = 14204, value = "configuration NumberFormatException: %s=[%s]")
    void configurationNumberFormatException(String name, String value, @Cause NumberFormatException nfe);
}
