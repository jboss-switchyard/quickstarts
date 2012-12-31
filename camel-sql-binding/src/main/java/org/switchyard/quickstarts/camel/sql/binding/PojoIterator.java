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
package org.switchyard.quickstarts.camel.sql.binding;

import java.util.Iterator;

/**
 * Iterator which converts {@link Greeting} into iterator.
 */
public class PojoIterator implements Iterator<Object> {

    private final Greeting _greeting;
    private int _pointer;

    public PojoIterator(Greeting greeting) {
        this._greeting = greeting;
    }

    @Override
    public boolean hasNext() {
        return _pointer < 3;
    }

    @Override
    public Object next() {
        switch (_pointer++) {
        case 0: return _greeting.getReceiver();
        case 1: return _greeting.getSender();
        case 2: return _greeting.getId();
        }
        return null;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
