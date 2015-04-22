package org.switchyard.component.http;

import java.net.MalformedURLException;
import java.util.Set;

import org.jboss.logging.Cause;
import org.jboss.logging.Messages;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;
import org.switchyard.HandlerException;
import org.switchyard.SwitchYardException;
import org.switchyard.metadata.ServiceOperation;
/**
 * <p/>
 * This file is using the subset 36400-36799 for logger messages.
 * <p/>
 *
 */
@MessageBundle(projectCode = "SWITCHYARD")
public interface HttpMessages {
    /**
     * The default messages.
     */
    HttpMessages MESSAGES = Messages.getBundle(HttpMessages.class);

    /**
     * unableToFindPublisher method definition.
     * @param e the e
     * @return  HttpPublishException
     */
    @Message(id = 36400, value = "Unable to find any Http Endpoint Publisher")
    HttpPublishException unableToFindPublisher(@Cause Exception e);

    /**
     * unableToPublish method definition.
     * @param e the e
     * @return  HttpPublishException
     */
    @Message(id = 36401, value = "Unable to publish HttpEndpoint")
    HttpPublishException unableToPublish(@Cause Exception e);

    /**
     * unexpectedFault method definition.
     * @return  IllegalStateException
     */
    @Message(id = 36402, value = "Unexpected fault occurred, please see log file for more information")
    IllegalStateException unexpectedFault();

    /**
     * unexpectedMessage method definition.
     * @return  IllegalStateException
     */
    @Message(id = 36403, value = "Unexpected message, please see log for more information")
    IllegalStateException unexpectedMessage();

    /**
     * moreThanOneOperationSelector method definition.
     * @param operations the operations
     * @return  SwitchYardException
     */
    @Message(id = 36404, value = "No operationSelector was configured for the Http Component and the Service Interface contains more than one operation: %s. Please add an operationSelector element.")
    SwitchYardException moreThanOneOperationSelector(Set<ServiceOperation> operations);

    /**
     * invalidHttpURL method definition.
     * @param mue the mue
     * @return  HttpConsumeException
     */
    @Message(id = 36405, value = "The address URL seem to be invalid")
    HttpConsumeException invalidHttpURL(@Cause MalformedURLException mue);

    /**
     * bindingNotStarted method definition.
     * @param _referenceName the _referenceName
     * @param _bindingName the _bindingName
     * @return  HandlerException
     */
    @Message(id = 36406, value = "Reference binding '%s/%s' is not started.")
    HandlerException bindingNotStarted(String _referenceName, String _bindingName);

    /**
     * unexpectedExceptionHandlingHTTPMessage method definition.
     * @param e the e
     * @return  HandlerException
     */
    @Message(id = 36407, value = "Unexpected exception handling HTTP Message")
    HandlerException unexpectedExceptionHandlingHTTPMessage(@Cause Exception e);

}

