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
        for (Object key : map.keySet()) {
            Integer key_id = NodeBuilder.build(key, graph);
            if (!(graph.getReference(key_id) instanceof NoopNode)) {
                Object val = map.get(key);
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
