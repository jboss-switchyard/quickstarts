/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */

package org.switchyard;

import java.util.List;


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
}
