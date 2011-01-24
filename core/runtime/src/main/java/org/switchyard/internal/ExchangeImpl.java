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
import org.switchyard.ExchangePhase;
import org.switchyard.ExchangeState;
import org.switchyard.Message;
import org.switchyard.Service;
import org.switchyard.internal.transform.TransformSequence;
import org.switchyard.metadata.ExchangeContract;
import org.switchyard.spi.Endpoint;

/**
 * Implementation of Exchange.
 */
public class ExchangeImpl implements Exchange {

    private final String            _exchangeId;
    private final ExchangeContract  _contract;
    private ExchangePhase           _phase;
    private final Service           _service;
    private Message                 _message;
    private ExchangeState           _state = ExchangeState.OK;
    private Endpoint                _inputEndpoint;
    private Endpoint                _outputEndpoint;
    private final Context           _context;

    /**
     * Create a new exchange with no endpoints initialized.  At a minimum, the 
     * input endpoint must be set before sending an exchange.
     * @param service service
     * @param contract exchange contract
     */
    ExchangeImpl(Service service, ExchangeContract contract) {
        this(service, contract, null, null);
    }
    
    /**
     * Constructor.
     * @param service service
     * @param contract exchange contract
     * @param input input endpoint
     * @param input output endpoint
     */
    ExchangeImpl(Service service, ExchangeContract contract, Endpoint input, Endpoint output) {

        // Check that the ExchangeContract exists and has invoker metadata and a ServiceOperation defined on it...
        if (contract == null) {
            throw new IllegalArgumentException("null 'contract' arg.");
        } else if (contract.getInvokerInvocationMetaData() == null) {
            throw new IllegalArgumentException("Invalid 'contract' arg.  No invoker invocation metadata defined on the contract instance.");
        } else if (contract.getServiceOperation() == null) {
            throw new IllegalArgumentException("Invalid 'contract' arg.  No ServiceOperation defined on the contract instance.");
        }

        _service = service;
        _contract = contract;
        _inputEndpoint = input;
        _outputEndpoint = output;
        _exchangeId = UUID.randomUUID().toString();
        _context = new DefaultContext();
    }

    @Override
    public Context getContext() {
        return _context;
    }

    @Override
    public ExchangeContract getContract() {
        return _contract;
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
            initInTransformSequence(message);
        } else if (_phase.equals(ExchangePhase.IN)) {
            _phase = ExchangePhase.OUT;
            initOutTransformSequence(message);
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
     * Get input endpoint.
     * @return input endpoint
     */
    public Endpoint getInputEndpoint() {
        return _inputEndpoint;
    }

    /**
     * Get output endpoint.
     * @return output endpoint
     */
    public Endpoint getOutputEndpoint() {
        return _outputEndpoint;
    }

    /**
     * Set the input endpoint.
     * @param input input endpoint
     */
    public void setInputEndpoint(Endpoint input) {
        _inputEndpoint = input;
    }

    /**
     * Set the source endpoint.
     * @param output output endpoint
     */
    public void setOutputEndpoint(Endpoint output) {
        _outputEndpoint = output;
    }

    /**
     * Internal send method common to sendFault and sendMessage.  This method
     * assumes that the exchange phase has been assigned for the send and that
     * the exchange state has been verified (i.e. it's OK to deliver in the
     * exchange's current state).
     */
    private void sendInternal(Message message) {
        _message = message;
        
        switch (_phase) {
        case IN:
            _inputEndpoint.send(this);
            break;
        case OUT:
            _outputEndpoint.send(this);
            break;
        default:
            throw new RuntimeException("No endpoint assigned for exchange phase " + _phase);
        }
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

    private void initInTransformSequence(Message message) {
        String exchangeInputType = _contract.getInvokerInvocationMetaData().getInputType();
        String serviceOperationInputType = _contract.getServiceOperation().getInputType();

        if (exchangeInputType != null && serviceOperationInputType != null) {
            TransformSequence.
                    from(exchangeInputType).
                    to(serviceOperationInputType).
                    associateWith(message.getContext());
        }
    }

    private void initOutTransformSequence(Message message) {
        String serviceOperationOutputType = _contract.getServiceOperation().getOutputType();
        String exchangeOutputType = _contract.getInvokerInvocationMetaData().getOutputType();

        if (serviceOperationOutputType != null && exchangeOutputType != null) {
            TransformSequence.
                    from(serviceOperationOutputType).
                    to(exchangeOutputType).
                    associateWith(message.getContext());
        }
    }
}
