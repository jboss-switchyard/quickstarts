package org.switchyard.extensions.wsdl;

import org.jboss.logging.Messages;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * <p/>
 * This file is using the subset 12800-13000 for logger messages.
 * <p/>
 *
 */
@MessageBundle(projectCode = "SWITCHYARD")
public interface WSDLExtensionsMessages {
    /**
     * Default messages. 
     */
    WSDLExtensionsMessages MESSAGES = Messages.getBundle(WSDLExtensionsMessages.class);

    /**
     * unableFindPort method definition.
     * @param portType port type
     * @return WSDLReaderException
     */
    @Message(id = 12800, value = "Unable to find portType with name %s")
    WSDLReaderException unableFindPort(String portType);

    /**
     * wsdlInterfaceNeedsOneParameter method definition.
     * @return WSDLReaderException
     */
    @Message(id = 12801, value = "Service operations on a WSDL interface must have exactly one parameter.")
    WSDLReaderException wsdlInterfaceNeedsOneParameter();
    
    /**
     * unableResolveWSDL method definition.
     * @param wsdlURI wsdlURI
     * @param e e
     * @return WSDLReaderException
     */
    @Message(id = 12802, value = "Unable to resolve WSDL document at %s")
    WSDLReaderException unableResolveWSDL(String wsdlURI, @Cause Exception e);
    
    /**
     * invalidWSDLInterfacePart method definition.
     * @param wsdlLocationURI wsdlLocationURI
     * @return WSDLReaderException
     */
    @Message(id = 12803, value = "Invalid WSDL interface part %s")
    WSDLReaderException invalidWSDLInterfacePart(String wsdlLocationURI);
    
    /**
     * invalidWSDLInterface method definition.
     * @param wsdlLocationURI wsdlLocationURI 
     * @return WSDLReaderException
     */
    @Message(id = 12804, value = "Invalid WSDL interface %s")
    WSDLReaderException invalidWSDLInterface(String wsdlLocationURI);
    
    /**
     * messageNameMissing method definition.
     * @return WSDLReaderException
     */
    @Message(id = 12805, value = "Message name missing.")
    WSDLReaderException messageNameMissing();
    
    /**
     * missingOperationForMessage method definition.
     * @param localName localName
     * @return WSDLReaderException
     */
    @Message(id = 12806, value = "Missing operation for message %s")
    WSDLReaderException missingOperationForMessage(String localName);
    
}
