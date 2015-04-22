package org.switchyard.component.bean;

import java.io.IOException;

import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.xml.namespace.QName;

import org.jboss.logging.Cause;
import org.jboss.logging.Messages;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;
import org.switchyard.SwitchYardException;

/**
 * <p/>
 * This file is using the subset 30400-30799 for logger messages.
 * <p/>
 *
 */
@MessageBundle(projectCode = "SWITCHYARD")
public interface BeanMessages {
    /**
     * The default messages.
     */
    BeanMessages MESSAGES = Messages.getBundle(BeanMessages.class);

    /**
     * operationNameMustResolveToExactlyOneBeanMethodOnBeanType method definition.
     * @param operationName the operationName
     * @param serviceClassName serviceClassName
     * @return BeanComponentException
     */
    @Message(id = 30400, value = "Operation name '%s' must resolve to exactly one bean method on bean type '%s'.")
    BeanComponentException operationNameMustResolveToExactlyOneBeanMethodOnBeanType(String operationName, String serviceClassName);

    /**
     * operationNameNotSpecifiedOnExchange method definition.
     * @return BeanComponentException
     */
    @Message(id = 30401, value = "Operation name not specified on exchange.")
    BeanComponentException operationNameNotSpecifiedOnExchange();

    /**
     * aServiceReferenceToServiceIsNotBoundInto method definition.
     * @param serviceName the serviceName
     * @return BeanComponentException
     */
    @Message(id = 30402, value = "A service reference to service '%s' is not bound into this client proxy instance.  A reference configuration to the service may be required in the application configuration.")
    BeanComponentException aServiceReferenceToServiceIsNotBoundInto(String serviceName);

    /**
     * beanComponentInvocationFailureServiceOperation method definition.
     * @param serviceName serviceName
     * @param methodName methodName
     * @return BeanComponentException
     */
    @Message(id = 30403, value = "Bean Component invocation failure.  Service '%s', operation '%s'.")
    BeanComponentException beanComponentInvocationFailureServiceOperation(String serviceName, String methodName);

    /**
     * beanComponentInvocationFailureOperationIsNotDefinedOnService method definition.
     * @param operationName the operationName
     * @param serviceName serviceName
     * @return BeanComponentException
     */
    @Message(id = 30404, value = "Bean Component invocation failure.  Operation '%s' is not defined on Service '%s'.")
    BeanComponentException beanComponentInvocationFailureOperationIsNotDefinedOnService(String operationName, String serviceName);

    /**
     * unknownPolicy method definition.
     * @param secPolicy the secPolicy
     * @return IOException
     */
    @Message(id = 30405, value = "Unknown policy: %s")
    IOException unknownPolicy(String secPolicy);

    /**
     * null method definition.
     * @param ptx the ptx
     * @param stx the stx
     * @param name the name
     * @return IOException
     */
    @Message(id = 30407, value = "TransactionPolicies %s and %s cannot co-exist on service %s")
    IOException transactionPoliciesCannotCoexistService(QName ptx, QName stx, String name);

    /**
     * null method definition.
     * @param gtx the gtx
     * @param ltx the ltx
     * @param ntx the ntx
     * @param name the name
     * @return IOException
     */
    @Message(id = 30408, value = "TransactionPolicies %s, %s and %s cannot co-exist on implementation %s")
    IOException transactionPoliciesCannotCoexistImplementation(QName gtx, QName ltx, QName ntx, String name);

    /**
     * stringFormatReferenceOnlyCouldBeMarkedWithInteractionPolicyBut%sIsNotTheOne method definition.
     * @param policy policy
     * @return IOException
     */
    @Message(id = 30409, value = "Reference only could be marked with Interaction policy, but %s is not the one.")
    IOException referenceOnlyCouldBeMarkedWithInteractionPolicyButIsNotTheOne(String policy);

    /**
     * unknownServiceName method definition.
     * @param serviceName the serviceName
     * @return SwitchYardException
     */
    @Message(id = 30412, value = "Unknown Service name '%s'.")
    SwitchYardException unknownServiceName(String serviceName);

    /**
     * failedToLookupBeanDeploymentMetaDataFromBeanManagerMustBeBoundIntoBeanManagerPerhapsSwitchYardCDIExtensionsNotProperlyInstalledInContainer method definition.
     * @return SwitchYardException
     */
    @Message(id = 30414, value = "Failed to lookup BeanDeploymentMetaData from BeanManager.  Must be bound into BeanManager.  Perhaps SwitchYard CDI Extensions not properly installed in container.")
    SwitchYardException failedToLookupBeanDeploymentMetaDataFromBeanManagerMustBeBoundIntoBeanManagerPerhapsSwitchYardCDIExtensionsNotProperlyInstalledInContainer();

    /**
     * failedToLookupBeanDeploymentMetaDataFromBeanManagerMultipleBeansResolvedForType method definition.
     * @param className className
     * @return SwitchYardException
     */
    @Message(id = 30415, value = "Failed to lookup BeanDeploymentMetaData from BeanManager.  Multiple beans resolved for type '%s'.")
    SwitchYardException failedToLookupBeanDeploymentMetaDataFromBeanManagerMultipleBeansResolvedForType(String className);

    /**
     * failedToLookupBeanManagerMustBeBoundIntoJavaCompAsPerCDISpecification method definition.
     * @return SwitchYardException
     */
    @Message(id = 30416, value = "Failed to lookup BeanManager.  Must be bound into java:comp as per CDI specification.")
    SwitchYardException failedToLookupBeanManagerMustBeBoundIntoJavaCompAsPerCDISpecification();

