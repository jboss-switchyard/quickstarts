package org.switchyard.validate;

import static org.jboss.logging.Logger.Level.WARN;

import org.jboss.logging.Logger;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;

/**
 * <p/>
 * This file is using the subset 17000-17199 for logger messages.
 * <p/>
 *
 */
@MessageLogger(projectCode = "SWITCHYARD")
public interface ValidateLogger {
    /**
     * Default root logger. 
     */
    ValidateLogger ROOT_LOGGER = Logger.getMessageLogger(ValidateLogger.class, ValidateLogger.class.getPackage().getName());

    /**
     * schemaCatalogNotLocated method definition.
     * @param file file
     */
    @LogMessage(level = WARN)
    @Message(id = 17000, value = "Schema catalog %s could not be located.  Ignoring.")
    void schemaCatalogNotLocated(String file);

    /**
     * schemaFileNotLocated method definition.
     * @param file file
     */
    @LogMessage(level = WARN)
    @Message(id = 17001, value = "Schema file %s could not be located.  Ignoring.")
    void schemaFileNotLocated(String file);

    /**
     * warningDuringValidation method definition.
     * @param warning warning
     */
    @LogMessage(level = WARN)
    @Message(id = 17002, value = "Warning during validation: %s")
    void warningDuringValidation(String warning);

    /**
     * schemaCatalogNotParsed method definition.
     * @param file file
     * @param msg message
     */
    @LogMessage(level = WARN)
    @Message(id = 17003, value = "Schema catalog %s could not be parsed. Ignoring: %s")
    void schemaCatalogNotParsed(String file, String msg);
}
