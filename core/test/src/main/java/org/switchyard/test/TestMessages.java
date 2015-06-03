package org.switchyard.test;

import org.jboss.logging.Messages;
import org.jboss.logging.annotations.MessageBundle;

/**
 * <p/>
 * This file is using the subset 15600-15799 for logger messages.
 * <p/>
 *
 */
@MessageBundle(projectCode = "SWITCHYARD")
public interface TestMessages {
    /**
     * Default messages. 
     */
    TestMessages MESSAGES = Messages.getBundle(TestMessages.class);
}
