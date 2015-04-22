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

import java.util.List;

import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;

/**
 * An ordered list of {@code Handler} instances that can be associated with a
 * service or exchange instance.  Note that HandlerChain extends ExchangeHandler
 * which provides the ability to add a set of handlers instead of a single
 * handler to an exchange or service.
 */
public interface HandlerChain extends ExchangeHandler {
    
    /**
     * Reserved name for the service provider handler.  This name is used for
     * handlers passed to ServiceDomain.createExchange() and 
     * ServiceDomain.registerService().
     */
    final String PROVIDER_HANDLER = "provider";
    
    /**
     * Reserved name for the service consumer handler.  This name is used for
     * the callback handler used by a consumer to process reply messages.
     */
    final String CONSUMER_HANDLER = "consumer";

    /**
     * Add a handler to the front of the chain.
     * @param handlerName name of the handler to add
     * @param handler handler instance
     */
    void addFirst(String handlerName, ExchangeHandler handler);

    /**
     * Add a handler to the end of the chain.
     * @param handlerName name of the handler to add
     * @param handler handler instance
     */
    void addLast(String handlerName, ExchangeHandler handler);
    
    /**
     * Replaces a specified handler at it's current position in the chain. If 
     * no handler is found in the chain with the specified name, the new handler
     * is *not* added to the chain.
     * @param handlerName name of the handler to replace
     * @param handler handler instnace to replace
     * @return true if the handler was added, false if it was not
     */
    boolean replace(String handlerName, ExchangeHandler handler);

    /**
     * Remove the named handler from this chain.
     * @param handlerName name of the handler to remove
     * @return a reference to the removed handler or null if the named handler
     * was not present in the chain
     */
    ExchangeHandler remove(String handlerName);

    /**
     * Triggers the handler chain with the specified exchange.
     * @param exchange exchange that needs to be handled
     */
    void handle(Exchange exchange);
    
    /**
     * Returns an unmodifiable list of handlers in this chain.
     * @return list of handlers in chain
     */
    List<ExchangeHandler> getHandlers();
    
    /**
     * Create a shallow copy of the handler chain.  The returned chain represents 
     * a distinct collection from the original, but the individual handler
     * instances are identical.  
     * @return copy of the default chain.
     */
    HandlerChain copy();
}
