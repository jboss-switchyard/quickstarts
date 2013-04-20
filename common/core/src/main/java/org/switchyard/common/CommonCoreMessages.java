package org.switchyard.common;

import java.io.IOException;
import java.util.Collection;

import org.jboss.logging.Messages;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

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
     * compoundClassLoaderString method definition.
     * @param loaders loaders
     * @return Localized string
     */
    @Message(id = 11401, value = "CompoundClassloader %s")
    String compoundClassLoaderString(Collection<ClassLoader> loaders);

    /**
     * methodAccessString method definition.
     * @param name name
     * @param klass klass
     * @param readable readable
     * @param writeable writeable
     * @return Localized string
     */
    @Message(id = 11402, value = "MethodAccess(name=%s, type=%s, readable=%b, writeable=%b)")
    String methodAccessString(String name, String klass, boolean readable, boolean writeable);

    /**
     * fieldAccessString method definition.
     * @param name name
     * @param klass klass
     * @param readable readable
     * @param writeable writeable
     * @return Localized string
     */
    @Message(id = 11403, value = "FieldAccess(name=%s, type=%s, readable=%b, writeable=%b)")
    String fieldAccessString(String name, String klass, boolean readable, boolean writeable);

    /**
     * beanAccessString method definition.
     * @param name name
     * @param klass klass 
     * @param readable readable
     * @param writeable writeable
     * @return Localized string
     */
    @Message(id = 11404, value = "BeanAccess(name=%s, type=%s, readable=%b, writeable=%b)")
    String beanAccessString(String name, String klass, boolean readable, boolean writeable);

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
}
