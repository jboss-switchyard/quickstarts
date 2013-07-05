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
