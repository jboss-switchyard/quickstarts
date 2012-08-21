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
