package org.switchyard;

import static org.jboss.logging.Logger.Level.ERROR;

import org.jboss.logging.Logger;
import org.jboss.logging.annotations.MessageLogger;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;

/**
 * <p/>
 * This file is using the subset 12000-12199 for logger messages.
 * <p/>
 *
 */
@MessageLogger(projectCode = "SWITCHYARD")
public interface BaseDeployLogger {
    /**
     * Default root logger.
     */
    BaseDeployLogger ROOT_LOGGER = Logger.getMessageLogger(BaseDeployLogger.class, BaseDeployLogger.class.getPackage().getName());

    /**
     * errorStoppingServiceBinding method definition.
     * @param e e 
     */
    @LogMessage(level = ERROR) 
    @Message(id = 10900, value="Error stopping service binding.")
    void errorStoppingServiceBinding(@Cause Throwable e);
    
    /**
     * errorStoppingServiceBinding method definition.
     * @param e e 
     */
    @LogMessage(level = ERROR) 
    @Message(id = 10901, value="Error deactivating service binding.")
    void errorDeactivatingServiceBinding(@Cause Throwable e);

    
    /**
     * errorStoppingServiceBinding method definition.
     * @param e e 
     */
    @LogMessage(level = ERROR) 
    @Message(id = 10902, value="Error stopping service.")
    void errorStoppingService(@Cause Throwable e);
    
    /**
     * errorStoppingServiceBinding method definition.
     * @param e e 
     */
    @LogMessage(level = ERROR) 
    @Message(id = 10903, value="Error deactivating service.")
    void errorDeactivatingService(@Cause Throwable e);
    

    /**
     * errorStoppingReferenceBinding method definition.
     * @param e e 
     */
    @LogMessage(level = ERROR) 
    @Message(id = 10904, value="Error stopping reference binding.")
    void errorStoppingReferenceBinding(@Cause Throwable e);
    
    /**
     * errorDeactivatingReferenceBinding method definition.
     * @param e e 
     */
    @LogMessage(level = ERROR) 
    @Message(id = 10905, value="Error deactivating reference binding.")
    void errorDeactivatingReferenceBinding(@Cause Throwable e);
}
