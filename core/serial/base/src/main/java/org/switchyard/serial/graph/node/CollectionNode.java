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

import java.util.Collection;
import java.util.LinkedList;

import org.switchyard.serial.graph.Graph;

/**
 * A node representing a Collection.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
@SuppressWarnings("serial")
public final class CollectionNode implements Node {

    private LinkedList<Integer> _ids;

    /**
     * Default constructor.
     */
    public CollectionNode() {}

    /**
     * Gets the ids.
     * @return the ids
     */
    public LinkedList<Integer> getIds() {
        return _ids;
    }

    /**
     * Sets the ids.
     * @param ids the ids
     */
    public void setIds(LinkedList<Integer> ids) {
        _ids = ids;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("rawtypes")
    public void compose(Object obj, Graph graph) {
        _ids = new LinkedList<Integer>();
        Collection coll = (Collection)obj;
        for (Object o : coll) {
            Integer id = NodeBuilder.build(o, graph);
            if (!(graph.getReference(id) instanceof NoopNode)) {
                _ids.add(NodeBuilder.build(o, graph));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Object decompose(Graph graph) {
        if (_ids != null) {
            Collection coll = new LinkedList();
            for (Integer id : _ids) {
                coll.add(graph.decomposeReference(id));
            }
            return coll;
        }
        return null;
    }

}
