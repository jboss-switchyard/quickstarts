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
