package org.switchyard.serial;

import org.jboss.logging.Logger;
import org.jboss.logging.annotations.MessageLogger;

/**
 * <p/>
 * This file is using the subset 15000-15199 for logger messages.
 * <p/>
 *
 */
@MessageLogger(projectCode = "SWITCHYARD")
public interface SerialLogger {
    /**
     * Default root logger.
     */
    SerialLogger ROOT_LOGGER = Logger.getMessageLogger(SerialLogger.class, SerialLogger.class.getPackage().getName());
}
