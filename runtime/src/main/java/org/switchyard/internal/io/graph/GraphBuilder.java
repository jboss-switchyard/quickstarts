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
import java.io.InputStream;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.activation.DataSource;
import javax.xml.namespace.QName;

/**
 * Builds object Graphs.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public final class GraphBuilder {

    private static final Class<?>[] RAW_TYPES = new Class<?>[] {
        Boolean.class,
        Calendar.class,
        Character.class,
        CharSequence.class,
        Date.class,
        Number.class
    };

    private GraphBuilder() {}

    /**
     * Builds an object Graph.
     * @param <T> the type of object used to build the graph
     * @param object the object used to build the graph
     * @return the object Graph
     * @throws IOException if a problem building the graph occurs
     */
    public static <T> Graph<T> build(T object) throws IOException {
        return build(object, null);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    static <T> Graph<T> build(T object, Map<Integer,Object> visited) throws IOException {
        if (object == null) {
            return null;
        }
        Integer id = Integer.valueOf(System.identityHashCode(object));
        if (visited == null) {
            visited = new LinkedHashMap<Integer,Object>();
        }
        Graph<T> graph = (Graph<T>)visited.get(id);
        if (graph != null) {
            return graph;
        }
        boolean wrap;
        Class<?> clazz = object.getClass();
        if (isRaw(clazz)) {
            graph = new RawGraph<T>();
            graph.compose(object, visited);
            wrap = false;
        } else if (isArray(clazz)) {
            if (isRaw(clazz.getComponentType())) {
                graph = new RawGraph<T>();
                graph.compose(object, visited);
                wrap = false;
            } else {
                graph = (Graph<T>)new ArrayGraph();
                graph.compose(object, visited);
                wrap = true;
            }
        } else if (isClass(clazz)) {
            graph = (Graph<T>)new ClassGraph<T>();
            graph.compose(object, visited);
            wrap = false;
        } else if (isCollection(clazz)) {
            graph = (Graph<T>)new CollectionGraph();
            graph.compose(object, visited);
            wrap = true;
        } else if (isMap(clazz)) {
            graph = (Graph<T>)new MapGraph();
            graph.compose(object, visited);
            wrap = true;
        } else if (isQName(clazz)) {
            graph = (Graph<T>)new QNameGraph();
            graph.compose(object, visited);
            wrap = false;
        } else if (isDataSource(clazz)) {
            graph = (Graph<T>)new DataSourceGraph();
            graph.compose(object, visited);
            wrap = false;
        } else if (isInputStream(clazz)) {
            graph = (Graph<T>)new InputStreamGraph();
            graph.compose(object, visited);
            wrap = false;
        } else {
            graph = new PropertyGraph<T>();
            graph.compose(object, visited);
            wrap = true;
        }
        return wrap ? GraphWrapper.wrap(graph) : graph;
    }

    static boolean isArray(Class<?> clazz) {
        return clazz.isArray();
    }

    static boolean isClass(Class<?> clazz) {
        return Class.class.isAssignableFrom(clazz);
    }

    static boolean isCollection(Class<?> clazz) {
        return Collection.class.isAssignableFrom(clazz);
    }

    static boolean isDataSource(Class<?> clazz) {
        return DataSource.class.isAssignableFrom(clazz);
    }

    static boolean isInputStream(Class<?> clazz) {
        return InputStream.class.isAssignableFrom(clazz);
    }

    static boolean isMap(Class<?> clazz) {
        return Map.class.isAssignableFrom(clazz);
    }

    static boolean isQName(Class<?> clazz) {
        return QName.class.isAssignableFrom(clazz);
    }

    static boolean isRaw(Class<?> clazz) {
        if (clazz.isPrimitive() || clazz.isEnum()) {
            return true;
        }
        for (Class<?> rt : RAW_TYPES) {
            if (rt.isAssignableFrom(clazz)) {
                return true;
            }
        }
        return false;
    }

}
