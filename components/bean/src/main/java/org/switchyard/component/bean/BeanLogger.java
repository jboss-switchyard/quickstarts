package org.switchyard.component.bean;

import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;
/**
 * <p/>
 * This file is using the subset 30000-30399 for logger messages.
 * <p/>
 *
 */
@MessageLogger(projectCode = "SWITCHYARD")
public interface BeanLogger {
    /**
     * A root logger with the category of the package name.
     */
    BeanLogger ROOT_LOGGER = Logger.getMessageLogger(BeanLogger.class, BeanLogger.class.getPackage().getName());

    /**
     * propertyHasIncompatibleTypeBean method definition.
     * @param propAnnoName propAnnoName
     * @param serviceMetadataClassName the serviceMetadataClassName
     * @param fieldTypeName the fieldTypeName
     * @param propertyClassName the propertyClassName
     */
    @LogMessage(level = Level.WARN)
    @Message(id = 30000, value = "Property '%s' has incompatible type: Bean '%s' is expecting '%s', but was '%s'. ignoring...")
    void propertyHasIncompatibleTypeBean(String propAnnoName, String serviceMetadataClassName, String fieldTypeName, String propertyClassName);

}

