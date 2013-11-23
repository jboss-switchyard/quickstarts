package org.switchyard.as7.extension;

import javax.xml.stream.XMLStreamException;

import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.server.deployment.DeploymentUnitProcessingException;
import org.jboss.logging.Cause;
import org.jboss.logging.Messages;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;
import org.switchyard.SwitchYardException;

/**
 * <p/>
 * This file is using the subset 40400-40799 for logger messages.
 * <p/>
 *
 */
@MessageBundle(projectCode = "SWITCHYARD")
public interface ExtensionMessages {
    /**
     * The default messages.
     */
    ExtensionMessages MESSAGES = Messages.getBundle(ExtensionMessages.class);

    /**
     * alreadyDeclared method definition.
     * @param localName local name
     * @param location location
     * @return XMLStreamException
     */
    @Message(id = 40400, value = "%s already declared %s")
    XMLStreamException alreadyDeclared(String localName, String location);

    /**
     * errorStartingGateway method definition.
     * @return String
     */
    @Message(id = 40401, value = "Error starting gateway: ")
    String errorStartingGateway();
    
    /**
     * unknownGateway method definition.
     * @return String
     */
    @Message(id = 40402, value = "Unknown gateway.")
    String unknownGateway();

    /**
     * unableToCreateTempDirectory method definition.
     * @param path path to temp directory
     * @return RuntimeException
     */
    @Message(id = 40403, value = "Unable to create temp directory %s")
    RuntimeException unableToCreateTempDirectory(String path);
    
    /**
     * contextAlreadyExists method definition.
     * @param contextName context name
     * @return RuntimeException
     */
    @Message(id = 40404, value = "Context %s already exists!")
    RuntimeException contextAlreadyExists(String contextName);

    /**
     * couldNotInstantiateInterceptor method definition.
     * @param interceptorClassName interceptor class name
     * @param t cause 
     * @return SwitchYardException
     */
    @Message(id = 40405, value = "Could not instantiate interceptor class: %s")
    SwitchYardException couldNotInstantiateInterceptor(String interceptorClassName, @Cause Throwable t);

    /**
     * noSuchOperationGet method definition.
     * @param method the HTTP method
     * @param pathInfo the HTTP method path info
     * @return Exception
     */
    @Message(id = 40406, value = "No such operation: / (HTTP %s PATH_INFO: %s)")
    Exception noSuchOperationGet(String method, String pathInfo);
    
    /*
     * extensionNotfound method definition.
     * @param extensionClassName extension class name
     * @return DeploymentUnitProcessingException
     */
    @Message(id = 40407, value = "Could not find portable extension : %s")
    DeploymentUnitProcessingException extensionNotfound(String extensionClassName);

    /**
     * unableToStartContext method definition.
     * @param contextPath the context path
     * @return RuntimeException
     */
    @Message(id = 40408, value = "Unable to start context : %s")
    RuntimeException unableToStartContext(String contextPath);

}
