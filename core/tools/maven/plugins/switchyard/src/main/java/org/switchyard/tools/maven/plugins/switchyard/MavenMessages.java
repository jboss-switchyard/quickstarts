package org.switchyard.tools.maven.plugins.switchyard;

import org.jboss.logging.Messages;
import org.jboss.logging.annotations.MessageBundle;

/**
 * <p/>
 * This file is using the subset 16400-16599 for logger messages.
 * <p/>
 *
 */
@MessageBundle(projectCode = "SWITCHYARD")
public interface MavenMessages {
    /**
     * Default messages. 
     */
    MavenMessages MESSAGES = Messages.getBundle(MavenMessages.class);
}
