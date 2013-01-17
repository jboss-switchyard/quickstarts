/*
 * JBoss, Home of Professional Open Source Copyright 2009, Red Hat Middleware
 * LLC, and individual contributors by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.switchyard.component.camel;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.xml.namespace.QName;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.Service;
import org.switchyard.ServiceReference;
import org.switchyard.component.camel.common.composer.CamelBindingData;
import org.switchyard.component.camel.common.composer.CamelComposition;
import org.switchyard.component.common.composer.MessageComposer;
import org.switchyard.metadata.ExchangeContract;
import org.switchyard.metadata.ServiceOperation;
import org.switchyard.metadata.java.JavaService;

/**
 * Unit test for {@link CamelResponseHandler}
 * 
 * @author Daniel Bevenius
 */
public class CamelResponseHandlerTest {

    private static final QName JAVA_STRING_QNAME = new QName("java:java.lang.String");
    private static final QName SERVICE_QNAME = new QName("java:" + MockService.class.getName());

    private MessageComposer<CamelBindingData> _messageComposer;

    @Before
    public void before() {
        _messageComposer = CamelComposition.getMessageComposer();
    }

    @Test (expected = RuntimeException.class)
    public void constructor() throws Exception {
        final CamelResponseHandler responseHandler = new CamelResponseHandler(null, null, _messageComposer);
        responseHandler.handleMessage(null);
    }

    @Test
    public void handleMessageWithOutputType() throws HandlerException {
        final Exchange camelExchange = createCamelExchange();
        final ServiceReference serviceReference = createMockServiceRef();
        final org.switchyard.Exchange switchYardExchange = createMockExchangeWithBody(new MessageCreator() {
            @Override
            public Message create() {
                Message message = mock(Message.class);
                when(message.getContent(Integer.class)).thenReturn(10);
                return message;
            }
        });
        final CamelResponseHandler responseHandler = new CamelResponseHandler(camelExchange, serviceReference, _messageComposer);

        responseHandler.handleMessage(switchYardExchange);

        assertThat(switchYardExchange.getMessage().getContent(Integer.class), is(equalTo(new Integer(10))));
    }

    @Test
    public void fault() throws HandlerException {
        final String fault = "some fault";
        final Exchange camelExchange = createCamelExchange();
        final ServiceReference serviceReference = createMockServiceRef();
        final org.switchyard.Exchange switchYardExchange = createMockExchangeWithBody(new MessageCreator() {
            @Override
            public Message create() {
                Message message = mock(Message.class);
                when(message.getContent()).thenReturn(fault);
                return message;
            }
        });
        final CamelResponseHandler responseHandler = new CamelResponseHandler(camelExchange, serviceReference, _messageComposer);

        responseHandler.handleFault(switchYardExchange);

        assertTrue(camelExchange.getIn().isFault());
        assertSame(fault, camelExchange.getIn().getBody());
    }

    @Test
    public void exception() throws HandlerException {
        final IllegalArgumentException exception = new IllegalArgumentException();
        final Exchange camelExchange = createCamelExchange();
        final ServiceReference serviceReference = createMockServiceRef();
        final org.switchyard.Exchange switchYardExchange = createMockExchangeWithBody(new MessageCreator() {
            @Override
            public Message create() {
                Message message = mock(Message.class);
                when(message.getContent()).thenReturn(exception);
                return message;
            }
        });
        final CamelResponseHandler responseHandler = new CamelResponseHandler(camelExchange, serviceReference, _messageComposer);

        responseHandler.handleFault(switchYardExchange);
        assertSame(exception, camelExchange.getException());
    }

    private Exchange createCamelExchange() {
        return new DefaultExchange((CamelContext) null);
    }

    private ServiceReference createMockServiceRef() {
        final ServiceReference serviceReference = mock(ServiceReference.class);
        when(serviceReference.getInterface()).thenReturn(JavaService.fromClass(MockService.class));
        return serviceReference;
    }

    private org.switchyard.Exchange createMockExchangeWithBody(MessageCreator creator) {
        final org.switchyard.Exchange switchYardExchange = mock(org.switchyard.Exchange.class);
        final org.switchyard.Context switchYardContext = mock(org.switchyard.Context.class);
        final ExchangeContract exchangeContract = mock(ExchangeContract.class);
        final ServiceOperation serviceOperation = mock(ServiceOperation.class);
        final ServiceOperation referenceOperation = mock(ServiceOperation.class);
        final Service provider = mock(Service.class);

        final Message message = creator.create();
        when(switchYardExchange.getMessage()).thenReturn(message);

        when(provider.getName()).thenReturn(SERVICE_QNAME);
        when(switchYardExchange.getProvider()).thenReturn(provider);
        when(switchYardExchange.getContext()).thenReturn(switchYardContext);
        when(referenceOperation.getOutputType()).thenReturn(JAVA_STRING_QNAME);
        when(exchangeContract.getProviderOperation()).thenReturn(serviceOperation);
        when(serviceOperation.getInputType()).thenReturn(JAVA_STRING_QNAME);
        when(serviceOperation.getOutputType()).thenReturn(null);
        when(serviceOperation.getFaultType()).thenReturn(null);
        when(exchangeContract.getConsumerOperation()).thenReturn(referenceOperation);
        when(switchYardExchange.getContract()).thenReturn(exchangeContract);

        return switchYardExchange;
    }

    private class MockService {
        @SuppressWarnings("unused")
        public void print(final String msg) {
        }
    }

    /**
     * Interface which let populate mock message.
     */
    interface MessageCreator {
        Message create();
    }

}
