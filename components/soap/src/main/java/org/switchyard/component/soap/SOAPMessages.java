package org.switchyard.component.soap;

import javax.xml.soap.SOAPException;
import org.jboss.logging.Cause;
import org.jboss.logging.Messages;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;
import org.switchyard.HandlerException;
import org.switchyard.SwitchYardException;

/**
 * <p/>
 * This file is using the subset 35400-35799 for logger messages.
 * <p/>
 *
 */
@MessageBundle(projectCode = "SWITCHYARD")
public interface SOAPMessages {
    /**
     * The default messages.
     */
    SOAPMessages MESSAGES = Messages.getBundle(SOAPMessages.class);

    /**
     * missingSOAPBodyFromRequest method definition.
     * @return SOAPException
     */
    @Message(id = 35401, value = "Missing SOAP body from request")
    SOAPException missingSOAPBodyFromRequest();

    /**
     * foundMultipleSOAPElementsInSOAPBody method definition.
     * @return SOAPException
     */
    @Message(id = 35402, value = "Found multiple SOAPElements in SOAPBody")
    SOAPException foundMultipleSOAPElementsInSOAPBody();

    /**
     * couldNotFindSOAPElementInSOAPBody method definition.
     * @return SOAPException
     */
    @Message(id = 35403, value = "Could not find SOAPElement in SOAPBody")
    SOAPException couldNotFindSOAPElementInSOAPBody();

    /**
     * contentIDHeaderMissingForAttachmentPart method definition.
     * @return SOAPException
     */
    @Message(id = 35404, value = "Content-ID header missing for attachment part")
    SOAPException contentIDHeaderMissingForAttachmentPart();
    
    /**
     * unableToReadWSDL method definition.
     * @param wsdlLocation wsdlLocation
     * @return String
     */
    @Message(id = 35405, value = "Unable to read WSDL at %s")
    String unableToReadWSDL(String wsdlLocation);
    
    /**
     * unableToCreateSOAPBodyDueToNullMessageContent method definition.
     * @return SOAPException
     */
    @Message(id = 35406, value = "Unable to create SOAP Body due to null message content")
    SOAPException unableToCreateSOAPBodyDueToNullMessageContent();

    /**
     * unableToParseSOAPMessage method definition.
     * @param e the e
     * @return SOAPException
     */
    @Message(id = 35407, value = "Unable to parse SOAP Message")
    SOAPException unableToParseSOAPMessage(@Cause Exception e);

    /**
     * failedToMapContextPropertiesToSOAPMessage method definition.
     * @param ex the ex
     * @return SOAPException
     */
    @Message(id = 35408, value = "Failed to map context properties to SOAP message")
    SOAPException failedToMapContextPropertiesToSOAPMessage(@Cause Exception ex);

    /**
     * not method definition.
     * @param interceptors interceptors
     * @return IllegalArgumentException
     */
    @Message(id = 35409, value = "not %s")
    IllegalArgumentException not(String interceptors);
    
    /**
     * unableToReadWSDL method definition.
     * @param wsdlLocation wsdlLocation
     * @return String
     */
    @Message(id = 35410, value = "Unable to resolve WSDL document at %s")
    String unableToResolveWSDL(String wsdlLocation);
    
    /**
     * unexpectedSOAPExceptionWhenGeneratingASOAP11FaultMessage method definition.
     * @param from the from
     * @return IllegalStateException
     */
    @Message(id = 35411, value = "Unexpected SOAPException when generating a SOAP 1.1 Fault message.")
    IllegalStateException unexpectedSOAPExceptionWhenGeneratingASOAP11FaultMessage(@Cause Exception from);

    /**
     * unexpectedSOAPExceptionWhenGeneratingASOAP12FaultMessage method definition.
     * @param from the from
     * @return IllegalStateException
     */
    @Message(id = 35412, value = "Unexpected SOAPException when generating a SOAP 1.2 Fault message.")
    IllegalStateException unexpectedSOAPExceptionWhenGeneratingASOAP12FaultMessage(@Cause Exception from);

    /**
     * unexpected method definition.
     * @return IllegalStateException
     */
    @Message(id = 35418, value = "Unexpected")
    IllegalStateException unexpected();

