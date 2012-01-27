/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
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
            public boolean validate(String obj) {
                return obj.equals("Hello");
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
            exchange.getContext().setProperty(Exchange.CONTENT_TYPE, typeName, Scope.IN);
            
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
            public boolean validate(String obj) {
                return false;
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
        exchange.getContext().setProperty(Exchange.CONTENT_TYPE, typeName, Scope.IN);

        msg.setContent(input);

        exchange.send(msg);

        invokerHandler.waitForFaultMessage();
        Object content = invokerHandler.getFaults().poll().getMessage().getContent();
        Assert.assertTrue(content instanceof HandlerException);
        Assert.assertEquals("Validator 'org.switchyard.tests.ValidationTest$2' returned false.  Check input payload matches requirements of the Validator implementation.", ((HandlerException)content).getMessage());
    }
}
