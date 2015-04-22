package org.switchyard.admin;

import org.jboss.logging.Messages;
import org.jboss.logging.annotations.MessageBundle;

/**
 * <p/>
 * This file is using the subset 10400-10599 for logger messages.
 * <p/>
 *
 */
@MessageBundle(projectCode = "SWITCHYARD")
public interface AdminMessages {
    /**
     * The default messages.
     */
    AdminMessages MESSAGES = Messages.getBundle(AdminMessages.class);

}
