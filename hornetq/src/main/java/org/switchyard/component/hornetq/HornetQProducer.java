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
package org.switchyard.component.hornetq;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientProducer;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.hornetq.api.core.client.ServerLocator;
import org.switchyard.component.hornetq.internal.HornetQUtil;

/**
 * A HornetQProducer provides a channel for which clients can create and
 * invoke message exchanges on an HornetQEndpoint.
 * 
 * @author Daniel Bevenius
 *
 */
public class HornetQProducer extends DefaultProducer {

    private final String _destination;
    private ClientSession _session;
    private ClientProducer _producer;
    private ServerLocator _serverLocator;
    private ClientSessionFactory _factory;
    private HornetQEndpoint _hornetQEndpoint;

    /**
     * Sole constructor.
     * 
     * @param endpoint the endpoint to be associated with this producer.
     * @param serverLocator the serverLocator that this producer will use to communicate with HornetQ.
     * @param destination the HornetQ destination name.
     */
    public HornetQProducer(final HornetQEndpoint endpoint, final ServerLocator serverLocator, final String destination) {
        super(endpoint);
        _hornetQEndpoint = endpoint;
        _destination = destination;
        _serverLocator = serverLocator;
    }
    
    @Override
    protected void doStart() throws Exception {
        super.doStart();
        _factory =  _serverLocator.createSessionFactory();
        _session = _hornetQEndpoint.isXASession() ? _factory.createXASession() : _factory.createSession();
        _producer = _session.createProducer(_destination);
    }
    
    @Override
    protected void doStop() throws Exception {
        try {
            super.doStop();
        } finally {
            HornetQUtil.closeClientProducer(_producer);
            HornetQUtil.closeSession(_session);
            HornetQUtil.closeSessionFactory(_factory);
            HornetQUtil.closeServerLocator(_serverLocator);
        }
    }

    @Override
    public void process(final Exchange exchange) throws Exception {
        final Object body = exchange.getIn().getBody();
        
        final ClientMessage message = _session.createMessage(_hornetQEndpoint.isDurable());
        final ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        final ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOut);
        objectOutputStream.writeObject(body);
        message.getBodyBuffer().writeBytes(byteOut.toByteArray());
        _producer.send(message);
    }
    
}
