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
package org.switchyard.internal.io.graph;

import java.io.IOException;
import java.util.Map;

/**
 * A Graph representing a raw object, internalized as itself.
 * This would include primitives, enums, wrappers and basic types.
 *
 * @param <T> the type of raw object 
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
@SuppressWarnings("serial")
public class RawGraph<T> implements Graph<T> {

    private T _raw;

    /**
     * Gets the raw object.
     * @return the raw object
     */
    public T getRaw() {
        return _raw;
    }

    /**
     * Sets the raw object.
     * @param raw the raw object
     */
    public void setRaw(T raw) {
        _raw = raw;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void compose(T object, Map<Integer,Object> visited) throws IOException {
        setRaw(extract(object, visited));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T decompose(Map<Integer,Object> visited) throws IOException {
        return extract(getRaw(), visited);
    }

    // TODO: Find out why this method is necessary; it shouldn't be!
    @SuppressWarnings("unchecked")
    private T extract(T object, Map<Integer,Object> visited) throws IOException {
        while (object instanceof Graph) {
            object = ((Graph<T>)object).decompose(visited);
        }
        return object;
    }

    @Override
    public String toString() {
        return "RawGraph(raw=" + getRaw() + ")";
    }

}
