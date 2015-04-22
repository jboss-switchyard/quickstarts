package org.switchyard.transform.internal;

import static org.jboss.logging.Logger.Level.ERROR;
import static org.jboss.logging.Logger.Level.WARN;

import javax.xml.transform.TransformerException;

import org.jboss.logging.Logger;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;

/**
 * <p/>
 * This file is using the subset 16600-16799 for logger messages.
 * <p/>
 *
 */
@MessageLogger(projectCode = "SWITCHYARD")
public interface TransformLogger {
    /**
     * Default root logger.
     */
    TransformLogger ROOT_LOGGER = Logger.getMessageLogger(TransformLogger.class, TransformLogger.class.getPackage().getName());

    /**
     * warningDuringTransformation method definition.
     * @param te te
     */
    @LogMessage(level = WARN)
    @Message(id = 16600, value = "Warning during xslt transformation")
    void warningDuringTransformation(@Cause TransformerException te);

    /**
     * warningDuringCompilation method definition.
     * @param te te
     */
    @LogMessage(level = WARN)
    @Message(id = 16601, value = "Warning during xslt compilation")
    void warningDuringCompilation(@Cause TransformerException te);

    /**
     * exceptionClosingDOMInputSource method definition.
     * @param message message
     */
    @LogMessage(level = ERROR)
    @Message(id = 16602, value = "Exception while closing DOM InputSource: %s")
    void exceptionClosingDOMInputSource(String message);
}
