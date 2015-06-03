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

package org.switchyard.transform;

import java.lang.reflect.ParameterizedType;

import javax.xml.namespace.QName;

import org.switchyard.metadata.JavaTypes;


/**
 * Base transformer implementation.
 *
 * @param <F> From Type
 * @param <T> To Type.
 */
public abstract class BaseTransformer<F, T> implements Transformer<F, T> {

    private enum Types { F, T };
    private QName _from;
    private QName _to;

    /**
     * Constructor.
     */
    public BaseTransformer() {
        _to = JavaTypes.toMessageType(getType(Types.T));
        _from = JavaTypes.toMessageType(getType(Types.F));
    }

    /**
     * Constructor.
     * @param from from
     * @param to to
     */
    public BaseTransformer(QName from, QName to) {
        _from = from;
        _to = to;
    }

    @Override
    public Transformer<F,T> setFrom(QName fromType) {
        _from = fromType;
        return this;
    }

    @Override
    public QName getFrom() {
        return _from;
    }

    @Override
    public Transformer<F,T> setTo(QName toType) {
        _to = toType;
        return this;
    }

    @Override
    public QName getTo() {
        return _to;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Class<F> getFromType() {
        return (Class<F>) getType(Types.F);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<T> getToType() {
        return (Class<T>) getType(Types.T);
    }

    @Override
    public abstract T transform(F from);

    /**
     * Get the type QName for the specified Java type.
     * <p/>
     *
     * @param type The Java type.
     * @return  The QName type.
     */
    protected static QName toMessageType(Class<?> type) {
        return JavaTypes.toMessageType(type);
    }

    private Class<?> getType(Types type) {
        try {
            ParameterizedType pt =
                (ParameterizedType) getClass().getGenericSuperclass();

            return (Class<?>) pt.getActualTypeArguments()[type.ordinal()];
        } catch (Exception e) {
            // Generics not specified...
            return Object.class;
        }
    }
}
