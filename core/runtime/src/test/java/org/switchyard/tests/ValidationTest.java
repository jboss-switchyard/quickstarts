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

package org.switchyard.tests;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.switchyard.Exchange;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.MockDomain;
import org.switchyard.MockHandler;
import org.switchyard.Scope;
import org.switchyard.ServiceReference;
import org.switchyard.validate.BaseValidator;
import org.switchyard.validate.ValidationResult;
import org.switchyard.validate.Validator;

/**
 * Tests for exercising the ValidationHandler during message exchange.
 */
public class ValidationTest {
    
    private MockDomain _domain;

    @Before
    public void setUp() throws Exception {
        _domain = new MockDomain();
    }

    /* Tests to Add :
     * - test failed validation
     * - test validate reply 
     */
    
    /**
     * Validation is loaded from the validator registry based on the 
     * message name.
     */
    @Test
    public void testValidationByName() throws Exception {
        final QName serviceName = new QName("nameValidate");
        final QName typeName = new QName("A");
        final String input = "Hello";
        
        // Define the validation and register it
        Validator<String> helloValidate = 
                new BaseValidator<String>(typeName) {
            public ValidationResult validate(String obj) {
                if (obj.equals("Hello")) {
                    return validResult();
                } else {
                    return invalidResult("obj.equals(\"Hello\") was false");
                }
            }
        };
        _domain.getValidatorRegistry().addValidator(helloValidate);

        try {
            // Provide the service
            MockHandler provider = new MockHandler();
            ServiceReference service = _domain.createInOnlyService(serviceName, provider);

            // Create the exchange and invoke the service
            Exchange exchange = service.createExchange();

            // Set the message name.  NOTE: setting to the to message
            // name will not be necessary once the service definition is available
            // at runtime
            Message msg = exchange.createMessage().setContent(input);
            msg.getContext().setProperty(Exchange.CONTENT_TYPE, typeName);

            msg.setContent(input);
            exchange.send(msg);

            // wait for message and verify validation
            provider.waitForOKMessage();
            Assert.assertEquals(provider.getMessages().poll().getMessage().getContent(), input);
        } finally {
            // Must remove this validator, otherwise it's there for the following test... will be
            // fixed once we get rid of the static service domain.
            _domain.getValidatorRegistry().removeValidator(helloValidate);
        }
    }

    /**
     * Test that the ValidateHandler throws an exception if validations are not applied.
     */
    @Test
    public void testValidationsFailed() throws Exception {
        final QName serviceName = new QName("nameValidate");
        final QName typeName = new QName("A");
        final String input = "Hello";

        // Define the validation which always fail and register it
        Validator<String> failValidate = 
                new BaseValidator<String>(typeName) {
            public ValidationResult validate(String obj) {
                return invalidResult("validation fail test");
            }
        };
        _domain.getValidatorRegistry().addValidator(failValidate);

        // Provide the service
        MockHandler provider = new MockHandler();
        ServiceReference service = _domain.createInOnlyService(serviceName, provider);
        
        // Create the exchange and invoke the service
        MockHandler invokerHandler = new MockHandler();
        Exchange exchange = service.createExchange(invokerHandler);

        // Set the message name.  NOTE: setting to the to message
        // name will not be necessary once the service definition is available
        // at runtime
        Message msg = exchange.createMessage().setContent(input);
        msg.getContext().setProperty(Exchange.CONTENT_TYPE, typeName);

        msg.setContent(input);

        exchange.send(msg);

        invokerHandler.waitForFaultMessage();
        Object content = invokerHandler.getFaults().poll().getMessage().getContent();
        Assert.assertTrue(content instanceof HandlerException);
        
        boolean failed = ((HandlerException)content).getMessage().contains("Validator 'org.switchyard.tests.ValidationTest$2' failed: validation fail test");
        Assert.assertTrue(failed);
    }
    
}
