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

package org.switchyard.component.bean;

import org.switchyard.Exchange;
import org.switchyard.HandlerException;

/**
 * Bean Component Exception.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class BeanComponentException extends HandlerException {

    /**
     * Fault exchange instance associated with the exception.
     */
    private Exchange _faultExchange;

    /**
     * Public constructor.
     * @param message Exception message.
     */
    public BeanComponentException(final String message) {
        super(message);
    }
    
    /**
     * Create a new BeanComponentException with the specified cause.
     * @param cause underlying error
     */
    public BeanComponentException(final Throwable cause) {
        super(cause);
    }

    /**
     * Public constructor.
     * @param message Exception message.
     * @param cause The parent/cause exception.
     */
    public BeanComponentException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Set the fault {@link Exchange} instance on the exception.
     * @param faultExchange The fault exchange instance.
     * @return {@code this} exception instance.
     */
    public BeanComponentException setFaultExchange(Exchange faultExchange) {
        this._faultExchange = faultExchange;
        return this;
    }

    /**
     * Get the fault {@link Exchange} instance associated with this exception instance.
     * @return The fault exchange instance.
     */
    public Exchange getFaultExchange() {
        return _faultExchange;
    }
}
