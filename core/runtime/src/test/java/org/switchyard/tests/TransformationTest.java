/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.switchyard.tests;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.switchyard.Context;
import org.switchyard.Exchange;
import org.switchyard.ExchangePattern;
import org.switchyard.Message;
import org.switchyard.MessageBuilder;
import org.switchyard.MockHandler;
import org.switchyard.Scope;
import org.switchyard.ServiceDomain;
import org.switchyard.internal.ServiceDomains;
import org.switchyard.internal.transform.BaseTransformer;
import org.switchyard.transform.Transformer;

/**
 * Tests for exercising the TransformationHandler during message exchange.
 */
public class TransformationTest {
    
    private ServiceDomain _domain;

    @Before
    public void setUp() throws Exception {
        _domain = ServiceDomains.getDomain();
    }
    
    
    /* Tests to Add :
     * - test failed transformation
     * - test transform reply 
     */
    
    /**
     * Tests adding a transformer directly to the message context when 
     * invoking a service.
     */
    @Test
    public void testTransformationOnMessageContext() throws Exception {
        final QName serviceName = new QName("messageTransform");
        final Date input = Calendar.getInstance().getTime();
        final String output = new SimpleDateFormat().format(input);
        
        // Define the transformation
        Transformer<Date, String> dateToString = 
                new BaseTransformer<Date, String>() {
            public String transform(Date from) {
                // verify that we are getting the right content as input
                Assert.assertEquals(from, input);
                // transform the input date to the desired output string
                return new SimpleDateFormat().format(from);
            }
        };
        

        // Provide the service
        MockHandler provider = new MockHandler();
        _domain.registerService(serviceName, provider);
        
        // Create the exchange, add the transformer, and invoke the service
        Exchange exchange = _domain.createExchange(
                serviceName, ExchangePattern.IN_ONLY);
        Context msgCtx = exchange.createContext();
        msgCtx.setProperty(Transformer.class.getName(), dateToString);
        
        Message msg = MessageBuilder.newInstance().buildMessage();
        msg.setContent(input);
        exchange.send(msg, msgCtx);
        
        // wait for message and verify transformation
        provider.waitForMessage();
        Assert.assertEquals(provider.getMessages().poll().getMessage().getContent(), output);
    }
    

    /**
     * Identical to testTransformationOnMessageContext, but sets the 
     * transformer on the exchange instead of the message context.
     */
    @Test
    public void testTransformationOnExchangeContext() throws Exception {
        final QName serviceName = new QName("exchangeTransform");
        final Date input = Calendar.getInstance().getTime();
        final String output = new SimpleDateFormat().format(input);
        
        // Define the transformation
        Transformer<Date, String> dateToString = 
                new BaseTransformer<Date, String>() {
            public String transform(Date from) {
                // verify that we are getting the right content as input
                Assert.assertEquals(from, input);
                // transform the input date to the desired output string
                return new SimpleDateFormat().format(from);
            }
        };
        

        // Provide the service
        MockHandler provider = new MockHandler();
        _domain.registerService(serviceName, provider);
        
        // Create the exchange, add the transformer, and invoke the service
        Exchange exchange = _domain.createExchange(
                serviceName, ExchangePattern.IN_ONLY);
        exchange.getContext().setProperty(Transformer.class.getName(), dateToString);
        
        Message msg = MessageBuilder.newInstance().buildMessage();
        msg.setContent(input);
        exchange.send(msg);
        
        // wait for message and verify transformation
        provider.waitForMessage();
        Assert.assertEquals(provider.getMessages().poll().getMessage().getContent(), output);
    }
    
    /**
     * Transformation is loaded from the transformer registry based on the 
     * message names (from/to).
     */
    @Test
    public void testTransformationByName() throws Exception {
        final QName serviceName = new QName("nameTransform");
        final String fromName = "fromA";
        final String toName = "toB";
        final String input = "Hello";
        final String output = "Hello SwitchYard";
        
        // Define the transformation and register it
        Transformer<String, String> helloTransform = 
                new BaseTransformer<String, String>(fromName, toName) {
            public String transform(String from) {
                // transform the input date to the desired output string
                return from + " SwitchYard";
            }
        };
        _domain.getTransformerRegistry().addTransformer(helloTransform);
        
        // Provide the service
        MockHandler provider = new MockHandler();
        _domain.registerService(serviceName, provider);
        
        // Create the exchange and invoke the service
        Exchange exchange = _domain.createExchange(
                serviceName, ExchangePattern.IN_ONLY);
        
        // Set the from and to message names.  NOTE: setting to the to message
        // name will not be necessary once the service definition is available
        // at runtime
        Context msgCtx = exchange.getContext(Scope.MESSAGE);
        msgCtx.setProperty("org.switchyard.message.name", fromName);
        msgCtx.setProperty("org.switchyard.service.message.name", toName);
        
        Message msg = MessageBuilder.newInstance().buildMessage();
        msg.setContent(input);
        exchange.send(msg, msgCtx);
        
        // wait for message and verify transformation
        provider.waitForMessage();
        Assert.assertEquals(provider.getMessages().poll().getMessage().getContent(), output);
    }
}
