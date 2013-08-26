/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
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

package org.switchyard.internal.transform;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EventObject;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.namespace.QName;

import org.jboss.logging.Logger;
import org.switchyard.common.xml.QNameUtil;
import org.switchyard.event.EventPublisher;
import org.switchyard.event.TransformerAddedEvent;
import org.switchyard.event.TransformerRemovedEvent;
import org.switchyard.transform.TransformResolver;
import org.switchyard.transform.TransformSequence;
import org.switchyard.runtime.RuntimeMessages;
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

    private EventPublisher _eventPublisher;
    private TransformResolver _transformResolver = new BaseTransformResolver(this);

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
        publishEvent(new TransformerAddedEvent(transformer));
        return this;
    }

    @Override
    public TransformerRegistry addTransformer(Transformer<?, ?> transformer, QName from, QName to) {
        _fallbackTransformers.clear();
        _transformers.put(new NameKey(from, to), transformer);
        publishEvent(new TransformerAddedEvent(transformer));
        return null;
    }
    
    /**
     * Sets the EventPublisher instance to be used for transformer events.
     * @param eventPublisher event publisher
     */
    public void setEventPublisher(EventPublisher eventPublisher) {
        _eventPublisher = eventPublisher;
    }

    @Override
    public Transformer<?, ?> getTransformer(QName from, QName to) {
        NameKey nameKey = new NameKey(from, to);
        Transformer<?,?> transformer = _transformers.get(nameKey);

        if (transformer == null) {
            transformer = _fallbackTransformers.get(nameKey);
            if (transformer == null && QNameUtil.isJavaMessageType(from)) {
                transformer = getJavaFallbackTransformer(from, to);
                if (transformer != null && _log.isDebugEnabled()) {
                    _log.debug("Selecting fallback transformer: from '" + transformer.getFrom() + "' to '" + transformer.getTo() + "'. Type: " + transformer.getClass().getName());
                } else if (_log.isDebugEnabled()) {
                    _log.debug("No compatible transformer registered: from '" + from + "' to '" + to + "'");
                }
            }
        } else if (_log.isDebugEnabled()) {
            _log.debug("Selecting transformer: from '" + transformer.getFrom() + "' to '" + transformer.getTo() + "'. Type: " + transformer.getClass().getName());
        }

        return transformer;
    }

    @Override
    public TransformSequence getTransformSequence(QName from, QName to) {        
        return _transformResolver.resolveSequence(from, to);
    }

    @Override
    public boolean hasTransformer(QName from, QName to) {
        NameKey nameKey = new NameKey(from, to);
        return _transformers.containsKey(nameKey);
    }

    private Transformer<?, ?> getJavaFallbackTransformer(QName from, QName to) {
        Class<?> javaType = QNameUtil.toJavaMessageType(from);

        if (javaType == null) {
            return null;
        }

        Set<Map.Entry<NameKey,Transformer<?,?>>> entrySet = _transformers.entrySet();
        List<JavaSourceFallbackTransformer> fallbackTransforms = new ArrayList<JavaSourceFallbackTransformer>();
        for (Map.Entry<NameKey,Transformer<?,?>> entry : entrySet) {
            NameKey nameKey = entry.getKey();

            // "to" must be an exact match...
            if (nameKey.getTo().equals(to)) {
                if (QNameUtil.isJavaMessageType(nameKey.getFrom())) {
                    Class<?> candidateType = QNameUtil.toJavaMessageType(nameKey.getFrom());
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
            Transformer<?, ?> fallbackTransformer = fallbackTransforms.get(0).getTransformer();
            addFallbackTransformer(fallbackTransformer, from, to);
            return fallbackTransformer;
        }

        JavaSourceFallbackTransformerComparator comparator = new JavaSourceFallbackTransformerComparator();
        Collections.sort(fallbackTransforms, comparator);

        if (_log.isDebugEnabled()) {
            StringBuilder messageBuilder = new StringBuilder();

            
            messageBuilder.append(RuntimeMessages.MESSAGES.multipleFallbackRegistry());
            for (JavaSourceFallbackTransformer t : fallbackTransforms) {
                messageBuilder.append("\n\t- from '" + t.getTransformer().getFrom() + "' to '" + t.getTransformer().getTo() + "'");
            }
            _log.debug(messageBuilder.toString());
        }

        // Closest super-type will be first in the list..
        Transformer<?, ?> fallbackTransformer = fallbackTransforms.get(0).getTransformer();
        addFallbackTransformer(fallbackTransformer, from, to);
        return fallbackTransformer;
    }

    private void addFallbackTransformer(Transformer<?, ?> fallbackTransformer, QName from, QName to) {
        // Fallback transformers are cached so as to save on lookup times...
        _fallbackTransformers.put(new NameKey(from, to), fallbackTransformer);
    }

    @Override
    public boolean removeTransformer(Transformer<?, ?> transformer) {
        _fallbackTransformers.clear();
        boolean removed = _transformers.remove(
                new NameKey(transformer.getFrom(), transformer.getTo())) != null;
        if (removed) {
            publishEvent(new TransformerRemovedEvent(transformer));
        }
        
        return removed;
    }
    
    @Override
    public List<Transformer<?, ?>> getRegisteredTransformers() {
        return new ArrayList<Transformer<?, ?>>(_transformers.values());
    }
    
    @Override
    public List<Transformer<?,?>> getTransformersFrom(QName type) {
        List<Transformer<?,?>> transforms = new LinkedList<Transformer<?,?>>();
        
        for (NameKey key : _transformers.keySet()) {
            if (key.getFrom() != null && key.getFrom().equals(type)) {
                transforms.add(_transformers.get(key));
            }
        }
        
        return transforms;
    }

    @Override
    public List<Transformer<?,?>> getTransformersTo(QName type) {
        List<Transformer<?,?>> transforms = new LinkedList<Transformer<?,?>>();
        
        for (NameKey key : _transformers.keySet()) {
            if (key.getTo() != null && key.getTo().equals(type)) {
                transforms.add(_transformers.get(key));
            }
        }
        
        return transforms;
    }

    @Override
    public void setTransfomResolver(TransformResolver resolver) {
        this._transformResolver = resolver;        
    }

    // Convenience method to guard against cases when an event publisher has 
    // not been set.
    private void publishEvent(EventObject event) {
        if (_eventPublisher != null) {
            _eventPublisher.publish(event);
        }
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

        /**
         * Get the Transformer.
         * @return The Transformer.
         */
        public Transformer<?, ?> getTransformer() {
            return _transformer;
        }
    }

    /**
     * Fallback Transformer Sorter.
     * @param <T> JavaSourceFallbackTransformer.
     */
    @SuppressWarnings("serial")
    public static class JavaSourceFallbackTransformerComparator<T extends JavaSourceFallbackTransformer> implements Comparator<T>, Serializable {
        @Override
        public int compare(T t1, T t2) {
            if (t1.getJavaType() == t2.getJavaType()) {
                return 0;
            } else if (t1.getJavaType().isAssignableFrom(t2.getJavaType())) {
                return 1;
            } else if (t2.getJavaType().isAssignableFrom(t1.getJavaType())) {
                return -1;
            } else {
                // Unrelated types.  This means there are branches in the inheritance options,
                // which means there are multiple possibilities, therefore it's not uniquely resolvable.
                // Marking as equal for now...
                return 0;
            }
        }
    }
}
