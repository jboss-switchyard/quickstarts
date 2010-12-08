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

/**
 * Set of scopes available for {@code Context} instances.  Scope is used to
 * specify the lifecycle of a given property and assign it to the correct
 * context.
 */
public enum Scope {
    /**
     * Properties that apply across a service domain.
     */
    DOMAIN,
    /**
     * Properties which can be propagated across service invocations
     * (i.e. exchanges).
     */
    CORRELATION,
    /**
     * Properties specific to a given service invocation.  These should not be
     * propagated automatically between exchanges.
     */
    EXCHANGE,
    /**
     * Properties specific to a message within an exchange.  Individual messages
     * within an exchange may have the same context property with different
     * values.
     */
    MESSAGE;
}
