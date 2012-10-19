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
            _ids.add(NodeBuilder.build(o, graph));
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
