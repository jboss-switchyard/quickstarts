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
import java.util.ArrayList;
import java.util.Collection;

/**
 * A Graph representing a Collection, internalized as an ArrayList.
 *
 * @param <T> the type of Object in the Collection
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
@SuppressWarnings("serial")
public class CollectionGraph<T> implements Graph<Collection<T>> {

    private ArrayList<Graph<T>> _collection;

    /**
     * Gets the wrapped Collection.
     * @return the wrapped Collection
     */
    public ArrayList<Graph<T>> getCollection() {
        return _collection;
    }

    /**
     * Sets the wrapped Collection.
     * @param collection the Collection to wrap
     */
    public void setCollection(ArrayList<Graph<T>> collection) {
        _collection = collection;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void compose(Collection<T> object) throws IOException {
        _collection = new ArrayList<Graph<T>>(object.size());
        for (T o : object) {
            _collection.add(GraphBuilder.build(o));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<T> decompose() throws IOException {
        Collection<T> c = new ArrayList<T>();
        for (Graph<T> g : getCollection()) {
            c.add(g.decompose());
        }
        return c;
    }

}