    /**
     * invalidInputSOAPPayloadForServiceOperation method definition.
     * @param operationName operationName
     * @param serviceName serviceName
     * @param actualLN the actualLN
     * @return SOAPException
     */
    @Message(id = 35422, value = "Invalid input SOAP payload for service operation '%s' (service '%s').  No such Part '%s'.")
    SOAPException invalidInputSOAPPayloadForServiceOperation(String operationName, String serviceName, String actualLN);

    /**
     * invalidInputSOAPPayloadNamespaceForServiceOperation method definition.
     * @param operationName operationName
     * @param serviceName serviceName
     * @param expectedNS the expectedNS
     * @param actualNS the actualNS
     * @return SOAPException
     */
    @Message(id = 35423, value = "Invalid input SOAP payload namespace for service operation '%s' (service '%s').  Port defines operation namespace as '%s'.  Actual namespace on input SOAP message '%s'.")
    SOAPException invalidInputSOAPPayloadNamespaceForServiceOperation(String operationName, String serviceName, String expectedNS, String actualNS);

    /**
     * invalidInputSOAPPayloadLocalNamePartForServiceOperation method definition.
     * @param operationName operationName
     * @param serviceName serviceName
     * @param expectedLN the expectedLN
     * @param actualLN the actualLN
     * @return SOAPException
     */
    @Message(id = 35424, value = "Invalid input SOAP payload localNamePart for service operation '%s' (service '%s').  Port defines operation localNamePart as '%s'.  Actual localNamePart on input SOAP message '%s'.")
    SOAPException invalidInputSOAPPayloadLocalNamePartForServiceOperation(String operationName, String serviceName, String expectedLN, String actualLN);

    /**
     * referenceBindingNotStarted method definition.
     * @param referenceName the referenceName
     * @param bindingName the bindingName
     * @return HandlerException
     */
    @Message(id = 35427, value = "Reference binding \"%s/%s\" is not started.")
    HandlerException referenceBindingNotStarted(String referenceName, String bindingName);

    /**
     * failedToInstantiateSOAPMessageFactory method definition.
     * @return SOAPException
     */
    @Message(id = 35428, value = "Failed to instantiate SOAP Message Factory")
    SOAPException failedToInstantiateSOAPMessageFactory();

    /**
     * unexpectedExceptionHandlingSOAPMessage method definition.
     * @param se the se
     * @return HandlerException
     */
    @Message(id = 35429, value = "Unexpected exception handling SOAP Message")
    HandlerException unexpectedExceptionHandlingSOAPMessage(@Cause SOAPException se);

    /**
     * cannotProcessSOAPRequest method definition.
     * @param ex the ex
     * @return SOAPException
     */
    @Message(id = 35431, value = "Cannot process SOAP request")
    SOAPException cannotProcessSOAPRequest(@Cause Exception ex);

    /**
     * noAttachmentFoundWithName method definition.
     * @param contentId the contentId
     * @return RuntimeException
     */
    @Message(id = 35433, value = "No attachment found with name '%s'")
    RuntimeException noAttachmentFoundWithName(String contentId);

    /**
     * couldNotFindServiceInTheWSDL method definition.
     * @param portName the portName
     * @param definitionDocumentBaseURI definitionDocumentBaseURI
     * @return String
     */
    @Message(id = 35436, value = "Could not find service %s in the WSDL %s")
    String couldNotFindServiceInTheWSDL(String portName, String definitionDocumentBaseURI);

    /**
     * couldNotFindAPortDefinitionWithinService method definition.
     * @param wsdlServiceQName wsdlServiceQName
     * @return String
     */
    @Message(id = 35437, value = "Could not find a port definition within service %s")
    String couldNotFindAPortDefinitionWithinService(String wsdlServiceQName);

    /**
     * couldNotFindPortInTheService method definition.
     * @param portName the portName
     * @param wsdlServiceQName wsdlServiceQName
     * @return String
     */
    @Message(id = 35438, value = "Could not find port %s in the Service %s")
    String couldNotFindPortInTheService(String portName, String wsdlServiceQName);

    /**
     * incompatibleStyleOfSoapOperationLevelBindingsDetected method definition.
     * @return SwitchYardException
     */
    @Message(id = 35439, value = "Incompatible style of soap operation level bindings detected")
    SwitchYardException incompatibleStyleOfSoapOperationLevelBindingsDetected();

