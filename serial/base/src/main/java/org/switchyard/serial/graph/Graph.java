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

import java.io.Serializable;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.switchyard.serial.graph.node.Node;
import org.switchyard.serial.graph.node.NodeBuilder;

/**
 * A graph holds references to all nodes.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
@SuppressWarnings("serial")
public final class Graph implements Serializable {

    private Integer _root;
    private Map<Integer, Object> _references = new LinkedHashMap<Integer, Object>();
    private transient Map<Object, Integer> _ids;
    private transient AtomicInteger _sequence;
    private transient Queue<Runnable> _resolutions;

    /**
     * Default constructor.
     */
    public Graph() {}

    /**
     * Constructor which composes the specified root object.
     * @param obj the specified root object
     */
    public Graph(Object obj) {
        composeRoot(obj);
    }

    /**
     * Gets the root node id.
     * @return the root node id
     */
    public Integer getRoot() {
        return _root;
    }

    /**
     * Sets the root node id.
     * @param root the root node id
     */
    public void setRoot(Integer root) {
        _root = root;
    }

    /**
     * Gets the node references.
     * @return the node references
     */
    public Map<Integer, Object> getReferences() {
        return _references;
    }

    /**
     * Sets the node references.
     * @param references the node references
     */
    public void setReferences(Map<Integer, Object> references) {
        _references = references;
    }

    /**
     * Gets a reference with the specified id.
     * @param id the specified id
     * @return the reference
     */
    public Object getReference(Integer id) {
        return _references.get(id);
    }

    /**
     * Puts a reference with the specified id.
     * @param id the specified id
     * @param obj the reference
     */
    public void putReference(Integer id, Object obj) {
        _references.put(id, obj);
    }

    private Queue<Runnable> getResolutions() {
        if (_resolutions == null) {
            _resolutions = new ConcurrentLinkedQueue<Runnable>();
        }
        return _resolutions;
    }

    /**
     * Adds a runnable reference resolution.
     * @param res the runnable reference resolution
     */
    public void addResolution(Runnable res) {
        getResolutions().offer(res);
    }

    /**
     * Composes the root object.
     * @param obj the root object
     */
    public void composeRoot(Object obj) {
        setRoot(NodeBuilder.build(obj, this));
    }

    /**
     * Decomposes the root object.
     * @return the root object
     */
    public Object decomposeRoot() {
        Object obj = decomposeReference(getRoot());
        Runnable res = getResolutions().poll();
        while (res != null) {
            res.run();
            res = getResolutions().poll();
        }
        return obj;
    }

    /**
     * Decomposes a reference with the specified id.
     * @param id the specified id
     * @return the decomposed reference
     */
    public Object decomposeReference(Integer id) {
        Object obj = getReference(id);
        if (obj instanceof Node) {
            obj = ((Node)obj).decompose(this);
            putReference(id, obj);
        }
        return obj;
    }

    private Map<Object, Integer> getIds() {
        if (_ids == null) {
            _ids = new IdentityHashMap<Object, Integer>();
        }
        return _ids;
    }

    private Integer nextId() {
        if (_sequence == null) {
            _sequence = new AtomicInteger(0);
        }
        return _sequence.incrementAndGet();
    }

    /**
     * Creates an id for the specified object.
     * @param obj the specified object
     * @return the id, or 0 if obj is null
     */
    public Integer id(Object obj) {
        if (obj == null) {
            return 0;
        }
        Map<Object, Integer> ids = getIds();
        Integer id = ids.get(obj);
        if (id == null) {
            id = nextId();
            ids.put(obj, id);
        }
        return id;
    }

}
