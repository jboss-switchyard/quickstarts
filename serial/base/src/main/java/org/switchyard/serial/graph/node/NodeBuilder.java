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
package org.switchyard.serial.graph.node;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import javax.activation.DataSource;
import javax.xml.namespace.QName;

import org.switchyard.serial.graph.Graph;

/**
 * Builds nodes for a graph.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public final class NodeBuilder {

    private static final Class<?>[] SIMPLE_TYPES = new Class<?>[] {
        Boolean.class,
        Calendar.class,
        Character.class,
        CharSequence.class,
        Date.class,
        Number.class
    };

    /**
     * Builds a node representing the specified object and adds it to the graph.
     * @param obj the specified object
     * @param graph the graph
     * @return the node id
     */
    public static Integer build(Object obj, Graph graph) {
        Integer id = graph.id(obj);
        if (id == 0 || graph.getReference(id) != null) {
            return id;
        }
        Class<?> clazz = obj.getClass();
        if (isSimple(clazz)) {
            graph.putReference(id, obj);
        } else if (isArray(clazz)) {
            if (isSimple(clazz.getComponentType())) {
                graph.putReference(id, obj);
            } else {
                Node node = new ArrayNode();
                graph.putReference(id, node);
                node.compose(obj, graph);
            }
        } else if (isClass(clazz)) {
            Node node = new ClassNode();
            graph.putReference(id, node);
            node.compose(obj, graph);
        } else if (isCollection(clazz)) {
            Node node = new CollectionNode();
            graph.putReference(id, node);
            node.compose(obj, graph);
        } else if (isMap(clazz)) {
            Node node = new MapNode();
            graph.putReference(id, node);
            node.compose(obj, graph);
        } else if (isQName(clazz)) {
            Node node = new QNameNode();
            graph.putReference(id, node);
            node.compose(obj, graph);
        } else if (isDataSource(clazz)) {
            Node node = new DataSourceNode();
            graph.putReference(id, node);
            node.compose(obj, graph);
        } else if (isInputStream(clazz)) {
            Node node = new InputStreamNode();
            graph.putReference(id, node);
            node.compose(obj, graph);
        } else {
            Node node = new AccessNode();
            graph.putReference(id, node);
            node.compose(obj, graph);
        }
        return id;
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

    static boolean isSimple(Class<?> clazz) {
        if (clazz.isPrimitive() || clazz.isEnum()) {
            return true;
        }
        for (Class<?> st : SIMPLE_TYPES) {
            if (st.isAssignableFrom(clazz)) {
                return true;
            }
        }
        return false;
    }

    private NodeBuilder() {}

}
