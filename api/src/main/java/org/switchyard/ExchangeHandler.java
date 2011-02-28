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
