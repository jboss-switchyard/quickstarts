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

package org.switchyard.bus.hornetq;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.Message;
import org.hornetq.api.core.client.ClientConsumer;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientProducer;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.hornetq.api.core.client.MessageHandler;
import org.switchyard.Exchange;
import org.switchyard.ExchangePattern;
import org.switchyard.ExchangePhase;
import org.switchyard.ServiceReference;
import org.switchyard.handlers.HandlerChain;
import org.switchyard.internal.ExchangeImpl;
import org.switchyard.metadata.ExchangeContract;
import org.switchyard.metadata.ServiceOperation;
import org.switchyard.spi.Dispatcher;

/**
 * Creates a Dispatcher instance for handling message exchange for a SwitchYard
 * service.  A HornetQDispatcher creates a core queue and client session for
 * each exchange phase.  So a service which is limited to InOnly operations will
 * only have a single queue and client session.
 */
public class HornetQDispatcher implements Dispatcher, MessageHandler {

    private ServiceReference _service;
    private DispatchQueue _inQueue;
    private DispatchQueue _outQueue;
    private ClientSessionFactory _sessionFactory;
    private HandlerChain _inputHandler;
    private Map<String, HandlerChain> _outputHandlers = new ConcurrentHashMap<String, HandlerChain>();
    
    /**
     * Create a new Dispatcher instance.
     * @param service dispatch for this service
     * @param sessionFactory used to create client sessions for this dispatcher
     * @param inputHandler the exchange handler used to process exchanges for the service
     */
    public HornetQDispatcher(ServiceReference service, 
            ClientSessionFactory sessionFactory, 
            HandlerChain inputHandler) {
        _service = service;
        _sessionFactory = sessionFactory;
        _inputHandler = inputHandler;
    }
    
    @Override
    public ServiceReference getService() {
        return _service;
    }

    @Override
    public void dispatch(Exchange exchange) {
        DispatchQueue dispatch = null;
        
        if (exchange.getPhase().equals(ExchangePhase.IN)) {
             dispatch = _inQueue;
             if (ExchangePattern.IN_OUT.equals(exchange.getContract().getServiceOperation().getExchangePattern())) {
                 _outputHandlers.put(exchange.getId(), ((ExchangeImpl)exchange).getReplyChain());
             }
        } else if (exchange.getPhase().equals(ExchangePhase.OUT)) {
            dispatch = _outQueue;
        } else {
            throw new IllegalArgumentException(
                    "Invalid exchange phase for dispatch: " + exchange.getPhase());
        }
        
        try {
            Message msg = exchangeToMessage(exchange, dispatch.getSession());
            dispatch.getProducer().send(msg);
        } catch (HornetQException hqEx) {
            throw new RuntimeException("Send to HornetQ endpoint failed", hqEx);
        }
    }

    @Override
    public void onMessage(ClientMessage message) {
        Exchange exchange = messageToExchange(message);
        if (ExchangePhase.IN.equals(exchange.getPhase())) {
            _inputHandler.handle(exchange);
        } else if (ExchangePhase.OUT.equals(exchange.getPhase())) {
            HandlerChain chain = _outputHandlers.remove(exchange.getId());
            if (chain != null) {
                chain.handle(exchange);
            }
        }
    }
    
    @Override
    public void stop() {
        try {
            if (_inQueue != null) {
                _inQueue.destroy();
            }
            
            if (_outQueue != null) {
                _outQueue.destroy();
            }
        } catch (HornetQException ex) {
            throw new RuntimeException("Failed to stop HornetQ endpoint " + _service.getName(), ex);
        }
    }
    
    @Override
    public void start() {
        try {
            // Create a queue for receiving input messages
            _inQueue = new DispatchQueue(_sessionFactory.createSession(), 
                    _service.getName().toString() + ExchangePhase.IN, this);
            _inQueue.init();
            
            // Check to see if a queue is required for output messages based on operation MEPs
            for (ServiceOperation op : _service.getInterface().getOperations()) {
                if (op.getExchangePattern().equals(ExchangePattern.IN_OUT)) {
                    // Found at least one InOut, so we need a reply queue
                    _outQueue = new DispatchQueue(_sessionFactory.createSession(), 
                            _service.getName().toString() + ExchangePhase.OUT, this);
                    _outQueue.init();
                    break;
                }
            }
        } catch (HornetQException ex) {
            throw new RuntimeException("Failed to start HornetQ endpoint " + _service.getName(), ex);
        }
    }
    
    private Exchange messageToExchange(Message message) {
        // Read serialized exchange info from message body:
        //     1: exchange ID
        //     2: message exchange pattern
        //     3: exchange phase
        String exchangeId = message.getBodyBuffer().readString();
        String mep = message.getBodyBuffer().readString();
        String phase = message.getBodyBuffer().readString();
        
        // This is a hack until we have serialization stuff sorted
        ExchangeContract contract = ExchangePattern.IN_ONLY.equals(mep)
                ? ExchangeContract.IN_ONLY : ExchangeContract.IN_OUT;
        ExchangePhase exchangePhase = ExchangePhase.valueOf(phase);
        
        return new ExchangeImpl(exchangeId, this, exchangePhase, contract);
    }
    
    private static Message exchangeToMessage(Exchange exchange, ClientSession session) {
        Message msg = session.createMessage(false);  // NOT PERSISTENT
        // Write serialized exchange info to message body:
        //     1: exchange ID
        //     2: message exchange pattern
        //     3: exchange phase
        msg.getBodyBuffer().writeString(exchange.getId());
        msg.getBodyBuffer().writeString(exchange.getContract().getServiceOperation().getExchangePattern().toString());
        msg.getBodyBuffer().writeString(exchange.getPhase().toString());
        
        return msg;
    }
    
}

/**
 * A DispatchQueue is created for each exchange phase on a service operation.
 * An InOnly operation maps to a single dispatch queue, while an InOut operation
 * will map to two dispatch queues (one for the input and one for the output/fault).
 */
class DispatchQueue {
    
    private ClientSession _session;
    private String _name;
    private ClientConsumer _consumer;
    private ClientProducer _producer;
    private MessageHandler _handler;
    
    DispatchQueue(ClientSession session, String name, MessageHandler handler) {
        _session = session;
        _name = name;
        _handler = handler;
    }
    
    void init() throws HornetQException {
        _session.start();
        _session.createQueue(_name, _name);
        _producer = _session.createProducer(_name);
        if (_handler != null) {
            _consumer = _session.createConsumer(_name);
            _consumer.setMessageHandler(_handler);
        }
    }
    
    void destroy() throws HornetQException {
        _consumer.close();
        _producer.close();
        _session.deleteQueue(_name);
        _session.close();
    }
    
    String getName() {
        return _name;
    }
    
    ClientSession getSession() {
        return _session;
    }
    
    ClientConsumer getConsumer() {
        return _consumer;
    }
    
    ClientProducer getProducer() {
        return _producer;
    }
}
