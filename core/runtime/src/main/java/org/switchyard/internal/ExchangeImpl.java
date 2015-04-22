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

package org.switchyard.internal;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.xml.namespace.QName;

import org.jboss.logging.Logger;
import org.switchyard.Context;
import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.ExchangePattern;
import org.switchyard.ExchangePhase;
import org.switchyard.ExchangeSecurity;
import org.switchyard.ExchangeState;
import org.switchyard.Message;
import org.switchyard.Scope;
import org.switchyard.Service;
import org.switchyard.ServiceDomain;
import org.switchyard.ServiceReference;
import org.switchyard.label.BehaviorLabel;
import org.switchyard.metadata.BaseExchangeContract;
import org.switchyard.metadata.ExchangeContract;
import org.switchyard.metadata.ServiceOperation;
import org.switchyard.runtime.RuntimeLogger;
import org.switchyard.runtime.RuntimeMessages;
import org.switchyard.runtime.event.ExchangeCompletionEvent;
import org.switchyard.runtime.event.ExchangeInitiatedEvent;
import org.switchyard.security.context.DefaultExchangeSecurity;
import org.switchyard.spi.Dispatcher;

/**
 * Implementation of Exchange.
 */
public class ExchangeImpl implements Exchange {

    private static Logger _log = Logger.getLogger(ExchangeImpl.class);

    private ExchangePhase              _phase;
    private Message                    _message;
    private ExchangeState              _state = ExchangeState.OK;
    private Dispatcher                 _dispatch;
    private ExchangeHandler            _replyHandler;
    private ServiceDomain              _domain;
    private Long                       _startTime;
    private Context                    _context;
    private CompositeContext           _compositeContext;
    private ServiceReference           _consumer;
    private Service                    _provider;
    private BaseExchangeContract       _contract = new BaseExchangeContract();
    private transient ExchangeSecurity _security = null;

    /**
     * Create a new exchange with no endpoints initialized.  At a minimum, the 
     * input endpoint must be set before sending an exchange.
     * @param domain Service domain for this exchange
     * @param dispatcher Exchange dispatcher to use
     */
    public ExchangeImpl(ServiceDomain domain, Dispatcher dispatcher) {
        this(domain, dispatcher, null);
    }

    /**
     * Constructor.
     * @param domain service domain for this exchange
     * @param dispatch exchange dispatcher
     * @param replyHandler handler for replies
     */
    public ExchangeImpl(ServiceDomain domain, Dispatcher dispatch, ExchangeHandler replyHandler) {
        _domain = domain;
        _dispatch = dispatch;
        _replyHandler = replyHandler;
        _context = new DefaultContext();
        _compositeContext = new CompositeContext();
        _compositeContext.setContext(Scope.EXCHANGE, _context);
    }

    @Override
    public Context getContext() {
        return _compositeContext;
    }

    @Override
    public Context getContext(Message message) {
        if (_message != null && _message == message) {
            return getContext();
        }
        return message.getContext();
    }

    @Override
    public Message getMessage() {
        return _message;
    }

    @Override
    public synchronized void send(Message message) {
        assertMessageOK(message);

        // Set exchange phase
        if (_phase == null) {
            _phase = ExchangePhase.IN;
            initContentType(message);
        } else if (_phase.equals(ExchangePhase.IN)) {
            _phase = ExchangePhase.OUT;
            initContentType(message);
            // set relatesTo header on OUT context
            Object propertyValue = _message.getContext().getPropertyValue(MESSAGE_ID);
            message.getContext().setProperty(RELATES_TO, propertyValue)
                .addLabels(BehaviorLabel.TRANSIENT.label());
        } else {
            throw RuntimeMessages.MESSAGES.sendMessageNotAllowed(_phase.toString());
        }

        sendInternal(message);
    }

    @Override
    public synchronized void sendFault(Message message) {
        assertMessageOK(message);
        
        // You can't send a fault before you send a message
        if (_phase == null) {
            throw RuntimeMessages.MESSAGES.sendFaultNotAllowed();
        }
        
        _phase = ExchangePhase.OUT;
        _state = ExchangeState.FAULT;
        initFaultContentType();

        // set relatesTo header on OUT context
        message.getContext().setProperty(RELATES_TO, _message.getContext().getPropertyValue(MESSAGE_ID))
            .addLabels(BehaviorLabel.TRANSIENT.label());

        sendInternal(message);
    }

    @Override
    public ExchangeState getState() {
        return _state;
    }

    /**
     * Get exchange dispatcher.
     * @return exchange dispatcher
     */
    public Dispatcher getDispatcher() {
        return _dispatch;
    }

    @Override
    public ExchangeHandler getReplyHandler() {
        return _replyHandler;
    }

    @Override
    public ExchangePattern getPattern() {
        ExchangePattern pattern = null;
        if (getContract() != null) {
            pattern = getContract().getConsumerOperation().getExchangePattern();
        }
        return pattern;
    }

    @Override
    public ExchangeSecurity getSecurity() {
        if (_security == null) {
            _security = new DefaultExchangeSecurity(this);
        }
        return _security;
    }

