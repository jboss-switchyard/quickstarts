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
package org.switchyard.serial.graph;

import org.switchyard.common.type.reflect.Construction;
import org.switchyard.serial.graph.node.Node;

/**
 * The factory the AccessNode will use.
 * 
 * @param <T> the factory type
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public abstract class Factory<T> {

    /**
     * Whether this factory supports the specified type.
     * @param type the type
     * @return if the type is supported
     */
    public abstract boolean supports(Class<?> type);

    /**
     * Creates a new object of the specified type.
     * @param type the type
     * @param node the graph node for implementations that need extra information
     * @return the object
     */
    public abstract T create(Class<T> type, Node node);

    /**
     * Gets the factory for the specified type.
     * @param <T> the factory type
     * @param type the type
     * @return the factory
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static final <T> Factory<T> getFactory(Class<T> type) {
        Strategy strategy = type.getAnnotation(Strategy.class);
        if (strategy != null) {
            Class<? extends Factory> factoryClass = strategy.factory();
            if (!UndefinedFactory.class.equals(factoryClass)) {
                return Construction.construct(factoryClass);
            }
        }
        if (Throwable.class.isAssignableFrom(type)) {
            return new ThrowableFactory();
        }
        return new DefaultFactory<T>();
    }

    /**
     * Used as the default value for the {@link Strategy#factory()} annotation attribute.
     * @param <T> the factory type
     */
    static final class UndefinedFactory<T> extends Factory<T> {
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean supports(Class<?> type) {
            return false;
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public T create(Class<T> type, Node node) {
            return null;
        }
    }

}
