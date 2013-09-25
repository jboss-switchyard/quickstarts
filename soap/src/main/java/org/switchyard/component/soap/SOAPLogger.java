package org.switchyard.component.soap;

import javax.xml.soap.SOAPException;
import org.jboss.logging.Cause;
import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;
/**
 * <p/>
 * This file is using the subset 35000-35399 for logger messages.
 * <p/>
 *
 */
@MessageLogger(projectCode = "SWITCHYARD")
public interface SOAPLogger {
    /**
     * A root logger with the category of the package name.
     */
    SOAPLogger ROOT_LOGGER = Logger.getMessageLogger(SOAPLogger.class, SOAPLogger.class.getPackage().getName());

    /**
     * credentialsAreIgnoredForServletRequest! method definition.
     */
    @LogMessage(level = Level.WARN)
    @Message(id = 35000, value = "Credentials are ignored for ServletRequest!")
    void credentialsAreIgnoredForServletRequest();

    /**
     * addressingEnabledRequired method definition.
     * @param isEnabled isEnabled
     * @param isRequired isRequired
     */
    @LogMessage(level = Level.INFO)
    @Message(id = 35001, value = "Addressing [enabled = %s, required = %s]")
    void addressingEnabledRequired(String isEnabled, String isRequired);

    /**
     * mTOMEnabledThreshold method definition.
     * @param isEnabled isEnabled
     * @param threshold threshold
     */
    @LogMessage(level = Level.INFO)
    @Message(id = 35002, value = "MTOM [enabled = %s, threshold = %s]")
    void mTOMEnabledThreshold(String isEnabled, String threshold);

    /**
     * publishingWebServiceAt method definition.
     * @param publishUrl the publishUrl
     */
    @LogMessage(level = Level.INFO)
    @Message(id = 35003, value = "Publishing WebService at %s")
    void publishingWebServiceAt(String publishUrl);

    /**
     * stoppingWebServiceAt method definition.
     * @param publishUrl publishUrl
     */
    @LogMessage(level = Level.INFO)
    @Message(id = 35004, value = "Stopping WebService at %s")
    void stoppingWebServiceAt(String publishUrl);

    /**
     * webService method definition.
     * @param port port
     */
    @LogMessage(level = Level.INFO)
    @Message(id = 35010, value = "WebService %s stopped.")
    void webService(String port);
    
    /**
     * creatingDispatchWithWSDL method definition.
     * @param wsdlUrl the wsdlUrl
     */
    @LogMessage(level = Level.INFO)
    @Message(id = 35014, value = "Creating dispatch with WSDL %s")
    void creatingDispatchWithWSDL(String wsdlUrl);

    /**
     * sentAMessageWithReplyToToARequestResponseWebserviceSoNoResponseReturned method definition.
     */
    @LogMessage(level = Level.WARN)
    @Message(id = 35015, value = "Sent a message with ReplyTo to a Request_Response Webservice, so no response returned!")
    void sentAMessageWithReplyToToARequestResponseWebserviceSoNoResponseReturned();

    /**
     * couldNotParseSOAPMessage method definition.
     * @param e the e
     */
    @LogMessage(level = Level.ERROR)
    @Message(id = 35016, value = "Could not parse SOAP Message")
    void couldNotParseSOAPMessage(@Cause Exception e);

    /**
     * couldNotParseMessageString method definition.
     * @param e the e
     */
    @LogMessage(level = Level.ERROR)
    @Message(id = 35018, value = "Could not parse Message String")
    void couldNotParseMessageString(@Cause Exception e);

    /**
     * couldNotInstantiateSOAP11MessageFactory method definition.
     * @param soape the soape
     */
    @LogMessage(level = Level.ERROR)
    @Message(id = 35019, value = "Could not instantiate SOAP 1.1 Message Factory")
    void couldNotInstantiateSOAP11MessageFactory(@Cause SOAPException soape);

    /**
     * couldNotInstantiateSOAP12MessageFactory method definition.
     * @param soape the soape
     */
    @LogMessage(level = Level.ERROR)
    @Message(id = 35020, value = "Could not instantiate SOAP 1.2 Message Factory")
    void couldNotInstantiateSOAP12MessageFactory(@Cause SOAPException soape);
}

