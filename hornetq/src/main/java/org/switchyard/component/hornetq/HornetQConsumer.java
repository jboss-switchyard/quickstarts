/*
 * JBoss, Homek of Professional Open Source Copyright 2009, Red Hat Middleware
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
package org.switchyard.component.hornetq;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;
import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientConsumer;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.hornetq.api.core.client.MessageHandler;
import org.hornetq.api.core.client.ServerLocator;
import org.hornetq.api.core.client.SessionFailureListener;
import org.switchyard.component.hornetq.internal.HornetQUtil;

/**
 * A Camel consumer that is capible of listening to a HornetQ destination
 * and passing messages to a Camel route.
 * 
 * @author Daniel Bevenius
 *
 */
public class HornetQConsumer extends DefaultConsumer implements MessageHandler, SessionFailureListener { 

    private final String _destination;
    private ClientSession _session;
    private ClientConsumer _consumer;
    private ServerLocator _serverLocator;
    private ClientSessionFactory _factory;
    private final HornetQEndpoint _hornetQEndpoint;


    /**
     * Sole constructor for creating HornetQConsumers.
     * 
     * @param endpoint the endpoint associated with this Consumer.
     * @param processor the processor associated with this Consumer.
     * @param serverLocator the HornetQ {@link ServerLocator} that this Consumer will use.
     * @param destination the name of the HornetQ destination.
     */
    public HornetQConsumer(final HornetQEndpoint endpoint, final Processor processor, final ServerLocator serverLocator, final String destination) {
        super(endpoint, processor);
        _hornetQEndpoint = endpoint;
        _destination = destination;
        _serverLocator = serverLocator;
    }
    
    @Override
    protected void doStart() throws Exception {
        super.doStart();
        _factory =  _serverLocator.createSessionFactory();
        _session = _hornetQEndpoint.isXASession() ? _factory.createXASession() : _factory.createSession();
        _consumer = _session.createConsumer(_destination);
        _consumer.setMessageHandler(this);
        _session.addFailureListener(this);
        _session.start();
    }

    @Override
    protected void doStop() throws Exception {
        try {
            super.doStop();
        } finally {
            HornetQUtil.closeClientConsumer(_consumer);
            HornetQUtil.closeSession(_session);
            HornetQUtil.closeSessionFactory(_factory);
            HornetQUtil.closeServerLocator(_serverLocator);
        }
    }

    @Override
    public void onMessage(final ClientMessage message) {
        final Exchange exchange = getEndpoint().createExchange();
        byte[] bytes = new byte[message.getBodyBuffer().readableBytes()];
        message.getBodyBuffer().readBytes(bytes);
        exchange.getIn().setBody(bytes);
        try {
            getProcessor().process(exchange);
        } catch (final Exception e) {
            getExceptionHandler().handleException(e);
        }
    }

    @Override
    public void connectionFailed(final HornetQException exception, boolean failedOver) {
        if (!failedOver) {
            getExceptionHandler().handleException(exception);
        }
    }

    @Override
    public void beforeReconnect(final HornetQException exception) {
    }

}
