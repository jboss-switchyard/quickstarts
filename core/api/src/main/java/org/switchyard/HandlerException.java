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

import org.switchyard.message.PayloadTypeName;

/**
 * Represents a processing error when handling an exchange.  A
 * {@code HandlerException} stops processing within a HandlerChain and
 * is automagically converted into a fault by the chain when throw by an
 * {@code ExchangeHandler}.
 */
@PayloadTypeName(HandlerException.MESSAGE_TYPE)
public class HandlerException extends Exception {

    /**
     * Base fault Message type.
     */
    public static final String MESSAGE_TYPE = "java:/org.switchyard.HandlerException";

    /**
     * Generated serial version UID.
     */
    private static final long serialVersionUID = -4026111208898030754L;

    /**
     * Create a new HandlerException with the specified error message.
     * @param message error text
     */
    public HandlerException(final String message) {
        super(message);
    }

    /**
     * Create a new HandlerException with the specified cause.
     * @param cause error causing the handler to fail processing
     */
    public HandlerException(final Throwable cause) {
        super(cause);
    }

    /**
     * Create a new HandlerException with the specified error message and cause.
     * @param message error text
     * @param cause error causing the handler to fail processing
     */
    public HandlerException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
