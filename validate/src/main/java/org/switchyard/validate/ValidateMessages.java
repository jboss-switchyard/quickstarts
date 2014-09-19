package org.switchyard.validate;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import org.jboss.logging.Messages;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;
import org.switchyard.SwitchYardException;
import org.switchyard.validate.internal.DuplicateValidatorException;

/**
 * <p/>
 * This file is using the subset 17200-17399 for logger messages.
 * <p/>
 *
 */
@MessageBundle(projectCode = "SWITCHYARD")
public interface ValidateMessages {
    /**
     * Default messages. 
     */
    ValidateMessages MESSAGES = Messages.getBundle(ValidateMessages.class);

    /**
     * nullValidatorRegistryArgument method definition.
     * @return IllegalArgumentException
     */
    @Message(id=17200, value = "Null 'validatorRegistry' argument.")
    IllegalArgumentException nullValidatorRegistryArgument();

    /**
     * errorReadingValidator method definition.
     * @param validatesXML validatesXML
     * @param ioe ioe
     * @return SwitchYardException
     */
    @Message(id=17201, value = "Error reading out-of-the-box Validator configurations from classpath (%s).")
    SwitchYardException errorReadingValidator(String validatesXML, @Cause IOException ioe);

    /**
     * failedToRegisterValidator method definition.
     * @param validator validator
     * @param registeredValidator registeredValidator
     * @return DuplicateValidatorException
     */
    @Message(id=17202, value = "Failed to register Validator '%s'.  A Validator for these types "
            + "is already registered: '%s'.")
    DuplicateValidatorException failedToRegisterValidator(String validator, String registeredValidator);

    /**
     * cdiBeanManagerNotFound method definition.
     * @return SwitchYardException
     */
    @Message(id=17203, value = "CDI BeanManager couldn't be found. A Java validator class name must be specified if CDI is not enabled.")
    SwitchYardException cdiBeanManagerNotFound();

    /**
     * validatorBeanNotFound method definition.
     * @param validatorBean validatorBean
     * @return SwitchYardException
     */
    @Message(id=17204, value = "The Java validator bean '%s' couldn't be found in CDI registry.")
    SwitchYardException validatorBeanNotFound(String validatorBean);
    
    /**
     * beanOrClassRequired method definition.
     * @return SwitchYardException
     */
    @Message(id=17205, value = "'bean' or 'class' must be specified for Java validator definition.")
    SwitchYardException beanOrClassRequired();
    
    /**
     * errorConstructingValidator method definition.
     * @param className className
     * @param e e
     * @return SwitchYardException
     */
    @Message(id=17206, value = "Error constructing Validator instance for class '%s'.")
    SwitchYardException errorConstructingValidator(String className, @Cause Exception e);

    /**
     * unknownValidateModel method definition.
     * @param className className
     * @return SwitchYardException
     */
    @Message(id=17207, value = "Unknown ValidateModel type '%s'.")
    SwitchYardException unknownValidateModel(String className);
    
    /**
     * invalidValidatorClass method definition.
     * @param className className
     * @return SwitchYardException
     */
    @Message(id=17208, value = "Invalid Validator class '%s'.  Must implement the Validator interface, or have methods annotated with the @Validator annotation.")
    SwitchYardException invalidValidatorClass(String className);
    
    /**
     * errorConstructingValidatorConstructorRequired method definition.
     * @param className className
     * @param e e
     * @return SwitchYardException
     */
    @Message(id=17209, value = "Error constructing Validator instance for class '%s'.  Class must have a public default constructor.")
    SwitchYardException errorConstructingValidatorConstructorRequired(String className, @Cause Exception e);
    
    /**
     * errorConstructingValidatorClassNotSupported method definition.
     * @param className className
     * @param typeName typeName
     * @return SwitchYardException
     */
    @Message(id=17210, value = "Error constructing Validator instance for class '%s'.  Class does not support a validation for type '%s'.")
    SwitchYardException errorConstructingValidatorClassNotSupported(String className, String typeName);

