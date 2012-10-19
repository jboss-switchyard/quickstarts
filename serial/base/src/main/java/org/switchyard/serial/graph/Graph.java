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

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

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

    /**
     * Creates an id for the specified object.
     * @param obj the specified object
     * @return the id
     */
    public Integer id(Object obj) {
        return System.identityHashCode(obj);
    }

}
