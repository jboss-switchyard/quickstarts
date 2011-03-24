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
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A Graph representing a Map, internalized as a LinkedHashMap.
 *
 * @param <K> the type of key
 * @param <V> the type of value
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
@SuppressWarnings("serial")
public class MapGraph<K,V> implements Graph<Map<K,V>> {

    private LinkedHashMap<Graph<K>,Graph<V>> _map;

    /**
     * Gets the wrapped map.
     * @return the wrapped map
     */
    public LinkedHashMap<Graph<K>,Graph<V>> getMap() {
        return _map;
    }

    /**
     * Sets the wrapped map.
     * @param map the map to wrap
     */
    public void setMap(LinkedHashMap<Graph<K>,Graph<V>> map) {
        _map = map;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void compose(Map<K,V> object) throws IOException {
        _map = new LinkedHashMap<Graph<K>,Graph<V>>();
        for (Map.Entry<K,V> entry : object.entrySet()) {
            Graph<K> key = GraphBuilder.build(entry.getKey());
            Graph<V> value = GraphBuilder.build(entry.getValue());
            _map.put(key, value);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<K,V> decompose() throws IOException {
        Map<K,V> map = new LinkedHashMap<K,V>();
        for (Map.Entry<Graph<K>,Graph<V>> entry : getMap().entrySet()) {
            map.put(entry.getKey().decompose(), entry.getValue().decompose());
        }
        return map;
    }

}
