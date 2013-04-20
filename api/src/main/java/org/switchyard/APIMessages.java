package org.switchyard;

import java.util.Set;

import javax.xml.namespace.QName;

import org.jboss.logging.Messages;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

import org.switchyard.metadata.ServiceOperation;

/**
 * <p/>
 * This file is using the subset 10000-10199 for logger messages.
 * <p/>
 *
 */
@MessageBundle(projectCode = "SWITCHYARD")
public interface APIMessages {
    /**
     * The default messages.
     */
    APIMessages MESSAGES = Messages.getBundle(APIMessages.class);

    /**
     * unexpectedInterrupt method definition.
     * @return IllegalStateException
     */
    @Message(id = 10003, value = "Unexpected Interrupt exception.")
    IllegalStateException unexpectedInterrupt();

    /**
     * serviceOpNeedOneParamater method definition.
     * @return RuntimeException
     */
    @Message(id = 10004, value = "Service operations on a Java interface must have exactly one parameter.")
    RuntimeException serviceOpNeedOneParamater();

    /**
     * serviceOpOnlyOneParameter method definition.
     * @return SwitchYardException
     */
    @Message(id = 10005, value = "Service operations on a Java interface can only throw one type of exception.")
    SwitchYardException serviceOpOnlyOneParameter();

    /**
     * invalidPolicyName method definition.
     * @param name name
     * @return Exception
     */
    @Message(id = 10006, value = "Invalid policy name: %s doesn't exist.")
    Exception invalidPolicyName(String name);
    
    /**
     * baseInterfaceString method definition.
     * @param type type
     * @param operations operations
     * @return String
     */
    @Message(id = 10007, value = "BaseServiceInterface [type=%s, operations=%s]")
    String baseInterfaceString(String type, Set<ServiceOperation> operations);
    
    /**
     * @param name name
     * @param pattern pattern
     * @param inputType inputType
     * @param outputType outputType
     * @param faultType faultType
     * @return String
     */
    @Message(id = 10008, value = "%s : %s  : [%s, %s, %s]")
    String baseServiceOperationString(String name, ExchangePattern pattern, QName inputType, 
            QName outputType, QName faultType);
    
    /**
     * nullTypeNamePassed method definition.
     * @return IllegalArgumentException
     */
    @Message(id = 10009, value = "null 'typeName' arg passed.")
    IllegalArgumentException nullTypeNamePassed();
    
    /**
     * unrecognizedURI method definition.
     * @param uri uri
     * @return IllegalArgumentException
     */
    @Message(id = 10010, value = "Unrecognized URI: %s")
    IllegalArgumentException unrecognizedURI(String uri);
 
    
    /**
     * unableUpdateMetadataType method definition.
     * @param klass klass
     * @return IllegalArgumentException
     */
    @Message(id = 10011, value = "Unable to update metadata type %s")
    IllegalArgumentException unableUpdateMetadataType(String klass);
    
}
