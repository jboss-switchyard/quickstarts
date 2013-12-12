/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
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
            case 0:
                return _greeting.getReceiver();
            case 1:
                return _greeting.getSender();
            case 2:
                return _greeting.getId();
        }
        return null;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
