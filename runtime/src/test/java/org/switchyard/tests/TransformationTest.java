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
import org.switchyard.Context;
import org.switchyard.Exchange;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.MockHandler;
import org.switchyard.Scope;
import org.switchyard.ServiceReference;
import org.switchyard.ServiceDomain;
import org.switchyard.MockDomain;
import org.switchyard.metadata.ExchangeContract;
import org.switchyard.transform.BaseTransformer;
import org.switchyard.transform.TransformSequence;
import org.switchyard.transform.Transformer;

/**
 * Tests for exercising the TransformationHandler during message exchange.
 */
public class TransformationTest {
    
    private ServiceDomain _domain;

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
            ServiceReference service = _domain.registerService(serviceName, provider);

            // Create the exchange and invoke the service
            Exchange exchange = _domain.createExchange(
                    service, ExchangeContract.IN_ONLY);

            // Set the from and to message names.  NOTE: setting to the to message
            // name will not be necessary once the service definition is available
            // at runtime
            Message msg = exchange.createMessage().setContent(input);
            TransformSequence.
                    from(inType).
                    to(expectedDestType).
                    associateWith(exchange, Scope.IN);

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
        ServiceReference service = _domain.registerService(serviceName, provider);

        // Create the exchange and invoke the service
        MockHandler invokerHandler = new MockHandler();
        Exchange exchange = _domain.createExchange(
                service, ExchangeContract.IN_ONLY, invokerHandler);

        // Set the from and to message names.  NOTE: setting to the to message
        // name will not be necessary once the service definition is available
        // at runtime
        Message msg = exchange.createMessage().setContent(input);
        TransformSequence.
                from(inType).
                to(expectedDestType).
                associateWith(exchange, Scope.IN);

        msg.setContent(input);

        exchange.send(msg);

        invokerHandler.waitForFaultMessage();
        Object content = invokerHandler.getFaults().poll().getMessage().getContent();
        Assert.assertTrue(content instanceof HandlerException);
        Assert.assertEquals("Transformations not applied.  Required payload type of 'toB'.  Actual payload type is 'fromA'.  You must define and register a Transformer to transform between these types.", ((HandlerException)content).getMessage());
    }
}
