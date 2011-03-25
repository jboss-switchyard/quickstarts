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

package org.switchyard.internal.transform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.switchyard.metadata.java.JavaService;
import org.switchyard.transform.Transformer;
import org.switchyard.transform.TransformerRegistry;

/**
 * Maintains a local collection of transformation instances and provides
 * facilities to add, query, and remove transforms.
 */
public class BaseTransformerRegistry implements TransformerRegistry {

    private static Logger _log = Logger.getLogger(BaseTransformerRegistry.class);

    /**
     * Default hash code.
     */
    private static final int DEFAULT_HASHCODE = 32;

    private final ConcurrentHashMap<NameKey, Transformer<?,?>> _transformers =
        new ConcurrentHashMap<NameKey, Transformer<?,?>>();
    private final ConcurrentHashMap<NameKey, Transformer<?,?>> _fallbackTransformers =
        new ConcurrentHashMap<NameKey, Transformer<?,?>>();


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
    public BaseTransformerRegistry(Set<Transformer<?,?>> transformers) {
        for (Transformer<?,?> t : transformers) {
            addTransformer(t);
        }
    }

    @Override
    public BaseTransformerRegistry addTransformer(Transformer<?, ?> transformer) {
        _fallbackTransformers.clear();
        _transformers.put(new NameKey(transformer.getFrom(),
                transformer.getTo()), transformer);
        return this;
    }

    @Override
    public TransformerRegistry addTransformer(Transformer<?, ?> transformer, QName from, QName to) {
        _fallbackTransformers.clear();
        _transformers.put(new NameKey(from, to), transformer);
        return null;
    }

    @Override
    public Transformer<?, ?> getTransformer(QName from, QName to) {
        NameKey nameKey = new NameKey(from, to);
        Transformer<?,?> transformer = _transformers.get(nameKey);

        if (transformer == null) {
            transformer = _fallbackTransformers.get(nameKey);
            if (transformer == null && JavaService.isJavaMessageType(from)) {
                transformer = getJavaFallbackTransformer(from, to);
            }
        }

        return transformer;
    }

    @Override
    public boolean hasTransformer(QName from, QName to) {
        NameKey nameKey = new NameKey(from, to);
        return _transformers.containsKey(nameKey);
    }

    private Transformer<?, ?> getJavaFallbackTransformer(QName from, QName to) {
        Class<?> javaType = JavaService.toJavaMessageType(from);

        if (javaType == null) {
            return null;
        }

        Set<Map.Entry<NameKey,Transformer<?,?>>> entrySet = _transformers.entrySet();
        List<JavaSourceFallbackTransformer> fallbackTransforms = new ArrayList<JavaSourceFallbackTransformer>();
        for (Map.Entry<NameKey,Transformer<?,?>> entry : entrySet) {
            NameKey nameKey = entry.getKey();

            // "to" must be an exact match...
            if (nameKey.getTo().equals(to)) {
                if (JavaService.isJavaMessageType(nameKey.getFrom())) {
                    Class<?> candidateType = JavaService.toJavaMessageType(nameKey.getFrom());
                    if (candidateType != null && candidateType.isAssignableFrom(javaType)) {
                        fallbackTransforms.add(new JavaSourceFallbackTransformer(candidateType, entry.getValue()));
                    }
                }
            }
        }

        if (fallbackTransforms.size() == 0) {
            // No fallback...
            return null;
        }
        if (fallbackTransforms.size() == 1) {
            Transformer<?, ?> fallbackTransformer = fallbackTransforms.get(0)._transformer;
            addFallbackTransformer(fallbackTransformer, from, to);
            return fallbackTransformer;
        }

        try {
            JavaSourceFallbackTransformerComparator comparator = new JavaSourceFallbackTransformerComparator();
            Collections.sort(fallbackTransforms, comparator);

            // Closest super-type will be first in the list..
            Transformer<?, ?> fallbackTransformer = fallbackTransforms.get(0)._transformer;
            addFallbackTransformer(fallbackTransformer, from, to);
            return fallbackTransformer;
        } catch (MultipleFallbacks e) {
            _log.debug("Cannot resolve fallback Transformer from '" + from + "' to '" + to + "'.  " + e.getMessage());
            return null;
        }
    }

    private void addFallbackTransformer(Transformer<?, ?> fallbackTransformer, QName from, QName to) {
        // Fallback transformers are cached so as to save on lookup times...
        _fallbackTransformers.put(new NameKey(from, to), fallbackTransformer);
    }

    @Override
    public boolean removeTransformer(Transformer<?, ?> transformer) {
        _fallbackTransformers.clear();
        return _transformers.remove(
                new NameKey(transformer.getFrom(), transformer.getTo())) != null;
    }

    private class NameKey extends Key<QName, QName> {
        NameKey(QName from, QName to) {
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
            return isEqual(_from, ((Key<?,?>) obj).getFrom())
                && isEqual(_to, ((Key<?,?>) obj).getTo());
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

    /**
     * Holder type for Fallback Transformer.
     */
    public static class JavaSourceFallbackTransformer {

        private Class<?> _javaType;
        private Transformer<?, ?> _transformer;

        /**
         * Constructor.
         * @param javaType Java type.
         * @param transformer Transformer instance.
         */
        public JavaSourceFallbackTransformer(Class<?> javaType, Transformer<?, ?> transformer) {
            this._javaType = javaType;
            this._transformer = transformer;
        }

        /**
         * Get the Java type.
         * @return The Java type.
         */
        public Class<?> getJavaType() {
            return _javaType;
        }
    }

    /**
     * Fallback Transformer Sorter.
     * @param <T> JavaSourceFallbackTransformer.
     */
    public static class JavaSourceFallbackTransformerComparator<T extends JavaSourceFallbackTransformer> implements Comparator<T> {
        @Override
        public int compare(T t1, T t2) {
            if (t1._javaType == t2._javaType) {
                return 0;
            } else if (t1._javaType.isAssignableFrom(t2._javaType)) {
                return 1;
            } else if (t2._javaType.isAssignableFrom(t1._javaType)) {
                return -1;
            } else {
                // Unrelated types.  This means there are branches in the inheritance options,
                // which means there are multiple possibilities, therefore it's not uniquely resolvable.
                throw new MultipleFallbacks("Multiple possible fallback types '" + JavaService.toMessageType(t1._javaType) + "' and '" + JavaService.toMessageType(t2._javaType) + "'.");
            }
        }
    }

    private static final class MultipleFallbacks extends RuntimeException {
        private MultipleFallbacks(String s) {
            super(s);
        }
    }
}
