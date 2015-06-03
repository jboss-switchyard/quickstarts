package org.switchyard.component.rules;

import org.jboss.logging.Logger;
import org.jboss.logging.annotations.MessageLogger;
/**
 * <p/>
 * This file is using the subset 38400-38799 for logger messages.
 * <p/>
 *
 */
@MessageLogger(projectCode = "SWITCHYARD")
public interface RulesLogger {
    /**
     * A root logger with the category of the package name.
     */
    RulesLogger ROOT_LOGGER = Logger.getMessageLogger(RulesLogger.class, RulesLogger.class.getPackage().getName());

}

