package org.switchyard.tools.forge.plugin;

import org.jboss.logging.Logger;
import org.jboss.logging.annotations.MessageLogger;

/**
 * <p/>
 * This file is using the subset 15800-15999 for logger messages.
 * <p/>
 *
 */
@MessageLogger(projectCode = "SWITCHYARD")
public interface ForgePluginLogger {
    /**
     * Default root logger.
     */
    ForgePluginLogger ROOT_LOGGER = Logger.getMessageLogger(ForgePluginLogger.class, ForgePluginLogger.class.getPackage().getName());
}
