/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.switchyard.component.camel;

import org.jboss.logging.Messages;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;
import org.switchyard.SwitchYardException;

/**
 * <p/>
 * This file is using the subset 33000-33399 for logger messages.
 * <p/>
 *
 */
@MessageBundle(projectCode = "SWITCHYARD")
public interface CamelComponentMessages {
    /**
     * The default messages.
     */
    CamelComponentMessages MESSAGES = Messages.getBundle(CamelComponentMessages.class);

    /**
     * noRoutesFoundInXMLFile method definition.
     * @param xmlPath xmlPath
     * @return SwitchYardException
     */
    @Message(id = 33000, value = "No routes found in XML file %s")
    SwitchYardException noRoutesFoundInXMLFile(String xmlPath);
    
    /**
     * javaDSLClassMustExtend method definition.
     * @param javaDSLClassName Java DSL class name
     * @param className class Name
     * @return SwitchYardException
     */
    @Message(id = 33001, value = "Java DSL class %s must extend %s")
    SwitchYardException javaDSLClassMustExtend(String javaDSLClassName, String className);

    /**
     * noRoutesFoundinJavaDSLClass method definition.
     * @param className class name
     * @return SwitchYardException
     */
    @Message(id = 33002, value = "No routes found in Java DSL class %s")
    SwitchYardException noRoutesFoundinJavaDSLClass(String className);
    
    /**
     * failedToInitializeDSLClass method definition.
     * @param className class name
     * @param ex exception
     * @return SwitchYardException
     */
    @Message(id = 33003, value = "Failed to initialize Java DSL class %s")
    SwitchYardException failedToInitializeDSLClass(String className, @Cause Exception ex);
    
    /**
     * mustHaveAtLeastOneInput method definition.
     * @return SwitchYardException
     */
    @Message(id = 33004, value = "Every route must have at least one input")
    SwitchYardException mustHaveAtLeastOneInput();
    
    /**
     * onlyOneSwitchYardInputPerImpl method definition.
     * @return SwitchYardException
     */
    @Message(id = 33005, value = "Only one switchyard input per implementation is allowed")
    SwitchYardException onlyOneSwitchYardInputPerImpl();
    
    /**
     * implementationConsumerDoesNotMatchService method definition.
     * @param serviceName service name
     * @return SwitchYardException
     */
    @Message(id = 33006, value = "The implementation consumer doesn't match expected service %s")
    SwitchYardException implementationConsumerDoesNotMatchService(String serviceName);
    
    /**
     * couldNotFindServiceReference method definition.
     * @param referenceName reference name
     * @param to to 
     * @return SwitchYardException
     */
    @Message(id = 33007, value = "Could not find the service reference for '%s' which is referenced in %s")
    SwitchYardException couldNotFindServiceReference(String referenceName, String to);
    
    /**
     * couldNotFindServiceName method definition.
     * @param qualifiedRefName qualified reference name
     * @param to to
     * @return SwitchYardException
     */
    @Message(id = 33008, value = "Could not find the service name '%s' which is referenced in %s")
    SwitchYardException couldNotFindServiceName(String qualifiedRefName, String to);
    
    /**
     * cannotCreateComponentImpl method definition.
     * @return SwitchYardException
     */
    @Message(id = 33009, value = "Can not create camel based component implementation without consuming from switchyard service")
    SwitchYardException cannotCreateComponentImpl();
                
    /**
     * noServiceReferenceFoundForURI method definition.
     * @param uri uri
     * @return NullPointerException
     */
    @Message(id = 33015, value = "No ServiceReference was found for uri [%s]")
    NullPointerException noServiceReferenceFoundForURI(String uri);
        
}
