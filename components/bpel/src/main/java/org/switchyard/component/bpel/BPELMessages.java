package org.switchyard.component.bpel;

import org.jboss.logging.Cause;
import org.jboss.logging.Messages;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;
import org.switchyard.HandlerException;
import org.switchyard.SwitchYardException;
/**
 * <p/>
 * This file is using the subset 31200-31599 for logger messages.
 * <p/>
 *
 */
@MessageBundle(projectCode = "SWITCHYARD")
public interface BPELMessages {
    /**
     * The default messages.
     */
    BPELMessages MESSAGES = Messages.getBundle(BPELMessages.class);

    /**
     * interfaceNotDefinedForComponentWithBPELImplementation method definition.
     * @return SwitchYardException
     */
    @Message(id = 31200, value = "Interface not defined for component with BPEL implementation")
    SwitchYardException interfaceNotDefinedForComponentWithBPELImplementation();

    /**
     * failedToLoadDefaultProperties method definition.
     * @param e the e
     * @return SwitchYardException
     */
    @Message(id = 31201, value = "Failed to load default properties")
    SwitchYardException failedToLoadDefaultProperties(@Cause Exception e);

    /**
     * failedToInitializeTheEngine method definition.
     * @param e the e
     * @return SwitchYardException
     */
    @Message(id = 31202, value = "Failed to initialize the engine")
    SwitchYardException failedToInitializeTheEngine(@Cause Exception e);

    /**
     * unknownDeploymentEnvironment method definition.
     * @return SwitchYardException
     */
    @Message(id = 31203, value = "Unknown deployment environment")
    SwitchYardException unknownDeploymentEnvironment();

    /**
     * couldNotFindBPELImplementationAssociatedWithReference method definition.
     * @return SwitchYardException
     */
    @Message(id = 31207, value = "Could not find BPEL implementation associated with reference")
    SwitchYardException couldNotFindBPELImplementationAssociatedWithReference();

    /**
     * timedOutAfterMsWaitingOnSynchronousResponseFromTargetService method definition.
     * @param waitTimeout the waitTimeout
     * @param serviceReferenceName serviceReferenceName
     * @return HandlerException
     */
    @Message(id = 31208, value = "Timed out after %s ms waiting on synchronous response from target service '%s'.")
    HandlerException timedOutAfterMsWaitingOnSynchronousResponseFromTargetService(long waitTimeout, String serviceReferenceName);

    /**
     * responseNotReturnedFromOperationOnService method definition.
     * @param operationName the operationName
     * @param serviceReferenceName serviceReferenceName
     * @return Exception
     */
    @Message(id = 31209, value = "Response not returned from operation '%s' on service: %s")
    Exception responseNotReturnedFromOperationOnService(String operationName, String serviceReferenceName);

    /**
     * wSDLLocationHasNotBeenSpecified method definition.
     * @return SwitchYardException
     */
    @Message(id = 31211, value = "WSDL location has not been specified")
    SwitchYardException wSDLLocationHasNotBeenSpecified();

    /**
     * failedToLoadWSDL method definition.
     * @param location the location
     * @param e the e
     * @return SwitchYardException
     */
    @Message(id = 31212, value = "Failed to load WSDL '%s'")
    SwitchYardException failedToLoadWSDL(String location, @Cause Exception e);

    /**
     * unableToFindFaultOn method definition.
     * @param faultName the faultName
     * @param operationName operationName
     * @return SwitchYardException
     */
    @Message(id = 31214, value = "Unable to find fault '%s' on operation '%s'")
    SwitchYardException unableToFindFaultOn(String faultName, String operationName);

    /**
     * onlyExpectingASingleMessagePartForOperation method definition.
     * @param operationName operationName
     * @return SwitchYardException
     */
    @Message(id = 31215, value = "Only expecting a single message part for operation '%s'")
    SwitchYardException onlyExpectingASingleMessagePartForOperation(String operationName);

    /**
     * unableToFindPartNameFor method definition.
     * @param operationName operationName
     * @return SwitchYardException
     */
    @Message(id = 31216, value = "Unable to find part name for operation '%s'")
    SwitchYardException unableToFindPartNameFor(String operationName);

}

