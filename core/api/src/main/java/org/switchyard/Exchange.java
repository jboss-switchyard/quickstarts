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

import javax.xml.namespace.QName;

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
     * Creates a {@code Context} instance that can be used to specify
     * message context properties when sending a message.
     * @return a new, empty Context instance
     */
    Context createContext();
    /**
     * Retrieves the exchange context.  Equivalent to
     * {@code getContext(Scope.EXCHANGE)}.
     * @return the exchange context
     */
    Context getContext();
    /**
     * Retrieves context for the specified scope.
     * @param scope the context scope
     * @return {@code Context} instance for the specified scope.
     */
    Context getContext(Scope scope);

    /**
     * The unique identifier for this Exchange instance.  The exchange
     * id can be used to correlate message and fault activity within a message
     * exchange.  This value is assigned at Exchange creation time and never
     * changes over the lifetime of the exchange.
     * @return exchange id as a string.
     */
    String getId();
    /**
     * The exchange pattern used for this exchange instance.  The exchange
     * pattern defines the number, order, and cardinality of messages exchanged
     * as part of a service invocation.
     * @return the exchange pattern for this exchange
     */
    ExchangePattern getPattern();
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
     * Sends the specified message as part of this message exchange.
     * <p/>
     * Implementations must throw an {@link IllegalStateException} if this method is
     * called when the Exchange {@link #getState() state} is in {@link ExchangeState#FAULT}.
     * @param message message to send
     */
    void send(Message message);

    /**
     * Sends the specified message with additional context information.  The
     * supplied {@code Context} is mapped to the exchange with
     * {@code Scope.MESSAGE}.
     * <p/>
     * Implementations must throw an {@link IllegalStateException} if this method is
     * called when the Exchange {@link #getState() state} is in {@link ExchangeState#FAULT}.
     * @param message the message to send
     * @param messageContext context for the sent message
     */
    void send(Message message, Context messageContext);

    /**
     * Sends the specified message as part of this message exchange.
     * <p/>
     * Also sets the Exchange {@link #getState() state} to {@link ExchangeState#FAULT}.
     * @param message message to send
     */
    void sendFault(Message message);
    /**
     * Sends the specified message with additional context information.  The
     * supplied {@code Context} is mapped to the exchange with
     * {@code Scope.MESSAGE}.
     * <p/>
     * Also sets the Exchange {@link #getState() state} to {@link ExchangeState#FAULT}.
     * @param message the message to send
     * @param messageContext context for the sent message
     */
    void sendFault(Message message, Context messageContext);

    /**
     * Get the exchange state.
     * @return The exchange State.
     */
    ExchangeState getState();
}
