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

package org.switchyard.spi;

import org.switchyard.ServiceReference;
import org.switchyard.handlers.HandlerChain;

/**
 * An implementation-neutral representation of a message bus.  The bus handles
 * dispatch for message exchange between service consumer and provider.
 */
public interface ExchangeBus {
    /**
     * Create an exchange dispatcher for the specified service and handler chain.
     * @param service service metadata
     * @param handlerChain handlers invoked by dispatcher on delivery
     * @return Dispatcher service dispatch reference
     */
    Dispatcher createDispatcher(ServiceReference service, HandlerChain handlerChain);
    
    /**
     * Retrieves a dispatcher for the specified service.
     * @param service target service
     * @return dispatcher for the specified service, or null if no dispatcher
     * has been created
     */
    Dispatcher getDispatcher(ServiceReference service);
    
}
