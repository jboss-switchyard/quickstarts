package org.switchyard.component.bpm;

import java.io.IOException;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import org.jboss.logging.Cause;
import org.jboss.logging.Messages;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;
import org.switchyard.HandlerException;
import org.switchyard.SwitchYardException;

/**
 * <p/>
 * This file is using the subset 32000-32399 for logger messages.
 * <p/>
 *
 */
@MessageBundle(projectCode = "SWITCHYARD")
public interface BPMMessages {
    /**
     * The default messages.
     */
    BPMMessages MESSAGES = Messages.getBundle(BPMMessages.class);

    /**
     * bpmClassGetNameIsMissingTheBPMAnnotation method definition.
     * @param bpmClassName bpmClassName
     * @return IOException
     */
    @Message(id = 32000, value = "%s is missing the @BPM annotation.")
    IOException bpmClassGetNameIsMissingTheBPMAnnotation(String bpmClassName);

    /**
     * bpmInterfaceGetNameIsAClassBPMOnlyAllowedOnInterfaces method definition.
     * @param bpmInterfaceName bpmInterfaceName
     * @return IOException
     */
    @Message(id = 32001, value =  "%s is a class. @BPM only allowed on interfaces.")
    IOException bpmInterfaceGetNameIsAClassBPMOnlyAllowedOnInterfaces(String bpmInterfaceName);

    /**
     * cannotSignalEventUnknownProcessInstanceIdOrUnknown/unmatchedCorrelationKey method definition.
     * @return HandlerException
     */
    @Message(id = 32002, value = "Cannot signalEvent: unknown processInstanceId or unknown/unmatched correlationKey")
    HandlerException cannotSignalEventUnknownProcessInstanceIdOrUnknownunmatchedCorrelationKey();

    /**
     * cannotAbortProcessInstanceUnknownProcessInstanceIdOrUnknown/unmatchedCorrelationKey method definition.
     * @return HandlerException
     */
    @Message(id = 32003, value = "Cannot abortProcessInstance: unknown processInstanceId or unknown/unmatched correlationKey")
    HandlerException cannotAbortProcessInstance();

    /**
     * unsupportedOperationType method definition.
     * @param operationType the operationType
     * @return HandlerException
     */
    @Message(id = 32004, value = "Unsupported operation type: %s")
    HandlerException unsupportedOperationType(BPMOperationType operationType);

    /**
     * userTransactionBeginFailed method definition.
     * @param se the se
     * @return HandlerException
     */
    @Message(id = 32005, value = "UserTransaction begin failed")
    HandlerException userTransactionBeginFailedSystem(@Cause SystemException se);

    /**
     * userTransactionBeginFailed method definition.
     * @param nse the nse
     * @return HandlerException
     */
    @Message(id = 32006, value = "UserTransaction begin failed")
    HandlerException userTransactionBeginFailedNSE(@Cause NotSupportedException nse);

    /**
     * userTransactionCommitFailed method definition.
     * @param se the se
     * @return HandlerException
     */
    @Message(id = 32007, value = "UserTransaction commit failed")
    HandlerException userTransactionCommitFailedSystem(@Cause SystemException se);

    /**
     * userTransactionCommitFailed method definition.
     * @param hre the hre
     * @return HandlerException
     */
    @Message(id = 32008, value = "UserTransaction commit failed")
    HandlerException userTransactionCommitFailedRollback(@Cause HeuristicRollbackException hre);

    /**
     * userTransactionCommitFailed method definition.
     * @param hme the hme
     * @return HandlerException
     */
    @Message(id = 32009, value = "UserTransaction commit failed")
    HandlerException userTransactionCommitFailedMixed(@Cause HeuristicMixedException hme);

    /**
     * userTransactionCommitFailed method definition.
     * @param re the re
     * @return HandlerException
     */
    @Message(id = 32010, value = "UserTransaction commit failed")
    HandlerException userTransactionCommitFailed(@Cause RollbackException re);

    /**
     * userTransactionRollbackFailed method definition.
     * @param se the se
     * @return HandlerException
     */
    @Message(id = 32011, value = "UserTransaction rollback failed")
    HandlerException userTransactionRollbackFailed(@Cause SystemException se);

    /**
     * userTransactionSetRollbackOnlyFailed method definition.
     * @param se the se
     * @return HandlerException
     */
    @Message(id = 32012, value = "UserTransaction setRollbackOnly failed")
    HandlerException userTransactionSetRollbackOnlyFailed(@Cause SystemException se);

    /**
     * couldNotInstantiateUserGroupCallbackClass method definition.
     * @param callbackClassName callbackClassName
     * @return SwitchYardException
     */
    @Message(id = 32014, value = "Could not instantiate userGroupCallback class: %s")
    SwitchYardException couldNotInstantiateUserGroupCallbackClass(String callbackClassName);

    /**
     * couldNotLoadWorkItemHandlerClass method definition.
     * @param workItemHandlerModelClass workItemHandlerModelClass
     * @return SwitchYardException
     */
    @Message(id = 32015, value = "Could not load workItemHandler class: %s")
    SwitchYardException couldNotLoadWorkItemHandlerClass(String workItemHandlerModelClass);

    /**
     * couldNotUseNullNameToRegisterWorkItemHandler method definition.
     * @param workItemHandlerClassName workItemHandlerClassName
     * @return SwitchYardException
     */
    @Message(id = 32016, value = "Could not use null name to register workItemHandler: %s")
    SwitchYardException couldNotUseNullNameToRegisterWorkItemHandler(String workItemHandlerClassName);

    /**
     * couldNotInstantiateWorkItemHandlerClass method definition.
     * @param workItemHandlerClassName workItemHandlerClassName
     * @return SwitchYardException
     */
    @Message(id = 32017, value = "Could not instantiate workItemHandler class: %s")
    SwitchYardException couldNotInstantiateWorkItemHandlerClass(String workItemHandlerClassName);
}

