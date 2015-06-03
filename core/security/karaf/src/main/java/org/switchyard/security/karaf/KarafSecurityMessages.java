package org.switchyard.security.karaf;

import org.jboss.logging.Messages;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * <p/>
 * This file is using the subset 14900-14999 for logger messages.
 * <p/>
 */
@MessageBundle(projectCode = "SWITCHYARD")
public interface KarafSecurityMessages {

    /**
     * Default messages.
     */
    KarafSecurityMessages MESSAGES = Messages.getBundle(KarafSecurityMessages.class);

    /**
     * credentialsNotSet method definition.
     * @return IllegalStateException
     */
    @Message(id = 14900, value = "Credentials not set")
    IllegalStateException credentialsNotSet();

}
