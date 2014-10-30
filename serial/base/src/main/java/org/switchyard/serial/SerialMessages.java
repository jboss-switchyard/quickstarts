package org.switchyard.serial;

import org.jboss.logging.Messages;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;
import org.switchyard.SwitchYardException;

/**
 * <p/>
 * This file is using the subset 15200-15399 for logger messages.
 * <p/>
 *
 */
@MessageBundle(projectCode = "SWITCHYARD")
public interface SerialMessages {

    /**
     * Default messages. 
     */
    SerialMessages MESSAGES = Messages.getBundle(SerialMessages.class);

    /**
     * couldNotInstantiateThrowable method definition.
     * @param className className
     * @param cause cause
     * @return SwitchYardException
     */
    @Message(id=15200, value = "Could not instantiate Throwable class [%s] because [%s]")
    SwitchYardException couldNotInstantiateThrowable(String className, String cause);

}
