package org.switchyard.deploy.cdi;

import org.jboss.logging.Logger;
import org.jboss.logging.annotations.MessageLogger;

/**
 * <p/>
 * This file is using the subset 12400-15999 for logger messages.
 * <p/>
 *
 */
@MessageLogger(projectCode = "SWITCHYARD")
public interface CDIDeployLogger {
    /**
     * Default root logger.
     */
    CDIDeployLogger ROOT_LOGGER = Logger.getMessageLogger(CDIDeployLogger.class, CDIDeployLogger.class.getPackage().getName());
}
