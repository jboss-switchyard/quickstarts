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

package org.switchyard;

/**
 * An ordered list of {@code Handler} instances that can be associated with a 
 * service or exchange instance.  Note that HandlerChain extends ExchangeHandler
 * which provides the ability to add a set of handlers instead of a single handler
 * to an exchange or service.
 */
public interface HandlerChain extends ExchangeHandler {

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
}
