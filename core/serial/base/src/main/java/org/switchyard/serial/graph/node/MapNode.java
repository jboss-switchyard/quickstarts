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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.switchyard.serial.graph.Graph;

/**
 * A node representing a Map.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
@SuppressWarnings("serial")
public final class MapNode implements Node {

    private LinkedHashMap<Integer, Integer> _ids;

    /**
     * Default constructor.
     */
    public MapNode() {}

    /**
     * Gets the ids.
     * @return the ids
     */
    public LinkedHashMap<Integer, Integer> getIds() {
        return _ids;
    }

    /**
     * Sets the ids.
     * @param ids the ids
     */
    public void setIds(LinkedHashMap<Integer, Integer> ids) {
        _ids = ids;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("rawtypes")
    public void compose(Object obj, Graph graph) {
        _ids = new LinkedHashMap<Integer, Integer>();
        Map map = (Map)obj;
        
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            Object key = pairs.getKey();
            Integer key_id = NodeBuilder.build(key, graph);
            if (!(graph.getReference(key_id) instanceof NoopNode)) {
                Object val = pairs.getValue();
                Integer val_id = NodeBuilder.build(val, graph);
                if (!(graph.getReference(val_id) instanceof NoopNode)) {
                    _ids.put(key_id, val_id);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Object decompose(Graph graph) {
        Map map = new LinkedHashMap();
        for (Integer key : _ids.keySet()) {
            Integer val = _ids.get(key);
            map.put(graph.decomposeReference(key), graph.decomposeReference(val));
        }
        return map;
    }

}
