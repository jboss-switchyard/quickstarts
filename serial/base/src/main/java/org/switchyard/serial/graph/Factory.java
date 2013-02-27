/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.serial.graph;

import org.switchyard.common.type.reflect.Construction;

/**
 * The factory the AccessNode will use.
 * 
 * @param <T> the factory type
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public abstract class Factory<T> {

    /**
     * Creates a new object of the specified type.
     * @param type the type
     * @return the object
     */
    public abstract T create(Class<T> type);

    /**
     * Whether this factory supports the specified type.
     * @param type the type
     * @return if the type is supported
     */
    public abstract boolean supports(Class<?> type);

    /**
     * Gets the factory for the specified type.
     * @param <T> the factory type
     * @param type the type
     * @return the factory
     */
    @SuppressWarnings("unchecked")
    public static final <T> Factory<T> getFactory(Class<T> type) {
        Strategy strategy = type.getAnnotation(Strategy.class);
        if (strategy != null) {
            return Construction.construct(strategy.factory());
        }
        return new DefaultFactory<T>();
    }

}
