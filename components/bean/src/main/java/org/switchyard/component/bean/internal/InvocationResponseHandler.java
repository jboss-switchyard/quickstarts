package org.switchyard.component.bean.internal;

import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.HandlerException;

/**
 * Used to handle responses and faults for exchanges used by an 
 * ExchangeInvoker.
 */
public class InvocationResponseHandler implements ExchangeHandler {
    
    private Exchange _exchange;
    private boolean _isFault;

    @Override
    public void handleMessage(Exchange exchange) throws HandlerException {
        _exchange = exchange;
    }

    @Override
    public void handleFault(Exchange exchange) {
        _isFault = true;
        _exchange = exchange;
    }
    
    /**
     * Reports whether the exchange is in fault state.
     * @return true if the exchange resulted in a fault, false otherwise
     */
    public boolean isFault() {
        return _isFault;
    }
    
    /**
     * Gets the Exchange object received with a fault/reply message.
     * @return exchange
     */
    public Exchange getExchange() {
        return _exchange;
    }

}
