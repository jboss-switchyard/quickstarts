package org.switchyard.extensions.wsdl;

import org.jboss.logging.Logger;
import org.jboss.logging.annotations.MessageLogger;

/**
 * <p/>
 * This file is using the subset 13200-13399 for logger messages.
 * <p/>
 *
 */
@MessageLogger(projectCode = "SWITCHYARD")
public interface WSDLExtensionsLogger {
    /**
     * Default root logger.    
     */
    WSDLExtensionsLogger ROOT_LOGGER = Logger.getMessageLogger(WSDLExtensionsLogger.class, WSDLExtensionsLogger.class.getPackage().getName());
}
