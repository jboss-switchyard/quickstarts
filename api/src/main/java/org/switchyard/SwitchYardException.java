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
 * SwitchYard base exception class.
 *
 * @author Tom Cunningham <a href="mailto:tcunning@redhat.com">&lt;tcunning@redhat.com&gt;</a> (C) 2011 Red Hat Inc.
 */
public class SwitchYardException extends RuntimeException {

    /**
     * Serial UID.
     */
    private static final long serialVersionUID = -5932547061290094335L;

    /**
     * Public constructor.
     * @param message Exception message.
     */
    public SwitchYardException(String message) {
        super(message);
    }

    /**
     * Public constructor.
     * @param cause Throwable cause.
     */
    public SwitchYardException(Throwable cause) {
        super(cause);
    }

    /**
     * Public constructor.
     * @param message Exception message.
     * @param cause Throwable cause.
     */
    public SwitchYardException(String message, Throwable cause) {
        super(message, cause);
    }
}
