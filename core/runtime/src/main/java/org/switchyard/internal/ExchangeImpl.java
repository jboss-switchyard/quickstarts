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

package org.switchyard.internal;

import java.util.UUID;

import org.switchyard.Context;
import org.switchyard.Exchange;
import org.switchyard.ExchangePattern;
import org.switchyard.ExchangePhase;
import org.switchyard.ExchangeState;
import org.switchyard.HandlerChain;
import org.switchyard.Message;
import org.switchyard.Service;
import org.switchyard.spi.Endpoint;

/**
 * Implementation of Exchange.
 */
public class ExchangeImpl implements Exchange {

    /**
     * In message.
     */
    public static final String IN_MSG = "in";
    /**
     * Out message.
     */
    public static final String OUT_MSG = "out";
    /**
     * Fault message.
     */
    public static final String FAULT_MSG = "fault";

    private final String            _exchangeId;
    private final ExchangePattern   _pattern;
    private ExchangePhase           _phase;
    private final Service           _service;
    private Message                 _message;
    private ExchangeState           _state = ExchangeState.OK;
    private final HandlerChain      _handlers;
    private Endpoint                _source;
    private Endpoint                _target;
    private final Context           _context;

    /**
     * Constructor.
     * @param service service
     * @param pattern exchange pattern
     * @param handlers handlers
     */
    ExchangeImpl(Service service, ExchangePattern pattern, HandlerChain handlers) {
        _service = service;
        _pattern = pattern;
        _handlers = handlers;
        _exchangeId = UUID.randomUUID().toString();
        _context = new DefaultContext();
    }

    @Override
    public Context getContext() {
        return _context;
    }

    @Override
    public ExchangePattern getPattern() {
        return _pattern;
    }

    @Override
    public Service getService() {
        return _service;
    }

    @Override
    public String getId() {
        return _exchangeId;
    }

    @Override
    public Message getMessage() {
        return _message;
    }

    @Override
    public void send(Message message) {
        assertExchangeStateOK();
        
        // Set exchange phase
        if (_phase == null) {
            _phase = ExchangePhase.IN;
        } else if (_phase.equals(ExchangePhase.IN)) {
            _phase = ExchangePhase.OUT;
        } else {
            throw new IllegalStateException(
                    "Send message not allowed for exchange in phase " + _phase);
        }
        
        sendInternal(message);
    }

    @Override
    public void sendFault(Message message) {
        assertExchangeStateOK();
        
        // You can't send a fault before you send a message
        if (_phase == null) {
            throw new IllegalStateException("Send fault no allowed on new exchanges");        
        }
        
        _phase = ExchangePhase.OUT;
        _state = ExchangeState.FAULT;
        sendInternal(message);
    }

    @Override
    public ExchangeState getState() {
        return _state;
    }

    /**
     * Get source endpoint.
     * @return source
     */
    public Endpoint getSource() {
        return _source;
    }

    /**
     * Get target endpoint.
     * @return target
     */
    public Endpoint getTarget() {
        return _target;
    }

    /**
     * Set the target endpoint.
     * @param target target endpoint
     */
    public void setTarget(Endpoint target) {
        _target = target;
    }

    /**
     * Set the source endpoint.
     * @param source source endpoint
     */
    public void setSource(Endpoint source) {
        _source = source;
    }

    private void sendInternal(Message message) {
        _message = message;
        _handlers.handle(this);
    }

    private void assertExchangeStateOK() {
        if (_state == ExchangeState.FAULT) {
            throw new IllegalStateException("Exchange instance is in a FAULT state.");
        }
    }

    @Override
    public Message createMessage() {
        return new DefaultMessage();
    }

    @Override
    public ExchangePhase getPhase() {
        return _phase;
    }
}
