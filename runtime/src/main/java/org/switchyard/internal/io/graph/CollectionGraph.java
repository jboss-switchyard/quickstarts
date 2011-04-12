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
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

/**
 * A Graph representing a Collection, internalized as an LinkedList.
 *
 * @param <T> the type of Object in the Collection
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
@SuppressWarnings("serial")
public class CollectionGraph<T> implements Graph<Collection<T>> {

    private LinkedList<Graph<T>> _collection;

    /**
     * Gets the wrapped Collection.
     * @return the wrapped Collection
     */
    public LinkedList<Graph<T>> getCollection() {
        return _collection;
    }

    /**
     * Sets the wrapped Collection.
     * @param collection the Collection to wrap
     */
    public void setCollection(LinkedList<Graph<T>> collection) {
        _collection = collection;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void compose(Collection<T> object, Map<Integer,Object> visited) throws IOException {
        _collection = new LinkedList<Graph<T>>();
        for (T o : object) {
            _collection.add(GraphBuilder.build(o, visited));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<T> decompose(Map<Integer,Object> visited) throws IOException {
        Collection<Graph<T>> gc = getCollection();
        if (gc != null) {
            Collection<T> oc = new LinkedList<T>();
            for (Graph<T> g : gc) {
                oc.add(g.decompose(visited));
            }
            return oc;
        }
        return null;
    }

    @Override
    public String toString() {
        return "CollectionGraph(collection=" + getCollection() + ")";
    }

}
