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
package org.switchyard.internal.io.graph;

import java.io.IOException;
import java.util.Map;

import org.switchyard.common.type.Classes;

/**
 * A Graph representing a Class, internalized as the class' name.
 *
 * @param <T> the type of Class
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
@SuppressWarnings("serial")
public class ClassGraph<T> implements Graph<Class<T>> {

    private String _name;

    /**
     * Gets the wrapped Class name.
     * @return the wrapped Class name
     */
    public String getName() {
        return _name;
    }

    /**
     * Sets the wrapped Class name.
     * @param name the Class name to wrap
     */
    public void setName(String name) {
        _name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void compose(Class<T> object, Map<Integer,Object> visited) throws IOException {
        setName(object.getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Class<T> decompose(Map<Integer,Object> visited) throws IOException {
        return (Class<T>)Classes.forName(getName(), getClass());
    }

    @Override
    public String toString() {
        return "ClassGraph(name=" + getName() + ")";
    }

}
