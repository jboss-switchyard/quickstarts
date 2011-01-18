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
