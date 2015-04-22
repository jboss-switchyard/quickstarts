package org.switchyard.component.common;

import org.jboss.logging.Cause;
import org.jboss.logging.Messages;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * <p/>
 * This file is using the subset 34500-34599 for logger messages.
 * <p/>
 *
 */
@MessageBundle(projectCode = "SWITCHYARD")
public interface CommonCommonMessages {
    /**
     * The default messages.
     */
    CommonCommonMessages MESSAGES = Messages.getBundle(CommonCommonMessages.class);

    /**
     * isNotAValidRegexPattern method definition.
     * @param regex the regex
     * @param pseMessage pseMessage
     * @return IllegalArgumentException
     */
    @Message(id = 34500, value = "\"%s\" is not a valid regex pattern: %s")
    IllegalArgumentException isNotAValidRegexPattern(String regex, String pseMessage);

    /**
     * unsupportedOperationSelectorConfiguration method definition.
     * @param model the model
     * @return Exception
     */
    @Message(id = 34501, value = "Unsupported OperationSelector configuration: %s")
    Exception unsupportedOperationSelectorConfiguration(String model);

    /**
     * couldnTEvaluateXPathExpression method definition.
     * @param expression the expression
     * @param e the e
     * @return Exception
     */
    @Message(id = 34502, value = "Couldn't evaluate XPath expression '%s'")
    Exception couldnTEvaluateXPathExpression(String expression, @Cause Exception e);

    /**
     * noNodeHasBeenMatchedWithTheXPathExpression method definition.
     * @param expression the expression
     * @return Exception
     */
    @Message(id = 34503, value = "No node has been matched with the XPath expression '%s' in the payload. It couldn't determine the operation.")
    Exception noNodeHasBeenMatchedWithTheXPathExpression(String expression);

    /**
     * multipleNodesHaveBeenMatchedWithTheXPathExpression method definition.
     * @param expression the expression
     * @return Exception
     */
    @Message(id = 34504, value = "Multiple nodes have been matched with the XPath expression '%s' in the payload. It couldn't determine the operation.")
    Exception multipleNodesHaveBeenMatchedWithTheXPathExpression(String expression);

    /**
     * noNodeHasBeenMatchedWithTheRegexExpression method definition.
     * @param expression the expression
     * @return Exception
     */
    @Message(id = 34505, value = "No node has been matched with the Regex expression '%s' in the payload. It couldn't determine the operation.")
    Exception noNodeHasBeenMatchedWithTheRegexExpression(String expression);

    /**
     * multipleNodesHaveBeenMatchedWithTheRegexExpression method definition.
     * @param expression the expression
     * @return Exception
     */
    @Message(id = 34506, value = "Multiple nodes have been matched with the Regex expression '%s' in the payload. It couldn't determine the operation.")
    Exception multipleNodesHaveBeenMatchedWithTheRegexExpression(String expression);

    /**
     * unexpectedInterruptWhileWaitingOnOUTExchangeMessage method definition.
     * @param e the e
     * @return DeliveryException
     */
    @Message(id = 34507, value = "Unexpected interrupt while waiting on OUT Exchange message.")
    DeliveryException unexpectedInterruptWhileWaitingOnOUTExchangeMessage(@Cause InterruptedException e);

    /**
     * timedOutWaitingOnOUTExchangeMessage method definition.
     * @return DeliveryException
     */
    @Message(id = 34508, value = "Timed out waiting on OUT Exchange message.")
    DeliveryException timedOutWaitingOnOUTExchangeMessage();

    /**
     * unexpectedInterruptException method definition.
     * @param e the e
     * @return IllegalStateException
     */
    @Message(id = 34510, value = "Unexpected Interrupt exception.")
    IllegalStateException unexpectedInterruptException(@Cause InterruptedException e);

}

