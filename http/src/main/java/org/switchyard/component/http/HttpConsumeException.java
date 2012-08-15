/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
 
package org.switchyard.component.http;

import org.switchyard.exception.SwitchYardException;

/**
 * Wrapper for HTTP consumer exceptions.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class HttpConsumeException extends SwitchYardException {
    private static final long serialVersionUID = 1L;

    /**
     * Public constructor.
     * @param message Exception message.
     */
    public HttpConsumeException(final String message) {
        super(message);
    }

    /**
     * Public constructor.
     * @param message Exception message.
     * @param cause Exception cause.
     */
    public HttpConsumeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Public constructor.
     * @param cause Exception cause.
     */
    public HttpConsumeException(final Throwable cause) {
        super(cause);
    }
}
