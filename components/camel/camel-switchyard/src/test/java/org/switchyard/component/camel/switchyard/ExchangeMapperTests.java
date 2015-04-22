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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.switchyard.common.camel.SwitchYardCamelContextImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.impl.DefaultMessage;

import org.switchyard.ExchangePhase;
import org.switchyard.Message;
import org.switchyard.Property;
import org.switchyard.ServiceReference;
import org.switchyard.Scope;

import org.switchyard.internal.DefaultContext;
import org.switchyard.internal.ExchangeImpl;
import org.switchyard.component.camel.switchyard.ExchangeMapper;
import org.switchyard.component.camel.common.composer.CamelContextMapper;
import org.switchyard.component.camel.common.handler.OutboundHandler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * ExchangeMapperTests
 *
 * Tests to support ExchangeMapper.
 */
public class ExchangeMapperTests {
    @Before
    public void setUp() throws Exception {
    }
    
    /**
     * Test to confirm fixes for SWITCHYARD-2252, where message headers were unexpectedly
     * changed to lower case in Camel services.
     * @throws Exception exception
     */
    @Test
    public void testMapCamelToSwitchYard() throws Exception {
        org.apache.camel.Exchange exchange = createCamelExchange();
        org.apache.camel.Message in = exchange.getIn();
        exchange.getIn().setBody("bar", java.lang.String.class);
        in.setHeader("CamelFileName", "foobar");
        
        org.switchyard.Exchange syExchange = createSwitchYardExchange();
        
        org.switchyard.Message message = ExchangeMapper.mapCamelToSwitchYard(exchange, syExchange,
                ExchangePhase.IN);
        
        Property property = message.getContext().getProperty("CamelFileName");
        assertTrue(property != null);        
        assertTrue("foobar".equals(property.getValue()));
    }
    
    /**
     * Create a mocked SwitchYard exchange
     * @return exchange
     */
    private org.switchyard.Exchange createSwitchYardExchange() {
        org.switchyard.Exchange switchYardExchange = mock(org.switchyard.Exchange.class);
        Message message = mock(org.switchyard.Message.class);
        when(message.getContext()).thenReturn(new DefaultContext(Scope.MESSAGE));
        when(message.getContent(Integer.class)).thenReturn(10);
        when(switchYardExchange.getMessage()).thenReturn(message);
        when(switchYardExchange.createMessage()).thenReturn(message);
        return switchYardExchange;
    }
    
    /**
     * Create a camel exchange
     * @return exchange
     */
    private Exchange createCamelExchange() {
        DefaultMessage message = new DefaultMessage();
        message.setBody("foobar");
        message.setExchange(new DefaultExchange(new SwitchYardCamelContextImpl(false)));
        return message.getExchange();
    }
    
}
