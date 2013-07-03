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

import org.switchyard.annotations.DefaultType;

/**
 * Represents a runtime processing error when handling an exchange.
 */
@DefaultType(HandlerException.MESSAGE_TYPE)
public class HandlerException extends Exception {

    /**
     * Base fault Message type.
     */
    public static final String MESSAGE_TYPE = "java:org.switchyard.HandlerException";

    /**
     * Generated serial version UID.
     */
    private static final long serialVersionUID = -4026111208898030754L;
    
    private boolean _wrapper;

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
        _wrapper = true;
    }

    /**
     * Create a new HandlerException with the specified error message and cause.
     * @param message error text
     * @param cause error causing the handler to fail processing
     */
    public HandlerException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Indicates whether this HandlerException is simply a wrapper for another 
     * exception type - i.e. it contains no independent error message or stack
     * trace detail.
     * @return true if this is a wrapper exception, false otherwise
     */
    public boolean isWrapper() {
        return _wrapper;
    }
}
