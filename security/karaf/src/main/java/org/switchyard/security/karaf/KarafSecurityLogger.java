package org.switchyard.security.karaf;

import static org.jboss.logging.Logger.Level.WARN;

import org.jboss.logging.Logger;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;

/**
 * <p/>
 * This file is using the subset 14800-14899 for logger messages.
 * <p/>
 */
@MessageLogger(projectCode = "SWITCHYARD")
public interface KarafSecurityLogger {

    /**
     * Default root logger.
     */
    KarafSecurityLogger ROOT_LOGGER = Logger.getMessageLogger(KarafSecurityLogger.class, KarafSecurityLogger.class.getPackage().getName());

    /**
     * switchyardDomainNotMatchKarafDomain method definition.
     * @param switchYardDomain switchYardDomain
     * @param karafDomain karafDomain
     */
    @LogMessage(level = WARN)
    @Message(id = 14800, value = "SwitchYard security domain (%s) does not match Karaf security domain (%s).")
    void switchyardDomainNotMatchKarafDomain(String switchYardDomain, String karafDomain);

}
