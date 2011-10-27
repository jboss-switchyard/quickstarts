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

package org.switchyard.validate;

import java.lang.reflect.ParameterizedType;

import javax.xml.namespace.QName;

import org.switchyard.metadata.java.JavaService;


/**
 * Base validator implementation.
 *
 * @param <T> Type
 */
public abstract class BaseValidator<T> implements Validator<T> {

    private QName _name;

    /**
     * Constructor.
     */
    public BaseValidator() {
        _name = JavaService.toMessageType(getType());
    }

    /**
     * Constructor.
     * @param name name
     */
    public BaseValidator(QName name) {
        _name = name;
    }

    @Override
    public Validator<T> setName(QName name) {
        _name = name;
        return this;
    }

    @Override
    public QName getName() {
        return _name;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<T> getType() {
        try {
            ParameterizedType pt =
                (ParameterizedType) getClass().getGenericSuperclass();

            return (Class<T>) pt.getActualTypeArguments()[0];
        } catch (Exception e) {
            // Generics not specified...
            return (Class<T>) Object.class;
        }
    }

    @Override
    public abstract boolean validate(T subject);

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

}
