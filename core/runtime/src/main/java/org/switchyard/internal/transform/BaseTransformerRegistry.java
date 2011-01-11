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

package org.switchyard.internal.transform;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.switchyard.transform.Transformer;
import org.switchyard.transform.TransformerRegistry;

/**
 * Maintains a local collection of transformation instances and provides
 * facilities to add, query, and remove transforms.
 */
public class BaseTransformerRegistry implements TransformerRegistry {

    /**
     * Default hash code.
     */
    private static final int DEFAULT_HASHCODE = 32;

    @SuppressWarnings("unchecked")
    private final ConcurrentHashMap<NameKey, Transformer> _transformers =
        new ConcurrentHashMap<NameKey, Transformer>();


    /**
     * Constructor.
     */
    public BaseTransformerRegistry() {
    }

    /**
     * Create a new TransformRegistry instance and add the list of transformers
     * to the registry.
     * @param transformers set of transformers to add to registry
     */
    @SuppressWarnings("unchecked")
    public BaseTransformerRegistry(Set<Transformer> transformers) {
        for (Transformer t : transformers) {
            addTransformer(t);
        }
    }

    @Override
    public void addTransformer(Transformer<?, ?> transformer) {
        _transformers.put(new NameKey(transformer.getFrom(),
                transformer.getTo()), transformer);
    }

    @Override
    public Transformer<?, ?> getTransformer(String from, String to) {
        return _transformers.get(new NameKey(from, to));
    }

    @Override
    public boolean removeTransformer(Transformer<?, ?> transformer) {
        return _transformers.remove(
                new NameKey(transformer.getFrom(), transformer.getTo())) != null;
    }

    private class NameKey extends Key<String, String> {
        NameKey(String from, String to) {
            super(from, to);
        }
    }

    private class Key<F, T> {
        private final F _from;
        private final T _to;

        Key(F from, T to) {
            _from = from;
            _to = to;
        }

        public F getFrom() {
            return _from;
        }

        public T getTo() {
            return _to;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || !Key.class.isAssignableFrom(obj.getClass())) {
                return false;
            }
            return isEqual(_from, ((Key) obj).getFrom())
                && isEqual(_to, ((Key) obj).getTo());
        }

        @Override
        public int hashCode() {
            return (_from != null ? _from.hashCode() : (DEFAULT_HASHCODE - 1))
                + (_to != null ? _to.hashCode() : DEFAULT_HASHCODE);
        }

        @Override
        public String toString() {
            return _from + "::" + _to;
        }

        // handy method for comparing field refs for equality
        private boolean isEqual(Object a, Object b) {
            return a == null ? b == null : a.equals(b);
        }
    }
}
