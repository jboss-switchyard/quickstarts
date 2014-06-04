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
package org.switchyard.serial.graph.node;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.activation.DataSource;
import javax.xml.namespace.QName;

import org.switchyard.serial.graph.Factory;
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
        } else if (isDOM(clazz)) {
            Node node = new DOMNode();
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
        } else if (isUUID(clazz)) {
            Node node = new UUIDNode();
            graph.putReference(id, node);
            node.compose(obj, graph);
        } else if (isStackTraceElement(clazz)) {
            Node node = new StackTraceElementNode();
            graph.putReference(id, node);
            node.compose(obj, graph);
        } else if (isAccessible(clazz)) {
            Node node;
            if (isThrowable(clazz)) {
                node = new ThrowableAccessNode();
            } else {
                node = new DefaultAccessNode();
            }
            graph.putReference(id, node);
            node.compose(obj, graph);
        } else {
            graph.putReference(id, NoopNode.INSTANCE);
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

    static boolean isDOM(Class<?> clazz) {
        return org.w3c.dom.Node.class.isAssignableFrom(clazz);
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

    static boolean isStackTraceElement(Class<?> clazz) {
        return StackTraceElement.class.isAssignableFrom(clazz);
    }

    static boolean isThrowable(Class<?> clazz) {
        return Throwable.class.isAssignableFrom(clazz);
    }

    static boolean isUUID(Class<?> clazz) {
        return UUID.class.isAssignableFrom(clazz);
    }

    static boolean isAccessible(Class<?> clazz) {
        return Factory.getFactory(clazz).supports(clazz);
    }

    private NodeBuilder() {}

}
