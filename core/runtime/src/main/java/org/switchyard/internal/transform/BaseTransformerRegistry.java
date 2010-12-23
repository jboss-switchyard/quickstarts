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
    
    @SuppressWarnings("unchecked")
    private ConcurrentHashMap<NameKey, Transformer> transformers = 
        new ConcurrentHashMap<NameKey, Transformer>();


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
        transformers.put(new NameKey(transformer.getFrom(), 
                transformer.getTo()), transformer);
    }

    @Override
    public Transformer<?, ?> getTransformer(String from, String to) {
        return transformers.get(new NameKey(from, to));
    }

    @Override
    public boolean removeTransformer(Transformer<?, ?> transformer) {
        return transformers.remove(
                new NameKey(transformer.getFrom(), transformer.getTo())) != null;
    }
    
    private class NameKey extends Key<String, String> {
        NameKey(String from, String to) {
            super(from, to);
        }
    }
    
    private class Key<F,T> {
        F from;
        T to;
        
        Key(F from, T to) {
            this.from = from;
            this.to = to;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || !Key.class.isAssignableFrom(obj.getClass())) {
                return false;
            }
            return isEqual(from, ((Key)obj).from) && isEqual(to, ((Key)obj).to);
        }
        
        @Override
        public int hashCode() {
            return (from != null ? from.hashCode() : 31) + 
                (to != null ? to.hashCode() : 32);
        }
        
        @Override
        public String toString() {
            return from + "::" + to;
        }
        
        // handy method for comparing field refs for equality
        private boolean isEqual(Object a, Object b) {
            return a == null ? b == null : a.equals(b);
        }
    }

}
