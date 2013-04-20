package org.switchyard.tools.forge.plugin;

import org.jboss.logging.Messages;
import org.jboss.logging.annotations.MessageBundle;

/**
 * <p/>
 * This file is using the subset 16000-16199 for logger messages.
 * <p/>
 *
 */
@MessageBundle(projectCode = "SWITCHYARD")
public interface ForgePluginMessages {
    /**
     * Default messages. 
     */
    ForgePluginMessages MESSAGES = Messages.getBundle(ForgePluginMessages.class);
}
