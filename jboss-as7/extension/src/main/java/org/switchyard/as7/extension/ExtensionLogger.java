package org.switchyard.as7.extension;

import static org.jboss.logging.Logger.Level.ERROR;
import static org.jboss.logging.Logger.Level.WARN;

import org.jboss.logging.Cause;
import org.jboss.logging.Logger;

import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;
import org.jboss.modules.ModuleLoadException;
/**
 * <p/>
 * This file is using the subset 40000-40399 for logger messages.
 * <p/>
 *
 */
@MessageLogger(projectCode = "SWITCHYARD")
public interface ExtensionLogger {
    /**
     * A root logger with the category of the package name.
     */
    ExtensionLogger ROOT_LOGGER = Logger.getMessageLogger(ExtensionLogger.class, ExtensionLogger.class.getPackage().getName());

    /**
     * unableToDestroyWebContext method definition.
     * @param e cause
     */
    @LogMessage(level = ERROR)
    @Message(id = 40000, value = "Unable to destroy web context")
    void unableToDestroyWebContext(@Cause Exception e);

    /**
     * unableToDetermineHostAddress method definition.
     */
    @LogMessage(level = WARN)
    @Message(id = 40001, value = "Unable to determine host address from connector.  Using alias definition instead.")
    void unableToDetermineHostAddress();
    
    /**
     * unableToInstantiateClass method definition.
     * @param className class name
     * @param ie cause
     */
    @LogMessage(level = ERROR)
    @Message(id = 40002, value = "Unable to instantiate class %s")
    void unableToInstantiateClass(String className, @Cause InstantiationException ie);
    
    /**
     * unableToAccessConstructor method definition.
     * @param className class name 
     * @param iae cause
     */
    @LogMessage(level = ERROR)
    @Message(id = 40003, value = "Unable to access constructor for %s")
    void unableToAccessConstructor(String className, @Cause IllegalAccessException iae);

    /**
     * unableToLoadClass method definition.
     * @param className class name
     * @param cnfe cause
     */
    @LogMessage(level = ERROR)
    @Message(id = 40004, value = "Unable to load class %s")
    void unableToLoadClass(String className, @Cause ClassNotFoundException cnfe);

    /**
     * unableToLoadModule method definition.
     * @param moduleId module ID
     * @param mle cause
     */
    @LogMessage(level = ERROR)
    @Message(id = 40005, value = "Unable to load module %s")
    void unableToLoadModule(String moduleId, @Cause ModuleLoadException mle);
    
    /**
     * unableToStop method definition.
     * @param componentName component name
     * @param e cause
     */
    @LogMessage(level = ERROR)
    @Message(id = 40006, value = "Unable to stop %s")
    void unableToStop(String componentName, @Cause Exception e);
    
    /**
     * cannotReadPackage method definition.
     * @param packageName package name
     * @param e cause
     */
    @LogMessage(level = WARN)
    @Message(id = 40007, value = "Cannot read package: %s")
    void cannotReadPackage(String packageName, @Cause Exception e);
    
    /**
     * failedToParseURL method definition.
     * @param url url
     * @param e cause
     */
    @LogMessage(level = WARN)
    @Message(id = 40008, value = "Failed to parse URL: %s")
    void failedToParseURL(String url, @Cause Exception e);
    

}