    /**
     * errorConstructingValidatorMustHavePublicConstructor method definition.
     * @param className className
     * @param e e
     * @return SwitchYardException
     */
    @Message(id=17211, value = "Error constructing Validator instance for class '%s'.  Class must have a public default constructor.")
    SwitchYardException errorConstructingValidatorMustHavePublicConstructor(String className, @Cause Exception e);
    
    /**
     * invalidMethodSignatureMustReturnValidationResult method definition.
     * @param methodName methodName
     * @param className className
     * @return SwitchYardException
     */
    @Message(id=17212, value = "Invalid method signature: @Validator method '%s' on class '%s' must return org.switchyard.validate.ValidationResult.")
    SwitchYardException invalidMethodSignatureMustReturnValidationResult(String methodName, String className);
    
    /**
     * errorExecutingValidatorInvocationTargetException method definition.
     * @param method method
     * @param className className
     * @param ite ite
     * @return SwitchYardException
     */
    @Message(id=17213, value = "Error executing @Validator method '%s' on class '%s'.")
    SwitchYardException errorExecutingValidatorInvocationTargetException(String method, String className, 
            @Cause InvocationTargetException ite);

    /**
     * errorExecutingValidatorException method definition.
     * @param method method
     * @param className className
     * @param ite ite
     * @return SwitchYardException
     */
    @Message(id=17214, value = "Error executing @Validator method '%s' on class '%s'.")
    SwitchYardException errorExecutingValidatorException(String method, String className, 
            @Cause Exception ite);
    
    /**
     * invalidValidatorOneParameter method definition.
     * @param methodName methodName
     * @param className className
     * @return SwitchYardException
     */
    @Message(id=17215, value = "Invalid @Validator method '%s' on class '%s'.  Must have exactly 1 parameter.")
    SwitchYardException invalidValidatorOneParameter(String methodName, String className);
    
    /**
     * validateModelNotAnnotated method definition.
     * @param className className
     * @return SwitchYardException
     */
    @Message(id=17216, value = "ValidateModel type '%s' is not annotated with an @ValidatorFactoryClass annotation.")
    SwitchYardException validateModelNotAnnotated(String className);
    
    /**
     * invalidValidatorFactoryImplementation method definition.
     * @param className className
     * @return SwitchYardException
     */
    @Message(id=17217, value = "Invalid ValidatorFactory implementation.  Must implement '%s'.")
    SwitchYardException invalidValidatorFactoryImplementation(String className);
    
    /**
     * failedToInstantiateValidatorFactory method definition.
     * @param className className
     * @return SwitchYardException
     */
    @Message(id=17218, value = "Failed to create an instance of ValidatorFactory '%s'.  Class must have a public default constructor and not be abstract.")
    SwitchYardException failedToInstantiateValidatorFactory(String className);
    
    /**
     * couldNotInstantiateXmlValidator method definition.
     * @return SwitchYardException
     */
    @Message(id=17219, value = "Could not instantiate XmlValidator: schemaType must be specified.")
    SwitchYardException couldNotInstantiateXmlValidator();
    
    /**
     * couldNotInstantiateXmlValidatorBadSchemaType method definition.
     * @param schemaType schemaType
     * @param values values
     * @return SwitchYardException
     */
    @Message(id=17220, value = "Could not instantiate XmlValidator: schemaType '%s' is invalid."
                    + "It must be the one of %s.")
    SwitchYardException couldNotInstantiateXmlValidatorBadSchemaType(String schemaType, String values);

    /**
     * schemaFileMustBeSpecified method definition.
     * @param schemaType schemaType
     * @return SwitchYardException
     */
    @Message(id=17221, value = "Schema file must be specified for %s validation.")
    SwitchYardException schemaFileMustBeSpecified(String schemaType);

    /**
     * noValidSchemaFileFound method definition.
     * @return SwitchYardException
     */
    @Message(id=17222, value = "No valid schema file was found.")
    SwitchYardException noValidSchemaFileFound();
    
    /**
     * nullValidatorRegistryArgument method definition.
     * @param fileEntryModel fileEntryModel
     * @return IllegalArgumentException
     */
    @Message(id=17223, value = "Please check the switchyard validator configuration.  No DTD file is specified : %s")
    IOException noDTDFile(String fileEntryModel);


}