    /**
     * nameBeanManagerIsNotBoundInThisContext method definition.
     * @return NameNotFoundException
     */
    @Message(id = 30417, value = "Name BeanManager is not bound in this Context")
    NameNotFoundException nameBeanManagerIsNotBoundInThisContext();

    /**
     * unexpectedExceptionRetrieving method definition.
     * @param jndiName the jndiName
     * @param e the e
     * @return SwitchYardException
     */
    @Message(id = 30418, value = "Unexpected Exception retrieving '%s' from JNDI namespace.")
    SwitchYardException unexpectedExceptionRetrieving(String jndiName, @Cause Exception e);

    /**
     * unexpectedErrorClosingInitialContext method definition.
     * @param e the e
     * @return SwitchYardException
     */
    @Message(id = 30419, value = "Unexpected error closing InitialContext.")
    SwitchYardException unexpectedErrorClosingInitialContext(@Cause NamingException e);

    /**
     * unexpectedExceptionThe@ServiceAnnotationRequiresAServiceInterfaceClassValueToBeDefinedYetTheAnnotationHasNoValue method definition.
     * @return SwitchYardException
     */
    @Message(id = 30420, value = "Unexpected exception. The @Service annotation has no value. It cannot be ommitted unless the bean implements exactly one interface.")
    SwitchYardException unexpectedExceptionTheServiceAnnotationHasNoValueItCannotBeOmmittedUnlessTheBeanImplementsExactlyOneInterface();

    /**
     * invalid@ServiceSpecification@Service method definition.
     * @param serviceInterfaceName serviceInterfaceName
     * @return SwitchYardException
     */
    @Message(id = 30421, value = "Invalid @Service specification @Service(%s.class).  @Service interface Class must be a Java Interface.  Cannot be a concrete implementation.")
    SwitchYardException invalidServiceSpecificationService(String serviceInterfaceName);

    /**
     * illegalCallToGetTheSwitchYardContext;MustBeCalledWithinTheExecutionOfAnExchangeHandlerChain method definition.
     * @return IllegalStateException
     */
    @Message(id = 30422, value = "Illegal call to get the SwitchYard Context; must be called within the execution of an ExchangeHandler chain.")
    IllegalStateException illegalCallToGetTheSwitchYardContextMustBeCalledWithinTheExecutionOfAnExchangeHandlerChain();

    /**
     * failedToLookupBeanDeploymentMetaDataFromNamingContext method definition.
     * @return SwitchYardException
     */
    @Message(id = 30424, value = "Failed to lookup BeanDeploymentMetaData from Naming Context.")
    SwitchYardException failedToLookupBeanDeploymentMetaDataFromNamingContext();

    /**
     * beanServiceOperation method definition.
     * @param operationName operationName
     * @return BeanComponentException
     */
    @Message(id = 30425, value = "Bean service operation '%s' has more than 1 argument.  Bean component currently only supports single argument operations.")
    BeanComponentException beanServiceOperationMoreThanOne(String operationName);

    /**
     * beanServiceOperation method definition.
     * @param operationName operationName
     * @return BeanComponentException
     */
    @Message(id = 30426, value = "Bean service operation '%s' requires a single argument.  Exchange payload specifies no payload.")
    BeanComponentException beanServiceOperationRequiresSingle(String operationName);

    /**
     * beanServiceOperation method definition.
     * @param operationName operationName
     * @param argsLength argsLength
     * @return BeanComponentException
     */
    @Message(id = 30427, value = "Bean service operation '%s' only supports a single argument.  Exchange payload specifies %s args.")
    BeanComponentException beanServiceOperationSupportsSingle(String operationName, String argsLength);

    /**
     * beanServiceOperationRequiresAPayloadTypeOf method definition.
     * @param operationName operationName
     * @param argTypeName argTypeName
     * @param className className
     * @return BeanComponentException
     */
    @Message(id = 30428, value = "Bean service operation '%s' requires a payload type of '%s'.  Actual payload type is '%s'.  You must define and register a Transformer.")
    BeanComponentException beanServiceOperationRequiresAPayloadTypeOf(String operationName, String argTypeName, String className);

    /**
     * unexpectedErrorBeanServiceMetadataShouldReturnAnInvocationInstanceOrThrowABeanComponentException method definition.
     * @return SwitchYardException
     */
    @Message(id = 30430, value = "Unexpected error.  BeanServiceMetadata should return an Invocation instance, or throw a BeanComponentException.")
    SwitchYardException unexpectedErrorBeanServiceMetadataShouldReturnAnInvocationInstanceOrThrowABeanComponentException();

    /**
     * invocationOfOperationFailed method definition.
     * @param invocationMethodName invocationMethodName
     * @param serviceBeanClassName serviceBeanClassName
     * @return String
     */ 
    @Message(id = 30431, value = "Invocation of operation '%s' on bean component '%s' failed.")
    String invocationOfOperationFailed(String invocationMethodName, String serviceBeanClassName);
    
    /**
     * illegalExchangeAccessOutsideHandlerChain method definition.
     * @return String
     */ 
    @Message(id = 30432, value = "Illegal call to get the SwitchYard Exchange; must be called within the execution of an ExchangeHandler chain.")
    IllegalStateException illegalExchangeAccessOutsideHandlerChain();
    
}
