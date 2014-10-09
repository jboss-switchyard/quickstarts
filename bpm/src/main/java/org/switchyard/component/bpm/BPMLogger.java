package org.switchyard.component.bpm;

import org.jboss.logging.Logger;
import org.jboss.logging.annotations.MessageLogger;
/**
 * <p/>
 * This file is using the subset 31600-31999 for logger messages.
 * <p/>
 *
 */
@MessageLogger(projectCode = "SWITCHYARD")
public interface BPMLogger {
    /**
     * A root logger with the category of the package name.
     */
    BPMLogger ROOT_LOGGER = Logger.getMessageLogger(BPMLogger.class, BPMLogger.class.getPackage().getName());

}
