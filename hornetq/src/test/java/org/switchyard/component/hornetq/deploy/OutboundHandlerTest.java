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
package org.switchyard.component.hornetq.deploy;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.core.client.ClientConsumer;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ServerLocator;
import org.hornetq.core.remoting.impl.invm.InVMConnectorFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.switchyard.Context;
import org.switchyard.Exchange;
import org.switchyard.Message;
import org.switchyard.component.hornetq.ServerLocatorBuilder;
import org.switchyard.component.hornetq.config.model.HornetQBindingModel;
import org.switchyard.component.hornetq.config.model.v1.V1HornetQBindingModel;
import org.switchyard.component.hornetq.config.model.v1.V1HornetQConfigModel;
import org.switchyard.component.hornetq.internal.HornetQUtil;
import org.switchyard.test.mixins.HornetQMixIn;

/**
 * Unit test for {@link OutboundHandler}.
 * 
 * @author Daniel Bevenius
 *
 */
public class OutboundHandlerTest {
    
    private static final String QUEUE = "jms.queue.producer";
    private static HornetQMixIn _hornetQMixIn;

    @BeforeClass
    public static void setupHornetQ() {
		_hornetQMixIn = new HornetQMixIn();
		_hornetQMixIn.initialize();
    }

    @Test
    public void handleMessage() throws Exception {
        final String payload = "payload";
        final ServerLocator serverLocator = getServerLocator();
        final HornetQBindingModel implModel = getBindingModel();
        
        final OutboundHandler outboundHandler = new OutboundHandler(implModel, serverLocator);
        outboundHandler.start();
        outboundHandler.handleMessage(createExchange(payload));
        
        final ClientConsumer consumer = _hornetQMixIn.getClientSession().createConsumer(QUEUE);
        final ClientMessage hornetQMessage = consumer.receive(3000);
        assertThat(hornetQMessage, is(notNullValue()));
        assertThat(new String(HornetQUtil.readBytes(hornetQMessage)), is(equalTo(payload)));
        
        HornetQUtil.closeClientConsumer(consumer);
        outboundHandler.stop();
    }
    
    private V1HornetQBindingModel getBindingModel() {
        final V1HornetQBindingModel bindingModel = new V1HornetQBindingModel();
        V1HornetQConfigModel configModel = new V1HornetQConfigModel();
        configModel.setQueue(QUEUE);
        bindingModel.setHornetQConfig(configModel);
        return bindingModel;
    }
    
    private ServerLocator getServerLocator() {
        return new ServerLocatorBuilder()
            .transportConfigurations(new TransportConfiguration(InVMConnectorFactory.class.getName()))
            .build();
    }
    
    private Exchange createExchange(final String content) {
        final Exchange exchange = mock(Exchange.class);
        final Message message = mock(Message.class);
        final Context context = mock(Context.class);
        when(message.getContent(byte[].class)).thenReturn(content.getBytes());
        when(exchange.getMessage()).thenReturn(message);
        when(exchange.getContext()).thenReturn(context);
        return exchange;
    }
    
    @AfterClass
    public static void shutdownHornetQ() {
        _hornetQMixIn.uninitialize();
    }

}
