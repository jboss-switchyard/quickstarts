package org.switchyard.deploy.cdi;

import org.jboss.logging.Messages;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

import org.switchyard.SwitchYardException;

/**
 * <p/>
 * This file is using the subset 12600-12799 for logger messages.
 * <p/>
 *
 */
@MessageBundle(projectCode = "SWITCHYARD")
public interface CDIDeployMessages {
    /**
     * Default messages.
     */
    CDIDeployMessages MESSAGES = Messages.getBundle(CDIDeployMessages.class);

    /**
     * failedReadingConfig method definition.
     * @param e e
     * @return SwitchYardException
     */
    @Message(id = 12600, value = "Failed while reading config stream.")
    SwitchYardException failedReadingConfig(@Cause Exception e);
}
