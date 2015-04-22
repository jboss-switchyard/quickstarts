package org.switchyard.admin;

import org.jboss.logging.Logger;

import org.jboss.logging.annotations.MessageLogger;
/**
 * <p/>
 * This file is using the subset 10600-10799 for logger messages.
 * <p/>
 *
 */
@MessageLogger(projectCode = "SWITCHYARD")
public interface AdminLogger {
    /**
     * A root logger with the category of the package name.
     */
    AdminLogger ROOT_LOGGER = Logger.getMessageLogger(AdminLogger.class, AdminLogger.class.getPackage().getName());
}
