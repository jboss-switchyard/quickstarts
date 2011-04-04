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

package org.switchyard.transform;

import java.lang.reflect.ParameterizedType;

import javax.xml.namespace.QName;

import org.switchyard.metadata.java.JavaService;


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
        _to = JavaService.toMessageType(getType(Types.T));
        _from = JavaService.toMessageType(getType(Types.F));
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
    public Transformer setFrom(QName fromType) {
        _from = fromType;
        return this;
    }

    @Override
    public QName getFrom() {
        return _from;
    }

    @Override
    public Transformer setTo(QName toType) {
        _to = toType;
        return this;
    }

    @Override
    public QName getTo() {
        return _to;
    }

    @Override
    public abstract T transform(F from);

    /**
     * Get the type QName for the specified Java type.
     * <p/>
     * Utility method for {@link JavaService#toMessageType(Class)}.
     *
     * @param type The Java type.
     * @return  The QName type.
     */
    protected static QName toMessageType(Class<?> type) {
        return JavaService.toMessageType(type);
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
