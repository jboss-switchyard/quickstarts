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


import org.switchyard.metadata.ExchangeContract;

/**
 * An Exchange represents an instance of a service invocation with a specific
 * message exchange pattern (e.g. InOnly, InOut).  Exchange provides a conduit
 * for the messages that flow into and out of a service  as part of a service
 * invocation.  Unlike messages, an exchange cannot be copied and reused across
 * service invocations.  State associated with  an invocation (i.e. context) is
 * maintained at the exchange level.
 */
public interface Exchange {

    /**
     * Retrieves the exchange context.
     * @return the exchange context
     */
    Context getContext();

    /**
     * The unique identifier for this Exchange instance.  The exchange
     * id can be used to correlate message and fault activity within a message
     * exchange.  This value is assigned at Exchange creation time and never
     * changes over the lifetime of the exchange.
     * @return exchange id as a string.
     */
    String getId();
    /**
     * Get the contract associated with this exchange.
     * @return The exchange contract.
     */
    ExchangeContract getContract();
    /**
     * The service being invoked by this exchange.
     * @return Service to be invoked
     */
    Service getService();

    /**
     * Returns the current message for the exchange.  On new exchanges, this
     * method will always return null.  The current message reference is updated
     * with each call to send().
     * @return the current message for the exchange
     */
    Message getMessage();
    
    /**
     * Create a message to be used with this exchange.
     * @return new message
     */
    Message createMessage();

    /**
     * Sends the specified message as part of this message exchange.
     * <p/>
     * Implementations must throw an {@link IllegalStateException} if this method is
     * called when the Exchange {@link #getState() state} is in {@link ExchangeState#FAULT}.
     * @param message message to send
     */
    void send(Message message);

    /**
     * Sends the specified message as part of this message exchange.
     * <p/>
     * Also sets the Exchange {@link #getState() state} to {@link ExchangeState#FAULT}.
     * @param message message to send
     */
    void sendFault(Message message);

    /**
     * Get the exchange state.
     * @return The exchange State.
     */
    ExchangeState getState();
    
    /**
     * Get the exchange phase.
     * @return the exchange phase
     */
    ExchangePhase getPhase();
}
