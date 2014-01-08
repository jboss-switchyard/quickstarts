package org.switchyard.component.bean.internal;

import org.switchyard.Context;
import org.switchyard.Exchange;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.component.bean.ReferenceInvocation;

/**
 * Implements ReferenceInvocation backed by an exchange.
 */
public class ExchangeInvocation implements ReferenceInvocation {
    
    private Exchange _exchange;
    private Message _inMessage;
    private InvocationResponseHandler _replyHandler;
    
    /**
     * Create a new instance of ExchangeInvocation.
     * @param exchange exchange to be used for the invocation
     * @param replyHandler takes care of replies/faults for the exchange
     */
    public ExchangeInvocation(Exchange exchange, InvocationResponseHandler replyHandler) {
        _exchange = exchange;
        _inMessage = _exchange.createMessage();
        _replyHandler = replyHandler;
    }

    @Override
    public Context getContext() {
        return _exchange.getContext();
    }

    @Override
    public Message getMessage() {
        return isNew() ? _inMessage : _exchange.getMessage();
    }

    @Override
    public ReferenceInvocation invoke() throws Exception {
        if (!isNew()) {
            throw new IllegalStateException(
                    "Repeated calls to invoke() on ExchangeInvocation are not permitted");
        }
        _exchange.send(_inMessage);
        if (_replyHandler.isFault()) {
            Object error = _replyHandler.getExchange().getMessage().getContent();

            // Handling case where fault content is not an exception
            if (!Throwable.class.isAssignableFrom(error.getClass())) {
                throw createException(_replyHandler.getExchange());
            }
            
            // Unwrap HandlerException instances if appropriate
            if (error instanceof HandlerException) {
                HandlerException haEx = (HandlerException)error;
                error = haEx.isWrapper() ? haEx.getCause() : haEx;
            }
            
            // Time to throw up!
            if (error instanceof Exception) {
                throw (Exception)error;
            } else {
                throw new Exception((Throwable)error);
            }
        }
        return this;
    }

    @Override
    public ReferenceInvocation invoke(Object content) throws Exception {
        _inMessage.setContent(content);
        return invoke();
    }

    @Override
    public ReferenceInvocation setProperty(String name, String value) {
        _exchange.getContext(_inMessage).setProperty(name, value);
        return this;
    }

    @Override
    public Object getProperty(String name) {
        return _exchange.getContext().getPropertyValue(name);
    }
    
    boolean isNew() {
        return _exchange.getPhase() == null;
    }
    
    /**
     * Utility method to create consistent exceptions for invocations.
     */
    Exception createException(Exchange exchange) {
        String exMsg = null;
        try {
            // Attempt to convert to String
            exMsg = exchange.getMessage().getContent(String.class);
        } catch (Exception ex) {
            // No luck converting, go with toString
            Object content = exchange.getMessage().getContent();
            if (content != null) {
                exMsg = content.toString();
            }
        }
        return new Exception(exMsg);
    }

}
