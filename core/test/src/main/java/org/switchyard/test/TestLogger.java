package org.switchyard.test;

import org.jboss.logging.Logger;
import org.jboss.logging.annotations.MessageLogger;

/**
 * <p/>
 * This file is using the subset 15400-15599 for logger messages.
 * <p/>
 *
 */
@MessageLogger(projectCode = "SWITCHYARD")
public interface TestLogger {
    /**
     * Default root logger.
     */
    TestLogger ROOT_LOGGER = Logger.getMessageLogger(TestLogger.class, TestLogger.class.getPackage().getName());
}
