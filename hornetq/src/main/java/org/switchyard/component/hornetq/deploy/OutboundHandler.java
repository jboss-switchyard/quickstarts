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

import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientProducer;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.hornetq.api.core.client.ServerLocator;
import org.switchyard.Exchange;
import org.switchyard.HandlerException;
import org.switchyard.component.common.composer.MessageComposer;
import org.switchyard.component.hornetq.composer.HornetQComposition;
import org.switchyard.component.hornetq.config.model.HornetQBindingModel;
import org.switchyard.component.hornetq.config.model.HornetQConfigModel;
import org.switchyard.component.hornetq.internal.HornetQUtil;
import org.switchyard.deploy.BaseServiceHandler;
import org.switchyard.exception.SwitchYardException;

/**
 * A SwitchYard exchange handler that is capable of sending messages to a HornetQ queue.
 * 
 * @author Daniel Bevenius
 *
 */
public class OutboundHandler extends BaseServiceHandler {

    private final ServerLocator _serverLocator;
    private final MessageComposer<ClientMessage> _messageComposer;
    private ClientSessionFactory _factory;
    private ClientSession _session;
    private ClientProducer _producer;
    private HornetQConfigModel _configModel;

    /**
     * Constructs the OutboundHandler.
     * @param hbm the {@link HornetQBindingModel}
     * @param serverLocator the HornetQ {@link ServerLocator}.
     */
    public OutboundHandler(final HornetQBindingModel hbm, final ServerLocator serverLocator) {
        _configModel = hbm.getHornetQConfig();
        _messageComposer = HornetQComposition.getMessageComposer(hbm);
        _serverLocator = serverLocator;
    }
    
    /**
     * Starts this InboundHandler which involves setting up this inbound handler as 
     * a HornetQ MessageHandler so that messgages that arrive on the configured destination
     * will be passed to the SwitchYard ServiceReference.
     * 
     */
    public void start() {
        try {
            _factory =  _serverLocator.createSessionFactory();
            _session = _configModel.isXASession() ? _factory.createXASession() : _factory.createSession();
            _producer = _session.createProducer(_configModel.getQueue());
            _session.start();
        } catch (final Exception e) {
            throw new SwitchYardException(e);
        }
    }
    
    @Override
    public void handleMessage(final Exchange exchange) throws HandlerException {
        // send using producer.
        try {
            final ClientMessage message = _messageComposer.decompose(exchange, _session.createMessage(true));
            _producer.send(message);
        } catch (final Exception e) {
            throw new HandlerException(e);
        }
    }
    
    /**
     * Closes the resources opened by this instance. This includes the producer, session, session factory, 
     * and the server locator.
     * 
     */
    public void stop() {
        HornetQUtil.closeClientProducer(_producer);
        HornetQUtil.closeSession(_session);
        HornetQUtil.closeSessionFactory(_factory);
        HornetQUtil.closeServerLocator(_serverLocator);
    }
}
