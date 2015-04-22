package org.switchyard.component.bpm;

import java.io.IOException;

import org.jboss.logging.Messages;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;
import org.switchyard.HandlerException;
import org.switchyard.component.bpm.operation.BPMOperationType;

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
}

