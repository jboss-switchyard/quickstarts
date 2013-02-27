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

import java.util.ArrayList;
import java.util.List;

import org.switchyard.serial.graph.Graph;

/**
 * A node representing an array.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
@SuppressWarnings("serial")
public class ArrayNode implements Node {

    private Integer[] _ids;

    /**
     * Default constructor.
     */
    public ArrayNode() {}

    /**
     * Gets the ids.
     * @return the ids
     */
    public Integer[] getIds() {
        return _ids;
    }

    /**
     * Sets the ids.
     * @param ids the ids
     */
    public void setIds(Integer[] ids) {
        _ids = ids;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void compose(Object obj, Graph graph) {
        Object[] array = (Object[])obj;
        List<Integer> ids = new ArrayList<Integer>();
        for (int i=0; i < array.length; i++) {
            Integer id = NodeBuilder.build(array[i], graph);
            if (!(graph.getReference(id) instanceof NoopNode)) {
                ids.add(id);
            }
        }
        _ids = ids.toArray(new Integer[ids.size()]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object decompose(Graph graph) {
        Object[] array = new Object[_ids.length];
        for (int i=0; i < _ids.length; i++) {
            array[i] = graph.decomposeReference(_ids[i]);
        }
        return array;
    }

}
