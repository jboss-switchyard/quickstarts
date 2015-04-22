package org.switchyard.security.jboss;

import static org.jboss.logging.Logger.Level.ERROR;
import static org.jboss.logging.Logger.Level.WARN;

import org.jboss.logging.Logger;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;

/**
 * <p/>
 * This file is using the subset 14600-14699 for logger messages.
 * <p/>
 */
@MessageLogger(projectCode = "SWITCHYARD")
public interface JBossSecurityLogger {

    /**
     * Default root logger.
     */
    JBossSecurityLogger ROOT_LOGGER = Logger.getMessageLogger(JBossSecurityLogger.class, JBossSecurityLogger.class.getPackage().getName());

    /**
     * switchyardDomainNotMatchJBossDomain method definition.
     * @param switchYardDomain switchYardDomain
     * @param jbossDomain jbossDomain
     */
    @LogMessage(level = WARN)
    @Message(id = 14600, value = "SwitchYard security domain (%s) does not match JBoss security domain (%s).")
    void switchyardDomainNotMatchJBossDomain(String switchYardDomain, String jbossDomain);

    /**
     * clearSecurityContextAssociation method definition.
     * @param throwable throwable
     */
    @LogMessage(level = ERROR)
    @Message(id = 14601, value = "Problem clearing SecurityContextAssociation : %s")
    void clearSecurityContextAssociation(Throwable throwable);

}