    /**
     * Internal send method common to sendFault and sendMessage.  This method
     * assumes that the exchange phase has been assigned for the send and that
     * the exchange state has been verified (i.e. it's OK to deliver in the
     * exchange's current state).
     */
    private void sendInternal(Message message) {
        if (_startTime == null) {
            _startTime = System.nanoTime();
        }
        ExchangePhase sendPhase = _phase;
        
        _message = message;
        // assign messageId
        _message.getContext().setProperty(MESSAGE_ID, UUID.randomUUID().toString())
            .addLabels(BehaviorLabel.TRANSIENT.label());
        _compositeContext.setContext(Scope.MESSAGE, _message.getContext());

        if (_log.isDebugEnabled()) {
            _log.debug("Sending " + _phase + " Message (" + System.identityHashCode(message) + ") on " 
                    + _contract.getConsumerOperation().getExchangePattern() 
                    +  " Exchange (" + instanceHash() + ") for Service '" + _consumer.getName() 
                    + "', operation '" + _contract.getProviderOperation() + "'.  Exchange state: " + _state);
        }
        
        // if a fault was thrown by the handler chain and there's no reply chain
        // we need to log.
        // TODO : stick this in a central fault/error queue
        if (ExchangeState.FAULT.equals(_state) && _replyHandler == null) {
            // Attempt to convert content to String
            String faultContent;
            try {
                faultContent = _message.getContent(String.class);
            } catch (Exception ex) {
                // Well, that didn't work.  Try the next best thing.
                faultContent = _message.getContent().toString();
            }
            RuntimeLogger.ROOT_LOGGER.faultGeneratedDuringExchange(faultContent);
        } else {
            // Publish exchange initiation event
            if (ExchangePhase.IN.equals(getPhase())) {
                getContext().setProperty(ExchangeInitiatedEvent.EXCHANGE_INITIATED_TIME + ".start", Long.toString(System.nanoTime()));
                _domain.getEventPublisher().publish(new ExchangeInitiatedEvent(this));
            }
            
            _dispatch.dispatch(this);
        }
        
        // Publish exchange completion event
        if (isDone(sendPhase)) {
            long duration = System.nanoTime() - _startTime;
            getContext().setProperty(ExchangeCompletionEvent.EXCHANGE_DURATION, 
                    TimeUnit.MILLISECONDS.convert(duration, TimeUnit.NANOSECONDS)).addLabels(BehaviorLabel.TRANSIENT.label());
            _domain.getEventPublisher().publish(new ExchangeCompletionEvent(this));
        }
    }

    private int instanceHash() {
        return System.identityHashCode(this);
    }

    private void assertMessageOK(Message message) {
        if (message == null) {
            throw RuntimeMessages.MESSAGES.invalidMessageArgument();
        }
        if (_state == ExchangeState.FAULT) {
            throw RuntimeMessages.MESSAGES.exchangeInFaultState();
        }

        if (!(message instanceof DefaultMessage)) {
            throw RuntimeMessages.MESSAGES.onlyDefaultMessageInstances();
        }

        if (((DefaultMessage) message).isSent()) {
            throw RuntimeMessages.MESSAGES.messageOnlySentOnce();
        }
        // mark message as sent
        ((DefaultMessage) message).send();
    }

    @Override
    public Message createMessage() {
        DefaultMessage msg = new DefaultMessage();
        if (_domain != null) {
            msg.setTransformerRegistry(_domain.getTransformerRegistry());
        }
        if (_message == null) {
            _compositeContext.setContext(Scope.MESSAGE, msg.getContext());
        }
        return msg;
    }

    @Override
    public ExchangePhase getPhase() {
        return _phase;
    }

    @Override
    public ExchangeContract getContract() {
        return _contract;
    }

    @Override
    public ServiceReference getConsumer() {
        return _consumer;
    }

    @Override
    public Service getProvider() {
        return _provider;
    }
    
    @Override
    public ExchangeImpl consumer(ServiceReference consumer, ServiceOperation operation) {
        if (_phase != null) {
            throw RuntimeMessages.MESSAGES.cannotChangeMetaDataAfterMessageSent();
        }
        if (_replyHandler == null && operation.getExchangePattern() == ExchangePattern.IN_OUT) {
            throw RuntimeMessages.MESSAGES.invalidConsumerContract(); 
        }
        _consumer = consumer;
        _contract.setConsumerOperation(operation);
        return this;
    }
    
    @Override
    public ExchangeImpl provider(Service provider, ServiceOperation operation) {
        if (_phase == ExchangePhase.OUT) {
            throw RuntimeMessages.MESSAGES.cannotChangeMetadataAfterInvoke();
        }
        _provider = provider;
        _contract.setProviderOperation(operation);
        return this;
    }

    protected void setPhase(ExchangePhase phase) {
        _phase = phase;
    }
    
    protected void setMessage(Message message) {
        _message = message;
    }
    
    private void initContentType(Message message) {
        QName exchangeInputType = _contract.getConsumerOperation().getInputType();

        if (exchangeInputType != null) {
            message.getContext().setProperty(Exchange.CONTENT_TYPE, exchangeInputType, Scope.MESSAGE)
                .addLabels(BehaviorLabel.TRANSIENT.label());
        }
    }

    private void initFaultContentType() {
        if (_contract.getProviderOperation() != null) {
            QName serviceOperationFaultType = _contract.getProviderOperation().getFaultType();
            if (serviceOperationFaultType != null) {
                _message.getContext().setProperty(Exchange.FAULT_TYPE, serviceOperationFaultType, Scope.MESSAGE)
                    .addLabels(BehaviorLabel.TRANSIENT.label());
            }
        }
    }
    
    private boolean isDone(ExchangePhase phase) {
        ExchangePattern mep = _contract.getConsumerOperation().getExchangePattern();
        return (ExchangePhase.IN.equals(phase) && ExchangePattern.IN_ONLY.equals(mep))
                || (ExchangePhase.OUT.equals(phase) && ExchangePattern.IN_OUT.equals(mep));
    }
}
