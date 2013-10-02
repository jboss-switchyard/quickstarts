package org.switchyard.component.resteasy;

import org.jboss.logging.Cause;
import org.jboss.logging.Messages;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;
import org.switchyard.HandlerException;
/**
 * <p/>
 * This file is using the subset 38000-38399 for logger messages.
 * <p/>
 *
 */
@MessageBundle(projectCode = "SWITCHYARD")
public interface RestEasyMessages {
    /**
     * The default messages.
     */
    RestEasyMessages MESSAGES = Messages.getBundle(RestEasyMessages.class);

    /**
     * unexpected method definition.
     * @return IllegalStateException
     */
    @Message(id = 38007, value = "Unexpected")
    IllegalStateException unexpected();

    /**
     * referenceBindingNotStarted method definition.
     * @param referenceName referenceName
     * @param bindingName bindingName
     * @return HandlerException
     */
    @Message(id = 38009, value = "Reference binding \"%s/%s\" is not started.")
    HandlerException referenceBindingNotStarted(String referenceName, String bindingName);

    /**
     * m method definition.
     * @param e the e
     * @return HandlerException
     */
    @Message(id = 38010, value = "m")
    HandlerException m(@Cause Exception e);

    /**
     * youMustUseAtLeastOneButNoMoreThanOneHttpMethodAnnotationOn method definition.
     * @param method method
     * @return RuntimeException
     */
    @Message(id = 38015, value = "You must use at least one, but no more than one http method annotation on: %s")
    RuntimeException youMustUseAtLeastOneButNoMoreThanOneHttpMethodAnnotationOn(String method);

    /**
     * youHaveNotSetABaseURIForTheClientProxy method definition.
     * @return RuntimeException
     */
    @Message(id = 38016, value = "You have not set a base URI for the client proxy")
    RuntimeException youHaveNotSetABaseURIForTheClientProxy();
    
    /**
     * unexpectedExceptionComposingRESTRequest method definition.
     * @return String
     */
    @Message(id = 38017, value = "Unexpected exception composing outbound REST request")
    String unexpectedExceptionComposingRESTRequest();

    /**
     * unableToMapAmongResources method definition.
     * @param opName opName
     * @param keySet keySet
     * @return String
     */
    @Message(id = 38018, value = "Unable to map %s among resources %s")
    String unableToMapAmongResources(String opName, String keySet);
}

