package org.switchyard;

import static org.jboss.logging.Logger.Level.WARN;

import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger;

import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;
import javax.xml.namespace.QName;

/**
 * <p/>
 * This file is using the subset 10200-10399 for logger messages.
 * <p/>
 *
 */
@MessageLogger(projectCode = "SWITCHYARD")
public interface APILogger extends BasicLogger {
    /**
     * Basic root logger.
     */
    APILogger ROOT_LOGGER = Logger.getMessageLogger(APILogger.class, APILogger.class.getPackage().getName());

    /**
     * nullTransformResult method definition.
     * @param className class name
     * @param fromType from type
     * @param toType to type
     */
    @LogMessage(level = WARN)
    @Message(id = 10200, value= "Transformer '%s' returned a null transformation result when transforming from type " 
                + "'%s' to type '%s'. Check input payload matches requirements of the Transformer implementation.")
    void nullTransformResult(String className, QName fromType, QName toType);   
}
