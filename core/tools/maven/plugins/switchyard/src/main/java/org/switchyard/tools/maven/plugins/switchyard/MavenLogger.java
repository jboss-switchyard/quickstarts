package org.switchyard.tools.maven.plugins.switchyard;

import org.jboss.logging.Logger;
import org.jboss.logging.annotations.MessageLogger;

/**
 * <p/>
 * This file is using the subset 16200-16399 for logger messages.
 * <p/>
 *
 */
@MessageLogger(projectCode = "SWITCHYARD")
public interface MavenLogger {
    /**
     * Default root logger.
     */
    MavenLogger ROOT_LOGGER = Logger.getMessageLogger(MavenLogger.class, MavenLogger.class.getPackage().getName());
}
