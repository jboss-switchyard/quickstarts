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
 * The role of a Handler is to handle messages and fault events during the
 * course of a service invocation.  When consuming a service, a handler can be
 * registered during Exchange creation to handle consumer events (e.g. fault
 * or reply message).  When providing a service, a handler is specified while
 * registering the service to process invocations of that service.
 */
public interface ExchangeHandler {
    /**
     * Called when a message is sent through an exchange.
     * @param exchange an {@code Exchange} instance containing a message to be
     * processed
     * @throws HandlerException when handling of the message event fails (e.g.
     * invalid request message).
     */
    void handleMessage(Exchange exchange) throws HandlerException;
    /**
     * Called when a fault is generated while processing an exchange.
     * @param exchange an {@code Exchange} instance containing a message to be
     * processed
     */
    void handleFault(Exchange exchange);
}
