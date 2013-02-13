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
package org.switchyard.common.type.reflect;

/**
 * An abstraction of field and method access.
 *
 * @param <T> the value type of this access
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public interface Access<T> {

    /**
     * The name of the wrapped access mechanism.
     * @return the name
     */
    public String getName();

    /**
     * The Class type of the wrapped access mechanism.
     * @return the Class type
     */
    public Class<T> getType();

    /**
     * Whether the wrapped access mechanism is readable.
     * @return true if it's readable
     */
    public boolean isReadable();

    /**
     * Whether the wrapped access mechanism is writable.
     * @return true if it's writable
     */
    public boolean isWriteable();

    /**
     * Reads via the wrapped access mechanism targeting the specified object.
     * @param target the target object to read from
     * @return the read value
     */
    public T read(Object target);

    /**
     * Writes via the wrapped access mechanism targeting the specified object.
     * @param target the target object to write to
     * @param value to value to write
     */
    public void write(Object target, T value);

}
