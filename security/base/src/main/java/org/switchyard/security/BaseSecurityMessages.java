package org.switchyard.security;

import java.io.IOException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLPeerUnverifiedException;
import javax.security.auth.login.LoginException;

import org.jboss.logging.Messages;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * <p/>
 * This file is using the subset 14400-14599 for logger messages.
 * <p/>
 *
 */
@MessageBundle(projectCode = "SWITCHYARD")
public interface BaseSecurityMessages {
    /**
     * Default messages.
     */
    BaseSecurityMessages MESSAGES = Messages.getBundle(BaseSecurityMessages.class);

    /**
     * propertiesNotSet method definition.
     * @return IllegalStateException
     */
    @Message(id = 14401, value="Properties are not set")
    IllegalStateException propertiesNotSet();

    /**
     * propertyNotSet method definition.
     * @param key key
     * @return IllegalStateException
     */
    @Message(id = 14402, value="Property [%s] is not set.")
    IllegalStateException propertyNotSet(String key);

    /**
     * credentialsNotSet method definition.
     * @return IllegalStateException
     */
    @Message(id = 14403, value="Credentials are not set.")
    IllegalStateException credentialsNotSet();

    /**
     * unableToExtractCredentials method definition.
     * @param message message
     * @param pe pe 
     * @return RuntimeException
     */
    @Message(id = 14404, value="Unable to extract Credentials from SSLSession: %s")
    RuntimeException unableToExtractCredentials(String message, @Cause SSLPeerUnverifiedException pe);

    /**
     * valueTypeRecognizedNotImplemented method definition.
     * @param valueType valueType
     * @return IllegalArgumentException
     */
    @Message(id = 14405, value="%s not implemented (although recognized)")
    IllegalArgumentException valueTypeRecognizedNotImplemented(String valueType);

    /**
     * valueTypeNotImplemented method definition.
     * @param valueType valueType
     * @return IllegalArgumentException
     */
    @Message(id = 14406, value="%s not implemented")
    IllegalArgumentException valueTypeNotImplemented(String valueType);

    /**
     * couldNotCreateCert method definition.
     * @param message message
     * @param ce ce
     * @return RuntimeException
     */
    @Message(id = 14407, value="Could not create certificate(s): %s")
    RuntimeException couldNotCreateCert(String message, @Cause CertificateException ce);

    /**
     * optionsNotSet method definition.
     * @param message message
     * @param ioe ioe
     * @return LoginException
     */
    @Message(id = 14408, value="Failed to invoke callback: %s")
    LoginException failedInvokeCallback(String message, @Cause IOException ioe);
    
    /**
     * callbackHandlerNoSupport method definition.
     * @param handler handler
     * @return LoginException
     */
    @Message(id = 14410, value="CallbackHandler does not support: %s")
    LoginException callbackHandlerNoSupport(String handler);

    /**
     * problemAccessingKeystore method definition.
     * @param message message
     * @return LoginException
     */
    @Message(id = 14411, value="Problem accessing KeyStore: %s")
    LoginException problemAccessingKeystore(String message);

    /**
     * problemVerifyingCallerCert method definition. 
     * @param message message
     * @return LoginException
     */
    @Message(id = 14412, value="Problem verifying caller Certificate: %s")
    LoginException problemVerifyingCallerCert(String message);

    /**
     * noCallerCertificateProvided method definition.
     * @return LoginException
     */
    @Message(id = 14413, value="No caller X509 Certificate provided")
    LoginException noCallerCertificateProvided();

    /**
     * optionsNotSet method definition.
     * @return IllegalStateException
     */
    @Message(id = 14414, value="Options not set")
    IllegalStateException optionsNotSet();

    /**
     * optionNotSet method definition.
     * @param name name
     * @return IllegalStateException
     */
    @Message(id = 14415, value="Option [%s] not set")
    IllegalStateException optionNotSet(String name);

    /**
     * groupNameCannotBeNull method definition.
     * @return IllegalArgumentException
     */
    @Message(id = 14416, value="Group name cannot be null")
    IllegalArgumentException groupNameCannotBeNull();

    /**
     * roleCannotBeNull method definition.
     * @return IllegalArgumentException
     */
    @Message(id = 14417, value="Role name cannot be null")
    IllegalArgumentException roleCannotBeNull();

    /**
     * userNameCannotBeNull method definition.
     * @return IllegalArgumentException
     */
    @Message(id = 14418, value="User name cannot be null")
    IllegalArgumentException userNameCannotBeNull();
}
