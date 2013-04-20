package org.switchyard.config;

import java.util.Map;

import javax.xml.namespace.QName;

import org.jboss.logging.Messages;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * <p/>
 * This file is using the subset 11600-11799 for logger messages.
 * <p/>
 *
 */
@MessageBundle(projectCode = "SWITCHYARD")
public interface ConfigMessages {
    /**
     * Default messages.
     */
    ConfigMessages MESSAGES = Messages.getBundle(ConfigMessages.class);

    /**
     * configurationString method definition.
     * @param qname qname
     * @param ids ids
     * @return Localized string
     */
    @Message(id = 11600, value = "Key [_qname=%s, _ids=%s]")
    String configurationString(QName qname, Map<String,Object> ids);

    /**
     * cannotFindMarshaller method definition.
     * @param marshaller marshaller
     * @param loader loader
     * @return IllegalArgumentException
     */
    @Message(id = 11601, value = "Can not find marshaller %s using classloader %s")
    IllegalArgumentException cannotFindMarshaller(String marshaller, ClassLoader loader);

    /**
     * modelInvalid method definition.
     * @param className class name
     * @param message message
     * @param t t
     * @return RuntimeException
     */ 
    @Message(id = 11602, value = "Model [%s] is invalid: %s")
    RuntimeException modelInvalid(String className, String message, @Cause Throwable t);
}

