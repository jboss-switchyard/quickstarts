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
import org.switchyard.ServiceReference;
import org.switchyard.transform.BaseTransformer;
import org.switchyard.transform.TransformSequence;
import org.switchyard.transform.Transformer;

/**
 * Tests for exercising the TransformationHandler during message exchange.
 */
public class TransformationTest {
    
    private MockDomain _domain;

    @Before
    public void setUp() throws Exception {
        _domain = new MockDomain();
    }

    /* Tests to Add :
     * - test failed transformation
     * - test transform reply 
     */
    
    /**
     * Transformation is loaded from the transformer registry based on the 
     * message names (from/to).
     */
    @Test
    public void testTransformationByName() throws Exception {
        final QName serviceName = new QName("nameTransform");
        final QName inType = new QName("fromA");
        final QName expectedDestType = new QName("toB");
        final String input = "Hello";
        final String output = "Hello SwitchYard";
        
        // Define the transformation and register it
        Transformer<String, String> helloTransform = 
                new BaseTransformer<String, String>(inType, expectedDestType) {
            public String transform(String from) {
                // transform the input date to the desired output string
                return from + " SwitchYard";
            }
        };
        _domain.getTransformerRegistry().addTransformer(helloTransform);

        try {
            // Provide the service
            MockHandler provider = new MockHandler();
            ServiceReference service = _domain.createInOnlyService(serviceName, provider);

            // Create the exchange and invoke the service
            Exchange exchange = service.createExchange();

            // Set the from and to message names.  NOTE: setting to the to message
            // name will not be necessary once the service definition is available
            // at runtime
            Message msg = exchange.createMessage().setContent(input);
            TransformSequence.
                    from(inType).
                    to(expectedDestType).
                    associateWith(msg);

            msg.setContent(input);
            exchange.send(msg);

            // wait for message and verify transformation
            provider.waitForOKMessage();
            Assert.assertEquals(provider.getMessages().poll().getMessage().getContent(), output);
        } finally {
            // Must remove this transformer, otherwise it's there for the following test... will be
            // fixed once we get rid of the static service domain.
            _domain.getTransformerRegistry().removeTransformer(helloTransform);
        }
    }

    /**
     * Test that the TransformHandler throws an exception if transformations are not applied.
     */
    @Test
    public void testTransformationsNotApplied() throws Exception {
        final QName serviceName = new QName("nameTransform");
        final QName inType = new QName("fromA");
        final QName expectedDestType = new QName("toB");
        final String input = "Hello";

        // Provide the service
        MockHandler provider = new MockHandler();
        ServiceReference service = _domain.createInOnlyService(serviceName, provider);

        // Create the exchange and invoke the service
        MockHandler invokerHandler = new MockHandler();
        Exchange exchange = service.createExchange(invokerHandler);

        // Set the from and to message names.  NOTE: setting to the to message
        // name will not be necessary once the service definition is available
        // at runtime
        Message msg = exchange.createMessage().setContent(input);
        TransformSequence.
                from(inType).
                to(expectedDestType).
                associateWith(msg);

        msg.setContent(input);

        exchange.send(msg);

        invokerHandler.waitForFaultMessage();
        Object content = invokerHandler.getFaults().poll().getMessage().getContent();
        Assert.assertTrue(content instanceof HandlerException);
        String testString = "Transformations not applied.  Required payload type of 'toB'.  Actual payload type is 'fromA'.  You must define and register a Transformer to transform between these types.";
        boolean transformsApplied = ((HandlerException)content).getMessage().contains(testString);
        Assert.assertTrue(transformsApplied);
    }
}
