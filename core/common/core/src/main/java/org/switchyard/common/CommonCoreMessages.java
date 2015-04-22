package org.switchyard.common;

import java.io.IOException;

import org.jboss.logging.Messages;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;
import org.switchyard.common.io.pull.PropertiesPuller.PropertiesType;

/**
 * <p/>
 * This file is using the subset 11400-11599 for logger messages.
 * <p/>
 *
 */
@MessageBundle(projectCode = "SWITCHYARD")
public interface CommonCoreMessages {
    /**
     * Default messages.
     */
    CommonCoreMessages MESSAGES = Messages.getBundle(CommonCoreMessages.class);

    /**
     * nameNull method definition.
     * @return IllegalArgumentException
     */
    @Message(id = 11400, value = "name == null")
    IllegalArgumentException nameNull();

    /**
     * invalidCall method definition.
     * @return RuntimeException
     */
    @Message(id = 11405, value = ("Invalid call.  Not a Java message type.  "
            + "Use isJavaMessageType before calling this method."))
    RuntimeException invalidCall();
    
    /**
     * couldNotDeleteFile method definition.
     * @param filename filename˙
     * @return IOException
     */
    @Message(id = 11406, value = ("Could not delete %s"))
    IOException couldNotDeleteFile(String filename);
    
    /**
     * unsupportedPropertiesTypeForMethod method definition.
     * @param propertiesType propertiesType
     * @param method method
     * @return IOException
     */
    @Message(id = 11407, value = ("Unsupported properties type %s for method %s"))
    IOException unsupportedPropertiesTypeForMethod(PropertiesType propertiesType, String method);
}
