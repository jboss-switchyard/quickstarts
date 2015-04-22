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
package org.switchyard.component.camel.switchyard;

import org.jboss.logging.Messages;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;
import org.switchyard.SwitchYardException;

import org.switchyard.HandlerException;

/**
 * <p/>
 * This file is using the subset 33900-33999 for logger messages.
 * <p/>
 *
 */
@MessageBundle(projectCode = "SWITCHYARD")
public interface SwitchYardCamelComponentMessages {
    /**
     * The default messages.
     */
    SwitchYardCamelComponentMessages MESSAGES = Messages.getBundle(SwitchYardCamelComponentMessages.class);
    
    /**
     * noServiceReferenceFoundForURI method definition.
     * @param uri uri
     * @return NullPointerException
     */
    @Message(id = 33900, value = "No ServiceReference was found for uri [%s]")
    NullPointerException noServiceReferenceFoundForURI(String uri);

    /**
     * unableToDetermineOperation method definition.
     * @return String
     */
    @Message(id = 33901, value = "Unable to determine operation as the target service contains more than one operation")
    String unableToDetermineOperation();
    
    /**
     * camelExchangeFailedWithoutException method definition.
     * @param camelFault camel fault
     * @return HandlerException
     */
    @Message(id = 33902, value = "camel exchange failed without an exception: %s")
    HandlerException camelExchangeFailedWithoutException(String camelFault);

    /**
     * camelExchangeFailed method definition.
     * @return HandlerException
     */
    @Message(id = 33903, value = "camel exchange failed without an exception")
    HandlerException camelExchangeFailed();

    /**
     * invalidHandlerState method definition.
     * @return SwitchYardException
     */
    @Message(id = 33904, value = "Invalid handler state.")
    SwitchYardException invalidHandlerState();

    /**
     * stateCannotBeNull method definition.
     * @return IllegalArgumentException
     */
    @Message(id = 33905, value = "State cannot be null.")
    IllegalArgumentException stateCannotBeNull();

    /**
     * camelExchangeArgumentMustNotBeNull method definition.
     * @return SwitchYardException
     */
    @Message(id = 33906, value = "[camelExchange] argument must not be null")
    SwitchYardException camelExchangeArgumentMustNotBeNull();
            
    /**
     * referenceArgumentMustNotBeNull method definition.
     * @return SwitchYardException
     */
    @Message(id = 33907, value = "[reference] argument must not be null")
    SwitchYardException referenceArgumentMustNotBeNull();

    
}