    /**
     * detectedMixingDifferentSoapBindingStyleOnPortTypeAndOperationLevel method definition.
     * @return SwitchYardException
     */
    @Message(id = 35440, value = "Detected mixing different soap binding style on port type and operation level")
    SwitchYardException detectedMixingDifferentSoapBindingStyleOnPortTypeAndOperationLevel();

    /**
     * faultNameNotFoundOnOperation method definition.
     * @param faultName the faultName
     * @param operationNameLocalPart operationNameLocalPart
     * @return IllegalArgumentException
     */
    @Message(id = 35441, value = "Fault name %s not found on operation %s")
    IllegalArgumentException faultNameNotFoundOnOperation(String faultName, String operationNameLocalPart);

    /**
     * policyReferenceURIMissingFor method definition.
     * @param portBindingQNametLocalPart portBindingQNametLocalPart
     * @return RuntimeException
     */
    @Message(id = 35442, value = "Policy reference URI missing for %s")
    RuntimeException policyReferenceURIMissingFor(String portBindingQNametLocalPart);

    /**
     * invalidWSDLNoOperationsFound method definition.
     * @return WebServicePublishException
     */
    @Message(id = 35443, value = "Invalid WSDL. No operations found.")
    WebServicePublishException invalidWSDLNoOperationsFound();

    /**
     * wSDLOperationNotFoundInService method definition.
     * @param name the name
     * @param serviceName serviceName
     * @return WebServicePublishException
     */
    @Message(id = 35444, value = "WSDL Operation %s not found in Service %s")
    WebServicePublishException wSDLOperationNotFoundInService(String name, String serviceName);

    /**
     * wSDLOperationDoesNotMatchServiceOperation method definition.
     * @param name the name
     * @param targetServiceOperationName targetServiceOperationName
     * @return WebServicePublishException
     */
    @Message(id = 35445, value = "WSDL Operation %s does not match Service Operation %s")
    WebServicePublishException wSDLOperationDoesNotMatchServiceOperation(String name, String targetServiceOperationName);

    /**
     * wSDLOperationDoesNotHaveAnyInputMessageParts method definition.
     * @param name the name
     * @return WebServicePublishException
     */
    @Message(id = 35446, value = "WSDL Operation %s does not have any input Message parts")
    WebServicePublishException wSDLOperationDoesNotHaveAnyInputMessageParts(String name);

    /**
     * wSDLOperationDoesNotHaveAnyOuputMessageParts method definition.
     * @param name the name
     * @return WebServicePublishException
     */
    @Message(id = 35447, value = "WSDL Operation %s does not have any ouput Message parts")
    WebServicePublishException wSDLOperationDoesNotHaveAnyOuputMessageParts(String name);

    /**
     * sendFailed method definition.
     * @return String
     */
    @Message(id = 35448, value = "Send Failed")
    String sendFailed();
    
    
    /**
     * noSuchOperation method definition.
     * @param operationName operationName
     * @return SOAPException
     */
    @Message(id = 35449, value = "No such operation: %s->null")
    SOAPException noSuchOperation(String operationName);
    
    
    /**
     * couldNotFindOperation method definition.
     * @param action action
     * @return SOAPException
     */
    @Message(id = 35450, value = "Could not find any operation associated with WS-A Action '%s'.")
    SOAPException couldNotFindOperation(String action);
    
    /**
     * operationNotAvailableTarget method definition.
     * @param firstBodyElement firstBodyElement
     * @param serviceName serviceName
     * @return SOAPException
     */
    @Message(id = 35451, value = "Operation for '%s' not available on target Service '%s'.")
    SOAPException operationNotAvailableTarget(String firstBodyElement, String serviceName);
    
    
    /**
     * operationNotAvailableTarget method definition.
     * @param waitTimeout waitTimeout
     * @param serviceName serviceName
     * @return SOAPException
     */
    @Message(id = 35452, value = "Timed out after %s ms waiting on synchronous response from target service '%s'.")
    SOAPException timedOut(String waitTimeout, String serviceName);
    
    /**
     * invalidResponseConstruction method definition.
     * @param messageComposerName messageComposerName
     * @return SOAPException
     */
    @Message(id = 35453, value = "Invalid response SOAPMessage construction.  The associated SwitchYard Exchange is in a FAULT state, but the SOAPMessage is not a Fault message.  The MessageComposer implementation in use (\"%s\") must generate the SOAPMessage instance properly as a Fault message.")
    SOAPException invalidResponseConstruction(String messageComposerName);
}

