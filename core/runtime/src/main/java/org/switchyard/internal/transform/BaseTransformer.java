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

import java.lang.reflect.ParameterizedType;
import org.switchyard.transform.Transformer;

public abstract class BaseTransformer<F,T> implements Transformer<F,T> {

    private enum Types {F, T};
    private String _from;
    private String _to;
    
    public BaseTransformer() {
        
    }
    
    public BaseTransformer(String from, String to) {
        _from = from;
        _to = to;
    }
    
    @Override
    public String getFrom() {
        return _from;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<F> getFromType() {
        return (Class<F>)getType(Types.F);
    }

    @Override
    public String getTo() {
        return _to;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<T> getToType() {
        return (Class<T>)getType(Types.T);
    }

    @Override
    public abstract T transform(F from);
    
    private Class<?> getType(Types type) {
        ParameterizedType pt = 
            (ParameterizedType)getClass().getGenericSuperclass();
        
        return (Class<?>)pt.getActualTypeArguments()[type.ordinal()];
    }
}
