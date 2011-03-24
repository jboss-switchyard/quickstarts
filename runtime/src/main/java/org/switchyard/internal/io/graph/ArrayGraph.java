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
import java.util.Arrays;
import java.util.Collection;

/**
 * A Graph representing an array, internalized as a CollectoinGraph.
 *
 * @param <T> the array composite Class type
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
@SuppressWarnings("serial")
public class ArrayGraph<T> implements Graph<T[]> {

    private CollectionGraph<T> _collectionGraph;

    /**
     * Gets the wrapped Collection graph.
     * @return the wrapped Collection graph
     */
    public CollectionGraph<T> getCollectionGraph() {
        return _collectionGraph;
    }

    /**
     * Sets the wrapped Collection graph.
     * @param collectionGraph the Collection graph to wrap
     */
    public void setCollectionGraph(CollectionGraph<T> collectionGraph) {
        _collectionGraph = collectionGraph;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void compose(T[] object) throws IOException {
        _collectionGraph = new CollectionGraph<T>();
        _collectionGraph.compose(Arrays.asList(object));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public T[] decompose() throws IOException {
        Collection<T> c = _collectionGraph.decompose();
        return c.toArray((T[])new Object[c.size()]);
    }

}
